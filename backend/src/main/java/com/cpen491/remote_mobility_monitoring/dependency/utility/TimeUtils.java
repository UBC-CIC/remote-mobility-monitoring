package com.cpen491.remote_mobility_monitoring.dependency.utility;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public class TimeUtils {
    public static LocalDateTime getCurrentUtcTime() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static String getCurrentUtcTimeString() {
        return getCurrentUtcTime().toString();
    }

    public static LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time);
    }

    public static long secondsBetweenTimes(LocalDateTime time1, LocalDateTime time2) {
        return ChronoUnit.SECONDS.between(time1, time2);
    }
}
