package com.iscoding.qrcode.features.scan.storage.allimages

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.iscoding.qrcode.domain.model.SharedStoragePhoto
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEffect
import com.iscoding.qrcode.features.scan.storage.allimages.intent.AllStorageImagesEvent
import com.iscoding.qrcode.features.scan.storage.allimages.widgets.PermissionRequestWidget
import com.iscoding.qrcode.features.scan.widgets.PermissionDialog
import com.iscoding.qrcode.graph.Screens
import com.iscoding.qrcode.util.sdk33AndUp
import kotlinx.coroutines.flow.collectLatest
import logcat.logcat
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder

@Composable
fun StorageScanScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = koinViewModel<AllStorageImagesViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val permission =
        sdk33AndUp {
            Manifest.permission.READ_MEDIA_IMAGES
        } ?: Manifest.permission.READ_EXTERNAL_STORAGE

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { granted ->
            val shouldShowRationale =
                if (!granted) {
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        permission,
                    )
                } else {
                    false
                }

            viewModel.onEvent(
                AllStorageImagesEvent.OnStoragePermissionResult(
                    granted = granted,
                    shouldShowRationale = shouldShowRationale,
                ),
            )
        }
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            // Check permission when app resumes
            val currentPermission =
                ContextCompat.checkSelfPermission(
                    context,
                    permission,
                ) == PackageManager.PERMISSION_GRANTED

            // Only update if changed (prevents unnecessary events)
            if (currentPermission != state.hasStoragePermission) {
                viewModel.onEvent(AllStorageImagesEvent.OnPermissionStatusChanged(currentPermission))
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasPermission =
            ContextCompat.checkSelfPermission(
                context,
                permission,
            ) == PackageManager.PERMISSION_GRANTED

        Log.d("ISLAM", "Initial permission check: $hasPermission")
        viewModel.onEvent(AllStorageImagesEvent.OnInitialPermissionCheck(hasPermission))
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { event ->

            Log.d("ISLAM", "UI Event received: $event")
            when (event) {
                is AllStorageImagesEffect.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                AllStorageImagesEffect.RequestStoragePermission -> {
                    Log.d("ISLAM", "Launching permission request")
                    permissionLauncher.launch(permission)
                }

                AllStorageImagesEffect.OpenAppSettings -> {
                    Log.d("ISLAM", "Opening app settings")
                    try {
                        val intent =
                            Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", context.packageName, null),
                            )
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        // Fallback to general app settings
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        context.startActivity(intent)
                    }
                }

                AllStorageImagesEffect.AnalyzeImage -> {
                    if (state.clickedImageUri != null) {
                        val inputStream =
                            context.contentResolver.openInputStream(state.clickedImageUri!!.toUri())
                        inputStream?.use {
                            viewModel.onEvent(
                                AllStorageImagesEvent.OnAnalyzeImage(
                                    state.clickedImageUri!!.toUri(),
                                    it,
                                ),
                            )
                        }
                    }
                }

                is AllStorageImagesEffect.NavigateToQrDetailsScreen -> {
                    val encodedQrData = URLEncoder.encode(event.qrCodeData, "UTF-8")
                    val encodedImageUri = URLEncoder.encode(event.imageUri, "UTF-8")
                    navController.navigate("${Screens.SHOW_QR_CODE_DATA_SCREEN}/$encodedQrData/$encodedImageUri")
                }
            }
        }
    }

    // UI Content
    Box(
        modifier =
        Modifier
            .padding(16.dp)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        when {
            !state.hasStoragePermission -> {
                PermissionRequestWidget(
                    onRequestPermission = {
                        viewModel.onEvent(AllStorageImagesEvent.OnRequestStoragePermission)
                    },
                )
            }

            state.isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = Color(0xFF138173),
                    trackColor = Color(0xFF8AE5D0),
                )
            }

            state.storageImagesList.isNotEmpty() || state.isLoadingMore -> {
                Column {
                    // Album Dropdown
                    if (state.albums.isNotEmpty()) {
                        AlbumDropdown(
                            albums = listOf("All Albums") + state.albums,
                            selectedAlbum = state.selectedAlbum ?: "All Albums",
                            onAlbumSelected = { album ->
                                val actualAlbum = if (album == "All Albums") null else album
                                viewModel.onEvent(AllStorageImagesEvent.SelectAlbum(actualAlbum))
                            },
                        )
                    }

                    // Paginated Images List
                    PaginatedImagesList(
                        photos = state.storageImagesList,
                        isLoadingMore = state.isLoadingMore,
                        hasMoreImages = state.hasMoreImages,
                        onLoadMore = {
                            viewModel.onEvent(AllStorageImagesEvent.LoadMoreImages)
                        },
                        onPhotoClick = { selectedUri ->
                            logcat("ISLAMMM") { "image clicked !$selectedUri" }
                            val inputStream = context.contentResolver.openInputStream(selectedUri)
                            inputStream?.let {
                                viewModel.onEvent(
                                    AllStorageImagesEvent.OnStorageImageClicked(selectedUri, it),
                                )
                            }
                        },
                    )
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "No images found",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.onEvent(AllStorageImagesEvent.RefreshImages)
                        },
                    ) {
                        Text("Refresh")
                    }
                }
            }
        }

