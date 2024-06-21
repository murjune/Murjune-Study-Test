package designpattern.delegate

import kotlin.reflect.KProperty

object VariableDelegateEx {
//    val lazyValue: Foo by PropertyDelegateImpl(FooImpl())

    @JvmStatic
    fun main(args: Array<String>) {
    }

    interface Foo {
        operator fun getValue(
            thisRef: Any?,
            property: KProperty<*>,
        ): Foo

        operator fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: Foo,
        )
    }

    class PropertyDelegateImpl(private var value: Foo) :
        Foo {
        override fun getValue(
            thisRef: Any?,
            property: KProperty<*>,
        ): Foo {
            return value
        }

        override fun setValue(
            thisRef: Any?,
            property: KProperty<*>,
            value: Foo,
        ) {
            this.value = value
        }
    }
}
