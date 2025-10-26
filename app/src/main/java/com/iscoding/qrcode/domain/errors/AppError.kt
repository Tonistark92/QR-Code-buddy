package com.iscoding.qrcode.domain.errors

interface AppError {
    val message: String
    val cause: Throwable?
}
