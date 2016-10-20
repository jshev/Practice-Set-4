package ch.makery.address.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtil {
	// helper functions for handling dates
	
	// date pattern used for conversion
	private static final String DATE_PATTERN = "dd.MM.yyy";
	
	// date formatter
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	
	public static String format(LocalDate date) {
		// returns the given date as a formatted string
        if (date == null) {
            return null;
        }
        else {
        return DATE_FORMATTER.format(date);
        }
    }
	
	public static LocalDate parse(String dateString) {
		// converts a string into a formatted date
		// returns null if string could not be converted
        try {
            return DATE_FORMATTER.parse(dateString, LocalDate::from);
        }
        catch (DateTimeParseException e) {
            return null;
        }
    }
	
	public static boolean validDate(String dateString) {
        // checks the string to see whether or not it is a valid date
        return DateUtil.parse(dateString) != null;
    }

}
