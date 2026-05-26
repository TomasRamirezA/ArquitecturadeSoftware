package edu.eci.arsw.parallelism.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PiDigitsTest {

    @Test
    void testGetDigitsValidInput() {
        byte[] digits = PiDigits.getDigits(0, 5);
        assertNotNull(digits);
        assertEquals(5, digits.length);
        for (byte d : digits) {
            assertTrue(d >= 0 && d <= 15);
        }
    }

    @Test
    void testGetDigitsHexValidInput() {
        String hex = PiDigits.getDigitsHex(0, 5);
        assertNotNull(hex);
        assertEquals(5, hex.length());
        assertTrue(hex.matches("[0-9A-F]+"));
    }

    @Test
    void testGetDigitsNegativeStart() {
        assertThrows(IllegalArgumentException.class, () -> PiDigits.getDigits(-1, 5));
    }

    @Test
    void testGetDigitsNegativeCount() {
        assertThrows(IllegalArgumentException.class, () -> PiDigits.getDigits(0, -1));
    }

    @Test
    void testGetDigitsZeroCount() {
        byte[] digits = PiDigits.getDigits(0, 0);
        assertNotNull(digits);
        assertEquals(0, digits.length);
    }

    @Test
    void testGetDigitsLargeStart() {
        byte[] digits = PiDigits.getDigits(100, 5);
        assertNotNull(digits);
        assertEquals(5, digits.length);
    }
}