import { PegHole } from './peg-hole';

export class BoardSide {
  private ctx: CanvasRenderingContext2D;
  color: string;
  pegHoles = new Map();
  center = {
    x: 0,
    y: 0
  };
  angle: number;
  startCoordinates = {
    x: 0,
    y: 0
  };
  endCoordinates = {
    x: 0,
    y: 0
  };
  boardLength = 0; // 720 for square
  // Relative directions based on unit circle
  private UP = Math.PI / 2;
  private DOWN = (3 * Math.PI) / 2; // not really used
  private LEFT = Math.PI;
  private RIGHT = 0; // not really used

  constructor(private side: any, private contx: CanvasRenderingContext2D, center: any, angle: number, boardLength: number) {
    console.log('Creating BoardSide --- ');
    this.color = side.color;
    this.boardLength = boardLength;
    this.center.x = center[0];
    this.center.y = center[1];
    this.angle = angle;
    this.startCoordinates.x = this.center.x + this.displacedX(this.boardLength / 2, this.angle);
    this.startCoordinates.y = this.center.y - this.displacedY(this.boardLength / 2, this.angle);
    this.endCoordinates.x = this.center.x + this.displacedX(this.boardLength / 2, this.angle + this.LEFT);
    this.endCoordinates.y = this.center.y - this.displacedY(this.boardLength / 2, this.angle + this.LEFT);
    this.ctx = contx;
    this.initializePegHoles(side);
    this.draw(side, center);
    return this;
  }

  private initializePegHoles(side: any): void {
    // initialize main track holes
    const distanceBetween = this.boardLength / 17;
    let currentx = this.startCoordinates.x;
    let currenty = this.startCoordinates.y;
    side.mainTrackPositions.forEach(trackPosition => {
      this.pegHoles.set(trackPosition.id, new PegHole(currentx, currenty, trackPosition.pegColor, trackPosition.hasPeg));
      currentx = currentx + this.displacedX(distanceBetween, this.angle + this.LEFT);
      currenty = currenty - this.displacedY(distanceBetween, this.angle + this.LEFT);
    });
    // initialize start holes
    currentx = this.pegHoles.get(this.color + '-8').x + this.displacedX(distanceBetween, this.angle + this.UP);
    currenty = this.pegHoles.get(this.color + '-8').y - this.displacedY(distanceBetween, this.angle + this.UP);
    let counter = 0;
    side.startPositions.forEach(startPosition => {
      if (counter === 3) {
        this.pegHoles.set(
          startPosition.id,
          new PegHole(
            this.pegHoles.get(this.color + 'Start-2').x + this.displacedX(distanceBetween, this.angle + this.LEFT),
            this.pegHoles.get(this.color + 'Start-2').y - this.displacedY(distanceBetween, this.angle + this.LEFT),
            startPosition.pegColor,
            startPosition.hasPeg
          )
        );
      } else if (counter === 4) {
        this.pegHoles.set(
          startPosition.id,
          new PegHole(
            this.pegHoles.get(this.color + 'Start-2').x + this.displacedX(distanceBetween, this.angle),
            this.pegHoles.get(this.color + 'Start-2').y - this.displacedY(distanceBetween, this.angle),
            startPosition.pegColor,
            startPosition.hasPeg
          )
        );
      } else {
        this.pegHoles.set(startPosition.id, new PegHole(currentx, currenty, startPosition.pegColor, startPosition.hasPeg));
        currentx = currentx + this.displacedX(distanceBetween, this.angle + this.UP);
        currenty = currenty - this.displacedY(distanceBetween, this.angle + this.UP);
      }
      counter = counter + 1;
    });
    // initialize home holes
    counter = 0;
    currentx = this.pegHoles.get(this.color + '-3').x;
    currenty = this.pegHoles.get(this.color + '-3').y;
    side.homePositions.forEach(homePosition => {
      if (counter < 2) {
        this.pegHoles.set(
          homePosition.id,
          new PegHole(
            currentx + this.displacedX(distanceBetween, this.angle + this.UP),
            currenty - this.displacedY(distanceBetween, this.angle + this.UP),
            homePosition.pegColor,
            homePosition.hasPeg
          )
        );
        currentx = currentx + this.displacedX(distanceBetween, this.angle + this.UP);
        currenty = currenty - this.displacedY(distanceBetween, this.angle + this.UP);
      } else if (counter >= 2 && counter < 4) {
        this.pegHoles.set(
          homePosition.id,
          new PegHole(
            currentx + this.displacedX(distanceBetween, this.angle + this.LEFT),
            currenty - this.displacedY(distanceBetween, this.angle + this.LEFT),
            homePosition.pegColor,
            homePosition.hasPeg
          )
        );
        currentx = currentx + this.displacedX(distanceBetween, this.angle + this.LEFT);
        currenty = currenty - this.displacedY(distanceBetween, this.angle + this.LEFT);
      } else {
        this.pegHoles.set(
          homePosition.id,
          new PegHole(
            currentx + this.displacedX(distanceBetween, this.angle + this.UP),
            currenty - this.displacedY(distanceBetween, this.angle + this.UP),
            homePosition.pegColor,
            homePosition.hasPeg
          )
        );
      }
      counter = counter + 1;
    });
  }

