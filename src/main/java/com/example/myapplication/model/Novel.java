package com.example.myapplication.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "novels")
public class Novel {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    @ColumnInfo(name = "title")
    private String title;
    
    @ColumnInfo(name = "description")
    private String description;
    
    @ColumnInfo(name = "content")
    private String content;
    
    @ColumnInfo(name = "cover_image_path")
    private String coverImagePath;
    
    @ColumnInfo(name = "word_count")
    private int wordCount;
    
    @ColumnInfo(name = "last_updated")
    private long lastUpdated;
    
    // Constructors
    public Novel() {}
    
    @Ignore
    public Novel(String title, String description, String content) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.wordCount = content != null ? content.length() : 0;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.wordCount = content != null ? content.length() : 0;
        this.lastUpdated = System.currentTimeMillis();
    }
    
    public String getCoverImagePath() { return coverImagePath; }
    public void setCoverImagePath(String coverImagePath) { this.coverImagePath = coverImagePath; }
    
    public int getWordCount() { return wordCount; }
    public void setWordCount(int wordCount) { this.wordCount = wordCount; }
    
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
    
    // Computed property for date object
    public java.util.Date getLastUpdatedDate() {
        return new java.util.Date(this.lastUpdated);
    }
}