<template>
  <div>
    <h2 id="page-heading" data-cy="AlbumHeading">
      <span id="album-heading">Albums</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon> <span>Refresh List</span>
        </button>
        <router-link :to="{ name: 'AlbumCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-album"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span> Create a new Album </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && albums && albums.length === 0">
      <span>No albums found</span>
    </div>
    <div class="table-responsive" v-if="albums && albums.length > 0">
      <table class="table table-striped" aria-describedby="albums">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span>ID</span> <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('title')">
              <span>Title</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'title'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('publishedAt')">
              <span>Published At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'publishedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('singer.id')">
              <span>Singer</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'singer.id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('songs.id')">
              <span>Songs</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'songs.id'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="album in albums" :key="album.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'AlbumView', params: { albumId: album.id } }">{{ album.id }}</router-link>
            </td>
            <td>{{ album.title }}</td>
            <td>{{ album.publishedAt | formatDate }}</td>
            <td>
              <div v-if="album.singer">
                <router-link :to="{ name: 'SingerView', params: { singerId: album.singer.id } }">{{ album.singer.id }}</router-link>
              </div>
            </td>
            <td>
              <div v-if="album.songs">
                <router-link :to="{ name: 'SongView', params: { songId: album.songs.id } }">{{ album.songs.id }}</router-link>
              </div>
            </td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'AlbumView', params: { albumId: album.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'AlbumEdit', params: { albumId: album.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(album)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline">Delete</span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span id="appApp.album.delete.question" data-cy="albumDeleteDialogHeading">Confirm delete operation</span></span
      >
      <div class="modal-body">
        <p id="jhi-delete-album-heading">Are you sure you want to delete this Album?</p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-album"
          data-cy="entityConfirmDeleteButton"
          v-on:click="removeAlbum()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="albums && albums.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./album.component.ts"></script>
