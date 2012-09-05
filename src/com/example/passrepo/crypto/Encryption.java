package com.example.passrepo.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Preconditions;

public class Encryption {
    private static final int REQURIED_KEY_LENGTH_IN_BITS = 256;

    public static byte[] encrypt(byte[] plainText, byte[] key) {
        return applyCipher(plainText, key, Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] cipherText, byte[] key) {
        return applyCipher(cipherText, key, Cipher.DECRYPT_MODE);
    }

    private static byte[] applyCipher(byte[] input, byte[] key, int mode) {
        try {
            Preconditions.checkArgument(key.length == REQURIED_KEY_LENGTH_IN_BITS / 8);
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(mode, new SecretKeySpec(key, "AES"));
            return cipher.doFinal(input);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
