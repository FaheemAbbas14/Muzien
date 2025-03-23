package com.tt.muzien.utilities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.text.format.DateFormat
import androidx.annotation.RequiresApi
import com.tt.muzien.data.responses.SaloonWorkHour
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.TimeZone
import kotlin.takeIf
import kotlin.text.isNotEmpty


/**
 * Created by Faheem Abbas on 15/08/2024.
 * Technical Lead(Mobile Apps)
 * faheemabbas60@yahoo.com
 * +923115284424
 */
object TimeHelper {
    fun getHourFormat(context: Context): String {
        // Check if the device is using 24-hour format
        val is24HourFormat = DateFormat.is24HourFormat(context)
        // Define the date format pattern
        val pattern = if (is24HourFormat) {
            "HH:mm" // 24-hour format
        } else {
            "hh:mm a" // 12-hour format with AM/PM
        }
        return pattern
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun convertISOToDate(
        isoString: String,
        outputPattern: String
    ): String {
        val instant = Instant.parse(isoString) // Parse ISO date
        val date = instant.atOffset(ZoneOffset.UTC).toLocalDateTime() // Convert to LocalDate in UTC
        val formatter = DateTimeFormatter.ofPattern(outputPattern) // Define output format

        return date.format(formatter)
    }

    fun convertTimeFormat(
        inputTime: String,
        inputPattern: String,
        outputPattern: String
    ): String? {
        return try {
            // Create a SimpleDateFormat object with the input pattern
            val inputFormat = SimpleDateFormat(inputPattern, Locale.getDefault())

            // Parse the input time string to a Date object
            val date = inputFormat.parse(inputTime)

            // Create a SimpleDateFormat object with the output pattern
            val outputFormat = SimpleDateFormat(outputPattern, Locale.getDefault())

            // Format the Date object to the desired output string
            outputFormat.format(date)
        } catch (e: Exception) {
            // Handle parsing errors
            e.printStackTrace()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun daysPassedFrom(dateTimeString: String): Long {
        // Define the date-time format with time zone
        val formatter = DateTimeFormatter.ISO_DATE_TIME

        // Parse the input date-time string to a ZonedDateTime object
        val specificDateTime = ZonedDateTime.parse(dateTimeString, formatter)

        // Get the current date-time in the same time zone (UTC)
        val currentDateTime = ZonedDateTime.now(specificDateTime.zone)

        // Calculate the number of days between the two date-times
        return ChronoUnit.DAYS.between(
            specificDateTime.toLocalDate(),
            currentDateTime.toLocalDate()
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isTimeInRange(timeToCheck: String, startTime: String, endTime: String): Boolean {
        // Define the time formatter
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        // Parse the times to LocalTime objects
        val checkTime = LocalTime.parse(timeToCheck, timeFormatter)
        val start = LocalTime.parse(startTime, timeFormatter)
        val end = LocalTime.parse(endTime, timeFormatter)

        // Check if the time is within the range
        return (checkTime.isAfter(start) || checkTime.equals(start)) &&
                (checkTime.isBefore(end) || checkTime.equals(end))
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayOfWeek(): String {
        // Get the current date
        val currentDate = LocalDate.now()

        // Get the day of the week and format it to the full name
        return currentDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentTime(format: String): String {
        val currentTime = LocalTime.now()

        // Define the time format as HH:mm:ss
        val formatter = DateTimeFormatter.ofPattern(format)
        // Format the current time
        return currentTime.format(formatter)
    }


    fun getTimeZoneName(): String {
        val timeZone: TimeZone = TimeZone.getDefault()
        return timeZone.id
    }

    @SuppressLint("ServiceCast")
    fun getUserCountryFromTelephonyManager(context: Context): String? {
        val telephonyManager =
            context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.simCountryIso.takeIf { it.isNotEmpty() }
            ?: telephonyManager.networkCountryIso.takeIf { it.isNotEmpty() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDay(): String {
        return LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDayTiming(timings: List<SaloonWorkHour?>?): String {
        var timing = ""
        var day = getCurrentDay()
        if (timings != null) {
            for (hour in timings) {
                if (hour?.day == day) {
                    timing = "${hour.openingTime}-${hour.closingTime}"
                }
            }
        }
        return timing
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getWeekAndMonthDates(): Map<String, String> {
        val today = LocalDate.now()

        // Get start and end of the week (Assuming week starts on Monday)
        val startOfWeek = today.with(DayOfWeek.MONDAY)
        val endOfWeek = today.with(DayOfWeek.SUNDAY)

        // Get start and end of the month
        val startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth())
        val endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth())

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd") // Change format if needed

        return mapOf(
            "startOfWeek" to startOfWeek.format(formatter),
            "endOfWeek" to endOfWeek.format(formatter),
            "startOfMonth" to startOfMonth.format(formatter),
            "endOfMonth" to endOfMonth.format(formatter)
        )
    }
}