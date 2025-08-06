package com.example.myapplication.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myapplication.model.Chapter;

import java.util.List;

@Dao
public interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE novel_id = :novelId ORDER BY order_index ASC")
    LiveData<List<Chapter>> getChaptersByNovel(int novelId);

    @Query("SELECT * FROM chapters WHERE id = :id")
    LiveData<Chapter> getChapterById(int id);

    @Insert
    long insert(Chapter chapter);

    @Update
    void update(Chapter chapter);

    @Delete
    void delete(Chapter chapter);

    @Query("DELETE FROM chapters WHERE id = :id")
    void deleteById(int id);
}