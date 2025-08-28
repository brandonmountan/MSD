package com.example.degreeplanner.model

data class Course(
    val department: String,
    val number: Int
) {
    override fun toString(): String = "$department $number"
}