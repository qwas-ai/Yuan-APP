package com.example.myapplication.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myapplication.model.Chapter;
import com.example.myapplication.repository.ChapterRepository;

import java.util.List;

public class ChapterViewModel extends AndroidViewModel {
    private final ChapterRepository repository;

    public ChapterViewModel(@NonNull Application application) {
        super(application);
        repository = new ChapterRepository(application);
    }

    public LiveData<List<Chapter>> getChaptersByNovel(int novelId) {
        return repository.getChaptersByNovel(novelId);
    }

    public LiveData<Chapter> getChapterById(int id) {
        return repository.getChapterById(id);
    }

    public void insert(Chapter chapter, ChapterRepository.OperationCallback cb) {
        repository.insert(chapter, cb);
    }

    public void update(Chapter chapter, ChapterRepository.OperationCallback cb) {
        repository.update(chapter, cb);
    }

    public void delete(Chapter chapter, ChapterRepository.OperationCallback cb) {
        repository.delete(chapter, cb);
    }
}