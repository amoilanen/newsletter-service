package com.github.amoilanen.error

case class ApplicationError(message: String, cause: Option[Throwable] = None)
    extends RuntimeException(message, cause.getOrElse(null))
