package com.example.myapplication.ui.registeredevents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisteredEventViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public RegisteredEventViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}