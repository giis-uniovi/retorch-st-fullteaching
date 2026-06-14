import { Component, Input } from '@angular/core';

import { Comment }    from '../../classes/comment';
import { Entry }      from '../../classes/entry';
import { FileGroup }  from '../../classes/file-group';

import { CourseDetailsModalDataService } from '../../services/course-details-modal-data.service';
import { AnimationService }      from '../../services/animation.service';

@Component({
    selector: 'app-comment',
    templateUrl: './comment.component.html',
    styleUrls: ['./comment.component.css'],
    standalone: false
})
export class CommentComponent {

  @Input()
  public comment: Comment;

  constructor(private readonly courseDetailsModalDataService: CourseDetailsModalDataService, readonly animationService: AnimationService) {}

  updatePostModalMode(mode: number, title: string, header: Entry, commentReplay: Comment, fileGroup: FileGroup){
    let objs = [mode, title, header, commentReplay, fileGroup];
    this.courseDetailsModalDataService.announcePostMode(objs);
  }

  isCommentTeacher(comment: Comment){
    return (comment.user.roles.indexOf('ROLE_TEACHER') > -1);
  }

  onHovering(event) {
    $(event.target).attr("controls", "");
  }

  onUnhovering(event) {
    $(event.target).removeAttr("controls");
  }

}
