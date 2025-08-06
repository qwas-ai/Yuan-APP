package com.example.myapplication;

import android.os.Bundle;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AlertDialog;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.model.Chapter;
import com.example.myapplication.viewmodel.ChapterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

/**
 * 简易的章节编辑界面，后续可扩展功能（标题、内容编辑，保存、删除等）。
 */
// ... existing code ...
import com.example.myapplication.BaseActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.model.Chapter;
import com.example.myapplication.viewmodel.ChapterViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.button.MaterialButton;

/**
 * 简易的章节编辑界面，后续可扩展功能（标题、内容编辑，保存、删除等）。
 */
public class ChapterEditActivity extends BaseActivity {

    private TextInputEditText editChapterTitle;
    private TextInputEditText editChapterContent;
    private MaterialButton buttonSave;
    private MaterialButton buttonDelete;

    private ChapterViewModel chapterViewModel;
    private Chapter currentChapter;
    private int novelId;
    private int chapterId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chapter_edit);

        initViews();
        setupViewModel();
        checkIntentForChapter();
    }

    private void initViews() {
        editChapterTitle = findViewById(R.id.edit_chapter_title);
        editChapterContent = findViewById(R.id.edit_chapter_content);
        buttonSave = findViewById(R.id.button_save_chapter);
        buttonDelete = findViewById(R.id.button_delete_chapter);
        buttonDelete.setOnClickListener(v -> confirmDelete());
        buttonDelete.setVisibility(View.GONE);

        novelId = getIntent().getIntExtra("novel_id", -1);
        if (novelId == -1) {
            Toast.makeText(this, "无效的小说ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonSave.setOnClickListener(v -> saveChapter());
    }

    private void setupViewModel() {
        chapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
        currentChapter = new Chapter();
        currentChapter.setNovelId(novelId);
    }

    private void checkIntentForChapter() {
        chapterId = getIntent().getIntExtra("chapter_id", -1);
        if (chapterId != -1) {
            // 加载已有章节并填充
            chapterViewModel.getChapterById(chapterId).observe(this, chapter -> {
                if (chapter != null) {
                    currentChapter = chapter;
                    editChapterTitle.setText(chapter.getTitle());
                    editChapterContent.setText(chapter.getContent());
                    buttonDelete.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("删除章节")
                .setMessage("确定要删除该章节吗？")
                .setPositiveButton("删除", (d, w) -> {
                    chapterViewModel.delete(currentChapter, () -> runOnUiThread(() -> {
                        Toast.makeText(this, "章节已删除", Toast.LENGTH_SHORT).show();
                        finish();
                    }));
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void saveChapter() {
        // 保存或更新章节逻辑
        String title = editChapterTitle.getText() != null ? editChapterTitle.getText().toString().trim() : "";
        String content = editChapterContent.getText() != null ? editChapterContent.getText().toString().trim() : "";

        if (title.isEmpty()) {
            editChapterTitle.setError("请输入章节标题");
            editChapterTitle.requestFocus();
            return;
        }

        currentChapter.setTitle(title);
        currentChapter.setContent(content);
        // orderIndex 和 lastUpdated 在 Repository 层或数据库中可进一步处理

        if (currentChapter.getId() == 0) {
            chapterViewModel.insert(currentChapter, () -> runOnUiThread(() -> {
                Toast.makeText(this, "章节已保存", Toast.LENGTH_SHORT).show();
                finish();
            }));
        } else {
            chapterViewModel.update(currentChapter, () -> runOnUiThread(() -> {
                Toast.makeText(this, "章节已更新", Toast.LENGTH_SHORT).show();
                finish();
            }));
        }
    }
}