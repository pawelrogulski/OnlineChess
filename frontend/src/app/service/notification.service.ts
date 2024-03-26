import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DataService } from './data.service';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private http: HttpClient, private dataService: DataService) { }
  getNotifications(): Observable<string> {
    return new Observable<string>(observer => {
      const playerId = this.dataService.getPlayerId();
      const eventSource = new EventSource(`http://localhost:8080/api/notification/notifications/${playerId}`);
      eventSource.onmessage = event => {
        observer.next(event.data);
      };
      eventSource.onerror = error => {
        observer.error('SSE error');
      };
      return () => {
        eventSource.close();
      };
    });
  }
}
