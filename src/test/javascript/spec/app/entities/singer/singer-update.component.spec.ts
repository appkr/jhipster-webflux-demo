/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import SingerUpdateComponent from '@/entities/singer/singer-update.vue';
import SingerClass from '@/entities/singer/singer-update.component';
import SingerService from '@/entities/singer/singer.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Singer Management Update Component', () => {
    let wrapper: Wrapper<SingerClass>;
    let comp: SingerClass;
    let singerServiceStub: SinonStubbedInstance<SingerService>;

    beforeEach(() => {
      singerServiceStub = sinon.createStubInstance<SingerService>(SingerService);

      wrapper = shallowMount<SingerClass>(SingerUpdateComponent, {
        store,
        localVue,
        router,
        provide: {
          singerService: () => singerServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.singer = entity;
        singerServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(singerServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.singer = entity;
        singerServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(singerServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundSinger = { id: 123 };
        singerServiceStub.find.resolves(foundSinger);
        singerServiceStub.retrieve.resolves([foundSinger]);

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
