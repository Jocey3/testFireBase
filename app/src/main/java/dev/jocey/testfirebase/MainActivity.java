package dev.jocey.testfirebase;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG = "myLog";
    private ImageView imageView;
    private EditText email;
    private EditText pass;
    private Button login;
    private Button changePic;
    private ProgressBar progressBar;
    private int numPic = 0;

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseAuth auth;
    FirebaseUser user;
    private List<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        auth = FirebaseAuth.getInstance();
        checkUser();
        login.setOnClickListener(view -> logining());
        changePic.setOnClickListener(view -> setPic());
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void checkUser() {
        user = auth.getCurrentUser();

        if (user != null) {
            getPics();
            login.setEnabled(false);
            changePic.setEnabled(true);
        } else {
            login.setEnabled(true);
            changePic.setEnabled(false);
        }
    }

    public void logining() {
        user = auth.getCurrentUser();
        if (user != null) {
            getPics();
            changePic.setEnabled(true);
            login.setEnabled(false);

        } else {
            createUser();
        }
    }

    public void createUser() {
        auth.createUserWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG, "Great");
                            user = auth.getCurrentUser();
                        } else {
                            Log.d(LOG, "Fuck");
                        }

                    }
                });
    }

    public void getPics() {
        firebaseStorage.getReference().child("cats").listAll()
                .addOnSuccessListener(listResult -> {

                    for (StorageReference some : listResult.getItems()) {
                        progressBar.setVisibility(View.VISIBLE);
                        Log.d("myLog", some.getPath() + "  " + Thread.currentThread().getName());

                        list.add(some.getPath());
                    }

                    Log.d("myLog", list.toString());
                }).addOnFailureListener(runnable -> Log.d("myLog", runnable.toString()));

    }

    public void setPic() {
        if (numPic >= 3) numPic = 0;
        firebaseStorage.getReference(list.get(numPic++)).getBytes(1024 * 1024)
                .addOnSuccessListener(bytes -> {
                    imageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> Log.d(LOG, e.toString()));
    }

    public void init() {
        imageView = findViewById(R.id.imafe_v);
        progressBar = findViewById(R.id.progress_bar);
        email = findViewById(R.id.ed_email);
        pass = findViewById(R.id.ed_pass);
        login = findViewById(R.id.btn_login);
        changePic = findViewById(R.id.btn_change_pic);
    }


}