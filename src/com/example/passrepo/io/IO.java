package com.example.passrepo.io;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import com.example.passrepo.Consts;
import com.example.passrepo.DecryptionFailedException;
import com.example.passrepo.PassRepoBaseSecurityException;
import com.example.passrepo.crypto.Encryption;
import com.example.passrepo.crypto.Encryption.CipherText;
import com.example.passrepo.crypto.PasswordHasher;
import com.example.passrepo.dummy.DummyContent;
import com.example.passrepo.model.Model;
import com.example.passrepo.util.GsonHelper;
import com.example.passrepo.util.Logger;
import com.google.common.base.Charsets;
import com.google.common.base.Stopwatch;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

import java.io.*;
import java.util.concurrent.TimeUnit;

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

    public static Model modelFromEncryptedString(String encryptedString, PasswordHasher.Keys keys) throws PassRepoBaseSecurityException {
        if (DEBUG_DO_NOT_ENCRYPT) {
            Model result = GsonHelper.customGson.fromJson(encryptedString, Model.class);
            result.populateIdsToPasswordEntriesMap();
            return result;
        }

        EncryptedFile encryptedFile = GsonHelper.customGson.fromJson(encryptedString, EncryptedFile.class);
        if (!encryptedFile.cipherText.hmacVerified(keys.hmacKey)) {
            throw new DecryptionFailedException("MAC doesn't match");
        }
        String modelJson = new String(Encryption.decrypt(encryptedFile.cipherText, keys.encryptionKey), Charsets.UTF_8);
        Model result = GsonHelper.customGson.fromJson(modelJson, Model.class);
        result.populateIdsToPasswordEntriesMap();   // TODO: For some reason isn't called by the GsonHelper. Temporary workaround..
        result.keys = keys;
        result.scryptParameters = encryptedFile.scryptParameters;
        return result;
    }

    public static void loadModelFromDisk(final Context context) {
        try {
            Logger.i("IO", "Loading model from disk");
            String fileContents = CharStreams.toString(new InputSupplier<InputStreamReader>() {
                public InputStreamReader getInput() throws IOException {
                    return new InputStreamReader(context.openFileInput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME), Charsets.UTF_8);
                }
            });

            Model.currentModel = IO.modelFromEncryptedString(fileContents, DummyContent.dummyKeys);
            Logger.i("IO", "Model loaded. Results are: " + fileContents);
        } catch (PassRepoBaseSecurityException e) {
            // TODO: proper UI flow for failed decryption instead of crashing
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            // Model doesn't exist on disk (probably first time install), use the dummy instead.
            Model.currentModel = DummyContent.model;
            Logger.i("IO", "Loaded model from dummy content (first time install)");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveModelToDisk(final Context context) {
        GlobalExecutors.BACKGROUND_PARALLEL_EXECUTOR.execute(new Runnable() {
            public void run() {
                try {
                    final Stopwatch stopwatch = new Stopwatch().start();
                    String fileContents = modelToEncryptedString(Model.currentModel);
                    final long elapsedMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                    // Using OutputSupplier<OutputStream> fails to compile for some reason, using OutputStreamWriter instead
                    CharStreams.write(fileContents, new OutputSupplier<OutputStreamWriter>() {
                        public OutputStreamWriter getOutput() throws IOException {
                            return new OutputStreamWriter(context.openFileOutput(Consts.PASS_REPO_LOCAL_DATABASE_FILENAME, Context.MODE_PRIVATE));
                        }
                    });
                    GlobalExecutors.MAIN_THREAD_EXECUTOR.execute(new Runnable() {
                        public void run() {
                            Toast.makeText(context, "Saved, encryption time=" + elapsedMs + "ms", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (IOException e) {
                    new AlertDialog.Builder(context).setTitle("Error writing file").setMessage(e.getMessage()).setCancelable(true).show();
                }
            }
        });
    }
}
