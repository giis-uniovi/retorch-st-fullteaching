import { Component, OnInit, OnDestroy, EventEmitter } from '@angular/core';
import { Location } from '@angular/common';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { Subscription } from 'rxjs';
import { environment } from '../../../environments/environment';

import { MaterializeAction } from '../../shared/materialize.directive';

import { CourseDetailsModalDataService } from '../../services/course-details-modal-data.service';
import { UploaderModalService } from '../../services/uploader-modal.service';
import { FilesEditionService } from '../../services/files-edition.service';
import { CourseService } from '../../services/course.service';
import { SessionService } from '../../services/session.service';
import { ForumService } from '../../services/forum.service';
import { FileService } from '../../services/file.service';
import { AuthenticationService } from '../../services/authentication.service';
import { VideoSessionService } from '../../services/video-session.service';
import { AnimationService } from '../../services/animation.service';

import { Session } from '../../classes/session';
import { Course } from '../../classes/course';
import { Entry } from '../../classes/entry';
import { Comment } from '../../classes/comment';
import { FileGroup } from '../../classes/file-group';
import { File } from '../../classes/file';
import { User } from '../../classes/user';

import { OpenVidu, LocalRecorder, Publisher, VideoElementEvent } from "openvidu-browser";


@Component({
    selector: 'app-course-details',
    providers: [FilesEditionService],
    templateUrl: './course-details.component.html',
    styleUrls: ['./course-details.component.css'],
    standalone: false
})
export class CourseDetailsComponent implements OnInit, OnDestroy {

  course: Course;
  selectedEntry: Entry;

  updatedFileGroup: FileGroup;
  updatedFile: File;

  fadeAnim = 'commentsHidden';
  tabId: number = 0;
  courseLoaded = false;

  //POST MODAL
  processingPost = false;
  postModalMode: number = 3; //0 -> New entry | 1 -> New comment | 2 -> New session | 4 -> Add fileGroup | 5 -> Add file | 6 -> New video comment
  postModalTitle: string = "New session";
  postModalEntry: Entry;
  postModalCommentReplay: Comment;
  postModalFileGroup: FileGroup;
  inputTitle: string;
  inputComment: string;
  inputDate: Date;
  inputTime: string;

  // recording
  recorder: LocalRecorder;
  recordRadioEnabled = false;
  recordType = 'video';
  publisherErrorMessage = '';
  recording = false;
  paused = false;
  OV: OpenVidu;
  publisher: Publisher;

  //PUT-DELETE MODAL
  processingPut = false;
  putdeleteModalMode: number = 0; //0 -> Modify session | 1 -> Modify forum | 2 -> Modify file group | 3 -> Modify file | 4 -> Add attenders
  putdeleteModalTitle: string = "Modify session";
  //Sessions
  inputSessionTitle: string;
  inputSessionDescription: string;
  inputSessionDate: Date;
  inputSessionTime: string;
  updatedSession: Session;
  updatedSessionDate: string;
  allowSessionDeletion = false;
  //Forum
  allowForumEdition = false;
  checkboxForumEdition: string;
  //Files
  inputFileTitle: string;
  //Attenders
  inputAttenderSimple: string;
  inputAttenderMultiple: string;
  inputAttenderSeparator: string = "";
  attenderTabSelected: number = 0;

  //COURSE INFO TAB
  processingCourseInfo = false;
  welcomeText: string;
  welcomeTextEdition = false;
  welcomeTextPreview = false;
  previewButton: string = 'preview';

  //FILES TAB
  allowFilesEdition = false;
  filesEditionIcon: string = "mode_edit";

  //ATTENDERS TAB
  allowAttendersEdition = false;
  addAttendersError = false;
  addAttendersCorrect = false;
  attErrorTitle: string;
  attErrorContent: string;
  attCorrectTitle: string;
  attCorrectContent: string;
  attendersEditionIcon: string = "mode_edit";
  arrayOfAttDels = [];

  private readonly actions2 = new EventEmitter<string | MaterializeAction>();
  private readonly actions3 = new EventEmitter<string | MaterializeAction>();

  subscription1: Subscription; //Subscription to service 'courseDetailsModalDataService' for receiving POST modal dialog changes
  subscription2: Subscription; //Subscription to service 'courseDetailsModalDataService' for receiving PUT/DELETE modal dialog changes
  subscription3: Subscription; //Subscription to service 'filesEditionService' for receiving FileGroup deletions
  subscription4: Subscription; //Subscription to service 'filesEditionService' for receiving FileGroup and File objects that are being updated

  private readonly URL_UPLOAD: string;
  private readonly URL_FILE_READER_UPLOAD: string;

