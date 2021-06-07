<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2 id="appApp.singer.home.createOrEditLabel" data-cy="SingerCreateUpdateHeading">Create or edit a Singer</h2>
        <div>
          <div class="form-group" v-if="singer.id">
            <label for="id">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="singer.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" for="singer-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="singer-name"
              data-cy="name"
              :class="{ valid: !$v.singer.name.$invalid, invalid: $v.singer.name.$invalid }"
              v-model="$v.singer.name.$model"
              required
            />
            <div v-if="$v.singer.name.$anyDirty && $v.singer.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.singer.name.required"> This field is required. </small>
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
            :disabled="$v.singer.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span>Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./singer-update.component.ts"></script>
