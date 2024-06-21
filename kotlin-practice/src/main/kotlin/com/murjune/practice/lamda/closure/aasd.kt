package com.murjune.practice.lamda.closure

class Student(val name: String) {
    fun study() {
        println("$name 학생은 공부중..")
    }
}
fun main() {
var student = Student("오둥이")
val studentStudyFunction = Student::study
val studyFunction = { studentStudyFunction(student) }
val studyFunction2 = student::study
student = Student("둥이")
studyFunction()
studyFunction2()
//    var numberGenerator = { 2 }
//    val lam = { numberGenerator() }
//    val memberReference = numberGenerator::invoke
//    numberGenerator = { 3 }
//    println(lam())
//    println(memberReference())
}
