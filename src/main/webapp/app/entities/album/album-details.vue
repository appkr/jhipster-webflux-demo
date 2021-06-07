<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="album">
        <h2 class="jh-entity-heading" data-cy="albumDetailsHeading"><span>Album</span> {{ album.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ album.title }}</span>
          </dd>
          <dt>
            <span>Published At</span>
          </dt>
          <dd>
            <span>{{ album.publishedAt | formatDate }}</span>
          </dd>
          <dt>
            <span>Singer</span>
          </dt>
          <dd>
            <div v-if="album.singer">
              <router-link :to="{ name: 'SingerView', params: { singerId: album.singer.id } }">{{ album.singer.id }}</router-link>
            </div>
          </dd>
          <dt>
            <span>Songs</span>
          </dt>
          <dd>
            <div v-if="album.songs">
              <router-link :to="{ name: 'SongView', params: { songId: album.songs.id } }">{{ album.songs.id }}</router-link>
            </div>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link v-if="album.id" :to="{ name: 'AlbumEdit', params: { albumId: album.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./album-details.component.ts"></script>
