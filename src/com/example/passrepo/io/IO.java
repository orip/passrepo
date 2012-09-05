package com.example.passrepo.io;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.model.Model;
import com.google.common.base.Charsets;
import com.google.gson.Gson;

public class IO {
    private static final Gson gson = new Gson();

    public static String modelToEncryptedString(Model model) {
        byte[] plainText = gson.toJson(model).getBytes(Charsets.UTF_8);
        CipherText cipherText = Encryption.encrypt(plainText, model.key);
        EncryptedFile encryptedFile = new EncryptedFile(model.scryptParameters, cipherText);
        return gson.toJson(encryptedFile);
    }
    
    public static Model modelFromEncryptedString(String encryptedString, byte[] key) {
        EncryptedFile encryptedFile = gson.fromJson(encryptedString, EncryptedFile.class);
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, key), Charsets.UTF_8);
        Model result = gson.fromJson(modelJson, Model.class);
        result.key = key;
        result.scryptParameters = encryptedFile.scryptParameters;
        return result;
    }
}
