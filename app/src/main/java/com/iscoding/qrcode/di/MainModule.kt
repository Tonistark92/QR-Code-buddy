package com.iscoding.qrcode.di

//import androidx.room.Room
//import com.iscoding.qrcode.data.local.AppDatabase
//import com.iscoding.qrcode.data.repos.QRCodeRepositoryImp
import android.content.Context
import com.iscoding.qrcode.data.repos.QrCodeGeneratorImpl
import com.iscoding.qrcode.data.repos.QrCodeScannerImpl
import com.iscoding.qrcode.domain.repos.QrCodeGenerator
import com.iscoding.qrcode.domain.repos.QrCodeScanner
import com.iscoding.qrcode.features.generate.GenerateQRCodeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

//    single {
////        Room.databaseBuilder(
////            get(),
////            AppDatabase::class.java,
////            AppDatabase.DATABASE_NAME
////        ).fallbackToDestructiveMigration().build()
////    }
////
////    single { get<AppDatabase>().scannedDao() }
////    single { get<AppDatabase>().generatedDao() }
////    single { QRCodeRepositoryImp(get(), get()) }
//
//
//    }

    viewModel {
        GenerateQRCodeViewModel( get<QrCodeGenerator>())
    }



    // Repositories
    single<QrCodeGenerator> { QrCodeGeneratorImpl() }
    single<QrCodeScanner> { QrCodeScannerImpl() }

    // Use Cases (if you have them)
//    factory { SaveGeneratedQrUseCase(get()) }
//    factory { GetGeneratedHistoryUseCase(get()) }
//    factory { DeleteGeneratedQrUseCase(get()) }
//
//    factory { SaveScannedQrUseCase(get()) }
//    factory { GetScannedHistoryUseCase(get()) }
//    factory { DeleteScannedQrUseCase(get()) }

    // Utilities
//    single<QRCodeGenerator> { QRCodeGeneratorImpl() }
//    single<QRCodeScanner> { QRCodeScannerImpl() }

}