  private url_file_upload: string;

  constructor(
    private readonly courseService: CourseService,
    private readonly forumService: ForumService,
    private readonly fileService: FileService,
    private readonly sessionService: SessionService,
    private readonly animationService: AnimationService,
    private readonly authenticationService: AuthenticationService,
    private readonly videoSessionService: VideoSessionService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly location: Location,
    private readonly courseDetailsModalDataService: CourseDetailsModalDataService,
    private readonly uploaderModalService: UploaderModalService,
    private readonly filesEditionService: FilesEditionService,
    ) {

    //URL for uploading files changes between development stage and production stage
    this.URL_UPLOAD = environment.URL_UPLOAD;
    this.URL_FILE_READER_UPLOAD = environment.URL_EMAIL_FILE_UPLOAD;

    //Subscription for receiving POST modal dialog changes
    this.subscription1 = this.courseDetailsModalDataService.postModeAnnounced$.subscribe(
      objs => {
        //objs is an array containing postModalMode, postModalTitle, postModalEntry, postModalCommentReplay and postModalFileGroup in that specific order
        this.postModalMode = objs[0];
        this.postModalTitle = objs[1];
        this.postModalEntry = objs[2];
        this.postModalCommentReplay = objs[3];
        this.postModalFileGroup = objs[4];
      });

    //Subscription for receiving PUT/DELETE modal dialog changes
    this.subscription2 = this.courseDetailsModalDataService.putdeleteModeAnnounced$.subscribe(
      objs => {
        //objs is an array containing putdeleteModalMode and putdeleteModalTitle, in that specific order
        this.putdeleteModalMode = objs[0];
        if (objs[1]) this.putdeleteModalTitle = objs[1]; //Only if the string is not empty
      });

    //Subscription for receiving FileGroup deletions
    this.subscription3 = this.filesEditionService.fileGroupDeletedAnnounced$.subscribe(
      fileGroupDeletedId => {
        //fileGroupDeletedId is the id of the FileGroup that has been deleted by the child component (FileGroupComponent)
        if (this.recursiveFileGroupDeletion(this.course.courseDetails.files, fileGroupDeletedId)) {
          if (this.course.courseDetails.files.length == 0) this.changeModeEdition(); //If there are no fileGroups, mode edit is closed
        }
      });

    //Subscription for receiving FileGroup and File objects that are being updated by the child component (FileGroupComponent)
    this.subscription4 = this.filesEditionService.fileFilegroupUpdatedAnnounced$.subscribe(
      objs => {
        //objs is an array containing updatedFileGroup and updatedFile, in that specific order
        if (objs[0]) {
          this.updatedFileGroup = objs[0];
          this.inputFileTitle = this.updatedFileGroup.title;
          this.url_file_upload = this.URL_UPLOAD + this.course.id + "/file-group/" + this.updatedFileGroup.id;
        }
        if (objs[1]) {
          this.updatedFile = objs[1];
          this.inputFileTitle = this.updatedFile.name;
        }
      });

  }

  ngOnInit() {
    this.authenticationService.checkCredentials()
      .then(() => {
        this.route.params.forEach((params: Params) => {
          this.courseLoaded = false;
          let id = +params['id'];
          this.tabId = +params['tabId'];
          this.courseService.getCourse(id).subscribe(
            course => {
              this.sortSessionsByDate(course.sessions);
              this.course = course;
              this.selectedEntry = this.course.courseDetails.forum.entries[0]; //selectedEntry default to first entry
              if (this.course.sessions.length > 0) this.changeUpdatedSession(this.course.sessions[0]); //updatedSession default to first session
              this.checkboxForumEdition = this.course.courseDetails.forum.activated ? 'DEACTIVATION' : 'ACTIVATION';
              this.welcomeText = this.course.courseDetails.info;
              this.courseLoaded = true;
            },
            error => { });
        });
      })
      .catch((e) => { });
  }

  ngOnDestroy() {
    this.subscription1.unsubscribe();
    this.subscription2.unsubscribe();
    this.subscription3.unsubscribe();
    this.subscription4.unsubscribe();
  }

  goToSessionVideo(session: Session) {
    this.videoSessionService.session = session;
    this.videoSessionService.course = this.course;
    if (this.isSessionReady(session)) this.router.navigate(['/session', session.id]);
  }

  updatePostModalMode(mode: number, title: string, header: Entry, commentReplay: Comment, fileGroup: FileGroup) {
    // mode[0: "New Entry", 1: "New comment", 2: "New session", 3: "New VideoEntry", 4: "New FileGroup", 5: "Add files"]
    let objs = [mode, title, header, commentReplay, fileGroup];
    this.courseDetailsModalDataService.announcePostMode(objs);
  }

