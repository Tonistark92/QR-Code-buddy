package com.iscoding.qrcode.data.repos

//import com.iscoding.qrcode.data.local.Generated
//import com.iscoding.qrcode.data.local.GeneratedDao
//import com.iscoding.qrcode.data.local.Scanned
//import com.iscoding.qrcode.data.local.ScannedDao
//import com.iscoding.qrcode.domain.repos.QRCodeRepository
//import kotlinx.coroutines.flow.Flow
//
//class QRCodeRepositoryImp(private val scannedDao: ScannedDao,private val generatedDao: GeneratedDao) : QRCodeRepository {
//
//    fun getAllUsers(): Flow<List<Scanned>> = scannedDao.getAllScanned()
//
//    suspend fun insert(user: Scanned) {
//        scannedDao.insert(user)
//    }
//
//    override fun GetAllGenerated(): Flow<List<Generated>> {
//        return generatedDao.getAllGenerated()
//    }
//
//    override fun GetAllScanned(): Flow<List<Scanned>> {
//        return scannedDao.getAllScanned()
//    }
//
//    override suspend fun insertScanned(scanned: Scanned) {
//        scannedDao.insert(scanned = scanned)
//    }
//
//    override suspend fun insertGenerated(generated: Generated) {
//        generatedDao.insert(genrated = generated)
//    }
//
//}