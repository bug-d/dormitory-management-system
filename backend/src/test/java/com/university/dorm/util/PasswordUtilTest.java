package com.university.dorm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    private final PasswordUtil passwordUtil = new PasswordUtil();

    @Test
    void shouldEncodeAndMatchPassword() {
        String encoded = passwordUtil.encode("SafePassword123");

        assertNotEquals("SafePassword123", encoded);
        assertTrue(passwordUtil.matches("SafePassword123", encoded));
        assertFalse(passwordUtil.matches("wrong-password", encoded));
    }

    @Test
    void shouldGeneratePasswordWithRequestedLength() {
        String password = passwordUtil.generateStrongPassword(16);

        assertEquals(16, password.length());
        assertTrue(password.matches("[A-Za-z0-9]+"));
    }
}

