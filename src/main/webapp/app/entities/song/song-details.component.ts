import { Component, Vue, Inject } from 'vue-property-decorator';

import { ISong } from '@/shared/model/song.model';
import SongService from './song.service';

@Component
export default class SongDetails extends Vue {
  @Inject('songService') private songService: () => SongService;
  public song: ISong = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.songId) {
        vm.retrieveSong(to.params.songId);
      }
    });
  }

  public retrieveSong(songId) {
    this.songService()
      .find(songId)
      .then(res => {
        this.song = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
