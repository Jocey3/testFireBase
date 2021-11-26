package dev.jocey.testfirebase.model;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class Model {
    private final FirebaseStorage firebaseStorage;
    private final FirebaseMessaging firebaseMessaging;
    private FirebaseRemoteConfig remoteConfig;
    private final FirebaseAuth auth;
    private FirebaseUser user;

    public Model() {
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseMessaging = FirebaseMessaging.getInstance();
        auth = FirebaseAuth.getInstance();

        getRemoteConfig();
    }

    public boolean isUserLogined() {
        user = auth.getCurrentUser();
        return user != null;
    }


    public void logIn(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("myLog", "signInWithEmail:success");
                        user = auth.getCurrentUser();
                    } else {
                        Log.w("myLog", "signInWithEmail:failure", task.getException());
                        signUp(email, pass);
                    }
                });
    }

    private void signUp(String email, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        Log.d("myLog", "Success creating user");
                    } else {
                        Log.d("myLog", "Failed creating user");
                    }

                });
    }

    private void getRemoteConfig() {
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(60)
                .build();
        remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("myLog", "Remote config fetch and activate success");
            } else {
                Log.d("myLog", "Remote config not get");
            }
        });
    }

    public String getCountriesFromRC() {
        String cou = remoteConfig.getValue("countries").asString();
        Log.d("myLog", "Countries " + cou);
        return cou;
    }

    public String getSq() {
        String col = remoteConfig.getValue("color").asString();
        Log.d("myLog", "Countries " + col);
        return col;
    }

    public Single<List<String>> getPics() {
        return Single.fromCallable(() -> {
            List<String> pics = new ArrayList<>();
            ListResult cats = Tasks.await(firebaseStorage.getReference().child("cats").listAll());
            for (StorageReference some : cats.getItems()) {
                pics.add(Tasks.await(some.getDownloadUrl()).toString());
            }
            return pics;
        });
    }

    private String token = "";

    public Single<String> getToken() {
        return Single.fromCallable(() -> {
            token = Tasks.await(firebaseMessaging.getToken());
            Log.d("myLog", "Curent token: " + token);

            return token;
        });
    }

    public void signOut() {
        if (isUserLogined()) auth.signOut();
    }
}



