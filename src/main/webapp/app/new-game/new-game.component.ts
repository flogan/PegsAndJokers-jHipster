import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GamesService } from '../games-service.service';

@Component({
  selector: 'jhi-new-game',
  templateUrl: './new-game.component.html',
  styleUrls: ['./new-game.component.scss']
})
export class NewGameComponent implements OnInit {
  game: any;

  constructor(private route: ActivatedRoute, private router: Router, private gamesService: GamesService) {}

  ngOnInit() {
    this.gamesService.postNewGame().subscribe(data => {
      this.game = data;
    });
  }
}
