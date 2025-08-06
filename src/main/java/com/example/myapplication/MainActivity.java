package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import com.example.myapplication.BaseActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.NovelAdapter;
import com.example.myapplication.model.Novel;
import com.example.myapplication.viewmodel.NovelViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import com.example.myapplication.SettingsActivity;

public class MainActivity extends BaseActivity implements NovelAdapter.SelectionModeListener {
    private NovelViewModel novelViewModel;
    private NovelAdapter adapter;
    private boolean isWriterMode = false;
    
    @Override
    public void onSelectionModeChanged(boolean selectionMode) {
        // 当选择模式更改时，重新创建菜单
        invalidateOptionsMenu();
    }
    
    // 添加一个方法来显示删除确认对话框
    private void showDeleteConfirmationDialog() {
        int selectedCount = adapter.getSelectedNovels().size();
        String message = "确定要删除选中的" + selectedCount + "部小说吗？";
        
        new AlertDialog.Builder(this)
                .setTitle("删除小说")
                .setMessage(message)
                .setPositiveButton("确定", (dialog, which) -> deleteSelectedNovels())
                .setNegativeButton("取消", null)
                .show();
    }
    
    // 添加一个方法来删除选中的小说
    private void deleteSelectedNovels() {
        List<Novel> selectedNovels = adapter.getSelectedNovels();
        int deletedCount = selectedNovels.size();
        
        for (Novel novel : selectedNovels) {
            novelViewModel.deleteById(novel.getId());
        }
        
        adapter.clearSelection();
        adapter.setSelectionMode(false);
        
        // 显示删除结果提示
        String message = "已删除" + deletedCount + "部小说";
        Snackbar.make(findViewById(R.id.recycler_view), message, Snackbar.LENGTH_SHORT).show();
        
        // 重新创建菜单以更新按钮标题
        invalidateOptionsMenu();
    }
    
    // 添加一个方法来退出选择模式
    private void exitSelectionMode() {
        adapter.setSelectionMode(false);
        adapter.clearSelection();
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        com.google.android.material.appbar.MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.recycler_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupRecyclerView();
        setupViewModel();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // 确保菜单正确更新
        invalidateOptionsMenu();
    }
    
    private void initViews() {
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(v -> {
            // 跳转到创建小说页面
            Intent intent = new Intent(MainActivity.this, NovelEditActivity.class);
            startActivity(intent);
        });
    }
    
    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        adapter = new NovelAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        adapter.setOnItemClickListener(new NovelAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Novel novel) {
                // 点击item，跳转到小说详情页
                Intent intent = new Intent(MainActivity.this, NovelDetailActivity.class);
                intent.putExtra("novel_id", novel.getId());
                intent.putExtra("is_writer_mode", isWriterMode);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Novel novel) {
                // 在阅读模式下，长按进入编辑页面
                Intent intent = new Intent(MainActivity.this, NovelEditActivity.class);
                intent.putExtra("novel_id", novel.getId());
                startActivity(intent);
            }

            @Override
            public void onSelectionModeChanged(boolean selectionMode) {
                // 当选择模式更改时，重新创建菜单
                invalidateOptionsMenu();
            }
        });
    }
    
    private void setupViewModel() {
        novelViewModel = new ViewModelProvider(this).get(NovelViewModel.class);
        novelViewModel.getAllNovels().observe(this, novels -> {
            // 更新适配器数据
            adapter.setNovels(novels);
        });
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (adapter != null && adapter.getSelectionMode()) {
            deleteItem.setTitle("取消");
        } else {
            deleteItem.setTitle("删除小说");
        }
        
        // 更新模式切换菜单项的标题
        MenuItem modeItem = menu.findItem(R.id.action_mode_toggle);
        if (isWriterMode) {
            modeItem.setTitle(R.string.reader_mode);
        } else {
            modeItem.setTitle(R.string.writer_mode);
        }
        
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_settings) {
            // TODO: 打开设置页面
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
             return true;
         } else if (id == R.id.action_delete) {
            // 检查是否处于选择模式
            if (adapter != null && adapter.getSelectionMode()) {
                // 如果处于选择模式，退出选择模式
                exitSelectionMode();
            } else {
                // 如果没有处于选择模式，检查是否有选中的小说
                if (adapter.getSelectedNovels().isEmpty()) {
                    // 如果没有选中的小说，显示提示信息
                    Toast.makeText(this, "请先选择要删除的小说", Toast.LENGTH_SHORT).show();
                } else {
                    // 如果有选中的小说，显示确认对话框
                    showDeleteConfirmationDialog();
                }
            }
            return true;
        } else if (id == R.id.action_mode_toggle) {
            // 切换模式
            isWriterMode = !isWriterMode;
            // 更新适配器的模式
            adapter.setWriterMode(isWriterMode);
            invalidateOptionsMenu();
            
            // 显示模式切换提示
            String modeText = isWriterMode ? getString(R.string.writer_mode) : getString(R.string.reader_mode);
            String message = modeText + "已启用";
            Snackbar.make(findViewById(R.id.recycler_view), message, Snackbar.LENGTH_SHORT).show();
            
            // 清除任何现有的选择
            adapter.clearSelection();
            adapter.setSelectionMode(false);
            
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
}