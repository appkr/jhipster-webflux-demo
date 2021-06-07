import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Song = () => import('@/entities/song/song.vue');
// prettier-ignore
const SongUpdate = () => import('@/entities/song/song-update.vue');
// prettier-ignore
const SongDetails = () => import('@/entities/song/song-details.vue');
// prettier-ignore
const Singer = () => import('@/entities/singer/singer.vue');
// prettier-ignore
const SingerUpdate = () => import('@/entities/singer/singer-update.vue');
// prettier-ignore
const SingerDetails = () => import('@/entities/singer/singer-details.vue');
// prettier-ignore
const Album = () => import('@/entities/album/album.vue');
// prettier-ignore
const AlbumUpdate = () => import('@/entities/album/album-update.vue');
// prettier-ignore
const AlbumDetails = () => import('@/entities/album/album-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/song',
    name: 'Song',
    component: Song,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/song/new',
    name: 'SongCreate',
    component: SongUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/song/:songId/edit',
    name: 'SongEdit',
    component: SongUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/song/:songId/view',
    name: 'SongView',
    component: SongDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/singer',
    name: 'Singer',
    component: Singer,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/singer/new',
    name: 'SingerCreate',
    component: SingerUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/singer/:singerId/edit',
    name: 'SingerEdit',
    component: SingerUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/singer/:singerId/view',
    name: 'SingerView',
    component: SingerDetails,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/album',
    name: 'Album',
    component: Album,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/album/new',
    name: 'AlbumCreate',
    component: AlbumUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/album/:albumId/edit',
    name: 'AlbumEdit',
    component: AlbumUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/album/:albumId/view',
    name: 'AlbumView',
    component: AlbumDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
