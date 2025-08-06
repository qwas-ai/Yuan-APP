package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.model.Novel;
import com.example.myapplication.util.ImageUtil;
import com.example.myapplication.viewmodel.NovelViewModel;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.appbar.MaterialToolbar;

// ... existing code ...
import com.example.myapplication.BaseActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication.model.Novel;
import com.example.myapplication.util.ImageUtil;
import com.example.myapplication.viewmodel.NovelViewModel;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.appbar.MaterialToolbar;

public class NovelDetailActivity extends BaseActivity {

    private NovelViewModel novelViewModel;
    private Novel currentNovel;
    private boolean isWriterMode = false;

    private ImageView imageCover;
    private TextView textTitle;
    private TextView textDescription;
    private Button buttonRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);

        initViews();
        setupToolbar();
        setupViewModel();
        isWriterMode = getIntent().getBooleanExtra("is_writer_mode", false);
        checkIntentForNovel();

        buttonRead.setOnClickListener(v -> {
            if (currentNovel != null) {
                Intent intent = new Intent(NovelDetailActivity.this, NovelReaderActivity.class);
                intent.putExtra("novel_id", currentNovel.getId());
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        imageCover = findViewById(R.id.image_cover);
        textTitle = findViewById(R.id.text_title);
        textDescription = findViewById(R.id.text_description);
        buttonRead = findViewById(R.id.button_read);
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("小说详情");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewModel() {
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);
    }

    private void checkIntentForNovel() {
        int novelId = getIntent().getIntExtra("novel_id", -1);
        if (novelId != -1) {
            novelViewModel.getNovelById(novelId).observe(this, novel -> {
                if (novel != null) {
                    currentNovel = novel;
                    populateFields();
                } else {
                    Toast.makeText(this, "小说不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "无效的小说ID", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void populateFields() {
        textTitle.setText(currentNovel.getTitle());
        textDescription.setText(currentNovel.getDescription());

        String coverImagePath = currentNovel.getCoverImagePath();
        if (coverImagePath != null && !coverImagePath.isEmpty()) {
            Bitmap coverBitmap = ImageUtil.loadBitmapFromFile(coverImagePath);
            if (coverBitmap != null) {
                imageCover.setImageBitmap(coverBitmap);
            } else {
                imageCover.setImageResource(R.drawable.ic_book_cover_placeholder);
            }
        } else {
            imageCover.setImageResource(R.drawable.ic_book_cover_placeholder);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isWriterMode) {
            getMenuInflater().inflate(R.menu.detail_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(NovelDetailActivity.this, NovelEditActivity.class);
            intent.putExtra("novel_id", currentNovel.getId());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}