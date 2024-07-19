package store.msolapps.flamingo.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object LocalTime {
    fun convertToTime(gmtTime: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")

        val outputFormat = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo") // Set your country's time zone

        try {
            val date = inputFormat.parse(gmtTime)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun convertToDate(gmtTime: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")

        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        outputFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo") // Set your country's time zone

        try {
            val date = inputFormat.parse(gmtTime)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun convertToFullDate(gmtTime: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")

        val outputFormat = SimpleDateFormat("dd MMM, yyyy hh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo") // Set your country's time zone

        try {
            val date = inputFormat.parse(gmtTime)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun convertToFullDate2(gmtTime: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")

        val outputFormat = SimpleDateFormat("dd MMM, yyyy\nhh:mm a", Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo") // Set your country's time zone

        try {
            val date = inputFormat.parse(gmtTime)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun convertDateString(inputDate: String, fm: String): String? {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.ENGLISH)
        inputFormat.timeZone = TimeZone.getTimeZone("GMT")

        val outputFormat = SimpleDateFormat(fm, Locale.getDefault())
        outputFormat.timeZone = TimeZone.getTimeZone("Africa/Cairo") // Set your country's time zone

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun convertDateAndDayFormat(inputDate: String): String? {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM (hh:mm a)", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }


    fun convertSlotTimeDate(inputDate: String): String? {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, d MMMM\n", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun convertDate(inputDate: String): String? {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputDate)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun convertTo12HourFormat(time24Hour: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val date = inputFormat.parse(time24Hour)
        return outputFormat.format(date)
    }

}