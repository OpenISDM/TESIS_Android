package sinica.earth.tesis;

import java.util.Calendar;

public class DurationHelper {
    int year;
    int month;
    int day;

    public static DurationHelper getTo() {
        return new DurationHelper();
    }

    public static DurationHelper getFrom() {
        DurationHelper from = new DurationHelper();
        if (from.month <= 3) {
            from.year = from.year - 1;
            from.month = from.month + 9;
        } else {
            from.month = from.month - 3;
        }
        return from;
    }

    public DurationHelper() {
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH) + 1;
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    public DurationHelper(int year, int month, int day) {
        this.year = year;
        this.month = month + 1;
        this.day = day;
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String DatetoString() {
        String date = "";
        if (month < 10)
            date = year + "-0" + month;
        else
            date = year + "-" + month;
        if (day < 10)
            date = date + "-0" + day;
        else
            date = date + "-" + day;
        return date;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }
}
