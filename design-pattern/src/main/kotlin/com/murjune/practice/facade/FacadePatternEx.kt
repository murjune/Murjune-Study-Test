package designpattern.facade

import kotlin.random.Random

object FacadePatternEx {
    @JvmStatic
    fun main(args: Array<String>) {
        val hourReporter = HourReporter()
        val employeeSaver = EmployeeSaver()
        val employee = Employee(hourReporter, employeeSaver)
        employee.reportHours()
        employee.calculatePay()
        employee.save()
    }

    class Employee(
        private val hourReporter: HourReporter,
        private val employeeSaver: EmployeeSaver,
    ) {
        fun calculatePay() {
            val hours = hourReporter.reportHours()
            val pay = hours * 10_000
            println("Pay: $pay")
        }

        fun reportHours() {
            hourReporter.reportHours()
        }

        fun save() {
            employeeSaver.saveEmployee()
        }
    }

    class HourReporter {
        fun reportHours(): Int {
            return Random.nextInt(1, 12)
        }
    }

    class EmployeeSaver {
        fun saveEmployee() {
            println("Saving employee...")
        }
    }
}