  updatePutDeleteModalMode(mode: number, title: string) {
    let objs = [mode, title];
    this.courseDetailsModalDataService.announcePutdeleteMode(objs);
  }

  getLastEntryComment(entry: Entry) {
    let comment = entry.comments[0];
    for (let c of entry.comments) {
      if (c.date > comment.date) comment = c;
      comment = this.recursiveReplyDateCheck(comment);
    }
    return comment;
  }

  numberToDate(d: number) {
    return new Date(d);
  }

  changeUpdatedSession(session: Session) {
    this.updatedSession = session;
    this.updatedSessionDate = (new Date(this.updatedSession.date)).toISOString().split("T")[0]; //YYYY-MM-DD format
    this.inputSessionTitle = this.updatedSession.title;
    this.inputSessionDescription = this.updatedSession.description;
    this.inputSessionDate = new Date(this.updatedSession.date);
    this.inputSessionTime = this.dateToTimeInputFormat(this.inputSessionDate);
  }

  changeModeEdition() {
    this.allowFilesEdition = !this.allowFilesEdition;
    if (this.allowFilesEdition) {
      this.filesEditionIcon = "keyboard_arrow_left";
    }
    else {
      this.filesEditionIcon = "mode_edit";
    }
    this.filesEditionService.announceModeEdit(this.allowFilesEdition);
  }

  changeModeAttenders() {
    this.allowAttendersEdition = !this.allowAttendersEdition;
    if (this.allowAttendersEdition) {
      this.attendersEditionIcon = "keyboard_arrow_left";
    }
    else {
      this.attendersEditionIcon = "mode_edit";
    }
  }

  isCurrentPostMode(possiblePostModes: string[]): boolean {
    return (possiblePostModes.indexOf(this.postModalMode.toString()) > -1);
  }

  isCurrentPutdeleteMode(possiblePutdeleteModes: string[]): boolean {
    return (possiblePutdeleteModes.indexOf(this.putdeleteModalMode.toString()) > -1);
  }


  filesUploadStarted(event) {
    console.log("File upload started...");
  }

  filesUploadCompleted(response) {
    let fg = JSON.parse(response) as FileGroup;
    console.log("File upload completed (items successfully uploadded). Response: ", fg);

    for (let i = 0; i < this.course.courseDetails.files.length; i++) {
      if (this.course.courseDetails.files[i].id == fg.id) {
        this.course.courseDetails.files[i] = fg;
        this.updatedFileGroup = fg;
        break;
      }
    }
  }

