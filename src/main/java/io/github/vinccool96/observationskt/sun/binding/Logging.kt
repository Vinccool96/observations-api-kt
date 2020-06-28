package io.github.vinccool96.observationskt.sun.binding

import sun.util.logging.PlatformLogger

object Logging {

    private class LoggerHolder {

        companion object {
            val INSTANCE: PlatformLogger = PlatformLogger.getLogger("io.github.vinccool96.observationskt.beans")
        }

    }

    fun getLogger(): PlatformLogger {
        return LoggerHolder.INSTANCE
    }

}