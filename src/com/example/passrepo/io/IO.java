package com.example.passrepo.io;

import android.content.Context;
import com.example.passrepo.Consts;
import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class IO {
    
    private final static boolean DEBUG_DO_NOT_ENCRYPT = false;
    
    public static String modelToEncryptedString(Model model) {
        if (DEBUG_DO_NOT_ENCRYPT) {
            return GsonHelper.customGson.toJson(model);
        }
        
        byte[] plainText = GsonHelper.customGson.toJson(model).getBytes(Charsets.UTF_8);
        CipherText cipherText = Encryption.encrypt(plainText, model.keys);
        EncryptedFile encryptedFile = new EncryptedFile(model.scryptParameters, cipherText);
        return GsonHelper.customGson.toJson(encryptedFile);
    }

    public static Model modelFromEncryptedString(String encryptedString, PasswordHasher.Keys keys) {
        if (DEBUG_DO_NOT_ENCRYPT) {
            Model result = GsonHelper.customGson.fromJson(encryptedString, Model.class);
            result.populateIdsToPasswordEntriesMap();
            return result;
        }

        // TODO: verify MAC

        EncryptedFile encryptedFile = GsonHelper.customGson.fromJson(encryptedString, EncryptedFile.class);
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, keys.encryptionKey), Charsets.UTF_8);
        Model result = GsonHelper.customGson.fromJson(modelJson, Model.class);
        result.populateIdsToPasswordEntriesMap();   // TODO: For some reason isn't called by the GsonHelper. Temporary workaround..
        result.keys = keys;
        result.scryptParameters = encryptedFile.scryptParameters;
        return result;
    }

    public static void loadModelFromDisk(final Context context) {
        try {
            Logger.i("IO", "lading model from disk..");
            String fileContents = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                public InputStreamReader getInput() throws IOException {
                    return new InputStreamReader(context.openFileInput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME), Charsets.UTF_8);
                }
            });
            
            Model.currentModel = IO.modelFromEncryptedString(fileContents, DummyContent.dummyKeys);
            Logger.i("IO", "sucessfully loaded model from disk. Results are: " + fileContents);
            
        } catch (FileNotFoundException e) {
            // Model doesn't exist on disk (probably first time install), use the dummy instead.
            Model.currentModel = DummyContent.model;
            Logger.i("IO", "sucessfully loaded model from dummy content (first time install)");
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
