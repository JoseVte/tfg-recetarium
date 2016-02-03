package util;

import org.mindrot.jbcrypt.BCrypt;

import java.math.BigInteger;

public class Encryptation {
    private static final int SALT_SIZE = 24;

    /**
     * Returns a salted PBKDF2 hash of the string.
     *
     * @param str the string to hash
     *
     * @return a salted PBKDF2 hash of the string
     */
    public static String createHash(String str) {
        return encrypt(str);
    }

    /**
     * Returns a salted PBKDF2 hash of the string.
     *
     * @param str the string to hash
     *
     * @return a salted PBKDF2 hash of the string
     */
    public static String createHash(char[] str) {
        return encrypt(new String(str));
    }

    /**
     * Computes the PBKDF2 hash of a string.
     *
     * @param string     the string to hash.
     *
     * @return the PBDKF2 hash of the string
     */
    public static String encrypt(String secret) {
        return BCrypt.hashpw(secret, BCrypt.gensalt(SALT_SIZE));
    }

    /**
     * Check if the hashed secret the same
     *
     * @param secret string to check
     * @param secretHashed hashed string
     *
     * @return boolean
     */
    public static boolean check(String secret, String secretHashed) {
        return BCrypt.checkpw(secret, secretHashed);
    }

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param array the byte array to convert
     *
     * @return a length*2 character string encoding the byte array
     */
    public static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    /**
     * Converts a string of hexadecimal characters into a byte array.
     *
     * @param hex the hex string
     *
     * @return the hex string decoded into a byte array
     */
    public static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }

    /**
     * Compares two byte arrays in length-constant time. This comparison method is used so that password hashes cannot
     * be extracted from an on-line system using a timing attack and then attacked off-line.
     *
     * @param a the first byte array
     * @param b the second byte array
     *
     * @return true if both byte arrays are the same, false if not
     */
    public static boolean slowEquals(byte[] a, byte[] b) {
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; i++)
            diff |= a[i] ^ b[i];
        return diff == 0;
    }
}
