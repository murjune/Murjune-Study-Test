// package factory
//
// object FactoryEx2 {
//    // model class
//    abstract class Figure() {
//        abstract val name: String
//        abstract val color: String
//        abstract val width: Double
//        abstract val height: Double
//        abstract val area: Double
//    }
//
//    data class Rectangle(
//        override val name: String,
//        override val color: String,
//        override val width: Double,
//        override val height: Double,
//        override val area: Double
//    ) : Figure()
//
//    data class Triangle(
//        override val name: String,
//        override val color: String,
//        override val width: Double,
//        override val height: Double,
//        override val area: Double
//    ) : Figure()
//    // strategy
//
//    fun interface AreaCalculateStrategy {
//        fun calculate(width: Double, height: Double): Double
//    }
//
//    // Factory
//
//    abstract class FigureFactory(protected val areaCalculateStrategy: AreaCalculateStrategy) {
//        abstract fun create(name: String, color: String, width: Double, height: Double): Figure
//    }
//
//    class RectangleFactory : FigureFactory(AreaCalculateStrategy { width, height -> width * height }) {
//        override fun create(name: String, color: String, width: Double, height: Double): Figure {
//            return Rectangle(name, color, width, height, areaCalculateStrategy.calculate(width, height))
//        }
//    }
//    @JvmStatic
//    fun main(args: Array<String>) {
//        val rectangleFactory = RectangleFactory()
//        val triangleFactory = TriangleFactory()
//
//        val rectangle = rectangleFactory.create("직사각형", "빨강", 10.0, 5.0)
//        val triangle = triangleFactory.create("삼각형", "파랑", 10.0, 5.0)
//
//        println(rectangle)
//        println(triangle)
//    }
// }
