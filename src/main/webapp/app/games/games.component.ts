import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { GamesService } from '../games-service.service';

@Component({
  selector: 'jhi-games',
  templateUrl: './games.component.html',
  styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {
  games: Array<any>;

  constructor(private route: ActivatedRoute, private router: Router, private gamesService: GamesService) {}

  ngOnInit() {
    this.gamesService.getAllGames().subscribe(data => {
      this.games = data;
    });
  }
}
