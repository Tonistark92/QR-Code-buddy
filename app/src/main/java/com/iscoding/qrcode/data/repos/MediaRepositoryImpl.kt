package com.iscoding.qrcode.data.repos

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.iscoding.qrcode.domain.model.AlbumInfo
import com.iscoding.qrcode.domain.model.SharedStoragePhoto
import com.iscoding.qrcode.domain.repos.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MediaRepositoryImpl(
    private val context: Context,
) : MediaRepository {

    override suspend fun loadPhotos(): List<SharedStoragePhoto> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )

        queryImages(collection, projection, null, null)
    }

    override suspend fun loadAlbums(): List<String> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )

        val albums = mutableSetOf<String>()

        try {
            context.contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC",
            )?.use { cursor ->
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val bucketName = cursor.getString(bucketColumn)
                    if (!bucketName.isNullOrBlank()) {
                        albums.add(bucketName)
                    }
                }
            }
        } catch (e: Exception) {
            // Handle any security exceptions or other errors
            e.printStackTrace()
        }

        albums.sorted()
    }

    override suspend fun loadForSelectedAlbum(album: String): List<SharedStoragePhoto> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
        )

        // Filter by album name
        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(album)

        queryImages(collection, projection, selection, selectionArgs)
    }

    /**
     * Helper function to query images with common logic
     */
    private fun queryImages(
        collection: Uri,
        projection: Array<String>,
        selection: String?,
        selectionArgs: Array<String>?,
    ): List<SharedStoragePhoto> {
        val photos = mutableListOf<SharedStoragePhoto>()

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                "${MediaStore.Images.Media.DATE_ADDED} DESC",
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    try {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn) ?: "Unknown"
                        val width = cursor.getInt(widthColumn)
                        val height = cursor.getInt(heightColumn)
                        val bucketName = cursor.getString(bucketColumn) ?: "Unknown"
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id,
                        )

                        photos.add(
                            SharedStoragePhoto(
                                id = id,
                                displayName = displayName,
                                width = width,
                                height = height,
                                contentUri = contentUri,
                                bucketName = bucketName,
                            ),
                        )
                    } catch (e: Exception) {
                        // Skip this image if there's an error reading its data
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: SecurityException) {
            // Handle permission issues
            throw SecurityException("Storage permission required to access images", e)
        } catch (e: Exception) {
            // Handle other errors
            throw RuntimeException("Failed to load images", e)
        }

        return photos
    }

    /**
     * Alternative implementation for loadAlbums that reuses loadPhotos()
     * (less efficient but more reliable for getting accurate album names)
     */
    suspend fun loadAlbumsAlternative(): List<String> = withContext(Dispatchers.IO) {
        try {
            val photos = loadPhotos()
            photos.map { it.bucketName }
                .filter { it.isNotBlank() && it != "Unknown" }
                .distinct()
                .sorted()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Get album with photo count
     */
    override suspend fun loadAlbumsWithCount(): List<AlbumInfo> = withContext(Dispatchers.IO) {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            "COUNT(*) as count",
        )

        val albumCounts = mutableMapOf<String, Int>()

        try {
            // Group by bucket name to get counts
            context.contentResolver.query(
                collection,
                arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                val bucketColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

                while (cursor.moveToNext()) {
                    val bucketName = cursor.getString(bucketColumn) ?: "Unknown"
                    if (bucketName.isNotBlank()) {
                        albumCounts[bucketName] = albumCounts.getOrDefault(bucketName, 0) + 1
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        albumCounts.map { (name, count) ->
            AlbumInfo(name, count)
        }.sortedBy { it.name }
    }
}
