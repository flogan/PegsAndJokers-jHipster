import { Component, ViewChild, ElementRef, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PlayerService } from '../player-service.service';
import { Subscription } from 'rxjs';
import { BoardSide } from './board-side';

@Component({
  selector: 'jhi-player-view',
  templateUrl: './player-view.component.html',
  styleUrls: ['./player-view.component.scss']
})
export class PlayerViewComponent implements OnInit {
  temp: any;
  playerView: any;
  cards: Array<any>;
  cardPaths: Array<any>;
  sub: Subscription;
  sides: BoardSide[] = [];

  @ViewChild('canvas', { static: true })
  canvas: ElementRef<HTMLCanvasElement>;
  private ctx: CanvasRenderingContext2D;

  constructor(private route: ActivatedRoute, private router: Router, private playerService: PlayerService) {}

  ngOnInit(): void {
    this.sub = this.route.params.subscribe(params => {
      const gameId = params['gameId'];
      const playerNum = params['playerNum'];
      if (gameId && playerNum) {
        this.playerService.getPlayerView(gameId, playerNum).subscribe(data => {
          this.temp = JSON.stringify(data);
          this.playerView = data;
          this.cards = data.playerHand.cards;
          this.initBoard(data);
        });
      }
    });
  }

  initBoard(data) {
    this.ctx = this.canvas.nativeElement.getContext('2d');
    this.ctx.fillStyle = 'papayawhip';
    this.ctx.fillRect(0, 0, this.canvas.nativeElement.width, this.canvas.nativeElement.height);
    const numPlayers = this.playerView.board.playerSides.length;
    const rotationStep = (-2 * Math.PI) / numPlayers;
    const boardLength = 720; // TODO: scale board size and pegs with players appropriately. Possibly Add responsivity to canvas, and grab which center
    const radius = boardLength / (2 * Math.tan(Math.PI / numPlayers));
    for (let player = 0; player < numPlayers; player++) {
      let x = 500 + this.displacedX(radius, rotationStep * player);
      let y = 500 + this.displacedY(radius, rotationStep * player);
      console.log('x: ' + x);
      console.log('y: ' + y);
      this.ctx.lineWidth = 10;
      this.sides[player] = new BoardSide(this.playerView.board.playerSides[player], this.ctx, [x, y], rotationStep * player, boardLength);
    }
  }

  private displacedX(distance: number, angle: number): number {
    return distance * Math.sin(angle); // negative angles
  }

  private displacedY(distance: number, angle: number): number {
    return distance * Math.cos(angle); // negative angles
  }

  log(): void {
    console.log(this.playerView);
  }
}
