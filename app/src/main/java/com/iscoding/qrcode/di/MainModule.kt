package com.iscoding.qrcode.di

import com.iscoding.qrcode.data.repos.MediaRepositoryImpl
import com.iscoding.qrcode.data.repos.QrCodeGeneratorImp
import com.iscoding.qrcode.data.repos.QrCodeScannerImp
import com.iscoding.qrcode.data.repos.QrCodeStorageAnalyzerImp
import com.iscoding.qrcode.domain.repos.MediaRepository
import com.iscoding.qrcode.domain.repos.QrCodeGenerator
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import com.iscoding.qrcode.domain.repos.QrCodeStorageAnalyzer
import com.iscoding.qrcode.features.generate.GenerateQRCodeViewModel
import com.iscoding.qrcode.features.mainactivity.MainActivityViewModel
import com.iscoding.qrcode.features.scan.camera.CameraScanViewModel
import com.iscoding.qrcode.features.scan.storage.allimages.AllStorageImagesViewModel
import com.iscoding.qrcode.features.scan.storage.details.QrDetailsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin DI module for the QR Code app.
 *
 * Provides ViewModels and Repository singletons.
 *
 * Key points:
 * - ViewModels are declared using `viewModel { ... }` and injected with their dependencies.
 * - Repositories are declared as singletons (`single { ... }`) to ensure one instance across the app.
 * - Use cases (commented out) can be added with `factory { ... }` if needed.
 */
val appModule = module {

    // -------------------------------
    // ViewModels
    // -------------------------------
    viewModel {
        GenerateQRCodeViewModel(get<QrCodeGenerator>())
    }
    viewModel {
        CameraScanViewModel(get<QrCodeScanner>())
    }
    viewModel {
        MainActivityViewModel(get<QrCodeStorageAnalyzer>())
    }
    viewModel {
        QrDetailsViewModel()
    }
    viewModel {
        AllStorageImagesViewModel(
            get<QrCodeStorageAnalyzer>(),
            get<MediaRepository>(),
        )
    }

    // -------------------------------
    // Repositories
    // -------------------------------
    single<QrCodeGenerator> { QrCodeGeneratorImp() }
    single<QrCodeScanner> { QrCodeScannerImp() }
    single<QrCodeStorageAnalyzer> { QrCodeStorageAnalyzerImp() }
    single<MediaRepository> { MediaRepositoryImpl(androidContext()) }

    // -------------------------------
    // Use Cases (optional)
    // -------------------------------
    // Example usage of factory for use cases:
    // factory { SaveGeneratedQrUseCase(get()) }
    // factory { GetGeneratedHistoryUseCase(get()) }
    // factory { DeleteGeneratedQrUseCase(get()) }
    //
    // factory { SaveScannedQrUseCase(get()) }
    // factory { GetScannedHistoryUseCase(get()) }
    // factory { DeleteScannedQrUseCase(get()) }
}
