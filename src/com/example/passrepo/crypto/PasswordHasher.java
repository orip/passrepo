package com.example.passrepo.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

import com.google.common.base.Charsets;
import com.lambdaworks.crypto.SCrypt;

public class PasswordHasher {
    public static class ScryptParameters {
        /** computational difficulty. Need to test on older devices to see if we can increase this. */
        public final int n = 1024;
        /** memory difficulty. RAM needed should be log2(N) * P * R * 128 bytes */
        public final int r = 4;
        /** parallelization */
        public final int p = 1; // no parallelization
    }

    // Only encrypting, not verifying passwords. Salt is irrelevant
    // TODO: can use salt to create a different key for HMAC
    private static final byte[] SALT = new byte[] {};
    private static final int KEY_LENGTH_BYTES = 32;

    public static byte[] hash(String password, ScryptParameters scryptParameters) {
        try {
            // can't use getBytes(Charset) in Android API 8
            byte[] passwordBytes = password.getBytes(Charsets.UTF_8.name());
            return SCrypt.scrypt(passwordBytes, SALT, scryptParameters.n, scryptParameters.r, scryptParameters.p, KEY_LENGTH_BYTES);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
