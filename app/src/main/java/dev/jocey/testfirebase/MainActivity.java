package dev.jocey.testfirebase;

import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigServerException;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String LOG = "myLog";
    private ImageView imageView;
    private EditText email;
    private EditText pass;
    private TextView eDtoken;
    private View sq;

    private Button login;
    private Button changePic;
    private ProgressBar progressBar;
    private int numPic = 0;
    private String token = "";

    private FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
    private FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
    private FirebaseAuth auth;
    private FirebaseUser user;
    private List<String> list = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        auth = FirebaseAuth.getInstance();
        checkUser();
        login.setOnClickListener(view -> logining());
        changePic.setOnClickListener(view -> {
            setPic();
            forSq();
        });
        getToken();

        rem();
        config();
        Log.d(LOG, "JSON " + json);
        forSq();


    }

    private String json = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void forSq() {
        updateRemConf();
        String col = remoteConfig.getString("color");
        Log.d(LOG, "color " + col);
        switch (col) {
            case "red":
                sq.setVisibility(View.VISIBLE);
                sq.setBackground(getDrawable(R.drawable.red_s));
                break;
            case "green":
                sq.setVisibility(View.VISIBLE);
                sq.setBackground(getDrawable(R.drawable.green_s));
                break;
            default:
                sq.setVisibility(View.GONE);

        }
    }

    public void updateRemConf() {
        rem();
    }

    public void config() {

        json = remoteConfig.getValue("countries").asString();
    }

    FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(12)
            .build();


    public void rem() {
        remoteConfig.setConfigSettingsAsync(configSettings);
        remoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(LOG, "rem " + task.getResult().toString());
            } else {

                Log.d(LOG, "rem not get");
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        token = task.getResult();
                        Log.d(LOG, token);
                        eDtoken.setText(token);
                    } else {
                        Log.d(LOG, "TOKEN " + String.valueOf(task.getException()));
                    }
                });
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
        eDtoken = findViewById(R.id.token);
        login = findViewById(R.id.btn_login);
        changePic = findViewById(R.id.btn_change_pic);
        sq = findViewById(R.id.col_sq);
    }


}