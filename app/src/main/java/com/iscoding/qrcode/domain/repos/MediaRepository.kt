package com.iscoding.qrcode.domain.repos

import com.iscoding.qrcode.domain.model.AlbumInfo
import com.iscoding.qrcode.domain.model.SharedStoragePhoto

interface MediaRepository {
    suspend fun loadPhotos(): List<SharedStoragePhoto>
    suspend fun loadAlbums(): List<String>
    suspend fun loadForSelectedAlbum(album: String): List<SharedStoragePhoto>

    suspend fun loadAlbumsWithCount(): List<AlbumInfo> // Optional: if you want photo counts
}
