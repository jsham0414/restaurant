package com.example.database.security

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.stereotype.Component
import java.lang.reflect.Field
import java.util.*

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Encrypt

@Aspect
@Component
class EncryptData {
    private val encryptString = EncryptString()

    @Pointcut("execution(* com.example.database.repository.*.save*(*))")
    private fun isSave() {
    }

    @Around("isSave()")
    fun encrypt(joinPoint: ProceedingJoinPoint): Any {
        val target = joinPoint.args[0]
        val fields: Array<Field> = target.javaClass.declaredFields

        for (field in fields) {
            if (field.isAnnotationPresent(Encrypt::class.java)) {
                try {
                    field.isAccessible = true

                    val data = field.get(target)
                    var stringData: String = ""
                    if (data is String) {
                        stringData = data
                    }

                    val encryptedData: String = encryptString.encryptString(stringData)
                    field.set(target, encryptedData)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    return joinPoint
                }
            }
        }

        return joinPoint.proceed(joinPoint.args)
    }

    @Pointcut("execution(* com.example.database.repository.*.find*(*))")
    private fun isFind() {
    }

    @Around("isFind()")
    fun decrypt(joinPoint: ProceedingJoinPoint): Any? {
        val targetData = joinPoint.proceed(joinPoint.args)
            ?: return null

        // 옵셔널인지 확인
        val target: Any = if (targetData is Optional<*>) {
            targetData.get()
        } else {
            targetData
        }

        // 이제부터 UserInfo
        val fields: Array<Field> = target.javaClass.declaredFields
        for (field in fields) {

            if (field.isAnnotationPresent(Encrypt::class.java)) {
                try {
                    field.isAccessible = true

                    val data = field.get(target)
                    var stringData: String = ""
                    if (data is String) {
                        stringData = data
                    }

                    val plainData: String = encryptString.decryptString(stringData)
                    field.set(target, plainData)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                    return targetData
                }
            }
        }

        return target;
    }
}

