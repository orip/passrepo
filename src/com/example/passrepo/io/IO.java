package com.example.passrepo.io;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.google.common.base.Charsets;

public class IO {
    public static String modelToEncryptedString(Model model) {
        byte[] plainText = GsonHelper.customGson.toJson(model).getBytes(Charsets.UTF_8);
        CipherText cipherText = Encryption.encrypt(plainText, model.key);
        EncryptedFile encryptedFile = new EncryptedFile(model.scryptParameters, cipherText);
        return GsonHelper.customGson.toJson(encryptedFile);
    }
    
    public static Model modelFromEncryptedString(String encryptedString, byte[] key) {
        EncryptedFile encryptedFile = GsonHelper.customGson.fromJson(encryptedString, EncryptedFile.class);
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, key), Charsets.UTF_8);
        Model result = GsonHelper.customGson.fromJson(modelJson, Model.class);
        result.key = key;
        result.scryptParameters = encryptedFile.scryptParameters;
        return result;
    }
}
