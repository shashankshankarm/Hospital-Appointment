package com.hospitalappointment.common.util;

public final class NormalizationUtils {

    private NormalizationUtils() {
    }

    public static String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
