package com.example.passrepo.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Preconditions;

public class Encryption {
    private static final int REQURIED_KEY_LENGTH_IN_BITS = 256;
    public static class CipherText {
        public final byte[] bytes;
        public final byte[] iv;
        public CipherText(byte[] bytes, byte[] iv) {
            this.bytes = bytes;
            this.iv = iv;
        }
    }

    public static CipherText encrypt(byte[] plainText, byte[] key) {
        try {
            Preconditions.checkArgument(key.length == REQURIED_KEY_LENGTH_IN_BITS / 8);
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"));
            byte[] cipherText = cipher.doFinal(plainText);
            return new CipherText(cipherText, cipher.getIV());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(CipherText cipherText, byte[] key) {
        try {
            Preconditions.checkArgument(key.length == REQURIED_KEY_LENGTH_IN_BITS / 8);
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(cipherText.iv));
            return cipher.doFinal(cipherText.bytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