  //POST new Entry, Comment or Session
  onCourseDetailsSubmit() {

    this.processingPost = true;

    //If modal is opened in "New Entry" mode
    if (this.postModalMode === 0) {
      let e = new Entry(this.inputTitle, [new Comment(this.inputComment, '', false, null)]);

      this.forumService.newEntry(e, this.course.courseDetails.id).subscribe( //POST method requires an Entry and the CourseDetails id that contains its Forum
        response => {
          this.course.courseDetails.forum.entries.push(response.entry as Entry); //Only on succesful post we update the modified forum
          this.processingPost = false;
          this.actions2.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPost = false; }
      );
    }

    //If modal is opened in "New Session" mode
    else if (this.postModalMode === 2) {
      let date = new Date(this.inputDate);
      let hoursMins = this.inputTime.split(":");
      date.setHours(Number.parseInt(hoursMins[0]), Number.parseInt(hoursMins[1]));
      let s = new Session(this.inputTitle, this.inputComment, date.getTime());
      this.sessionService.newSession(s, this.course.id).subscribe(
        response => {
          this.sortSessionsByDate(response.sessions);
          this.course = response;

          this.processingPost = false;
          this.actions2.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPost = false; }
      );
    }

    //If modal is opened in "New Comment" mode (replaying or not replaying)
    else if (this.postModalMode === 1) {
      let c = new Comment(this.inputComment, '', false, this.postModalCommentReplay);
      this.forumService.newComment(c, this.selectedEntry.id, this.course.courseDetails.id).subscribe(
        response => {
          //Only on succesful post we locally update the created entry
          let ents = this.course.courseDetails.forum.entries;
          for (let i = 0; i < ents.length; i++) {
            if (ents[i].id == this.selectedEntry.id) {
              this.course.courseDetails.forum.entries[i] = response.entry; //The entry with the required ID is updated
              this.selectedEntry = this.course.courseDetails.forum.entries[i];
              break;
            }
          }

          this.processingPost = false;
          this.actions2.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPost = false; }
      );
    }

    //If modal is opened in "New FileGroup" mode
    else if (this.postModalMode === 4) {
      let f = new FileGroup(this.inputTitle, this.postModalFileGroup);
      this.fileService.newFileGroup(f, this.course.courseDetails.id).subscribe(
        response => {
          //Only on succesful post we locally update the entire course details
          this.course.courseDetails = response;

          this.processingPost = false; // Stop the loading animation
          this.actions2.emit({ action: "modal", params: ['close'] }); // CLose the modal
          if (!this.allowFilesEdition) this.changeModeEdition(); // Activate file edition view if deactivated
        },
        error => { this.processingPost = false; }
      );
    }

    //If modal is opened in "New Videoentry" mode
    else if (this.postModalMode === 3) {
      console.log('Sending new videoentry');
      let e = new Entry(this.inputTitle, [new Comment(this.inputComment, 'new-url', this.recordType === 'audio', null)]);
      this.forumService.newEntry(e, this.course.courseDetails.id).subscribe( //POST method requires an Entry and the CourseDetails id that contains its Forum
        response => {
          this.recorder.uploadAsMultipartfile(this.URL_UPLOAD + this.course.id + '/comment/' + response.comment.id)
            .then((responseAsText) => {
              this.cleanRecording();
              let comment: Comment = JSON.parse(responseAsText) as Comment;
              let entry: Entry = response.entry as Entry;
              let index = entry.comments.map((c) => { return c.id; }).indexOf(response.comment.id);
              if (index != -1) {
                entry.comments[index] = comment;
              }
              this.course.courseDetails.forum.entries.push(entry); //Only on succesful post we update the modified forum
              this.processingPost = false;
              this.actions2.emit({ action: "modal", params: ['close'] });
            })
            .catch((e) => { });
        },
        error => { this.processingPost = false; }
      );
    }

    //If modal is opened in "New Video Comment" mode (replaying or not replaying)
    else if (this.postModalMode === 6) {
      let c = new Comment(this.inputComment, 'new-url', this.recordType === 'audio', this.postModalCommentReplay);
      this.forumService.newComment(c, this.selectedEntry.id, this.course.courseDetails.id).subscribe(
        response => {
          this.recorder.uploadAsMultipartfile(this.URL_UPLOAD + this.course.id + '/comment/' + response.comment.id)
            .then((responseAsText) => {
              this.cleanRecording();
              let comment: Comment = JSON.parse(responseAsText) as Comment;
              let entry: Entry = response.entry as Entry;

              this.replaceComment(entry.comments, comment);

              let ents = this.course.courseDetails.forum.entries;
              for (let i = 0; i < ents.length; i++) {
                if (ents[i].id == this.selectedEntry.id) {
                  this.course.courseDetails.forum.entries[i] = entry; // The entry with the required ID is updated
                  this.selectedEntry = this.course.courseDetails.forum.entries[i];
                  break;
                }
              }

              this.processingPost = false;
              this.actions2.emit({ action: "modal", params: ['close'] });
            })
            .catch((e) => { });
        },
        error => { this.processingPost = false; }
      );
    }
  }

