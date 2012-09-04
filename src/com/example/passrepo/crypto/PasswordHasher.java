package com.example.passrepo.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import com.google.common.base.Charsets;
import com.lambdaworks.crypto.SCrypt;

public class PasswordHasher {
    // Only encrypting, not verifying passwords. Salt is irrelevant
    private static final byte[] SALT = new byte[] {};
    private static final int KEY_LENGTH_BYTES = 32;

    /** scrypt's 'N' computational difficulty parameter. Need to test on older devices to see if we can increase this. */
    private static final int SCRYPT_N = 1024;

    /** script's 'R' memory difficulty parameter. RAM needed should be log2(N) * P * R * 128 bytes */
    private static final int SCRYPT_R = 4;

    /** scrypt's 'P' parallelization parameter. */
    private static final int SCRYPT_P = 1; // no parallelization

    public static byte[] hash(String password) {
        try {
            // can't use getBytes(Charset) in Android API 8
            byte[] passwordBytes = password.getBytes(Charsets.UTF_8.name());
            return SCrypt.scrypt(passwordBytes, SALT, SCRYPT_N, SCRYPT_R, SCRYPT_P, KEY_LENGTH_BYTES);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
