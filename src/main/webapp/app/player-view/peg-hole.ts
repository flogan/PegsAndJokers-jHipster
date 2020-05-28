export class PegHole {
  x: number;
  y: number;
  pegColor: string;
  hasPeg: boolean;

  constructor(x: number, y: number, pegColor: string, hasPeg: boolean) {
    this.x = x;
    this.y = y;
    this.pegColor = pegColor;
    this.hasPeg = hasPeg;
  }
}
