package com.murjune.practice.suspendcoroutine

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


interface CameraCallback {
    fun onFrameReceived(frame: ByteArray)
    fun onError(error: Throwable)
}

// 카메라 매니저: 콜백을 통해 프레임을 제공하는 가상 API
class CameraManager {
    private var callback: CameraCallback? = null

    fun startCameraPreview(callback: CameraCallback) {
        this.callback = callback
        // camera 가 찍고 받은 프레임 데이터를 callback 으로 전달
        val ba = ByteArray(1024)
        callback.onFrameReceived(ba)
    }

    fun stopCameraPreview() {
        callback = null
    }
}

class CameraRepository {
    private val cameraManager = CameraManager()

    suspend fun capturedFrame(): ByteArray = suspendCancellableCoroutine { continuation ->
        val cameraManager = CameraManager()

        val cameraCallback = object : CameraCallback {
            override fun onFrameReceived(frame: ByteArray) {
                continuation.resume(frame)
                cameraManager.stopCameraPreview()
            }

            override fun onError(error: Throwable) {
                continuation.resumeWithException(error)
                cameraManager.stopCameraPreview()
            }
        }

        cameraManager.startCameraPreview(cameraCallback)

        continuation.invokeOnCancellation {
            cameraManager.stopCameraPreview()
        }
    }
}