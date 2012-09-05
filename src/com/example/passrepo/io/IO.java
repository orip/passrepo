package com.example.passrepo.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;

import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.gdrive.GoogleDriveUtil;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class IO {
    private static final String PASSWORD_DATABASE_FILENAME = "password_database.json";

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

    public static void loadModel(final Context context) {
        if (Model.currentModel == null) {
            Logger.i("IO", "loading model");

            try {                
                String fileContents = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                    public InputStreamReader getInput() throws IOException {
                        return new InputStreamReader(context.openFileInput(PASSWORD_DATABASE_FILENAME), Charsets.UTF_8);
                    }
                });
                
                GoogleDriveUtil gdu = new GoogleDriveUtil(context.getApplicationContext());
                if (gdu.isAuthorized()) {
                    fileContents = gdu.download();
                }
                Model.currentModel = IO.modelFromEncryptedString(fileContents, DummyContent.dummyKey);
                Logger.i("IO", "sucessfully loaded model from disk");

            } catch (IOException e) {
                Model.currentModel = DummyContent.model;
                Logger.i("IO", "loaded dummy model");
            }
        }
    }
    
    public static void saveModel(final Context context) {
        try {
            CharStreams.write(IO.modelToEncryptedString(Model.currentModel), new OutputSupplier<OutputStreamWriter>() {
                public OutputStreamWriter getOutput() throws IOException {
                    return new OutputStreamWriter(context.openFileOutput(PASSWORD_DATABASE_FILENAME, Context.MODE_PRIVATE));
                }
            });
            File f = new File(new File("/mnt/sdcard"), PASSWORD_DATABASE_FILENAME);
            Files.write(IO.modelToEncryptedString(Model.currentModel), f, Charsets.UTF_8);
            Logger.i("IO", "saved model to disk");
            new GoogleDriveUtil(context.getApplicationContext()).create(f);
            
            Logger.i("IO", "saved model to drive!!!");
        } catch (IOException e) {
            Logger.i("IO", "error saving model to disk");
            throw new RuntimeException(e);
        }
    }

}
