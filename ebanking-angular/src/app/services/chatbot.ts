
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatbotService {

  private http = inject(HttpClient);
  private apiUrl = '/api/chat';

  sendMessage(message: string): Observable<string> {
    return this.http.get(this.apiUrl, {
      params: { message },
      responseType: 'text'
    });
  }
}
