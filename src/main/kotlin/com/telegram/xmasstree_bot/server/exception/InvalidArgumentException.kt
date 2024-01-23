package com.telegram.xmasstree_bot.server.exception

/**
 * Exception thrown when an invalid argument is passed to a method.
 * @param message The message of the exception.
 * @param cause The cause of the exception.
 * @see RuntimeException
 */
class InvalidArgumentException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)

