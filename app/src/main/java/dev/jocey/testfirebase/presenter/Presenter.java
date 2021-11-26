package dev.jocey.testfirebase.presenter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dev.jocey.testfirebase.model.Model;
import dev.jocey.testfirebase.ui.ViewAct;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class Presenter {
    private final Model model = new Model();
    private ViewAct view;
    private CompositeDisposable compositeDisposable;
    private List<String> pics = new ArrayList<>();
    private int picCounter;

    public void onResume(ViewAct view) {
        this.view = view;
        compositeDisposable = new CompositeDisposable();
        getPics();
        setCountries();
        setSq();
    }

    public void onPause() {
        if (!compositeDisposable.isDisposed()) compositeDisposable.dispose();
        view = null;
    }


    public boolean logIn(String email, String pass) {
        model.logIn(email, pass);
        return model.isUserLogined();
    }

    public void onDestroy() {
        model.signOut();
    }

    private void getPics() {
        compositeDisposable.add(
                model.getPics().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(disposable -> view.showProgressPic())
                        .doAfterTerminate(() -> view.hideProgressPic())
                        .subscribe(urls -> {
                            pics = urls;
                            picCounter = 0;
                            view.putPic(pics.get(picCounter++));
                        }, throwable -> Log.d("myLog", throwable.toString())));
    }

    public void changePic() {
        if (picCounter >= pics.size()) picCounter = 0;
        view.putPic(pics.get(picCounter++));

    }

    public void setToken() {
        compositeDisposable.add(
                model.getToken()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(view::setToken, Throwable::printStackTrace));
    }

    private void setCountries() {
        view.setCountries(model.getCountriesFromRC());
    }

    private void setSq() {
        view.setSq(model.getSq());
    }

}

