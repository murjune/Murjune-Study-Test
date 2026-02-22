package com.murjune.pratice.compose.study.navigation

import kotlinx.serialization.Serializable

@Serializable
object StudyMainRoute

@Serializable
data class TopicRoute(
    val topicId: String,
    val topicTitle: String,
)

@Serializable
object NavHostBasicRoute

@Serializable
object TypeSafeNavRoute

@Serializable
object PopBackStackRoute

@Serializable
object PopUpToRoute

@Serializable
object DeepLinkRoute

@Serializable
object LaunchSingleTopRoute

@Serializable
object SavedStateHandleRoute

@Serializable
object SaveRestoreStateRoute

@Serializable
object NestedNavGraphRoute

@Serializable
object NavigateUpRoute
