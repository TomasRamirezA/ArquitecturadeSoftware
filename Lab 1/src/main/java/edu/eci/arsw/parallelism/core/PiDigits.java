
package edu.eci.arsw.parallelism.core;

/**
 * Bailey–Borwein–Plouffe (BBP) formula implementation to compute hexadecimal
 * digits of Pi.
 *
 * Digits returned are the hexadecimal digits of Pi after the radix point (Pi =
 * 3.<digits...> in base 16).
 *
 * This is the same algorithm family used in the classic ARSW exercise, adapted
 * to this project structure.
 */
public class PiDigits {

    private static final int DIGITS_PER_SUM = 8;
    private static final double EPSILON = 1e-17;

    /**
     * Returns a range of hexadecimal digits of Pi (after the radix point).
     *
     * @param start 0-based position after the radix point.
     * @param count number of digits to return.
     * @return array of digits, each value 0..15.
     */
    public static byte[] getDigits(int start, int count) {
        if (start < 0 || count < 0) {
            throw new IllegalArgumentException("Invalid interval: start and count must be non-negative");
        }

        byte[] digits = new byte[count];
        double sum = 0.0;

        for (int i = 0; i < count; i++) {
            if (i % DIGITS_PER_SUM == 0) {
                sum = 4 * sum(1, start)
                        - 2 * sum(4, start)
                        - sum(5, start)
                        - sum(6, start);
                start += DIGITS_PER_SUM;
            }

            sum = 16 * (sum - Math.floor(sum));
            digits[i] = (byte) sum; // 0..15
        }

        return digits;
    }

    /**
     * Convenience method: returns the digits as an uppercase hex string (0-9A-F).
     */
    public static String getDigitsHex(int start, int count) {
        byte[] digits = getDigits(start, count);
        StringBuilder sb = new StringBuilder(digits.length);
        for (byte d : digits) {
            int v = d & 0xFF;
            if (v < 0 || v > 15) {
                throw new IllegalStateException("Unexpected digit value: " + v);
            }
            sb.append(Character.toUpperCase(Character.forDigit(v, 16)));
        }
        return sb.toString();
    }

    /**
     * Returns the sum of 16^(n-k)/(8k+m) from k=0 to infinity (until terms are
     * below EPSILON).
     */
    private static double sum(int m, int n) {
        double sum = 0.0;
        int d = m;
        int power = n;

        while (true) {
            double term;

            if (power > 0) {
                term = (double) hexExponentModulo(power, d) / d;
            } else {
                term = Math.pow(16, power) / d;
                if (term < EPSILON) {
                    break;
                }
            }

            sum += term;
            power--;
            d += 8;
        }

        return sum;
    }

    /**
     * Return 16^p mod m.
     */
    private static int hexExponentModulo(int p, int m) {
        int power = 1;
        while (power * 2 <= p) {
            power *= 2;
        }

        int result = 1;

        while (power > 0) {
            if (p >= power) {
                result *= 16;
                result %= m;
                p -= power;
            }

            power /= 2;

            if (power > 0) {
                result *= result;
                result %= m;
            }
        }

        return result;
    }
}
