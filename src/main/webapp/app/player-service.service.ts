import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  public API = '//localhost:8080';

  constructor(private http: HttpClient) {}

  getPlayerView(gameId, playerNum): Observable<any> {
    return this.http.get(this.API + `/game/${gameId}/playerView/${playerNum}`);
  }

  getPlayerHand(gameId, playerNum): Observable<any> {
    return this.http.get(this.API + `/game/${gameId}/playerhand/${playerNum}`);
  }
}
