package com.murjune.practice.exception

import kotlinx.coroutines.*

//val time = TimeSource.Monotonic
//fun mark() = time.markNow()
fun main() {
    runBlocking {
//        val s = mark()
//        val li = List(3) {
//            async {
//                try {
//                    println("start - ${mark() - s}")
//                    val imageUrl =
//                        URL("https://velog.velcdn.com/images/murjune/profile/998edd4f-1357-4c8f-b7a4-3d6cd723c800/image.jpg")
//                    val connection: HttpURLConnection = imageUrl.openConnection() as HttpURLConnection
//                    connection.doInput = true
//                    connection.connect()
//                    val inputStream: InputStream = connection.inputStream
//                    ImageIO.read(inputStream)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    null
//                } finally {
//                    println("finish - ${mark() - s}")
//                }
//            }
//        }

        CoroutineScope(Dispatchers.IO).launch {
            error("코루틴 error ⚠️")
        }
        delay(2000)
        println("얘가 실행안될 것 같죠? 실행됩니다~")
    }
}