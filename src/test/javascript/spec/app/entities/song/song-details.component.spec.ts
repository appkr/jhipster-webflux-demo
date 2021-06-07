/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import SongDetailComponent from '@/entities/song/song-details.vue';
import SongClass from '@/entities/song/song-details.component';
import SongService from '@/entities/song/song.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Song Management Detail Component', () => {
    let wrapper: Wrapper<SongClass>;
    let comp: SongClass;
    let songServiceStub: SinonStubbedInstance<SongService>;

    beforeEach(() => {
      songServiceStub = sinon.createStubInstance<SongService>(SongService);

      wrapper = shallowMount<SongClass>(SongDetailComponent, { store, localVue, router, provide: { songService: () => songServiceStub } });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundSong = { id: 123 };
        songServiceStub.find.resolves(foundSong);

        // WHEN
        comp.retrieveSong(123);
        await comp.$nextTick();

        // THEN
        expect(comp.song).toBe(foundSong);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSong = { id: 123 };
        songServiceStub.find.resolves(foundSong);

        // WHEN
        comp.beforeRouteEnter({ params: { songId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.song).toBe(foundSong);
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
