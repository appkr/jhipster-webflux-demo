/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import AlbumDetailComponent from '@/entities/album/album-details.vue';
import AlbumClass from '@/entities/album/album-details.component';
import AlbumService from '@/entities/album/album.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Album Management Detail Component', () => {
    let wrapper: Wrapper<AlbumClass>;
    let comp: AlbumClass;
    let albumServiceStub: SinonStubbedInstance<AlbumService>;

    beforeEach(() => {
      albumServiceStub = sinon.createStubInstance<AlbumService>(AlbumService);

      wrapper = shallowMount<AlbumClass>(AlbumDetailComponent, {
        store,
        localVue,
        router,
        provide: { albumService: () => albumServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundAlbum = { id: 123 };
        albumServiceStub.find.resolves(foundAlbum);

        // WHEN
        comp.retrieveAlbum(123);
        await comp.$nextTick();

        // THEN
        expect(comp.album).toBe(foundAlbum);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundAlbum = { id: 123 };
        albumServiceStub.find.resolves(foundAlbum);

        // WHEN
        comp.beforeRouteEnter({ params: { albumId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.album).toBe(foundAlbum);
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
