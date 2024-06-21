package designpattern.fly_weight

object FlyWeightEx {
    interface Tree {
        fun place(
            x: Int,
            y: Int,
        )
    }

    class TreeType(private val name: String, private val color: String) :
        Tree {
        override fun place(
            x: Int,
            y: Int,
        ) {
            println("나무 이름: $name, 색상: $color, 위치: ($x, $y)")
        }
    }

    object TreeManager {
        private val treeTypes = mutableMapOf<String, FlyWeightEx.TreeType>()

        fun getOrPutTreeType(
            name: String,
            color: String,
        ): FlyWeightEx.TreeType {
            val key = "$name-$color"
            return treeTypes.getOrPut(key) {
                FlyWeightEx.TreeType(
                    name,
                    color,
                )
            }
        }

        val numberOfTreeTypes: Int
            get() = treeTypes.size
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val redTree = TreeManager.getOrPutTreeType("나무", "Red")
        val greenTree = TreeManager.getOrPutTreeType("나무2", "Green")
        val brownTree = TreeManager.getOrPutTreeType("나무3", "Brown")

        redTree.place(1, 2)
        greenTree.place(3, 4)
        brownTree.place(5, 6)

        println("생성된 나무의 종류: ${TreeManager.numberOfTreeTypes}")
    }
}
