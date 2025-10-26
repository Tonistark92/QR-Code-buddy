package com.iscoding.qrcode.domain.model

/**
 * Represents basic information about a photo album on the device.
 *
 * This model is used in the domain layer to provide a simple abstraction of an album,
 * including its name and the number of photos it contains. It can be displayed in
 * album lists, galleries, or selection screens.
 *
 * @property name The display name of the album (e.g., "Camera", "Screenshots").
 * @property photoCount The total number of photos in the album.
 *
 * Example usage:
 * ```
 * val album = AlbumInfo(name = "Camera", photoCount = 120)
 * println("Album ${album.name} contains ${album.photoCount} photos.")
 * ```
 */
data class AlbumInfo(
    val name: String,
    val photoCount: Int,
)
