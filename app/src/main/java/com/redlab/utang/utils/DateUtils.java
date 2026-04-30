package com.redlab.utang.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String ISO_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DISPLAY_FORMAT = "MMM dd, yyyy hh:mm a";
    private static final String DATE_ONLY_FORMAT = "MMM dd, yyyy";

    public static String getCurrentDateTimeISO() {
        SimpleDateFormat sdf = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
        return sdf.format(new Date());
    }

    public static String formatForDisplay(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "—";
        try {
            SimpleDateFormat parser = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            SimpleDateFormat formatter = new SimpleDateFormat(DISPLAY_FORMAT, Locale.getDefault());
            Date date = parser.parse(isoDate);
            return date != null ? formatter.format(date) : isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }

    public static String formatDateOnly(String isoDate) {
        if (isoDate == null || isoDate.isEmpty()) return "—";
        try {
            SimpleDateFormat parser = new SimpleDateFormat(ISO_FORMAT, Locale.getDefault());
            SimpleDateFormat formatter = new SimpleDateFormat(DATE_ONLY_FORMAT, Locale.getDefault());
            Date date = parser.parse(isoDate);
            return date != null ? formatter.format(date) : isoDate;
        } catch (Exception e) {
            return isoDate;
        }
    }
}
