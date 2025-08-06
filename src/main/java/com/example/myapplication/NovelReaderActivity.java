package com.example.myapplication;

import com.example.myapplication.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;

import com.example.myapplication.model.Novel;

import com.example.myapplication.viewmodel.NovelViewModel;
import com.example.myapplication.viewmodel.ChapterViewModel;
import com.example.myapplication.model.Chapter;
import com.google.android.material.appbar.MaterialToolbar;

import com.example.myapplication.util.SettingsUtil;

public class NovelReaderActivity extends BaseActivity {
    private NovelViewModel novelViewModel;
    private Novel currentNovel;

    private TextView textContent;
    private com.google.android.material.appbar.MaterialToolbar bottomToolbar;
    private ChapterViewModel chapterViewModel;
    private java.util.List<Chapter> chapterList = new java.util.ArrayList<>();
    private int currentChapterIndex = 0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_novel_reader);
        

        
        initViews();
        setupToolbar();
        setupViewModel();
        checkIntentForNovel();
    }
    
    private void initViews() {

        textContent = findViewById(R.id.text_content);
        bottomToolbar = findViewById(R.id.bottom_toolbar);
        bottomToolbar.setOnClickListener(v -> showChapterDialog());
        bottomToolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_next) {
                goToNextChapter();
                return true;
            }
            return false;
        });

        // 应用阅读设置
        applyReaderSettings();
    }

    private void applyReaderSettings() {
        float fontSize = SettingsUtil.getFontSize(this);
        float lineSpacing = SettingsUtil.getLineSpacing(this);
        textContent.setTextSize(fontSize);
        textContent.setLineSpacing(0, lineSpacing);
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.reader_mode);
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupViewModel() {
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);
        chapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
    }
    
    private void checkIntentForNovel() {
        int novelId = getIntent().getIntExtra("novel_id", -1);
        if (novelId != -1) {
            novelViewModel.getNovelById(novelId).observe(this, novel -> {
                if (novel != null) {
                    currentNovel = novel;
                    chapterViewModel.getChaptersByNovel(novelId).observe(this, chapters -> {
                        chapterList = chapters;
                        if (chapters != null && !chapters.isEmpty()) {
                            currentChapterIndex = 0;
                            textContent.setText(chapters.get(0).getContent());
                            bottomToolbar.setTitle(chapters.get(0).getTitle());
                            updateNextButtonState();
                        }
                    });
                } else {
                    // 如果小说不存在，显示错误信息并关闭页面
                    Toast.makeText(this, "小说不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
    
    
    private void deleteNovel() {
        if (currentNovel != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_novel)
                    .setMessage(R.string.confirm_delete_novel)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        novelViewModel.delete(currentNovel, () -> {
                            runOnUiThread(() -> {
                                Snackbar.make(findViewById(R.id.toolbar), R.string.novel_deleted, Snackbar.LENGTH_SHORT).show();
                                // 延迟finish以显示Snackbar
                                new Handler().postDelayed(() -> finish(), 1500);
                            });
                        });
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reader_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_edit) {
            // 跳转到编辑小说页面
            Intent intent = new Intent(NovelReaderActivity.this, NovelEditActivity.class);
            intent.putExtra("novel_id", currentNovel.getId());
            startActivity(intent);
            return true;
        } else if (id == R.id.action_chapters) {
            showChapterDialog();
            return true;
        } else if (id == R.id.action_delete) {
            deleteNovel();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    private void showChapterDialog() {
        if (chapterList == null || chapterList.isEmpty()) {
            Toast.makeText(this, "暂无章节", Toast.LENGTH_SHORT).show();
            return;
        }
        CharSequence[] titles = new CharSequence[chapterList.size()];
        for (int i = 0; i < chapterList.size(); i++) {
            titles[i] = chapterList.get(i).getTitle();
        }
        new AlertDialog.Builder(this)
                .setTitle("章节目录")
                .setItems(titles, (dialog, which) -> {
                    currentChapterIndex = which;
                    Chapter chapter = chapterList.get(which);
                    textContent.setText(chapter.getContent());
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(chapter.getTitle());
                    }
                    bottomToolbar.setTitle(chapter.getTitle());
                    updateNextButtonState();
                })
                .show();
    }

    // 下一章功能
    private void goToNextChapter() {
        if (chapterList == null) return;
        if (currentChapterIndex < chapterList.size() - 1) {
            currentChapterIndex++;
            Chapter chapter = chapterList.get(currentChapterIndex);
            textContent.setText(chapter.getContent());
            bottomToolbar.setTitle(chapter.getTitle());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(chapter.getTitle());
            }
        }
        updateNextButtonState();
    }

    private void updateNextButtonState() {
        if (bottomToolbar.getMenu() == null) return;
        MenuItem item = bottomToolbar.getMenu().findItem(R.id.action_next);
        if (item != null) {
            item.setEnabled(chapterList != null && currentChapterIndex < (chapterList.size() - 1));
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        applyReaderSettings();
    }
}