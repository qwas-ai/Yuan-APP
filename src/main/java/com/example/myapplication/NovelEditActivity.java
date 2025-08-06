package com.example.myapplication;

import com.example.myapplication.R;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import com.example.myapplication.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.annotation.NonNull;
import java.util.List;

import com.example.myapplication.adapter.ChapterAdapter;
import com.example.myapplication.model.Chapter;
import com.example.myapplication.model.Novel;
import com.example.myapplication.repository.NovelRepository;
import com.example.myapplication.util.ImageUtil;
import com.example.myapplication.viewmodel.NovelViewModel;
import com.example.myapplication.viewmodel.ChapterViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class NovelEditActivity extends BaseActivity {
    private NovelViewModel novelViewModel;
    private TextInputEditText editTitle;
    private TextInputEditText editDescription;
    private ImageView imageCover;
    private Button buttonSelectCover;
    
    private Novel currentNovel;
    private boolean isNewNovel = true;
    private boolean shouldSwitchToReader = false;
    private Bitmap selectedCoverBitmap;
    private String coverImagePath;

    // 新增章节列表相关字段
    private RecyclerView recyclerChapters;
    private ChapterAdapter chapterAdapter;
    private ChapterViewModel chapterViewModel;
    
    // 用于选择图片的Activity结果启动器
    private ActivityResultLauncher<Intent> selectImageLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_novel_edit);

        // 先创建空对象避免空指针
        currentNovel = new Novel();

        initViews();
        setupToolbar();
        setupViewModel();
        setupImagePicker();
        checkIntentForNovel();
    }
    
    private void initViews() {
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        imageCover = findViewById(R.id.image_cover);
        buttonSelectCover = findViewById(R.id.button_select_cover);
        
        buttonSelectCover.setOnClickListener(v -> selectCoverImage());
        // 在 initViews 方法末尾增加
        recyclerChapters = findViewById(R.id.recycler_chapters);
        chapterAdapter = new ChapterAdapter();
        recyclerChapters.setLayoutManager(new LinearLayoutManager(this));
        recyclerChapters.setAdapter(chapterAdapter);

        // 启用拖拽重新排序
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                int from = vh.getAdapterPosition();
                int to = target.getAdapterPosition();
                chapterAdapter.swapItems(from, to);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 不支持侧滑删除
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // 拖拽结束后保存新的顺序
                List<Chapter> list = chapterAdapter.getChapters();
                for (int i = 0; i < list.size(); i++) {
                    Chapter c = list.get(i);
                    if (c.getOrderIndex() != i) {
                        c.setOrderIndex(i);
                        chapterViewModel.update(c, null);
                    }
                }
            }
        });
        helper.attachToRecyclerView(recyclerChapters);

        // 设置章节点击、长按监听器
        chapterAdapter.setOnItemClickListener(new ChapterAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Chapter chapter) {
                // 点击进入编辑章节界面
                Intent intent = new Intent(NovelEditActivity.this, ChapterEditActivity.class);
                intent.putExtra("novel_id", currentNovel.getId());
                intent.putExtra("chapter_id", chapter.getId());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Chapter chapter) {
                // 长按删除章节
                new AlertDialog.Builder(NovelEditActivity.this)
                        .setTitle("删除章节")
                        .setMessage("确定要删除该章节吗？")
                        .setPositiveButton("删除", (d, which) -> {
                            chapterViewModel.delete(chapter, () -> runOnUiThread(() -> Toast.makeText(NovelEditActivity.this, "章节已删除", Toast.LENGTH_SHORT).show()));
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        Button buttonAddChapter = findViewById(R.id.button_add_chapter);
        buttonAddChapter.setOnClickListener(v -> {
            if (currentNovel.getId() == 0 && isNewNovel) {
                Toast.makeText(this, "请先保存小说，再添加章节", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, ChapterEditActivity.class);
            intent.putExtra("novel_id", currentNovel.getId());
            startActivity(intent);
        });
    }
    
    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 根据是新建还是编辑小说来设置标题
            if (isNewNovel) {
                getSupportActionBar().setTitle("创建小说");
            } else {
                getSupportActionBar().setTitle("编辑小说");
            }
        }
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void setupViewModel() {
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);
        chapterViewModel = new ViewModelProvider(this).get(ChapterViewModel.class);
        // 章节数据的订阅将在拿到有效的 novelId 后再进行
    }

    private void observeChapters(int novelId) {
        chapterViewModel.getChaptersByNovel(novelId).observe(this, chapterAdapter::setChapters);
    }
    
    private void setupImagePicker() {
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            selectedCoverBitmap = ImageUtil.loadBitmapFromUri(this, imageUri);
                            if (selectedCoverBitmap != null) {
                                imageCover.setImageBitmap(selectedCoverBitmap);
                            }
                        }
                    }
                });
    }
    
    private void selectCoverImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        selectImageLauncher.launch(intent);
    }
    
    private void checkIntentForNovel() {
        int novelId = getIntent().getIntExtra("novel_id", -1);
        if (novelId != -1) {
            // 编辑现有小说
            novelViewModel.getNovelById(novelId).observe(this, novel -> {
                if (novel != null) {
                    currentNovel = novel;
                    isNewNovel = false;
                    populateFields();
                    observeChapters(currentNovel.getId());
                    // 由于数据是异步加载的，在此处更新标题
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("编辑小说");
                    }
                } else {
                    // 如果小说不存在，显示错误信息并关闭页面
                    Toast.makeText(this, "小说不存在", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // 创建新小说
            currentNovel = new Novel();
        }
    }
    
    private void populateFields() {
        editTitle.setText(currentNovel.getTitle());
        editDescription.setText(currentNovel.getDescription());
        
        // 加载封面图片
        coverImagePath = currentNovel.getCoverImagePath();
        if (coverImagePath != null && !coverImagePath.isEmpty()) {
            Bitmap coverBitmap = ImageUtil.loadBitmapFromFile(coverImagePath);
            if (coverBitmap != null) {
                imageCover.setImageBitmap(coverBitmap);
            }
        }
    }
    
    private void saveNovel() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        
        if (title.isEmpty()) {
            editTitle.setError("请输入小说标题");
            editTitle.requestFocus();
            return;
        }
        
        currentNovel.setTitle(title);
        currentNovel.setDescription(description);
        
        // 保存封面图片
        if (selectedCoverBitmap != null) {
            coverImagePath = ImageUtil.saveCoverImage(this, selectedCoverBitmap);
            currentNovel.setCoverImagePath(coverImagePath);
        } else {
            currentNovel.setCoverImagePath(coverImagePath);
        }
        
        if (isNewNovel) {
            novelViewModel.insert(currentNovel, id -> runOnUiThread(() -> {
                currentNovel.setId((int) id);
                isNewNovel = false; // 插入成功后，就不是新小说了
                observeChapters(currentNovel.getId());
                showSaveSuccessMessageAndProceed("小说已创建", id);
            }));
        } else {
            novelViewModel.update(currentNovel, () -> runOnUiThread(() -> {
                showSaveSuccessMessageAndProceed("小说已保存");
            }));
        }
    }
    
    private void showSaveSuccessMessageAndProceed(String message, long novelId) {
        Snackbar.make(findViewById(R.id.toolbar), message, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> {
            if (shouldSwitchToReader) {
                Intent intent = new Intent(NovelEditActivity.this, NovelReaderActivity.class);
                // 如果是新创建的小说，使用传入的novelId，否则使用currentNovel.getId()
                intent.putExtra("novel_id", (int) (novelId != -1 ? novelId : currentNovel.getId()));
                startActivity(intent);
            } else {
                // 默认返回主界面
                Intent intent = new Intent(NovelEditActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            shouldSwitchToReader = false; // 重置标志
            finish();
        }, 300);
    }

    private void showSaveSuccessMessageAndProceed(String message) {
        showSaveSuccessMessageAndProceed(message, -1);
    }

    private void deleteNovel() {
        if (!isNewNovel && currentNovel != null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.delete_novel)
                    .setMessage(R.string.confirm_delete_novel)
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        novelViewModel.delete(currentNovel, () -> runOnUiThread(() -> {
                            Toast.makeText(this, R.string.novel_deleted, Toast.LENGTH_SHORT).show();
                            // 返回主界面
                            Intent intent = new Intent(NovelEditActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }));
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_save) {
            saveNovel();
            return true;
        } else if (id == R.id.action_delete) {
            deleteNovel();
            return true;
        } else if (id == R.id.action_switch_to_reader) {
            // 切换到阅读模式
            shouldSwitchToReader = true;
            saveNovel(); // 统一调用saveNovel，逻辑在saveNovel和回调中处理
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}