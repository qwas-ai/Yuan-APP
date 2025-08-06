package com.example.myapplication.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.myapplication.dao.ChapterDao;
import com.example.myapplication.dao.NovelDao;
import com.example.myapplication.database.NovelDatabase;
import com.example.myapplication.model.Chapter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChapterRepository {
    private final ChapterDao chapterDao;
    private final NovelDao novelDao;
    private final ExecutorService executor;

    public ChapterRepository(Application application) {
        NovelDatabase db = NovelDatabase.getDatabase(application);
        chapterDao = db.chapterDao();
        novelDao = db.novelDao();
        executor = Executors.newFixedThreadPool(2);
    }

    public LiveData<List<Chapter>> getChaptersByNovel(int novelId) {
        return chapterDao.getChaptersByNovel(novelId);
    }

    public LiveData<Chapter> getChapterById(int id) {
        return chapterDao.getChapterById(id);
    }

    public void insert(Chapter chapter, OperationCallback callback) {
        executor.execute(() -> {
            chapterDao.insert(chapter);
            updateWordCount(chapter.getNovelId());
            if (callback != null) callback.onComplete();
        });
    }

    public void update(Chapter chapter, OperationCallback callback) {
        executor.execute(() -> {
            chapterDao.update(chapter);
            updateWordCount(chapter.getNovelId());
            if (callback != null) callback.onComplete();
        });
    }

    public void delete(Chapter chapter, OperationCallback callback) {
        executor.execute(() -> {
            chapterDao.delete(chapter);
            updateWordCount(chapter.getNovelId());
            if (callback != null) callback.onComplete();
        });
    }

    private void updateWordCount(int novelId) {
        novelDao.updateWordCount(novelId, System.currentTimeMillis());
    }

    public interface OperationCallback {
        void onComplete();
    }
}