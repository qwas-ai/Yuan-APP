package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.model.Chapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private final List<Chapter> chapters = new ArrayList<>();
    private OnItemClickListener listener;

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chapter, parent, false);
        return new ChapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter c = chapters.get(position);
        holder.textOrder.setText(String.valueOf(c.getOrderIndex() + 1));
        holder.textTitle.setText(c.getTitle());
    }

    @Override
    public int getItemCount() {
        return chapters.size();
    }

    public void setChapters(List<Chapter> list) {
        chapters.clear();
        chapters.addAll(list);
        notifyDataSetChanged();
    }

    /**
     * 交换两个位置的章节，实现拖拽排序。
     */
    public void swapItems(int fromPosition, int toPosition) {
        Collections.swap(chapters, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    /**
     * 获取当前章节列表，便于外部保存顺序。
     */
    public List<Chapter> getChapters() {
        return chapters;
    }

    class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView textOrder, textTitle;
        ChapterViewHolder(View itemView) {
            super(itemView);
            textOrder = itemView.findViewById(R.id.text_order);
            textTitle = itemView.findViewById(R.id.text_title);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemClick(chapters.get(pos));
                }
            });
            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (listener != null && pos != RecyclerView.NO_POSITION) {
                    listener.onItemLongClick(chapters.get(pos));
                    return true;
                }
                return false;
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Chapter chapter);
        void onItemLongClick(Chapter chapter);
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }
}