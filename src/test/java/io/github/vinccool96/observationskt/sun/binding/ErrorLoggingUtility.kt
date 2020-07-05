package io.github.vinccool96.observationskt.sun.binding

import java.util.logging.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ErrorLoggingUtility {

    private var level: Level? = null

    private var lastRecord: LogRecord? = null

    private val handler: Handler = object : Handler() {

        override fun publish(record: LogRecord?) {
            this@ErrorLoggingUtility.lastRecord = record
        }

        override fun flush() {
        }

        @Throws(SecurityException::class)
        override fun close() {
        }

    }

    fun start() {
        reset()
        this.level = logger.level
        logger.level = Level.ALL
        logger.addHandler(this.handler)
    }

    fun stop() {
        logger.level = this.level
        logger.removeHandler(this.handler)
    }

    fun reset() {
        this.lastRecord = null
    }

    fun checkFine(expectedException: Class<out Throwable>) {
        check(Level.FINE, expectedException)
    }

    fun check(expectedLevel: Level, expectedException: Class<out Throwable>) {
        assertNotNull(this.lastRecord)
        assertEquals(expectedLevel, this.lastRecord!!.level)
        assertEquals(expectedException, this.lastRecord!!.thrown.javaClass)
        reset()
    }

    companion object {

        init {
            // initialize PlatformLogger
            Logging.getLogger()
        }

        // getLogManager will redirect existing PlatformLogger to the Logger
        private val logger: Logger = LogManager.getLogManager().getLogger("io.github.vinccool96.observationskt.beans")

    }

}