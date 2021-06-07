export interface ISong {
  id?: number;
  title?: string;
  playTime?: string;
}

export class Song implements ISong {
  constructor(public id?: number, public title?: string, public playTime?: string) {}
}
