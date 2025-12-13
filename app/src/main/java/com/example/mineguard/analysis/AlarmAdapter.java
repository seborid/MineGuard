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
import com.example.mineguard.alarm.model.AlarmItem;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private final List<AlarmItem> alarmList;

    public AlarmAdapter(List<AlarmItem> alarmList) {
        this.alarmList = alarmList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarmanalysis_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmItem item = alarmList.get(position);

        // ==================== 核心修改开始 ====================
        String displayTitle = item.getName();

        // 如果 name 为空，或者为空字符串
        if (displayTitle == null || displayTitle.isEmpty() || "null".equals(displayTitle)) {

            // 1. 优先尝试获取 "报警类型" (对应图1中的 "异物")
            if (item.getType() != null && !item.getType().isEmpty()) {
                displayTitle = item.getType();
            }
            // 2. 如果类型也没有，尝试 "越限类型" (对应四超报警)
            else if (item.getExceedType() != null && !item.getExceedType().isEmpty()) {
                displayTitle = item.getExceedType();
            }
            // 3. 都没有，显示未知
            else {
                displayTitle = "未知报警";
            }

            // 4. 【关键】补上 ID，模仿图1的格式 (例如 "异物 #3970")
            displayTitle = displayTitle + " #" + item.getId();
        }

        holder.tvTitle.setText(displayTitle);
        // ==================== 核心修改结束 ====================

        // 设置时间 (如果时间太长，截取一下，只显示 HH:mm:ss)
        String time = item.getSolve_time();
        if (time != null && time.length() > 10) {
            // 假设格式是 "2023-12-13 15:05:32"，我们取 "15:05:32"
            // 你也可以选择不截取，直接显示 full string
            // holder.tvScene.setText(time.substring(11));
            holder.tvScene.setText(time);
        } else {
            holder.tvScene.setText("刚刚");
        }

        // 设置颜色等级
        boolean isCritical = "1".equals(item.getLevel());
        if (isCritical) {
            holder.tvLevel.setText("严重");
            holder.tvLevel.setTextColor(Color.parseColor("#C62828"));
            holder.tvLevel.setBackground(createLevelBackground("#FFEBEE"));
            holder.viewLevelIndicator.setBackgroundColor(Color.parseColor("#D32F2F"));
        } else {
            holder.tvLevel.setText("警告");
            holder.tvLevel.setTextColor(Color.parseColor("#EF6C00"));
            holder.tvLevel.setBackground(createLevelBackground("#FFF3E0"));
            holder.viewLevelIndicator.setBackgroundColor(Color.parseColor("#FB8C00"));
        }
    }

    private GradientDrawable createLevelBackground(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor(colorHex));
        drawable.setCornerRadius(8f);
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
            tvLevel = itemView.findViewById(R.id.tvLevel);
            viewLevelIndicator = itemView.findViewById(R.id.viewLevelIndicator);
        }
    }
}