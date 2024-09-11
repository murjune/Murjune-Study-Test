package com.murjune.practice.callbackflow

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeout

// 가상의 센서 API 콜백
interface SensorCallback {
    fun onSensorDataReceived(data: FloatArray)
    fun onSensorError(error: Throwable)
}

// 센서 매니저: 콜백을 통해 지속적으로 센서 데이터를 제공하는 가상 API
class SensorManager {
    private var callback: SensorCallback? = null

    // 센서 데이터 수신을 시작하는 메소드
    fun startSensorUpdates(callback: SensorCallback) {
        this.callback = callback
        // 가상의 센서 데이터 전달 시뮬레이션
        for (i in 1..10) {
            callback.onSensorDataReceived(floatArrayOf(randomFloat(), randomFloat(), randomFloat()))
        }
    }

    // 센서 업데이트를 중지하는 메소드
    fun stopSensorUpdates() {
        callback = null
    }

    private fun randomFloat() = (1..10).random().toFloat() + (0..9).random().toFloat() / 10
}

class SensorRepository {
    private val sensorManager = SensorManager()

    // 센서 데이터를 Flow로 변환하는 메소드
    fun sensorDataStream(): Flow<FloatArray> = callbackFlow {
        val sensorCallback = object : SensorCallback {
            override fun onSensorDataReceived(data: FloatArray) {
                trySend(data).isSuccess // 수신된 센서 데이터를 Flow로 전송
            }

            override fun onSensorError(error: Throwable) {
                close(error) // 에러 발생 시 Flow 종료
            }
        }

        // 센서 업데이트 시작
        sensorManager.startSensorUpdates(sensorCallback)

        // Flow가 종료되거나 취소되면 센서 업데이트 중지
        awaitClose {
            sensorManager.stopSensorUpdates()
        }
    }
}

suspend fun main() {
    withTimeout(1000) {
        SensorRepository().sensorDataStream().collect { data ->
            println("Received sensor data: ${data.joinToString(", ")}")
        }
    }
}