package com.example.myapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "chapters",
        foreignKeys = @ForeignKey(entity = Novel.class,
                parentColumns = "id",
                childColumns = "novel_id",
                onDelete = ForeignKey.CASCADE))
public class Chapter {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "novel_id", index = true)
    private int novelId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "order_index")
    private int orderIndex; // 用于排序

    @ColumnInfo(name = "last_updated")
    private long lastUpdated;

    public Chapter() {}

    @Ignore
    public Chapter(int novelId, String title, String content, int orderIndex) {
        this.novelId = novelId;
        this.title = title;
        this.content = content;
        this.orderIndex = orderIndex;
        this.lastUpdated = System.currentTimeMillis();
    }

    // getters & setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getNovelId() { return novelId; }
    public void setNovelId(int novelId) { this.novelId = novelId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}