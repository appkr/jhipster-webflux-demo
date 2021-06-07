<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="appApp.song.home.createOrEditLabel" data-cy="SongCreateUpdateHeading">Create or edit a Song</h2>
        <div>
          <div class="form-group" v-if="song.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="song.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="song-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="song-title"
              data-cy="title"
              :class="{ valid: !$v.song.title.$invalid, invalid: $v.song.title.$invalid }"
              v-model="$v.song.title.$model"
              required
            />
            <div v-if="$v.song.title.$anyDirty && $v.song.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.song.title.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="song-playTime">Play Time</label>
            <input
              type="text"
              class="form-control"
              name="playTime"
              id="song-playTime"
              data-cy="playTime"
              :class="{ valid: !$v.song.playTime.$invalid, invalid: $v.song.playTime.$invalid }"
              v-model="$v.song.playTime.$model"
              required
            />
            <div v-if="$v.song.playTime.$anyDirty && $v.song.playTime.$invalid">
              <small class="form-text text-danger" v-if="!$v.song.playTime.required"> This field is required. </small>
            </div>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span>Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.song.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./song-update.component.ts"></script>
