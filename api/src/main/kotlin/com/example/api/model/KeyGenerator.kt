package com.example.api.model

import org.springframework.stereotype.Component
import java.util.*

@Component
class KeyGenerator {
    fun generateKey() = UUID.randomUUID().toString().replace("-", "");
}