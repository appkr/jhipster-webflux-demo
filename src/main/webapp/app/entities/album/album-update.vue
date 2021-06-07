<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="appApp.album.home.createOrEditLabel" data-cy="AlbumCreateUpdateHeading">Create or edit a Album</h2>
        <div>
          <div class="form-group" v-if="album.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="album.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="album-title">Title</label>
            <input
              type="text"
              class="form-control"
              name="title"
              id="album-title"
              data-cy="title"
              :class="{ valid: !$v.album.title.$invalid, invalid: $v.album.title.$invalid }"
              v-model="$v.album.title.$model"
              required
            />
            <div v-if="$v.album.title.$anyDirty && $v.album.title.$invalid">
              <small class="form-text text-danger" v-if="!$v.album.title.required"> This field is required. </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="album-publishedAt">Published At</label>
            <div class="d-flex">
              <input
                id="album-publishedAt"
                data-cy="publishedAt"
                type="datetime-local"
                class="form-control"
                name="publishedAt"
                :class="{ valid: !$v.album.publishedAt.$invalid, invalid: $v.album.publishedAt.$invalid }"
                required
                :value="convertDateTimeFromServer($v.album.publishedAt.$model)"
                @change="updateInstantField('publishedAt', $event)"
              />
            </div>
            <div v-if="$v.album.publishedAt.$anyDirty && $v.album.publishedAt.$invalid">
              <small class="form-text text-danger" v-if="!$v.album.publishedAt.required"> This field is required. </small>
              <small class="form-text text-danger" v-if="!$v.album.publishedAt.ZonedDateTimelocal">
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="album-singer">Singer</label>
            <select class="form-control" id="album-singer" data-cy="singer" name="singer" v-model="album.singer">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="album.singer && singerOption.id === album.singer.id ? album.singer : singerOption"
                v-for="singerOption in singers"
                :key="singerOption.id"
              >
                {{ singerOption.id }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" for="album-songs">Songs</label>
            <select class="form-control" id="album-songs" data-cy="songs" name="songs" v-model="album.songs">
              <option v-bind:value="null"></option>
              <option
                v-bind:value="album.songs && songOption.id === album.songs.id ? album.songs : songOption"
                v-for="songOption in songs"
                :key="songOption.id"
              >
                {{ songOption.id }}
              </option>
            </select>
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
            :disabled="$v.album.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./album-update.component.ts"></script>
