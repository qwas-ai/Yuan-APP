package com.example.myapplication.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Novel;
import com.example.myapplication.util.ImageUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NovelAdapter extends RecyclerView.Adapter<NovelAdapter.NovelViewHolder> {
    private List<Novel> novels = new ArrayList<>();
    private List<Novel> selectedNovels = new ArrayList<>();
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private boolean selectionMode = false;
    private boolean isWriterMode = false;
    
    @NonNull
    @Override
    public NovelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_novel, parent, false);
        return new NovelViewHolder(itemView);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NovelViewHolder holder, int position) {
        Novel currentNovel = novels.get(position);
        holder.bind(currentNovel);
        
        // 加载封面图片
        String coverImagePath = currentNovel.getCoverImagePath();
        if (coverImagePath != null && !coverImagePath.isEmpty()) {
            Bitmap coverBitmap = ImageUtil.loadBitmapFromFile(coverImagePath);
            if (coverBitmap != null) {
                holder.imageCover.setImageBitmap(coverBitmap);
            } else {
                holder.imageCover.setImageResource(R.drawable.ic_book_cover_placeholder);
            }
        } else {
            holder.imageCover.setImageResource(R.drawable.ic_book_cover_placeholder);
        }
    }
    
    @Override
    public int getItemCount() {
        return novels.size();
    }
    
    public void setNovels(List<Novel> novels) {
        this.novels = novels;
        notifyDataSetChanged();
    }
    
    public void setSelectionMode(boolean selectionMode) {
        this.selectionMode = selectionMode;
        notifyDataSetChanged();
        // 通知MainActivity选择模式已更改
        if (listener != null && listener instanceof SelectionModeListener) {
            ((SelectionModeListener) listener).onSelectionModeChanged(selectionMode);
        }
    }
    
    // 添加长按事件以启用选择模式
    public void enableSelectionMode() {
        if (!selectionMode) {
            selectionMode = true;
            notifyDataSetChanged();
            // 通知MainActivity选择模式已更改
            if (listener != null && listener instanceof SelectionModeListener) {
                ((SelectionModeListener) listener).onSelectionModeChanged(selectionMode);
            }
        }
    }
    
    public List<Novel> getSelectedNovels() {
        return selectedNovels;
    }
    
    public void clearSelection() {
        selectedNovels.clear();
        notifyDataSetChanged();
    }
    
    public boolean getSelectionMode() {
        return selectionMode;
    }
    
    public Novel getNovelAt(int position) {
        return novels.get(position);
    }
    
    public List<Novel> getNovels() {
        return novels;
    }
    
    public void setWriterMode(boolean isWriterMode) {
        this.isWriterMode = isWriterMode;
    }
    
    class NovelViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageCover;
        private TextView textTitle;
        private TextView textDescription;
        private TextView textInfo;
        
        public NovelViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCover = itemView.findViewById(R.id.image_cover);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            textInfo = itemView.findViewById(R.id.text_info);
            
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (selectionMode) {
                    // 在选择模式下，点击切换选择状态
                    Novel novel = novels.get(position);
                    if (selectedNovels.contains(novel)) {
                        selectedNovels.remove(novel);
                    } else {
                        selectedNovels.add(novel);
                    }
                    notifyItemChanged(position);
                } else {
                    // 正常点击事件，跳转到详情页
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(novels.get(position));
                    }
                }
            });
            
            // 添加长按事件以启用选择模式或进入编辑模式
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (isWriterMode) {
                    // 在作家模式下，长按直接进入选择模式
                    if (!selectionMode) {
                        enableSelectionMode();
                    }
                    
                    // 切换当前项目的选择状态
                    Novel novel = novels.get(position);
                    if (selectedNovels.contains(novel)) {
                        selectedNovels.remove(novel);
                    } else {
                        selectedNovels.add(novel);
                    }
                    notifyItemChanged(position);
                    return true;
                } else {
                    // 在阅读模式下，长按进入编辑页面
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(novels.get(position));
                        return true;
                    }
                }
                return false;
            });
        }
        
        public void bind(Novel novel) {
            textTitle.setText(novel.getTitle());
            textDescription.setText(novel.getDescription());
            String info = "字数: " + novel.getWordCount() + " | " + dateFormat.format(novel.getLastUpdatedDate());
            textInfo.setText(info);
            
            // 根据选择状态改变背景
        if (selectionMode && selectedNovels.contains(novel)) {
            // 使用更明显的选中状态视觉反馈
            itemView.setBackgroundColor(0xFF4CAF50); // 绿色背景表示选中
        } else {
            // 根据模式设置不同的背景颜色
            if (isWriterMode) {
                itemView.setBackgroundColor(0xFFFFFFFF); // 写作模式下使用白色背景
            } else {
                itemView.setBackgroundColor(0xFFF5F5F5); // 阅读模式下使用浅灰色背景
            }
        }
        }
    }
    
    public interface OnItemClickListener extends SelectionModeListener {
        void onItemClick(Novel novel);
        void onItemLongClick(Novel novel);
    }
    
    public interface SelectionModeListener {
        void onSelectionModeChanged(boolean selectionMode);
    }
    
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}