package com.murjune.practice.annotation

import io.kotest.matchers.shouldBe
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.test.Test

class AnnotationWIthReflectionTest {

    @Target(AnnotationTarget.CONSTRUCTOR, AnnotationTarget.PROPERTY)
    annotation class MyInject

    @Target(AnnotationTarget.ANNOTATION_CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class Qualifier

    @Qualifier
    annotation class TimmyQualifier

    @Qualifier
    annotation class OdoongQualifier

    class Odoong {
        val name: String = "Odoong"
        val age: Int = 28

        @MyInject
        @TimmyQualifier
        lateinit var team: Team

        @MyInject
        @OdoongQualifier
        lateinit var team2: Team
    }

    interface Team

    class TimmyRoom : Team

    class OdoongTeam : Team

    @Test
    fun `Odoong 이의 team 에 TimmyRoom 을 주입한다`() {
        // given
        val odoong = Odoong()
        val team: Team = TimmyRoom()
        val properties = odoong::class.declaredMemberProperties
        // when: inject team
        val prop = properties.first { prop ->
            prop is KMutableProperty<*> && prop.findAnnotation<MyInject>() != null
        } as KMutableProperty<*>
        prop.setter.call(odoong, team)
        // then
        odoong.team shouldBe team
    }

    @Test
    fun `Odoong 이의 team2 에 OdoongRoom 을 주입한다`() {
        // given
        val odoong = Odoong()
        val team1: Team = OdoongTeam()
        val team2: Team = TimmyRoom()
        val properties = odoong::class.declaredMemberProperties
        // when
        // inject team
        properties.forEach { prop ->
            if (prop is KMutableProperty<*> && prop.findAnnotation<MyInject>() != null) {
                if (prop.findAnnotation<OdoongQualifier>() != null) {
                    prop.setter.call(odoong, team1)
                    return@forEach
                }
                if (prop.findAnnotation<TimmyQualifier>() != null) {
                    prop.setter.call(odoong, team2)
                    return@forEach
                }
            }
        }
        // then
        odoong.team shouldBe team2
        odoong.team2 shouldBe team1
    }
}