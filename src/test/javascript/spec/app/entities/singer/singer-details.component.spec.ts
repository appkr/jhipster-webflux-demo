/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import SingerDetailComponent from '@/entities/singer/singer-details.vue';
import SingerClass from '@/entities/singer/singer-details.component';
import SingerService from '@/entities/singer/singer.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Singer Management Detail Component', () => {
    let wrapper: Wrapper<SingerClass>;
    let comp: SingerClass;
    let singerServiceStub: SinonStubbedInstance<SingerService>;

    beforeEach(() => {
      singerServiceStub = sinon.createStubInstance<SingerService>(SingerService);

      wrapper = shallowMount<SingerClass>(SingerDetailComponent, {
        store,
        localVue,
        router,
        provide: { singerService: () => singerServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundSinger = { id: 123 };
        singerServiceStub.find.resolves(foundSinger);

        // WHEN
        comp.retrieveSinger(123);
        await comp.$nextTick();

        // THEN
        expect(comp.singer).toBe(foundSinger);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSinger = { id: 123 };
        singerServiceStub.find.resolves(foundSinger);

        // WHEN
        comp.beforeRouteEnter({ params: { singerId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.singer).toBe(foundSinger);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
