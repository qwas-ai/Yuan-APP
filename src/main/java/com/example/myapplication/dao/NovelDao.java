package com.example.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.Novel;

import java.util.List;

@Dao
public interface NovelDao {
    @Query("SELECT * FROM novels ORDER BY last_updated DESC")
    LiveData<List<Novel>> getAllNovels();
    
    @Query("SELECT * FROM novels WHERE id = :id")
    LiveData<Novel> getNovelById(int id);
    
    @Insert
    long insert(Novel novel);
    
    @Update
    void update(Novel novel);
    
    @Delete
    void delete(Novel novel);
    
    @Query("DELETE FROM novels WHERE id = :id")
    void deleteById(int id);

    // 更新小说字数和最后更新时间，依赖 SQLite LENGTH 统计章节内容长度
    @Query("UPDATE novels SET word_count = (SELECT IFNULL(SUM(LENGTH(content)),0) FROM chapters WHERE novel_id = :novelId), last_updated = :timestamp WHERE id = :novelId")
    void updateWordCount(int novelId, long timestamp);
}