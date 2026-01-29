package com.teachera.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static String requireNonEmpty(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new ServiceException(message);
        }
        return value.trim();
    }

    public static void requireNotNull(Object obj, String message) {
        if (obj == null)
            throw new ServiceException(message);
    }

    public static BigDecimal parseNonNegativeBigDecimal(String text, String fieldName) {
        if (text == null || text.trim().isEmpty())
            return null;
        try {
            BigDecimal v = new BigDecimal(text.trim());
            if (v.compareTo(BigDecimal.ZERO) < 0) {
                throw new ServiceException(fieldName + " must be zero or positive.");
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new ServiceException("Invalid numeric value for " + fieldName + ": " + text);
        }
    }

    public static void requireDateRange(LocalDate start, LocalDate end, String message) {
        if (start == null || end == null)
            throw new ServiceException(message);
        if (end.isBefore(start))
            throw new ServiceException("End date cannot be before start date.");
    }

    public static int requireMonth(int month) {
        if (month < 1 || month > 12)
            throw new ServiceException("Invalid month: " + month);
        return month;
    }
}
