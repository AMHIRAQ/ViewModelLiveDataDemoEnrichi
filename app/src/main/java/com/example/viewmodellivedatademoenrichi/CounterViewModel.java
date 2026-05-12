package com.example.viewmodellivedatademoenrichi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

public class CounterViewModel extends ViewModel {

    private static final String KEY_COUNT = "count";
    private final SavedStateHandle savedStateHandle;
    private final MutableLiveData<Integer> countLiveData;

    public CounterViewModel(SavedStateHandle handle) {
        this.savedStateHandle = handle;
        Integer saved = savedStateHandle.get(KEY_COUNT);
        countLiveData = new MutableLiveData<>(saved != null ? saved : 0);
    }

    // Exposé en lecture seule à l'Activity
    public LiveData<Integer> getCount() {
        return countLiveData;
    }

    // setValue() → thread principal uniquement
    public void increment() {
        Integer current = countLiveData.getValue();
        int newVal = (current != null ? current : 0) + 1;
        countLiveData.setValue(newVal);
        savedStateHandle.set(KEY_COUNT, newVal);
    }

    public void decrement() {
        Integer current = countLiveData.getValue();
        int newVal = (current != null ? current : 0) - 1;
        countLiveData.setValue(newVal);
        savedStateHandle.set(KEY_COUNT, newVal);
    }

    public void reset() {
        countLiveData.setValue(0);
        savedStateHandle.set(KEY_COUNT, 0);
    }

    // Bonus 1 : postValue() → safe depuis n'importe quel thread
    public void incrementFromBackground() {
        new Thread(() -> {
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            Integer current = countLiveData.getValue();
            int newVal = (current != null ? current : 0) + 1;
            countLiveData.postValue(newVal);
            savedStateHandle.set(KEY_COUNT, newVal);
        }).start();
    }
}