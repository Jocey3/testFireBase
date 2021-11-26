package dev.jocey.testfirebase.ui;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import dev.jocey.testfirebase.R;
import dev.jocey.testfirebase.presenter.Presenter;

public class MainActivity extends AppCompatActivity implements ViewAct {
    private ProgressBar progressBar;
    private ImageView imageCat;
    private Button btnChangeCat;
    private Presenter presenter;
    private TextView tokenView;
    private TextView countriesView;
    private EditText email;
    private EditText pass;
    private Button btnLogin;
    private LinearLayout loginLayout;
    private View viewSq;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        presenter = new Presenter();
        btnChangeCat.setOnClickListener(view -> presenter.changePic());
        btnLogin.setOnClickListener(view -> {
            if (!isLoginFieldsEmpty()) {
                if (presenter.logIn(email.getText().toString(), pass.getText().toString())) {
                    loginLayout.setVisibility(View.GONE);
                    presenter.setToken();
                } else loginLayout.setVisibility(View.VISIBLE);
            } else Toast.makeText(this, "Input login date", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    public boolean isLoginFieldsEmpty() {
        return email.getText().toString().isEmpty() || pass.getText().toString().isEmpty();
    }

    public void initViews() {
        progressBar = findViewById(R.id.progress);
        imageCat = findViewById(R.id.image_cat);
        btnChangeCat = findViewById(R.id.btn_pic);
        tokenView = findViewById(R.id.put_token);
        countriesView = findViewById(R.id.put_countries);
        email = findViewById(R.id.get_email);
        pass = findViewById(R.id.get_pass);
        btnLogin = findViewById(R.id.btn_login);
        loginLayout = findViewById(R.id.login_linear);
        viewSq = findViewById(R.id.sq);
    }

    @Override
    public void putPic(String url) {
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageCat);
    }

    @Override
    public void setToken(String token) {
        tokenView.setText(token);
    }

    @Override
    public void setCountries(String countries) {
        countriesView.setText(countries);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSq(String color) {
        switch (color) {
            case "red":
                viewSq.setBackground(getDrawable(R.drawable.rect_red));
                viewSq.setVisibility(View.VISIBLE);
                break;
            case "green":
                viewSq.setBackground(getDrawable(R.drawable.rect_green));
                viewSq.setVisibility(View.VISIBLE);
                break;
            default:
                viewSq.setVisibility(View.GONE);
        }
    }


    @Override
    public void showProgressPic() {
        progressBar.setVisibility(View.VISIBLE);
        btnChangeCat.setEnabled(false);
    }

    @Override
    public void hideProgressPic() {
        progressBar.setVisibility(View.GONE);
        btnChangeCat.setEnabled(true);
    }
}
