package com.iscoding.qrcode.domain.repos

import arrow.core.Either
import com.iscoding.qrcode.domain.errors.MediaError
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
/**
 * Repository interface for accessing media (photos) from the device's shared storage.
 *
 * Provides functions to:
 * - Load photos with optional pagination.
 * - Load photos filtered by album.
 * - Retrieve album lists with or without photo counts.
 * - Get total photo counts globally or per album.
 *
 * This interface is part of the domain layer and abstracts away the
 * implementation details (MediaStore queries, content URIs, etc.).
 */
interface MediaRepository {

    /**
     * Load a paginated list of photos from the device.
     *
     * @param limit Maximum number of photos to load. Default is 50.
     * @param offset Number of photos to skip. Default is 0.
     * @return A list of [SharedStoragePhoto] objects.
     */
    suspend fun loadPhotos(
        limit: Int = 50,
        offset: Int = 0,
    ): Either<MediaError, List<SharedStoragePhoto>>

    /**
     * Load a paginated list of photos from a specific album.
     *
     * @param album The name of the album.
     * @param limit Maximum number of photos to load. Default is 50.
     * @param offset Number of photos to skip. Default is 0.
     * @return A list of [SharedStoragePhoto] objects.
     */
    suspend fun loadPhotosForAlbum(
        album: String,
        limit: Int = 50,
        offset: Int = 0,
    ): Either<MediaError, List<SharedStoragePhoto>>

    /**
     * Load a list of all album names on the device.
     *
     * @return List of album names as [String].
     */
    suspend fun loadAlbums(): Either<MediaError, List<String>>

    /**
     * Load albums along with the number of photos they contain.
     *
     * @return A list of [AlbumInfo], each containing the album name and photo count.
     */
    suspend fun loadAlbumsWithCount(): Either<MediaError, List<AlbumInfo>>

    /**
     * Get the total number of photos available in shared storage.
     *
     * @return Total count of photos.
     */
    suspend fun getTotalPhotosCount(): Either<MediaError, Int>

    /**
     * Get the total number of photos in a specific album.
     *
     * @param album The name of the album.
     * @return Total count of photos in the specified album.
     */
    suspend fun getTotalPhotosCountForAlbum(album: String): Either<MediaError, Int>

    /**
     * Load photos for a selected album with pagination.
     *
     * @param album The name of the album.
     * @param limit Maximum number of photos to load.
     * @param offset Number of photos to skip.
     * @return A list of [SharedStoragePhoto] objects.
     */
    suspend fun loadForSelectedAlbum(
        album: String,
        limit: Int,
        offset: Int,
    ): Either<MediaError, List<SharedStoragePhoto>>
}
