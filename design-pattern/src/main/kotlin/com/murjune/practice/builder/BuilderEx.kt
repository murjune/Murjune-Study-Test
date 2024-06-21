package com.murjune.practice.builder

object BuilderEx {
    class User private constructor(
        val id: Long,
        val username: String,
        val email: String,
        val age: Int,
    ) {
        class Builder {
            private var id: Long = 0
            private var username: String = ""
            private var email: String = ""
            private var age: Int = 0

            fun setId(id: Long): Builder {
                this.id = id
                return this
            }

            fun setUsername(username: String): Builder {
                this.username = username
                return this
            }

            fun setEmail(email: String): Builder {
                this.email = email
                return this
            }

            fun setAge(age: Int): Builder {
                this.age = age
                return this
            }

            fun build(): User {
                return User(id, username, email, age)
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // 빌더 패턴을 사용하면 객체 생성에 필요한 매개변수가 많은 경우(복잡한 객체)에 특히 유용
        // 매개 변수 순서를 고려하지 않아도 됨
        // chain 형태라 가독성 높음!
        // 불변성!
        // kotlin Result 이 빌더 패턴을 사용함
        val user =
            User.Builder()
                .setId(1997_11_08)
                .setUsername("이준원")
                .setEmail("june922@naver.com")
                .setAge(27)
                .build()

        println(user)
    }
}