  public drawBoard(side: any): void {
    this.draw(side, this.center);
  }

  private draw(side: any, center: any): void {
    this.drawTrack();
    this.drawHoles();
  }

  private drawTrack(): void {
    // draw main track
    this.ctx.beginPath();
    this.ctx.moveTo(this.startCoordinates.x, this.startCoordinates.y);
    this.ctx.lineTo(this.endCoordinates.x, this.endCoordinates.y);
    this.ctx.stroke();
    this.ctx.closePath();
    // draw start track
    this.ctx.beginPath();
    this.ctx.lineWidth = 5;
    this.ctx.moveTo(this.pegHoles.get(this.color + '-8').x, this.pegHoles.get(this.color + '-8').y);
    this.ctx.lineTo(this.pegHoles.get(this.color + 'Start-3').x, this.pegHoles.get(this.color + 'Start-3').y);
    this.ctx.moveTo(this.pegHoles.get(this.color + 'Start-4').x, this.pegHoles.get(this.color + 'Start-4').y);
    this.ctx.lineTo(this.pegHoles.get(this.color + 'Start-5').x, this.pegHoles.get(this.color + 'Start-5').y);
    this.ctx.stroke();
    this.ctx.closePath();
    // draw home track
    this.ctx.beginPath();
    this.ctx.moveTo(this.pegHoles.get(this.color + '-3').x, this.pegHoles.get(this.color + '-3').y);
    this.ctx.lineTo(this.pegHoles.get(this.color + 'Home-2').x, this.pegHoles.get(this.color + 'Home-2').y);
    this.ctx.lineTo(this.pegHoles.get(this.color + 'Home-4').x, this.pegHoles.get(this.color + 'Home-4').y);
    this.ctx.lineTo(this.pegHoles.get(this.color + 'Home-5').x, this.pegHoles.get(this.color + 'Home-5').y);
    this.ctx.stroke();
    this.ctx.closePath();
  }

  private drawHoles(): void {
    this.pegHoles.forEach(pegHole => {
      this.ctx.beginPath();
      this.ctx.moveTo(pegHole.x, pegHole.y);
      this.ctx.arc(pegHole.x, pegHole.y, 10, 0, 2 * Math.PI);
      this.ctx.stroke();
      this.ctx.arc(pegHole.x, pegHole.y, 8, 0, 2 * Math.PI);
      if (pegHole.hasPeg) {
        this.ctx.fillStyle = pegHole.pegColor.toLowerCase();
      } else {
        this.ctx.fillStyle = 'white';
      }
      this.ctx.fill();
    });
  }

  private displacedX(distance: number, angle: number): number {
    return distance * Math.cos(angle);
  }

  private displacedY(distance: number, angle: number): number {
    return distance * Math.sin(angle);
  }
}
