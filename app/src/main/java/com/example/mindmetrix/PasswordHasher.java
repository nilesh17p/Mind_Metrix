package com.example.mindmetrix;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordHasher {

    private static final int ITERATIONS = 120000;
    private static final int SALT_LENGTH = 16;
    private static final int KEY_LENGTH = 256;

    private static final String SHA256 =
            "PBKDF2WithHmacSHA256";

    private static final String SHA1 =
            "PBKDF2WithHmacSHA1";

    public static class HashResult {

        public final String hash;
        public final String salt;
        public final String algorithm;

        public HashResult(
                String hash,
                String salt,
                String algorithm) {

            this.hash = hash;
            this.salt = salt;
            this.algorithm = algorithm;
        }
    }

    // Generate a password hash with a unique random salt
    public static HashResult hashPassword(String password)
            throws Exception {

        byte[] saltBytes = new byte[SALT_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(saltBytes);

        String algorithm;

        // SHA-256 preferred; SHA-1 fallback for older Android devices.
        try {
            SecretKeyFactory.getInstance(SHA256);
            algorithm = SHA256;
        } catch (Exception e) {
            algorithm = SHA1;
        }

        byte[] hashBytes = generateHash(
                password,
                saltBytes,
                algorithm
        );

        String encodedHash = Base64.encodeToString(
                hashBytes,
                Base64.NO_WRAP
        );

        String encodedSalt = Base64.encodeToString(
                saltBytes,
                Base64.NO_WRAP
        );

        Arrays.fill(hashBytes, (byte) 0);

        return new HashResult(
                encodedHash,
                encodedSalt,
                algorithm
        );
    }

    // Verify a password during login
    public static boolean verifyPassword(
            String enteredPassword,
            String storedHash,
            String storedSalt,
            String storedAlgorithm) throws Exception {

        byte[] saltBytes = Base64.decode(
                storedSalt,
                Base64.NO_WRAP
        );

        byte[] expectedHash = Base64.decode(
                storedHash,
                Base64.NO_WRAP
        );

        byte[] actualHash = generateHash(
                enteredPassword,
                saltBytes,
                storedAlgorithm
        );

        boolean matched = MessageDigest.isEqual(
                expectedHash,
                actualHash
        );

        Arrays.fill(actualHash, (byte) 0);

        return matched;
    }

    // Internal PBKDF2 calculation
    private static byte[] generateHash(
            String password,
            byte[] salt,
            String algorithm) throws Exception {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(algorithm);

            return factory.generateSecret(spec).getEncoded();

        } finally {
            spec.clearPassword();
        }
    }
}
