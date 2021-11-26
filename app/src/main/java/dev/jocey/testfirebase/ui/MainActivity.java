package dev.jocey.testfirebase.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import dev.jocey.testfirebase.R;
import dev.jocey.testfirebase.presenter.Presenter;

public class MainActivity extends AppCompatActivity implements ViewM {
    private ProgressBar progressBar;
    private ImageView imageCat;
    private Button btnChangeCat;
    private Presenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        presenter = new Presenter(this);
        btnChangeCat.setOnClickListener(view -> presenter.changePic());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void initViews() {
        progressBar = findViewById(R.id.progress);
        imageCat = findViewById(R.id.image_cat);
        btnChangeCat = findViewById(R.id.btn_pic);
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
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
        btnChangeCat.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        btnChangeCat.setEnabled(true);
    }
}
