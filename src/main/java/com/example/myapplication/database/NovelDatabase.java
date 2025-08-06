package com.example.myapplication.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myapplication.dao.NovelDao;
import com.example.myapplication.model.Novel;
import com.example.myapplication.model.Chapter;

@Database(entities = {Novel.class, Chapter.class}, version = 3, exportSchema = false)
public abstract class NovelDatabase extends RoomDatabase {
    public abstract NovelDao novelDao();
    public abstract com.example.myapplication.dao.ChapterDao chapterDao();
    
    private static volatile NovelDatabase INSTANCE;
    
    public static NovelDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NovelDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NovelDatabase.class, "novel_database")
                            .fallbackToDestructiveMigration() // 添加迁移策略
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}