/**
 * 
 */
package com.rayzr522.stafflogin.util;

import java.math.BigInteger;
import java.util.Objects;
import java.util.Random;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Various encryption code gathered from The Internet and optimized by Rayzr
 * 
 * @author Rayzr & THE INTERWEBZ
 *
 */
public class Encrypter {

    public static String randomString(int len) {
        Random random = new Random();
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (random.nextInt(96) + 32);
        }
        return new String(bytes);
    }

    /**
     * Encrypts a bit of text with the given salt, iterations, and key length
     * 
     * @param text The text to encrypt
     * @param salt The salt
     * @param iterations The number of iterations
     * @param keyLength The length of the encrypted key
     * @return The encrypted String
     */
    public static String apply(String text, byte[] salt, int iterations, int keyLength) {
        try {
            PBEKeySpec spec = new PBEKeySpec(text.toCharArray(), salt, iterations, keyLength);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return iterations + ":" + toHex(salt) + ":" + toHex(hash);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks a bit of text against some encrypted content.
     * 
     * @param text The text to encrypt for comparison
     * @param encrypted The already-encrypted text to compare to
     * @return
     */
    public static boolean check(String text, String encrypted) {
        Objects.requireNonNull(text, "text cannot be null!");
        Objects.requireNonNull(encrypted, "encrypted cannot be null!");
        String[] split = encrypted.split(":");
        int iterations = Integer.parseInt(split[0]);
        byte[] salt = fromHex(split[1]);
        byte[] hash = fromHex(split[2]);

        return apply(text, salt, iterations, hash.length * 8).equals(encrypted);
    }

    private static String toHex(byte[] array) {
        String hex = new BigInteger(1, array).toString(16);
        if (hex.length() % 2 == 1) {
            return "0" + hex;
        } else {
            return hex;
        }
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}
