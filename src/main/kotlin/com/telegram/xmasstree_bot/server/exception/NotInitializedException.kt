package com.telegram.xmasstree_bot.server.exception

/**
 * Exception thrown when some field is not initialized.
 * @param message The message of the exception.
 * @param cause The cause of the exception.
 * @see RuntimeException
 */
class NotInitializedException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
