package com.murjune.practice.suspense

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

suspend fun fetchWootecoCrews(): List<String> = coroutineScope {
    val androidCrews = async { fetchWootecoAndroidCrews() }
    val frontendCrews = async { fetchWootecoFrontendCrews() }
    val backendCrews = async { fetchWootecoBackendCrews() }

    androidCrews.await() + frontendCrews.await() + backendCrews.await()
}

suspend fun fetchWootecoAndroidCrews(): List<String> {
    delay(300)
    return listOf(
        "오둥이", "차람", "해음", "알송", "빙티", "케이엠",
        "채채", "올리브", "서기", "벼리",
        "심지", "팡태", "악어", "해나",
        "하디", "누누", "채드", "꼬상", "예니", "호두", "에디"
    )
}

suspend fun fetchWootecoFrontendCrews(): List<String> {
    delay(200)
    return listOf("토다리", "제이드", "파란")
}

suspend fun fetchWootecoBackendCrews(): List<String> {
    delay(100)
    return listOf("종이", "비토", "폴라", "미아", "켈리", "레디", "커비")
}

suspend fun fetchAndroidCoaches(): List<String> {
    delay(50)
    return listOf("제이슨", "레아", "제임스")
}

suspend fun fetchFrontCoaches(): List<String> {
    delay(150)
    return listOf("준", "크론")
}

suspend fun fetchBackCoaches(): List<String> {
    delay(70)
    throw NoSuchElementException("제가 백엔드 코치님들은 모릅니다..")
}
