package com.murjune.practice.bridge

object BridgePatterEx {
    // TV 추상 클래스
    abstract class TV {
        abstract fun powerOn()

        abstract fun powerOff()

        abstract fun setChannel(channel: Int)
    }

    // 리모컨 추상 클래스
    abstract class RemoteControl(protected val tv: TV) {
        abstract fun turnOn()

        abstract fun turnOff()

        abstract fun changeChannel(channel: Int)
    }

    // LG TV 클래스
    class LGTV : TV() {
        override fun powerOn() {
            println("LG TV를 켭니다.")
        }

        override fun powerOff() {
            println("LG TV를 끕니다.")
        }

        override fun setChannel(channel: Int) {
            println("LG TV 채널을 $channel 로 설정합니다.")
        }
    }

    // LG 리모컨 클래스
    class LGRemoteControl(tv: TV) : RemoteControl(tv) {
        override fun turnOn() {
            println("LG 리모컨으로 TV를 켭니다.")
            tv.powerOn()
        }

        override fun turnOff() {
            println("LG 리모컨으로 TV를 끕니다.")
            tv.powerOff()
        }

        override fun changeChannel(channel: Int) {
            println("LG 리모컨으로 채널을 변경합니다.")
            tv.setChannel(channel)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // Tv, RemoteControl 모두 구현 부분을 추상화 부분과 분리하여 확장성을 높임
        val lgTV: TV =
            LGTV()
        val lgRemote: RemoteControl =
            LGRemoteControl(lgTV)

        lgRemote.turnOn()
        lgRemote.changeChannel(3)
        lgRemote.turnOff()
    }
}
