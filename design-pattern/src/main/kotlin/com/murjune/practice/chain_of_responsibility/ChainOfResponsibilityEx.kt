package com.murjune.practice.chain_of_responsibility

enum class LogLevel {
    INFO,
    DEBUG,
    ERROR,
}

interface LogHandler {
    fun handleLog(
        level: LogLevel,
        message: String,
    )

    fun setNextHandler(nextHandler: LogHandler)
}

class InfoLogHandler : LogHandler {
    private var nextHandler: LogHandler? = null

    override fun handleLog(
        level: LogLevel,
        message: String,
    ) {
        if (level == LogLevel.INFO) {
            println("INFO: $message")
        } else {
            nextHandler?.handleLog(level, message)
        }
    }

    override fun setNextHandler(nextHandler: LogHandler) {
        this.nextHandler = nextHandler
    }
}

class DebugLogHandler : LogHandler {
    private var nextHandler: LogHandler? = null

    override fun handleLog(
        level: LogLevel,
        message: String,
    ) {
        if (level == LogLevel.DEBUG) {
            println("DEBUG: $message")
        } else {
            nextHandler?.handleLog(level, message)
        }
    }

    override fun setNextHandler(nextHandler: LogHandler) {
        this.nextHandler = nextHandler
    }
}

class ErrorLogHandler : LogHandler {
    override fun handleLog(
        level: LogLevel,
        message: String,
    ) {
        if (level == LogLevel.ERROR) {
            println("ERROR: $message")
        }
    }

    override fun setNextHandler(nextHandler: LogHandler) {
        // 아무일도 하지 않음
    }
}

fun main() {
    val infoLogHandler = InfoLogHandler()
    val debugLogHandler = DebugLogHandler()
    val errorLogHandler = ErrorLogHandler()

    infoLogHandler.setNextHandler(debugLogHandler)
    debugLogHandler.setNextHandler(errorLogHandler)

    infoLogHandler.handleLog(LogLevel.INFO, "INFO 메시지입니다.")
    // 이제 debug 핸들러로 책임 넘어감
    infoLogHandler.handleLog(LogLevel.DEBUG, "DEBUG 메시지입니다.")
    // 이제 error 핸들러로 책임 넘어감
    infoLogHandler.handleLog(LogLevel.ERROR, "ERROR 메시지입니다.")
    // 아무일도 하지 않음
    infoLogHandler.handleLog(LogLevel.ERROR, "FATAL 메시지입니다.")
}
