package com.iscoding.qrcode.domain.di

import androidx.room.Room
import com.iscoding.qrcode.data.local.AppDatabase
import com.iscoding.qrcode.data.repos.QRCodeRepositoryImp
import com.iscoding.qrcode.features.generatecode.GenerateQRCodeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single {
//        Room.databaseBuilder(
//            get(),
//            AppDatabase::class.java,
//            AppDatabase.DATABASE_NAME
//        ).fallbackToDestructiveMigration().build()
//    }
//
//    single { get<AppDatabase>().scannedDao() }
//    single { get<AppDatabase>().generatedDao() }
//    single { QRCodeRepositoryImp(get(), get()) }


    }

    viewModel{
        GenerateQRCodeViewModel()

    }

}




