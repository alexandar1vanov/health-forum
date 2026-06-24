package com.sorsix.healthforum.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailService(
    private val mailSender: JavaMailSender
) {
    fun sendRegistrationSuccess(to: String) {
        val message = SimpleMailMessage()
        message.setTo(to)
        message.setSubject("Успешна регистрација - Health Forum")
        message.setText(
            "Здраво,\n\nТвојата регистрација е успешна! " +
                    "Веќе можеш да се најавиш на Health Forum.\n\nПоздрав,\nHealth Forum тим"
        )
        mailSender.send(message)
    }
}