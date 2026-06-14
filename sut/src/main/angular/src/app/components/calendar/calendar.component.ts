import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from '../../services/authentication.service';
import { Session } from '../../classes/session';
import {
  subDays, addDays, isSameDay, isSameMonth,
  addWeeks, subWeeks, addMonths, subMonths
} from 'date-fns';
import { CalendarEvent, CalendarEventAction } from 'angular-calendar';

const colors: any = {
  red: { primary: '#ad2121', secondary: '#FAE3E3' },
  blue: { primary: '#1e90ff', secondary: '#D1E8FF' },
  yellow: { primary: '#e3bc08', secondary: '#FDF1BA' }
};

class MyCalendarEvent implements CalendarEvent {
  start: Date;
  title: string;
  color = colors.red;
  actions: CalendarEventAction[];
  session: Session;
}

@Component({
    selector: 'calendar-app',
    templateUrl: './calendar.component.html',
    styleUrls: ['./calendar.component.css'],
    standalone: false
})
export class CalendarComponent implements OnInit {

  view = 'month';
  viewDate: Date = new Date();
  events: MyCalendarEvent[] = [];
  activeDayIsOpen = false;
  loadingSessions = true;

  constructor(private readonly authenticationService: AuthenticationService, private readonly router: Router) {}

  ngOnInit() { this.getAllSessions(); }

  increment(): void {
    const addFn: any = { day: addDays, week: addWeeks, month: addMonths }[this.view];
    this.viewDate = addFn(this.viewDate, 1);
    this.activeDayIsOpen = false;
  }

  decrement(): void {
    const subFn: any = { day: subDays, week: subWeeks, month: subMonths }[this.view];
    this.viewDate = subFn(this.viewDate, 1);
    this.activeDayIsOpen = false;
  }

  today(): void {
    this.viewDate = new Date();
    this.activeDayIsOpen = true;
  }

  dayClicked({ date, events }: { date: Date; events: CalendarEvent[] }): void {
    if (isSameMonth(date, this.viewDate)) {
      if ((isSameDay(this.viewDate, date) && this.activeDayIsOpen) || events.length === 0) {
        this.activeDayIsOpen = false;
      } else {
        this.activeDayIsOpen = true;
        this.viewDate = date;
      }
    }
  }

  getAllSessions() {
    const userCourses = this.authenticationService.getCurrentUser().courses;
    for (const c of userCourses) {
      for (const s of c.sessions) {
        s.course = c;
        const d = new Date(s.date);
        const min = d.getMinutes();
        const minutesString = min < 10 ? '0' + min : '' + min;
        this.events.push({
          start: d,
          title: s.title + '  |  ' + d.getHours() + ':' + minutesString,
          color: colors.red,
          actions: [{
            label: '<i class="material-icons calendar-event-icon">forward</i>',
            onClick: ({ event }: { event: CalendarEvent }): void => {
              this.router.navigate(['/courses', s.course.id, 1]);
            }
          }],
          session: s,
        });
      }
    }
    this.loadingSessions = false;
  }
}