//    when {
//            !state.hasStoragePermission -> {
//                PermissionRequestWidget(
//                    onRequestPermission = {
//                        viewModel.onEvent(AllStorageImagesEvent.OnRequestStoragePermission)
//                    },
//                )
//            }
//
//            state.isLoading -> {
//                CircularProgressIndicator(
//                    modifier = Modifier.width(64.dp),
//                    color = Color(0xFF138173),
//                    trackColor = Color(0xFF8AE5D0),
//                )
//            }
//
//            state.storageImagesList.isNotEmpty() -> {
//                Column {
//                    // Album Dropdown
//                    if (state.albums.isNotEmpty()) {
//                        AlbumDropdown(
//                            albums = listOf("All Albums") + state.albums,
//                            selectedAlbum = state.selectedAlbum ?: "All Albums",
//                            onAlbumSelected = { album ->
//                                val actualAlbum = if (album == "All Albums") null else album
//                                viewModel.onEvent(AllStorageImagesEvent.SelectAlbum(actualAlbum))
//                            },
//                        )
//                    }
//
//                    // Images List
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                            .background(MaterialTheme.colorScheme.background),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                    ) {
//                        items(state.storageImagesList) { photo ->
//                            PhotoItem(photo = photo) { selectedUri ->
//                                val inputStream = context.contentResolver.openInputStream(selectedUri)
//                                inputStream?.let {
//                                    viewModel.onEvent(
//                                        AllStorageImagesEvent.OnStorageImageClicked(selectedUri, it),
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            else -> {
//                PermissionRequestWidget(
//                    onRequestPermission = {
//                        viewModel.onEvent(AllStorageImagesEvent.OnRequestStoragePermission)
//                    },
//                )
//            }
//        }
    }
    if (state.shouldPermissionDialog) {
        PermissionDialog(
            title = "Storage Permission Required",
            body =
            if (state.shouldLaunchAppSettings) {
                "Storage permission was permanently denied. Please enable it in app settings to scan QR codes."
            } else {
                "We need Storage access to scan QR codes from the Photos. Please grant the permission."
            },
            confirmButtonText = if (state.shouldLaunchAppSettings) "Open Settings" else "Grant Permission",
            onConfirm = {
                if (state.shouldLaunchAppSettings) {
                    viewModel.onEvent(AllStorageImagesEvent.OnOpenAppSettings)
                } else {
                    viewModel.onEvent(AllStorageImagesEvent.OnRequestStoragePermission)
                }
                viewModel.onEvent(AllStorageImagesEvent.OnDismissPermissionDialog)
            },
            onDismiss = {
                viewModel.onEvent(AllStorageImagesEvent.OnDismissPermissionDialog)
            },
        )
    }
}

