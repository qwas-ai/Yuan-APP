package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.dao.NovelDao;
import com.example.myapplication.database.NovelDatabase;
import com.example.myapplication.model.Novel;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NovelRepository {
    private NovelDao novelDao;
    private LiveData<List<Novel>> allNovels;
    private ExecutorService executorService;
    
    public NovelRepository(Application application) {
        NovelDatabase db = NovelDatabase.getDatabase(application);
        novelDao = db.novelDao();
        allNovels = novelDao.getAllNovels();
        executorService = Executors.newFixedThreadPool(2);
    }
    
    public LiveData<List<Novel>> getAllNovels() {
        return allNovels;
    }
    
    public LiveData<Novel> getNovelById(int id) {
        return novelDao.getNovelById(id);
    }
    
    public void insert(Novel novel, InsertCallback callback) {
        executorService.execute(() -> {
            long id = novelDao.insert(novel);
            if (callback != null) {
                callback.onInsertComplete(id);
            }
        });
    }
    
    public interface InsertCallback {
        void onInsertComplete(long id);
    }
    
    public void update(Novel novel, OperationCallback callback) {
        executorService.execute(() -> {
            novelDao.update(novel);
            if (callback != null) {
                callback.onOperationComplete();
            }
        });
    }

    public void delete(Novel novel, OperationCallback callback) {
        executorService.execute(() -> {
            novelDao.delete(novel);
            if (callback != null) {
                callback.onOperationComplete();
            }
        });
    }

    public interface OperationCallback {
        void onOperationComplete();
    }
    
    public void deleteById(int id) {
        executorService.execute(() -> novelDao.deleteById(id));
    }
}