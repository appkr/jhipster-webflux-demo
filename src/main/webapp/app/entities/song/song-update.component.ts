import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import { ISong, Song } from '@/shared/model/song.model';
import SongService from './song.service';

const validations: any = {
  song: {
    title: {
      required,
    },
    playTime: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class SongUpdate extends Vue {
  @Inject('songService') private songService: () => SongService;
  public song: ISong = new Song();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.songId) {
        vm.retrieveSong(to.params.songId);
      }
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
    if (this.song.id) {
      this.songService()
        .update(this.song)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Song is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.songService()
        .create(this.song)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Song is created with identifier ' + param.id;
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

  public retrieveSong(songId): void {
    this.songService()
      .find(songId)
      .then(res => {
        this.song = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
