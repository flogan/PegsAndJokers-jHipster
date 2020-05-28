import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GamesService {
  public API = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  postNewGame(): Observable<any> {
    return this.http.post(this.API + '/games', {});
  }

  getAllGames(): Observable<any> {
    return this.http.get(this.API + '/games');
  }

  getGame(Id): Observable<any> {
    return this.http.get(this.API + `/game/${Id}`);
  }

  getGameBoard(Id): Observable<any> {
    return this.http.get(this.API + `/game/${Id}/board`);
  }

  postGameTurn(Id): Observable<any> {
    return this.http.post(this.API + `/game/${Id}/turns`, {});
  }
}
