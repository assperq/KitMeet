package com.digital.registration.presentation

object StringChecker {
    /**
     * Проверяет строку на соответствие email @mgutu.loc
     *
     * @return если почта соответствует нужной возвращает true
     */
    fun checkMailString(email : String) = Regex("^[A-Za-z0-9._%+-]+@mgutu\\.loc\$").matches(email)

    /**
     * Проверяет пароль на длину
     *
     * @return если пароль соответствует трубованиям true
     */
    fun checkPassword(password: String) = password.length >= 6
}