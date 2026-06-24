import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'timeAgo',
    standalone: false
})
export class TimeAgoPipe implements PipeTransform {
  transform(value: Date | string | number): string {
    const date = new Date(value);
    const seconds = Math.floor((Date.now() - date.getTime()) / 1000);
    if (seconds < 60) { return 'a few seconds ago'; }
    const intervals: [number, string][] = [
      [31536000, 'year'], [2592000, 'month'], [604800, 'week'],
      [86400, 'day'],     [3600,    'hour'],  [60,     'minute'],
    ];
    for (const [n, label] of intervals) {
      const count = Math.floor(seconds / n);
      if (count >= 1) { return `${count} ${label}${count > 1 ? 's' : ''} ago`; }
    }
    return date.toLocaleDateString();
  }
}
