package com.digital.registration.presentation

object StringChecker {
    /**
     * Проверяет строку на соответствие email
     *
     * @return если почта соответствует нужной возвращает true
     */
    fun checkMailString(email : String) = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$").matches(email)

    /**
     * Проверяет пароль на длину
     *
     * @return если пароль соответствует трубованиям true
     */
    fun checkPassword(password: String) = password.length >= 6
}