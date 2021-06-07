export interface ISinger {
  id?: number;
  name?: string;
}

export class Singer implements ISinger {
  constructor(public id?: number, public name?: string) {}
}