@Composable
private fun PaginatedImagesList(
    photos: List<SharedStoragePhoto>,
    isLoadingMore: Boolean,
    hasMoreImages: Boolean,
    onLoadMore: () -> Unit,
    onPhotoClick: (Uri) -> Unit,
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier =
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(
            items = photos,
            key = { photo -> photo.id },
        ) { photo ->
            PhotoItem(
                photo = photo,
                onItemClick = onPhotoClick,
            )
        }

        // Loading more indicator
        if (isLoadingMore) {
            item {
                Box(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color(0xFF138173),
                        trackColor = Color(0xFF8AE5D0),
                    )
                }
            }
        }

        // Load more button (optional - you can remove this if you prefer auto-loading)
        if (!isLoadingMore && hasMoreImages && photos.isNotEmpty()) {
            item {
                Button(
                    onClick = onLoadMore,
                    modifier = Modifier.padding(16.dp),
                ) {
                    Text("Load More Images")
                }
            }
        }

        // End of list indicator
        if (!hasMoreImages && photos.isNotEmpty()) {
            item {
                Text(
                    text = "No more images to load",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }

    // Auto-load more when near the end of the list
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItems = listState.layoutInfo.totalItemsCount

            // Load more when user is within 5 items of the end
            lastVisibleIndex >= totalItems - 5 && hasMoreImages && !isLoadingMore
        }
    }

//    LaunchedEffect(shouldLoadMore) {
//        if (shouldLoadMore) {
//            onLoadMore()
//        }
//    }
}

// @Composable
// private fun PhotoItem(
//    photo: SharedStoragePhoto,
//    onItemClick: (Uri) -> Unit,
// ) {
//    Box(
//        modifier =
//        Modifier
//            .background(MaterialTheme.colorScheme.background)
//            .padding(8.dp)
//            .clickable { onItemClick(photo.contentUri) },
//    ) {
//        Image(
//            painter = rememberImagePainter(photo.contentUri),
//            contentDescription = null,
//            modifier = Modifier.size(250.dp),
//            contentScale = ContentScale.Crop,
//        )
//    }
//    Spacer(modifier = Modifier.height(12.dp))
// }
@Composable
private fun PhotoItem(
    photo: SharedStoragePhoto,
    onItemClick: (Uri) -> Unit,
) {
    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clickable { onItemClick(photo.contentUri) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = photo.contentUri,
                contentDescription = photo.displayName,
                modifier =
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
//                placeholder = painterResource(R.drawable.ic_image_placeholder), // Add placeholder
//                error = painterResource(R.drawable.ic_error_image) // Add error image
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = photo.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Album: ${photo.bucketName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "${photo.width} × ${photo.height}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "Scan QR Code",
                tint = Color(0xFF138173),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlbumDropdown(
    albums: List<String>,
    selectedAlbum: String,
    onAlbumSelected: (String) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            value = selectedAlbum,
            onValueChange = { },
            readOnly = true,
            label = { Text("Select Album") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier =
            Modifier
                .menuAnchor()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            albums.forEach { album ->
                DropdownMenuItem(
                    text = { Text(album) },
                    onClick = {
                        onAlbumSelected(album)
                        expanded = false
                    },
                )
            }
        }
    }
}

// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// private fun AlbumDropdown(
//    albums: List<String>,
//    selectedAlbum: String,
//    onAlbumSelected: (String) -> Unit,
// ) {
//    var expanded by remember { mutableStateOf(false) }
//
//    ExposedDropdownMenuBox(
//        expanded = expanded,
//        onExpandedChange = { expanded = !expanded },
//    ) {
//        OutlinedTextField(
//            value = selectedAlbum,
//            onValueChange = { },
//            readOnly = true,
//            label = { Text("Select Album") },
//            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//            modifier = Modifier
//                .menuAnchor()
//                .fillMaxWidth()
//                .padding(horizontal = 16.dp, vertical = 8.dp),
//        )
//
//        ExposedDropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//        ) {
//            albums.forEach { album ->
//                DropdownMenuItem(
//                    text = { Text(album) },
//                    onClick = {
//                        onAlbumSelected(album)
//                        expanded = false
//                    },
//                )
//            }
//        }
//    }
// }
