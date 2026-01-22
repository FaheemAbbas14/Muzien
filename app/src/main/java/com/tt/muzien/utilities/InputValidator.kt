package com.tt.muzien.utilities

import android.util.Patterns
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.util.Locale

object InputValidator {

    // Validate Email
    fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validate Phone Number
    fun isValidPhoneNumber(country: String, phoneNumber: String): Boolean {
        val phoneUtil = PhoneNumberUtil.getInstance()
        try {
            if (country.length+phoneNumber.length==13 || country.length+phoneNumber.length==12 ){
                return  true
            }
            else{
                return  false
            }
//            val parsedNumber: Phonenumber.PhoneNumber =
//                phoneUtil.parse(phoneNumber, getCountryCode(country))
//            return phoneUtil.isValidNumber(parsedNumber)
        } catch (e: Exception) {
            return false
        }
    }

    fun getCountryCode(country: String): String {
        val localeList = Locale.getAvailableLocales()
        for (locale in localeList) {
            if (locale.displayCountry == country) {
                return locale.country
            }
        }
        return ""
    }

    // Validate Password (example: at least 8 characters, one uppercase, one lowercase, one digit)
    fun isValidPassword(password: CharSequence?): Boolean {
        if (password.isNullOrEmpty()) return false
        val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}\$"
        return password.matches(Regex(passwordPattern))
    }

    // Validate Non-Empty Input
    fun isNonEmpty(input: CharSequence?): Boolean {
        return !input.isNullOrEmpty()
    }
    fun getPhoneNumberPlaceholder(countryCode: String): String {

//        // Get the instance of Google's PhoneNumberUtil
//        val phoneNumberUtil = PhoneNumberUtil.getInstance()
//
//        // Get an example phone number for the country
//        val exampleNumber = phoneNumberUtil.getExampleNumberForType(
//            countryCode,
//            PhoneNumberUtil.PhoneNumberType.MOBILE
//        )
//
//        // Format the example number
//        val placeholder = if (exampleNumber != null) {
//            phoneNumberUtil.format(exampleNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)
//        } else {
//            "" // Fallback if example number is not available
//        }
        var placholder="000 000 0000"
        return placholder
    }
}