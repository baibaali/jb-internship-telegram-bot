package com.telegram.xmasstree_bot.server.exception

/**
 * Exception thrown when the file is not loaded.
 * @param message The message of the exception.
 * @param cause The cause of the exception.
 * @see RuntimeException
 */
class FileNotLoadedException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
