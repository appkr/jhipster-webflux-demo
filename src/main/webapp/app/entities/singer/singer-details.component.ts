import { Component, Vue, Inject } from 'vue-property-decorator';

import { ISinger } from '@/shared/model/singer.model';
import SingerService from './singer.service';

@Component
export default class SingerDetails extends Vue {
  @Inject('singerService') private singerService: () => SingerService;
  public singer: ISinger = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.singerId) {
        vm.retrieveSinger(to.params.singerId);
      }
    });
  }

  public retrieveSinger(singerId) {
    this.singerService()
      .find(singerId)
      .then(res => {
        this.singer = res;
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
