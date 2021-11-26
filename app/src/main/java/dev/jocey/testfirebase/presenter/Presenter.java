package dev.jocey.testfirebase.presenter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.jocey.testfirebase.model.Model;
import dev.jocey.testfirebase.ui.ViewM;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Presenter {
    private Model model;
    private ViewM viewM;
    private List<String> pics = new ArrayList<>();
    private int picCounter;

    public Presenter(ViewM viewM) {
        model = new Model();
        this.viewM = viewM;
        getPics();


    }


    private void getPics() {
        model.getPics().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> viewM.showProgress())
                .doAfterTerminate(() -> viewM.hideProgress())
                .subscribe(urls -> {
                    pics = urls;
                    picCounter = 0;
                    viewM.putPic(pics.get(0));
                }, throwable -> Log.d("myLog", throwable.toString()));
    }

    public void changePic() {
        if (picCounter >= pics.size()) picCounter = 0;
        viewM.putPic(pics.get(picCounter++));

    }

}

