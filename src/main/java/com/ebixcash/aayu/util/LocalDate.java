package com.ebixcash.aayu.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author parag.bandekar
 *
 */
public class LocalDate {

    private LocalDate() {
        super();
    }

    /**
     * generateLocalDateTime
     * 
     * @return formatDateTime
     */
    public static String generateLocalDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return now.format(format);
    }

    //

    public static Timestamp convertNowToTimeStamp() {
        LocalDateTime now = LocalDateTime.now();
        return Timestamp.valueOf(now);

    }

}