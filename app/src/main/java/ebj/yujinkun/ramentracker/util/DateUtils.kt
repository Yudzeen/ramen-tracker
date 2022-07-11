package ebj.yujinkun.ramentracker.util

import kotlin.jvm.JvmOverloads
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    const val DATE_FORMAT_DEFAULT = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    const val DATE_FORMAT_DATE_ONLY = "yyyy-MM-dd"
    const val DATE_FORMAT_DATE_PRETTY = "MMMM d, yyyy"
    const val DATE_FORMAT_DATE_AND_TIME_PRETTY = "MMMM d, yyyy  h:mm a"
    @JvmOverloads
    fun formatDate(year: Int, month: Int, day: Int, format: String? = DATE_FORMAT_DEFAULT): String {
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        return formatDate(calendar.timeInMillis, format)
    }

    @JvmOverloads
    fun formatDate(dateInMillis: Long, dateFormat: String? = DATE_FORMAT_DEFAULT): String {
        val date = Date(dateInMillis)
        val sdf = SimpleDateFormat(dateFormat, Locale.US)
        return sdf.format(date)
    }

    fun formatDate(date: String?, currentFormat: String?, newFormat: String?): String {
        return formatDate(getDate(date, currentFormat), newFormat)
    }

    fun getDate(date: String?, dateFormat: String?): Long {
        try {
            return Objects.requireNonNull(SimpleDateFormat(dateFormat, Locale.US).parse(date)).time
        } catch (e: ParseException) {
            Timber.e(e, "Unable to parse: %s", date)
        }
        return -1
    }

    fun getDate(date: String?): Long {
        return getDate(date, DATE_FORMAT_DEFAULT)
    }

    val currentDate: String
        get() = formatDate(currentTimeInMillis)
    val currentTimeInMillis: Long
        get() = Date().time
}