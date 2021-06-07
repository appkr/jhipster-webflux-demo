import { Component, Vue, Inject } from 'vue-property-decorator';

import { required } from 'vuelidate/lib/validators';

import { ISinger, Singer } from '@/shared/model/singer.model';
import SingerService from './singer.service';

const validations: any = {
  singer: {
    name: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class SingerUpdate extends Vue {
  @Inject('singerService') private singerService: () => SingerService;
  public singer: ISinger = new Singer();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.singerId) {
        vm.retrieveSinger(to.params.singerId);
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
    if (this.singer.id) {
      this.singerService()
        .update(this.singer)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Singer is updated with identifier ' + param.id;
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.singerService()
        .create(this.singer)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = 'A Singer is created with identifier ' + param.id;
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

  public retrieveSinger(singerId): void {
    this.singerService()
      .find(singerId)
      .then(res => {
        this.singer = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
