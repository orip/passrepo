package com.example.passrepo.io;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher.ScryptParameters;
import com.example.passrepo.model.Model;
import com.google.common.base.Charsets;
import com.google.gson.Gson;

public class IO {
    private static final Gson gson = new Gson();

    String modelToEncryptedString(Model model, ScryptParameters scryptParameters) {
        byte[] plainText = gson.toJson(model).getBytes(Charsets.UTF_8);
        CipherText cipherText = Encryption.encrypt(plainText, model.key);
        EncryptedFile encryptedFile = new EncryptedFile(scryptParameters, cipherText);
        return gson.toJson(encryptedFile);
    }
    
    Model modelFromEncryptedString(String encryptedString, byte[] key) {
        EncryptedFile encryptedFile = gson.fromJson(encryptedString, EncryptedFile.class);
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, key), Charsets.UTF_8);
        Model result = gson.fromJson(modelJson, Model.class);
        result.key = key;
        return result;
    }
}