  //PUT existing Session or Forum
  onPutDeleteSubmit() {

    this.processingPut = true;

    //If modal is opened in PUT existing Session
    if (this.putdeleteModalMode === 0) {
      let modifiedDate: number = this.fromInputToNumberDate(this.updatedSessionDate, this.inputSessionTime);
      let s: Session = new Session(this.inputSessionTitle, this.inputSessionDescription, modifiedDate);
      s.id = this.updatedSession.id; //The new session must have the same id as the modified session in order to replace it
      this.sessionService.editSession(s).subscribe(
        response => {
          //Only on succesful put we locally update the modified session
          for (let i = 0; i < this.course.sessions.length; i++) {
            if (this.course.sessions[i].id == response.id) {
              this.course.sessions[i] = response; //The session with the required ID is updated
              this.updatedSession = this.course.sessions[i];
              break;
            }
          }
          this.processingPut = false;
          this.actions3.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPut = false; }
      );
    }

    //If modal is opened in PUT existing Forum
    else if (this.putdeleteModalMode === 1) {
      this.forumService.editForum(!this.course.courseDetails.forum.activated, this.course.courseDetails.id).subscribe(
        response => {
          //Only on succesful put we locally update the modified session
          this.course.courseDetails.forum.activated = response;
          this.allowForumEdition = false;
          this.checkboxForumEdition = response ? 'DEACTIVATION' : 'ACTIVATION';

          this.processingPut = false;
          this.actions3.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPut = false; }
      );
    }

    //If modal is opened in PUT existing FileGroup
    else if (this.putdeleteModalMode === 2) {
      let fg: FileGroup = new FileGroup(this.inputFileTitle, null);
      fg.id = this.updatedFileGroup.id;
      this.fileService.editFileGroup(fg, this.course.id).subscribe(
        response => {
          for (let i = 0; i < this.course.courseDetails.files.length; i++) {
            if (this.course.courseDetails.files[i].id == response.id) {
              this.course.courseDetails.files[i] = response; //The root fileGroup with the required ID is updated
              //this.updatedFileGroup = this.course.courseDetails.files[i];
              break;
            }
          }

          this.processingPut = false;
          this.actions3.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPut = false; }
      );
    }

    //If modal is opened in PUT existing File
    else if (this.putdeleteModalMode === 3) {
      let f: File = new File(1, this.inputFileTitle, "www.newlink.com");
      f.id = this.updatedFile.id;
      this.fileService.editFile(f, this.updatedFileGroup.id, this.course.id).subscribe(
        response => {
          for (let i = 0; i < this.course.courseDetails.files.length; i++) {
            if (this.course.courseDetails.files[i].id == response.id) {
              this.course.courseDetails.files[i] = response; //The root fileGroup with the required ID is updated
              //this.updatedFileGroup = this.course.courseDetails.files[i];
              break;
            }
          }

          this.processingPut = false;
          this.actions3.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPut = false; }
      );
    }

    //If modal is opened in Add attenders
    else if (this.putdeleteModalMode === 4) {
      //If the attenders are being added in the SIMPLE tab
      if (this.attenderTabSelected === 0) {
        console.log("Adding one attender in the SIMPLE tab");
        let arrayNewAttenders = [this.inputAttenderSimple];
        this.courseService.addCourseAttenders(this.course.id, arrayNewAttenders).subscribe(
          response => {
            let newAttenders = response.attendersAdded as User[];
            this.course.attenders = this.course.attenders.concat(newAttenders);
            this.handleAttendersMessage(response);

            this.processingPut = false;
            this.actions3.emit({ action: "modal", params: ['close'] });
          },
          error => { this.processingPut = false; }
        );
      }
      //If the attenders are being added in the MULTIPLE tab
      else if (this.attenderTabSelected === 1) {
        console.log("Adding multiple attenders in the MULTIPLE tab");

        //The input text is divided into entire words (by whitespaces, new lines and the custom separator)
        let emailsFiltered = this.inputAttenderMultiple.replace('\n', ' ').replace('\r', ' ');
        if (this.inputAttenderSeparator) {
          let regExSeparator = new RegExp(this.inputAttenderSeparator, 'g');
          emailsFiltered = emailsFiltered.replace(regExSeparator, ' ');
        }
        let arrayNewAttenders = emailsFiltered.split(/\s+/).filter(v => v != '');

        this.courseService.addCourseAttenders(this.course.id, arrayNewAttenders).subscribe(
          response => { //response is an object with 4 arrays: attenders added, attenders that were already added, emails invalid and emails not registered
            let newAttenders = response.attendersAdded as User[];
            this.course.attenders = this.course.attenders.concat(newAttenders);
            this.handleAttendersMessage(response);

            this.processingPut = false;
            this.actions3.emit({ action: "modal", params: ['close'] });
          },
          error => { this.processingPut = false; }
        );
      }
      //If the attenders are being added in the FILE UPLOAD tab
      else if (this.attenderTabSelected === 2) {
        console.log("Adding attenders by file upload in the FILE UPLOAD tab");
        this.processingPut = false;
      }
    }
  }

  //DELETE existing Session
  deleteSession() {
    this.processingPut = true;

    this.sessionService.deleteSession(this.updatedSession.id).subscribe(
      response => {
        //Only on succesful put we locally delete the session
        for (let i = 0; i < this.course.sessions.length; i++) {
          if (this.course.sessions[i].id == response.id) {
            this.course.sessions.splice(i, 1); //The session with the required ID is deleted
            this.updatedSession = this.course.sessions[0];
            break;
          }
        }

        this.processingPut = false;
        this.actions3.emit({ action: "modal", params: ['close'] });
      },
      error => { this.processingPut = false; }
    );
  }

