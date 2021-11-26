package dev.jocey.testfirebase.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;

public class Model {
    private FirebaseStorage firebaseStorage;
    private FirebaseUser user;
    private FirebaseRemoteConfig remoteConfig;


    public Model() {
        firebaseStorage = FirebaseStorage.getInstance();

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


}
