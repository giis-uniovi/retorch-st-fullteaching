import { Component, OnInit, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';

import { MaterializeAction } from '../../shared/materialize.directive';

import { Course } from '../../classes/course';
import { CourseDetails } from '../../classes/course-details';
import { Forum } from '../../classes/forum';


import { CourseService } from '../../services/course.service';
import { AuthenticationService } from '../../services/authentication.service';
import { AnimationService } from '../../services/animation.service';

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css'],
    standalone: false
})
export class DashboardComponent implements OnInit {

  courses: Course[];

  //POST MODAL
  processingPost: boolean = false;
  inputPostCourseName: string;

  //PUT-DELETE MODAL
  processingPut: boolean = false;
  inputPutCourseName: string;
  inputPutCourseImage: string;
  updatedCourse: Course;
  allowCourseDeletion: boolean = false;
  pendingDelete: boolean = false;

  actions1 = new EventEmitter<string | MaterializeAction>();
  actions4 = new EventEmitter<string | MaterializeAction>();

  constructor(
    private readonly courseService: CourseService,
    private readonly authenticationService: AuthenticationService,
    private readonly animationService: AnimationService,
    private readonly router: Router,
  ) { }

  ngOnInit(): void {
    this.authenticationService.checkCredentials()
      .then(() => { this.getCourses(); })
      .catch((e) => { });
  }

  goToCourseDetail(id): void {
    if (document.querySelector('.modal-overlay')) {
      return;
    }
    this.router.navigate(['/courses', id, 0]);
  }

  logout() {
    this.authenticationService.logOut();
  }

  getCourses(): void {
    this.courseService.getCourses(this.authenticationService.getCurrentUser()).subscribe(
      courses => {
        this.authenticationService.getCurrentUser().courses = courses;
        this.courses = courses;
        if (this.courses.length > 0) this.updatedCourse = this.courses[0];
      },
      error => { });
  }


  getImage(c: Course) {
    if (c.image) {
      return c.image;
    }
    else {
      return "/../assets/images/default_session_image.png";
    }
  }

  //POST new Course
  onCourseSubmit() {
    this.processingPost = true;

    let newForum = new Forum(true);
    let newCourseDetails = new CourseDetails(newForum, []);
    let newCourse = new Course(this.inputPostCourseName, this.authenticationService.getCurrentUser().picture, newCourseDetails);
    this.courseService.newCourse(newCourse).subscribe(
      course => {
        this.courses.push(course);

        this.processingPost = false;
        this.actions1.emit({ action: "modal", params: ['close'] });
      },
      error => { this.processingPost = false; }
    )
  }

  //PUT existing Course — also handles deletion when pendingDelete is set
  onPutDeleteCourseSubmit() {
    if (this.pendingDelete) {
      this.pendingDelete = false;
      this.courseService.deleteCourse(this.updatedCourse.id).subscribe(
        response => {
          for (let i = 0; i < this.courses.length; i++) {
            if (this.courses[i].id == response.id) {
              this.courses.splice(i, 1);
              this.updatedCourse = this.courses[0];
              break;
            }
          }
          this.actions4.emit({ action: "modal", params: ['close'] });
        },
        error => { }
      );
    } else {
      this.processingPut = true;
      let c: Course = new Course(this.inputPutCourseName, this.updatedCourse.image, this.updatedCourse.courseDetails);
      c.id = this.updatedCourse.id;
      this.courseService.editCourse(c, "updating course name").subscribe(
        response => {
          for (let i = 0; i < this.courses.length; i++) {
            if (this.courses[i].id == response.id) {
              this.courses[i] = response;
              this.updatedCourse = this.courses[i];
              break;
            }
          }
          this.processingPut = false;
          this.actions4.emit({ action: "modal", params: ['close'] });
        },
        error => { this.processingPut = false; }
      );
    }
  }

  changeUpdatedCourse(course: Course) {
    this.updatedCourse = course;
    this.inputPutCourseName = this.updatedCourse.title;
  }

}