  //Remove attender from course
  deleteAttender(attender: User, j: number) {
    console.log("Deleting attender " + attender.nickName);

    this.arrayOfAttDels[j] = true; // Start deleting animation

    const c = new Course(this.course.title, this.course.image, this.course.courseDetails);
    c.id = this.course.id;
    for (const a of this.course.attenders) {
      if (a.id !== attender.id) {
        c.attenders.push(new User(a));
      }
    }
    this.courseService.deleteCourseAttenders(c).subscribe(
      response => {
        this.course.attenders = response;
        this.arrayOfAttDels[j] = false;
        if (this.course.attenders.length <= 1) this.changeModeAttenders(); //If there are no attenders, mode edit is closed
      },
      error => { this.arrayOfAttDels[j] = false; }
    );
  }

  //Updates the course info
  updateCourseInfo() {
    console.log("Updating course info");

    this.processingCourseInfo = true;

    let c: Course = new Course(this.course.title, this.course.image, this.course.courseDetails);
    c.courseDetails.info = this.welcomeText;
    c.id = this.course.id;
    this.courseService.editCourse(c, "updating course info").subscribe(
      response => {
        //Only on succesful put we locally update the modified course
        this.course = response;
        this.welcomeText = this.course.courseDetails.info;

        this.processingCourseInfo = false;
      },
      error => { this.processingCourseInfo = false; }
    )
  }

  //Closes the course info editing mode
  closeUpdateCourseInfo() {
    this.welcomeText = this.course.courseDetails.info;
    this.welcomeTextEdition = false;
    this.welcomeTextPreview = false;
    this.previewButton = 'preview';
  }

  changeUrlTab(tab: number) {
    this.location.replaceState("/courses/" + this.course.id + "/" + tab);
  }

  isEntryTeacher(entry: Entry) {
    return (entry.user.roles.indexOf('ROLE_TEACHER') > -1);
  }


  fileReaderUploadStarted(started: boolean) {
    this.processingPut = started;
  }

  fileReaderUploadCompleted(response) {
    console.log("File uploaded succesfully. Waiting for the system to add all students...  ");
    let objResponse = JSON.parse(response);
    if ("attendersAdded" in objResponse) {
      let newAttenders = objResponse.attendersAdded as User[];

      console.log("New attenders added:");
      console.log(newAttenders);

      this.course.attenders = this.course.attenders.concat(newAttenders);
      this.handleAttendersMessage(objResponse);

      this.processingPut = false; // Stop the loading animation
      this.uploaderModalService.announceUploaderClosed(true); // Clear the uploader file queue
      this.actions3.emit({ action: "modal", params: ['close'] }); // Close the modal
    } else {
      this.processingPut = false;
      console.log("There has been an error: " + response);
    }
  }


  //INTERNAL AUXILIAR METHODS
  //Sorts an array of Session by their 'date' attribute (the first are the erliest)
  sortSessionsByDate(sessionArray: Session[]): void {
    sessionArray.sort((a, b) => {
      if (a.date > b.date) { return 1; }
      if (b.date > a.date) { return -1; }
      return 0;
    });
  }

  //Transforms a Date object into a single string ("HH:MM")
  dateToTimeInputFormat(date: Date): string {
    let hours = date.getHours() < 10 ? "0" + date.getHours().toString() : date.getHours().toString();
    let minutes = date.getMinutes() < 10 ? "0" + date.getMinutes().toString() : date.getMinutes().toString();
    return (hours + ":" + minutes);
  }

  //Transforms two strings ("YYYY-MM-DD", "HH:MM") into a new Date object
  fromInputToNumberDate(date: string, time: string): number {
    const newDate: Date = new Date(date); //date parameter has a valid ISO format: YYYY-MM-DD
    const timeArray = time.split(":");
    newDate.setHours(Number.parseInt(timeArray[0]));
    newDate.setMinutes(Number.parseInt(timeArray[1]));
    return newDate.getTime(); //returning miliseconds
  }

  //Returns the earliest Comment (by 'date' attribute) in the recursive structure of comments which has Comment 'c' as root
  recursiveReplyDateCheck(c: Comment): Comment {
    for (const r of c.replies) {
      const checked = this.recursiveReplyDateCheck(r);
      if (checked.date > c.date) { c = checked; }
    }
    return c;
  }

  //Delets a fileGroup from this.course.courseDetails.files recursively, given a fileGroup id
  recursiveFileGroupDeletion(fileGroupLevel: FileGroup[], fileGroupDeletedId: number): boolean {
    if (fileGroupLevel) {
      for (let i = 0; i < fileGroupLevel.length; i++) {
        if (fileGroupLevel[i].id == fileGroupDeletedId) {
          fileGroupLevel.splice(i, 1);
          return true;
        }
        let deleted = this.recursiveFileGroupDeletion(fileGroupLevel[i].fileGroups, fileGroupDeletedId);
        if (deleted) return deleted;
      }
    }
    return false;
  }

