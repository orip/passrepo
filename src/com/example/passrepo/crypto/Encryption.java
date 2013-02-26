package com.example.passrepo.crypto;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.base.Preconditions;

public class Encryption {
    private static final int REQUIRED_KEY_LENGTH_IN_BITS = 256;
    public static class CipherText {
        public final byte[] bytes;
        public final byte[] iv;
        public final byte[] mac;
        public CipherText(byte[] bytes, byte[] iv, byte[] mac) {
            this.bytes = bytes;
            this.iv = iv;
            this.mac = mac;
        }
    }
    
    public static CipherText encrypt(byte[] plainText, PasswordHasher.Keys keys) {
        try {
            Preconditions.checkArgument(keys.encryptionKey.length == REQUIRED_KEY_LENGTH_IN_BITS / 8);
            Preconditions.checkArgument(keys.hmacKey.length == REQUIRED_KEY_LENGTH_IN_BITS / 8);
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keys.encryptionKey, "AES"));
            final byte[] cipherText = cipher.doFinal(plainText);
            final byte[] iv = cipher.getIV();
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            hmacSHA256.init(new SecretKeySpec(keys.hmacKey, "HmacSHA256"));
            hmacSHA256.update(iv);
            hmacSHA256.update(cipherText);
            final byte[] mac = hmacSHA256.doFinal();
            return new CipherText(cipherText, iv, mac);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decrypt(CipherText cipherText, byte[] key) {
        try {
            Preconditions.checkArgument(key.length == REQUIRED_KEY_LENGTH_IN_BITS / 8);
            Cipher cipher = Cipher.getInstance("AES/CTR/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(cipherText.iv));
            return cipher.doFinal(cipherText.bytes);
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }
}
