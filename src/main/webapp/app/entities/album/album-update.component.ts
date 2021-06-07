import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import SingerService from '@/entities/singer/singer.service';
import { ISinger } from '@/shared/model/singer.model';

import SongService from '@/entities/song/song.service';
import { ISong } from '@/shared/model/song.model';

import { IAlbum, Album } from '@/shared/model/album.model';
import AlbumService from './album.service';

const validations: any = {
  album: {
    title: {
      required,
    },
    publishedAt: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class AlbumUpdate extends Vue {
  @Inject('albumService') private albumService: () => AlbumService;
  public album: IAlbum = new Album();

  @Inject('singerService') private singerService: () => SingerService;

  public singers: ISinger[] = [];

  @Inject('songService') private songService: () => SongService;

  public songs: ISong[] = [];
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.albumId) {
        vm.retrieveAlbum(to.params.albumId);
      }
      vm.initRelationships();
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    if (this.album.id) {
      this.albumService()
        .update(this.album)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Album is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.albumService()
        .create(this.album)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Album is created with identifier ' + param.id;
          this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Success',
            variant: 'success',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    }
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.album[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.album[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.album[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.album[field] = null;
    }
  }

  public retrieveAlbum(albumId): void {
    this.albumService()
      .find(albumId)
      .then(res => {
        res.publishedAt = new Date(res.publishedAt);
        this.album = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {
    this.singerService()
      .retrieve()
      .then(res => {
        this.singers = res.data;
      });
    this.songService()
      .retrieve()
      .then(res => {
        this.songs = res.data;
      });
  }
}
