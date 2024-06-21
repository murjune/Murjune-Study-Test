package designpattern.delegate

object ClassDelegateEx {
    @JvmStatic
    fun main(args: Array<String>) {
        val fileLogger = FileLogger()
        val consoleLogger = ConsoleLogger()

        val someInstance = SomeClass(fileLogger, consoleLogger)

        someInstance.doSomething() // 파일과 콘솔에 로깅
    }

    fun interface Logger {
        fun log(message: String)
    }

    fun interface Logger2 {
        fun log2(message: String)
    }

    class FileLogger : Logger {
        override fun log(message: String) {
            println("Logging to file: $message")
        }
    }

    class ConsoleLogger : Logger2 {
        override fun log2(message: String) {
            println("Logging to console: $message")
        }
    }

    class SomeClass(fileLogger: Logger, consoleLogger: Logger2) : Logger by fileLogger, Logger2 by consoleLogger {
        fun doSomething() {
            log("Doing something...")
            log2("Doing something...")
        }
    }
}
