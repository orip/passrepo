package com.example.passrepo.crypto;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Arrays;

import android.util.Base64;
import com.google.common.base.Charsets;
import com.lambdaworks.crypto.SCrypt;

public class PasswordHasher {
    public static class ScryptParameters {
        public ScryptParameters(byte[] salt) {
            saltBase64 = Base64.encode(salt, Base64.NO_WRAP);
        }

        /** computational difficulty. Need to test on older devices to see if we can increase this. */
        public final int n = 1024;
        /** memory difficulty. RAM needed should be log2(N) * P * R * 128 bytes */
        public final int r = 4;
        /** parallelization */
        public final int p = 1; // no parallelization

        public final byte[] saltBase64;
        public byte[] getSalt() {
            return Base64.decode(saltBase64, Base64.NO_WRAP);
        }
    }

    // TODO: can use salt to create a different key for HMAC
    private static final int KEY_LENGTH_BYTES = 32;

    public static class Keys {
        public final byte[] encryptionKey;
        public final byte[] hmacKey;

        public Keys(byte[] encryptionKey, byte[] hmacKey) {
            this.encryptionKey = encryptionKey;
            this.hmacKey = hmacKey;
        }
    }

    public static Keys hash(String password, ScryptParameters scryptParameters) {
        try {
            // can't use getBytes(Charset) in Android API 8
            final byte[] passwordBytes = password.getBytes(Charsets.UTF_8.name());

            // Generate enough bytes for 2 keys
            final byte[] generatedBytes = SCrypt.scrypt(passwordBytes, scryptParameters.getSalt(), scryptParameters.n, scryptParameters.r, scryptParameters.p, KEY_LENGTH_BYTES * 2);

            final byte[] encryptionKey = Arrays.copyOfRange(generatedBytes, 0, KEY_LENGTH_BYTES);
            final byte[] hmacKey = Arrays.copyOfRange(generatedBytes, KEY_LENGTH_BYTES, KEY_LENGTH_BYTES*2);
            return new Keys(encryptionKey, hmacKey);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
