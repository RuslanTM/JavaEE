package ru.javacourse.blog.util;

import java.time.LocalDate;
import java.util.Date;

public class DateUtil {
    public static java.sql.Date utilToSQLDate(Date fromDate) {
        return new java.sql.Date(fromDate.getTime());
    }
}