  //Creates an error message when there is some problem when adding Attenders to a Course
  //It also generates a correct feedback message when any student has been correctly added to the Course
  handleAttendersMessage(response) {
    this.attErrorContent = '';
    this.attCorrectContent = '';

    if (response.attendersAdded.length > 0) {
      this.attCorrectContent = response.attendersAdded.map(u => `<span class='feedback-list'>${u.name}</span>`).join('');
      this.attCorrectTitle = "The following users where properly added";
      this.addAttendersCorrect = true;
    }

    this.attErrorContent = this.buildAttenderErrorContent(response);
    if (this.attErrorContent) {
      this.attErrorTitle = "There have been some problems";
      this.addAttendersError = true;
    } else if (response.attendersAdded.length === 0) {
      this.attErrorTitle = "No emails there!";
      this.addAttendersError = true;
    }
  }

  private buildAttenderErrorContent(response): string {
    let content = '';
    if (response.attendersAlreadyAdded.length > 0) {
      content += "<span class='feedback-span'>The following users were already added to the course</span>";
      content += response.attendersAlreadyAdded.map(u => `<span class='feedback-list'>${u.name}</span>`).join('');
    }
    if (response.emailsValidNotRegistered.length > 0) {
      content += "<span class='feedback-span'>The following users are not registered</span>";
      content += response.emailsValidNotRegistered.map(e => `<span class='feedback-list'>${e}</span>`).join('');
    }
    if (response.emailsInvalid?.length > 0) {
      content += "<span class='feedback-span'>These are not valid emails</span>";
      content += response.emailsInvalid.map(e => `<span class='feedback-list'>${e}</span>`).join('');
    }
    return content;
  }

  changeFilesOrder(event: { fileId: number; sourceGroupId: number; targetGroupId: number; newPosition: number }) {
    this.fileService.editFileOrder(
      event.fileId, event.sourceGroupId, event.targetGroupId, event.newPosition, this.course.id
    ).subscribe({
      next: response => { this.course.courseDetails.files = response; },
      error: () => {}
    });
  }

  getFilePosition(fileMoved: number, fileGroupTarget: number): number {
    let fileGroupFound: FileGroup = null;
    let i = 0;
    while (!fileGroupFound && i < this.course.courseDetails.files.length) {
      fileGroupFound = this.findFileGroup(fileGroupTarget, this.course.courseDetails.files[i]);
      i++;
    }
    if (fileGroupFound) {
      for (let j = 0; j < fileGroupFound.files.length; j++) {
        if (fileGroupFound.files[j].id == fileMoved) {
          return j;
        }
      }
      return -1;
    }
    return -1;
  }


  findFileGroup(id: number, currentFileGroup: FileGroup): FileGroup {
    let i: number;
    let currentChildFileGroup: FileGroup;
    let result: FileGroup;

    if (id == currentFileGroup.id) {
      return currentFileGroup;
    } else {
      for (i = 0; i < currentFileGroup.fileGroups.length; i++) {
        currentChildFileGroup = currentFileGroup.fileGroups[i];
        result = this.findFileGroup(id, currentChildFileGroup);
        if (result !== null) {
          return result;
        }
      }
      return null;
    }
  }

  isSessionReady(_session: Session) {
    return true;
  }

  recordVideo(publisherOptions: any) {
    this.recordRadioEnabled = false;
    this.OV = new OpenVidu();
    this.publisher = this.OV.initPublisher(
      'post-video',
      publisherOptions,
      (err) => {
        if (err) {
          this.publisherErrorMessage = err.message;
          console.warn(err);
          if (err.name === 'SCREEN_EXTENSION_NOT_INSTALLED') {
            this.publisherErrorMessage = 'In Chrome you need an extension installed to share your screen. Go to <a href="' + err.message + '">this link</a> to install the extension. YOU MUST REFRESH THE PAGE AFTER INSTALLING IT'
          }
        }
      }
    );
    this.publisher.on('videoElementCreated', (e: VideoElementEvent) => {
      if (publisherOptions.audio && !publisherOptions.video) {
        $(e.element).css({ 'background-color': '#4d4d4d', 'padding': '50px' });
        $(e.element).attr('poster', 'assets/images/volume.png');
      }
    })
    this.publisher.on('videoPlaying', (e: VideoElementEvent) => {
      this.recordRadioEnabled = true;
      this.addRecordingControls(e.element);
    });
  }

