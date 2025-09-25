package com.iscoding.qrcode.domain.repos

import com.iscoding.qrcode.domain.model.AlbumInfo
import com.iscoding.qrcode.domain.model.SharedStoragePhoto

// interface MediaRepository {
//    suspend fun loadPhotos(): List<SharedStoragePhoto>
//    suspend fun loadAlbums(): List<String>
//    suspend fun loadForSelectedAlbum(album: String): List<SharedStoragePhoto>
//
//    suspend fun loadAlbumsWithCount(): List<AlbumInfo> // Optional: if you want photo counts
// }
// for pagination
interface MediaRepository {
    suspend fun loadPhotos(limit: Int = 50, offset: Int = 0): List<SharedStoragePhoto>
    suspend fun loadPhotosForAlbum(
        album: String,
        limit: Int = 50,
        offset: Int = 0,
    ): List<SharedStoragePhoto>

    suspend fun loadAlbums(): List<String>
    suspend fun loadAlbumsWithCount(): List<AlbumInfo>
    suspend fun getTotalPhotosCount(): Int
    suspend fun getTotalPhotosCountForAlbum(album: String): Int

    suspend fun loadForSelectedAlbum(
        album: String,
        limit: Int,
        offset: Int,
    ): List<SharedStoragePhoto>
}
