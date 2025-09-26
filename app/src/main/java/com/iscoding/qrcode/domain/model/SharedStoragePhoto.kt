package com.iscoding.qrcode.domain.model

import android.net.Uri

/**
 * Represents a photo stored in the device's shared storage.
 *
 * This model abstracts the essential metadata for a photo, making it
 * easy to display in galleries, lists, or selection screens without
 * exposing low-level storage details.
 *
 * @property id The unique identifier of the photo in shared storage.
 * @property displayName The display name or filename of the photo.
 * @property width The width of the photo in pixels.
 * @property height The height of the photo in pixels.
 * @property contentUri The content URI used to access the photo safely.
 * @property bucketName The album or folder name where the photo is located.
 *
 * Example usage:
 * ```
 * val photo = SharedStoragePhoto(
 *     id = 12345L,
 *     displayName = "IMG_001.jpg",
 *     width = 1920,
 *     height = 1080,
 *     contentUri = uri,
 *     bucketName = "Camera"
 * )
 * ```
 */
data class SharedStoragePhoto(
    val id: Long,
    val displayName: String,
    val width: Int,
    val height: Int,
    val contentUri: Uri,
    val bucketName: String,
)
