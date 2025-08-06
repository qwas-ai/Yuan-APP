package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.Novel;
import com.example.myapplication.repository.NovelRepository;

import java.util.List;

public class NovelViewModel extends AndroidViewModel {
    private NovelRepository repository;
    private LiveData<List<Novel>> allNovels;
    
    public NovelViewModel(@NonNull Application application) {
        super(application);
        repository = new NovelRepository(application);
        allNovels = repository.getAllNovels();
    }
    
    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }
    
    public LiveData<Novel> getNovelById(int id) {
        return repository.getNovelById(id);
    }
    
    public void insert(Novel novel, NovelRepository.InsertCallback callback) {
        repository.insert(novel, callback);
    }
    
    public void update(Novel novel, NovelRepository.OperationCallback callback) {
        repository.update(novel, callback);
    }

    public void delete(Novel novel, NovelRepository.OperationCallback callback) {
        repository.delete(novel, callback);
    }
    
    public void deleteById(int id) {
        repository.deleteById(id);
    }
}