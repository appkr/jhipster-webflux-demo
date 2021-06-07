import { ISinger } from '@/shared/model/singer.model';
import { ISong } from '@/shared/model/song.model';

export interface IAlbum {
  id?: number;
  title?: string;
  publishedAt?: Date;
  singer?: ISinger | null;
  songs?: ISong | null;
}

export class Album implements IAlbum {
  constructor(
    public id?: number,
    public title?: string,
    public publishedAt?: Date,
    public singer?: ISinger | null,
    public songs?: ISong | null
  ) {}
}
