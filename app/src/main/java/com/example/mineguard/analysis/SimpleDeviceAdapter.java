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
import com.example.mineguard.data.DeviceItem;
import java.util.List;

/**
 * 左侧设备列表适配器 (视觉优化版)
 */
public class SimpleDeviceAdapter extends RecyclerView.Adapter<SimpleDeviceAdapter.ViewHolder> {

    private List<DeviceItem> deviceList;

    public SimpleDeviceAdapter(List<DeviceItem> deviceList) {
        this.deviceList = deviceList;
    }
    // 新增 setDeviceList 方法，用于 LiveData 更新
    public void setDeviceList(List<DeviceItem> newDeviceList) {
        this.deviceList = newDeviceList;
        // 通知 RecyclerView 整个数据集已更改
        notifyDataSetChanged();
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
        //String deviceName = deviceList.get(position);
        DeviceItem deviceItem = deviceList.get(position); // 【修改点 3】获取 DeviceItem
        // 【修改点 4】使用 DeviceItem 的 getter 方法设置文本
        holder.tvName.setText(deviceItem.getDeviceName());
        holder.tvIp.setText(deviceItem.getIpAddress()); // 使用 IP 地址

        // --- 动态设置状态样式 ---
        String status = deviceItem.getStatus();
//        holder.tvName.setText(deviceName);
//        holder.tvIp.setText("192.168.10." + (100 + position));

        // 假设 status 字符串是 "在线" 或 "离线"
        boolean isOnline = "在线".equals(status);

        if (isOnline) {
            holder.tvStatus.setText("在线");
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32"));
            holder.tvStatus.setBackground(createStatusBackground("#E8F5E9"));
        } else {
            holder.tvStatus.setText("离线");
            holder.tvStatus.setTextColor(Color.parseColor("#616161"));
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