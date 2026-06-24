import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';

import { routing } from './app.routing';

import { MaterializeModule } from './shared/materialize.module';
import { NgxCaptchaModule } from 'ngx-captcha';

import { InterventionAskedPipe } from './pipes/intervention-asked.pipe';
import { TimeAgoPipe } from './pipes/time-ago.pipe';

import { LoginModalComponent } from './components/login-modal/login-modal.component';
import { PresentationComponent } from './components/presentation/presentation.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { CourseDetailsComponent } from './components/course-details/course-details.component';
import { SettingsComponent } from './components/settings/settings.component';
import { ErrorMessageComponent } from './components/error-message/error-message.component';
import { CommentComponent } from './components/comment/comment.component';
import { FileGroupComponent } from './components/file-group/file-group.component';
import { VideoSessionComponent } from './components/video-session/video-session.component';
import { FileUploaderComponent } from './components/file-uploader/file-uploader.component';
import { StreamComponent } from './components/video-session/stream.component';
import { ChatLineComponent } from './components/chat-line/chat-line.component';

import { AuthenticationService } from './services/authentication.service';
import { CourseService } from './services/course.service';
import { SessionService } from './services/session.service';
import { ForumService } from './services/forum.service';
import { FileService } from './services/file.service';
import { CourseDetailsModalDataService } from './services/course-details-modal-data.service';
import { LoginModalService } from './services/login-modal.service';
import { UploaderModalService } from './services/uploader-modal.service';
import { UserService } from './services/user.service';
import { AnimationService } from './services/animation.service';
import { VideoSessionService } from './services/video-session.service';

import { CalendarModule, DateAdapter } from 'angular-calendar';
import { adapterFactory } from 'angular-calendar/date-adapters/date-fns';
import { CalendarComponent } from './components/calendar/calendar.component';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { EditorModule } from 'primeng/editor';

@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MaterializeModule,
    routing,
    CalendarModule.forRoot({ provide: DateAdapter, useFactory: adapterFactory }),
    DragDropModule,
    EditorModule,
    NgxCaptchaModule,
  ],
  declarations: [
    PresentationComponent,
    DashboardComponent,
    CourseDetailsComponent,
    LoginModalComponent,
    SettingsComponent,
    ErrorMessageComponent,
    CommentComponent,
    FileGroupComponent,
    CalendarComponent,
    TimeAgoPipe,
    VideoSessionComponent,
    FileUploaderComponent,
    StreamComponent,
    ChatLineComponent,
    InterventionAskedPipe
  ],
  providers: [
    AuthenticationService,
    CourseService,
    SessionService,
    ForumService,
    FileService,
    CourseDetailsModalDataService,
    LoginModalService,
    UploaderModalService,
    UserService,
    AnimationService,
    VideoSessionService,
  ],
  bootstrap: []
})
export class AppModule { }