  startStopRecording() {
    if (this.recording) {
      this.recorder.stop()
        .then(() => {
          document.getElementById('post-video').getElementsByTagName('video')[0].style.display = 'none';
          this.removeRecordingControls();
          let recordingPreview: HTMLVideoElement = this.recorder.preview('post-video');
          recordingPreview.controls = true;
          this.addPostRecordingControls(recordingPreview);
        })
        .catch((e) => {
        });
    } else {
      this.recorder = new LocalRecorder(this.publisher.stream);
      this.recorder.record();
      document.getElementById('record-start-stop').innerHTML = 'Finish';
      document.getElementById('record-pause-resume').style.display = 'inline-block';
    }
    this.recording = !this.recording;
  }

  pauseResumeRecording() {
    if (this.paused) {
      this.recorder.resume();
      document.getElementById('record-pause-resume').innerHTML = 'Pause';
      document.getElementById('post-video').getElementsByTagName('video')[0].play();
    } else {
      this.recorder.pause();
      document.getElementById('record-pause-resume').innerHTML = 'Resume';
      document.getElementById('post-video').getElementsByTagName('video')[0].pause();
    }
    this.paused = !this.paused;
  }

  recordRadioChange(event) {
    this.cleanRecording();
    this.recordType = event.target.value;
    this.recordVideo(this.getPublisherOptions(this.recordType));
  }

  cleanRecording() {
    if (this.recorder) this.recorder.clean();
    delete this.publisher;
    this.recordRadioEnabled = true;
    this.publisherErrorMessage = '';
    this.recordType = 'video';
    this.removeRecordingControls();
    this.removePostRecordingControls();
    let el = document.getElementById('post-video');
    if (el) {
      el = el.getElementsByTagName('video')[0];
      if (el) {
        el.outerHTML = '';
      }
    }
    this.recording = false;
    this.paused = false;
  }

  repeatRecording(type: string) {
    this.cleanRecording();
    this.recordType = type;
    this.recordVideo(this.getPublisherOptions(type));
  }

  getPublisherOptions(option: string): any {
    let options = {};
    switch (option) {
      case 'video':
        options = {
          audio: true,
          video: true,
          quality: 'MEDIUM'
        }
        break;
      case 'audio':
        options = {
          audio: true,
          video: false
        }
        break;
      case 'screen':
        options = {
          audio: true,
          video: true,
          quality: 'MEDIUM',
          screen: true
        }
        break;
    }
    return options;
  }

  private addRecordingControls(videoElement: HTMLVideoElement): void {
    const dataNode = document.createElement('div');
    dataNode.id = 'recording-controls';
    dataNode.innerHTML =
      '<a id="record-start-stop" class="btn waves-effect button-small button-small-margin" title="Start/End recording">Start</a>' +
      '<a id="record-pause-resume" class="btn waves-effect button-small button-small-margin" title="Pause/Resume recording" style="display: none">Pause</a>' +
      '<a id="record-cancel" class="btn waves-effect button-small button-small-margin" title="Cancel recording">Cancel</a>';
    videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
    document.getElementById('record-start-stop').addEventListener('click', this.startStopRecording.bind(this));
    document.getElementById('record-pause-resume').addEventListener('click', this.pauseResumeRecording.bind(this));
    document.getElementById('record-cancel').addEventListener('click', this.cleanRecording.bind(this));
  }

  private removeRecordingControls() {
    let el = document.getElementById('recording-controls');
    if (el) {
      el.outerHTML = '';
    }
  }

  private addPostRecordingControls(videoElement: HTMLVideoElement): void {
    const dataNode = document.createElement('div');
    dataNode.id = 'recording-post-controls';
    dataNode.innerHTML =
      '<a id="record-post-repeat" class="btn waves-effect button-small button-small-margin" title="Repeat recording">Repeat</a>' +
      '<button id="record-post-send" class="btn waves-effect button-small button-small-margin" title="Send entry" type="submit">Send</button>';
    videoElement.parentNode.insertBefore(dataNode, videoElement.nextSibling);
    let recordTypeAux = this.recordType;
    document.getElementById('record-post-repeat').addEventListener('click', this.repeatRecording.bind(this, recordTypeAux));
  }

  private removePostRecordingControls() {
    let el = document.getElementById('recording-post-controls');
    if (el) {
      el.outerHTML = '';
    }
  }

  private replaceComment(comments, newComment): boolean {
    let rep = false;
    for (let i = 0; i < comments.length; i++) {
      if (comments[i].id === newComment.id) {
        comments[i] = newComment;
        return true;
      }
      if (comments[i].replies.length > 0) {
        let replaced = this.replaceComment(comments[i].replies, newComment);
        if (replaced) {
          rep = true;
          break;
        }
      }
    }
    return rep;
  }

}
