<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="song">
        <h2 class="jh-entity-heading" data-cy="songDetailsHeading"><span>Song</span> {{ song.id }}</h2>
        <dl class="row jh-entity-details">
          <dt>
            <span>Title</span>
          </dt>
          <dd>
            <span>{{ song.title }}</span>
          </dd>
          <dt>
            <span>Play Time</span>
          </dt>
          <dd>
            <span>{{ song.playTime }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span> Back</span>
        </button>
        <router-link v-if="song.id" :to="{ name: 'SongEdit', params: { songId: song.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./song-details.component.ts"></script>
