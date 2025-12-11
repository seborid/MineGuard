package com.example.mineguard.analysis;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable; // 引入这个类
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mineguard.R;

import java.util.List;

/**
 * 左侧设备列表适配器 (视觉优化版)
 */
public class SimpleDeviceAdapter extends RecyclerView.Adapter<SimpleDeviceAdapter.ViewHolder> {

    private final List<String> deviceList;

    public SimpleDeviceAdapter(List<String> deviceList) {
        this.deviceList = deviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_device_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String deviceName = deviceList.get(position);

        holder.tvName.setText(deviceName);
        holder.tvIp.setText("192.168.10." + (100 + position));

        // --- 动态设置状态样式 (核心修改) ---
        boolean isOnline = (position != 2); // 模拟第3个设备离线，其他在线

        if (isOnline) {
            holder.tvStatus.setText("在线");
            // 设置文字颜色：深绿色
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            // 动态生成背景：浅绿色 + 圆角
            holder.tvStatus.setBackground(createStatusBackground("#E8F5E9"));
        } else {
            holder.tvStatus.setText("离线");
            // 设置文字颜色：深灰色
            holder.tvStatus.setTextColor(Color.parseColor("#616161"));
            // 动态生成背景：浅灰色 + 圆角
            holder.tvStatus.setBackground(createStatusBackground("#F5F5F5"));
        }
    }

    /**
     * 辅助方法：动态创建一个带圆角的纯色背景
     * @param colorHex 背景色 HEX 值
     */
    private GradientDrawable createStatusBackground(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.parseColor(colorHex)); // 设置填充色
        drawable.setCornerRadius(12f); // 设置圆角半径 (像素)
        return drawable;
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvIp, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_device_name);
            tvIp = itemView.findViewById(R.id.tv_device_ip);
            tvStatus = itemView.findViewById(R.id.tv_device_status);
        }
    }
}