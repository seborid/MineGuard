package com.example.mineguard.analysis;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mineguard.R;

import java.util.List;

/**
 * 右侧报警列表适配器 (精简视觉优化版)
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private final List<String> alarmList;

    public AlarmAdapter(List<String> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 修改点：使用新的精简布局 item_alarmanalysis_card
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarmanalysis_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String alarmTitle = alarmList.get(position);

        holder.tvTitle.setText(alarmTitle);
        holder.tvScene.setText("东翼巷道 #" + (position + 1));

        // 模拟逻辑：前两个为严重，其余为警告
        boolean isCritical = (position < 2);

        if (isCritical) {
            // 严重报警样式
            holder.tvLevel.setText("严重");
            holder.tvLevel.setTextColor(Color.parseColor("#C62828")); // 深红文字
            holder.tvLevel.setBackground(createLevelBackground("#FFEBEE")); // 浅红背景

            holder.viewLevelIndicator.setBackgroundColor(Color.parseColor("#D32F2F")); // 左侧条：红
        } else {
            // 警告样式
            holder.tvLevel.setText("警告");
            holder.tvLevel.setTextColor(Color.parseColor("#EF6C00")); // 深橙文字
            holder.tvLevel.setBackground(createLevelBackground("#FFF3E0")); // 浅橙背景

            holder.viewLevelIndicator.setBackgroundColor(Color.parseColor("#FB8C00")); // 左侧条：橙
        }
    }

    /**
     * 辅助方法：创建带圆角的浅色背景
     */
    private GradientDrawable createLevelBackground(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor(colorHex));
        drawable.setCornerRadius(8f); // 圆角稍微小一点，适配小标签
        return drawable;
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvScene, tvLevel;
        View viewLevelIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvScene = itemView.findViewById(R.id.tvScene);
            tvLevel = itemView.findViewById(R.id.tvLevel); // 新的等级标签
            viewLevelIndicator = itemView.findViewById(R.id.viewLevelIndicator);
        }
    }
}