package com.murjune.practice.utils

import com.github.javafaker.Faker
import java.util.*


val faker get() = Faker.instance(Locale.US)
