package com.iscoding.qrcode.domain.util

import arrow.core.Either
import com.iscoding.qrcode.data.errors.ErrorReporter
import com.iscoding.qrcode.domain.errors.AppError

suspend inline fun <T, E : AppError> runCatchingError(
    crossinline block: suspend () -> T,
    crossinline mapError: (Exception) -> E,
): Either<E, T> {
    return try {
        Either.Right(block())
    } catch (e: Exception) {
        val error = mapError(e)
        ErrorReporter.log(error)
        Either.Left(error)
    }
}

inline fun <T, E : AppError> runBlockingError(
    block: () -> T,
    mapError: (Exception) -> E,
): Either<E, T> {
    return try {
        Either.Right(block())
    } catch (e: Exception) {
        val error = mapError(e)
        ErrorReporter.log(error)
        Either.Left(error)
    }
}
