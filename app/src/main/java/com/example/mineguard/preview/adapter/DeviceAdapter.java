//package com.example.mineguard.preview.adapter;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.RotateAnimation;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.mineguard.R;
//import com.example.mineguard.preview.model.DeviceItem;
//import com.example.mineguard.preview.view.SplitScreenManager;
//import java.util.List;
//import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//
///**
// * 设备列表适配器，按区域分组显示设备，支持折叠面板
// */
//public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_DEVICE = 1;
//
//    private List<DeviceItem> deviceList;
//    private List<String> locationList;
//    private Map<String, List<DeviceItem>> locationDeviceMap;
//    private Map<String, Boolean> locationExpandedMap; // 记录每个位置的展开状态
//    private List<Object> itemList; // 包含header和device的混合列表
//    private OnDeviceClickListener listener;
//private SplitScreenManager splitScreenManager;
//
//    public interface OnDeviceClickListener {
//        void onDeviceClick(DeviceItem device);
//        void onDeviceLongClick(DeviceItem device);
//        void onQuickViewClick(String location, List<DeviceItem> devices);
//    }
//
//    public DeviceAdapter(OnDeviceClickListener listener) {
//        this.listener = listener;
//        this.deviceList = new ArrayList<>();
//        this.locationList = new ArrayList<>();
//        this.locationDeviceMap = new HashMap<>();
//        this.locationExpandedMap = new HashMap<>();
//        this.itemList = new ArrayList<>();
//    }
//
//    public void setSplitScreenManager(SplitScreenManager manager) {
//        this.splitScreenManager = manager;
//    }
//
//    public void updateDevices(List<DeviceItem> devices) {
//        this.deviceList.clear();
//        this.deviceList.addAll(devices);
//        groupDevicesByLocation();
//        notifyDataSetChanged();
//    }
//
//    private void groupDevicesByLocation() {
//        locationList.clear();
//        locationDeviceMap.clear();
//        locationExpandedMap.clear();
//        itemList.clear();
//
//        // 按位置分组，统一视为摄像头设备
//        for (DeviceItem device : deviceList) {
//            String location = device.getLocation();
//            if (location == null || location.trim().isEmpty()) {
//                location = "未知位置";
//            }
//            if (!locationDeviceMap.containsKey(location)) {
//                locationDeviceMap.put(location, new ArrayList<>());
//                locationList.add(location);
//                locationExpandedMap.put(location, true); // 默认展开
//            }
//            locationDeviceMap.get(location).add(device);
//        }
//
//        // 构建显示列表
//        for (String location : locationList) {
//            itemList.add(location); // 添加位置header
//            // 如果该位置展开，则添加设备
//            if (locationExpandedMap.get(location)) {
//                itemList.addAll(locationDeviceMap.get(location));
//            }
//        }
//    }
//
//    private void toggleRegionExpansion(String location) {
//        boolean isExpanded = locationExpandedMap.get(location);
//        locationExpandedMap.put(location, !isExpanded);
//
//        // 重新构建itemList
//        itemList.clear();
//        for (String r : locationList) {
//            itemList.add(r);
//            if (locationExpandedMap.get(r)) {
//                itemList.addAll(locationDeviceMap.get(r));
//            }
//        }
//        notifyDataSetChanged();
//    }
//
////    @Override
//   public int getItemViewType(int position) {
//       Object item = itemList.get(position);
//       return item instanceof String ? TYPE_HEADER : TYPE_DEVICE;
//   }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == TYPE_HEADER) {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_collapsible_header, parent, false);
//            return new HeaderViewHolder(view);
//        } else {
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.item_device_card, parent, false);
//            return new DeviceViewHolder(view);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return itemList.size();
//    }
//
//    class HeaderViewHolder extends RecyclerView.ViewHolder {
//        TextView tvRegionName;
//        TextView tvDeviceCount;
//        ImageView ivExpandIndicator;
//        Button btnQuickView;
//
//        public HeaderViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvRegionName = itemView.findViewById(R.id.tv_location_name);
//            tvDeviceCount = itemView.findViewById(R.id.tv_device_count);
//            ivExpandIndicator = itemView.findViewById(R.id.iv_expand_indicator);
//            btnQuickView = itemView.findViewById(R.id.btn_quick_view);
//        }
//
//        public void bind(String location) {
//            tvRegionName.setText(location);
//
//            // 计算该区域的设备数量
//            List<DeviceItem> devices = locationDeviceMap.get(location);
//            int onlineCount = 0;
//            if (devices != null) {
//                for (DeviceItem device : devices) {
//                    if (device.is_online()) {
//                        onlineCount++;
//                    }
//                }
//                tvDeviceCount.setText(onlineCount + "/" + devices.size() + " 在线");
//            }
//
//            // 设置展开/收起图标
//            boolean isExpanded = locationExpandedMap.get(location);
//            if (isExpanded) {
//                ivExpandIndicator.setRotation(0);
//            } else {
//                ivExpandIndicator.setRotation(-90);
//            }
//
//            // 设置点击事件
//            itemView.setOnClickListener(v -> {
//                toggleRegionExpansion(location);
//                // 添加旋转动画
//                RotateAnimation rotateAnimation = new RotateAnimation(
//                    isExpanded ? 0 : -90,
//                    isExpanded ? -90 : 0,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f,
//                    RotateAnimation.RELATIVE_TO_SELF, 0.5f
//                );
//                rotateAnimation.setDuration(200);
//                ivExpandIndicator.startAnimation(rotateAnimation);
//            });
//
//            // 设置快速查看按钮点击事件
//            btnQuickView.setOnClickListener(v -> {
//                if (listener != null && devices != null) {
//                    listener.onQuickViewClick(location, devices);
//                }
//            });
//        }
//    }
//
//    class DeviceViewHolder extends RecyclerView.ViewHolder {
//        ImageView ivDeviceIcon;
//        TextView tvDeviceName;
//        TextView tvDeviceModel;
//        TextView tvDeviceIp;
//        TextView tvDeviceStatus;
//        View statusIndicator;
//
//        public DeviceViewHolder(@NonNull View itemView) {
//            super(itemView);
//            ivDeviceIcon = itemView.findViewById(R.id.iv_device_icon);
//            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
//            tvDeviceModel = itemView.findViewById(R.id.tv_device_model);
//            tvDeviceIp = itemView.findViewById(R.id.tv_device_ip);
//            tvDeviceStatus = itemView.findViewById(R.id.tv_device_status);
//            statusIndicator = itemView.findViewById(R.id.status_indicator);
//        }
//
//        public void bind(DeviceItem device) {
//            tvDeviceName.setText(device.getName());
//            tvDeviceModel.setText(device.getAlgorithm());
//            tvDeviceIp.setText(device.getIp());
//            tvDeviceStatus.setText(device.is_online()? "在线" : "离线");
//
//            // 只设置摄像头图标
//            ivDeviceIcon.setImageResource(R.drawable.ic_camera);
//
//            // 设置状态指示器颜色
//            switch (device.is_online()? "在线" : "离线") {
//                case "在线":
//                    statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.primary_green));
//                    break;
//                case "离线":
//                    statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.text_secondary));
//                    break;
////                case DeviceItem.STATUS_ERROR:
////                    statusIndicator.setBackgroundColor(itemView.getContext().getResources().getColor(R.color.primary_red));
////                    break;
//            }
//
//            // 设置点击事件
//            itemView.setOnClickListener(v -> {
//                if (splitScreenManager != null) {
//                    splitScreenManager.enterSingleDeviceMode(device);
//                }
//                if (listener != null) {
//                    listener.onDeviceClick(device);
//                }
//            });
//
//            itemView.setOnLongClickListener(v -> {
//                if (listener != null) {
//                    listener.onDeviceLongClick(device);
//                }
//                return true;
//            });
//        }
//    }
//
//    // 更新区域设备数量
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        Object item = itemList.get(position);
//
//        if (holder instanceof HeaderViewHolder) {
//            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
//            String location = (String) item;
//            headerHolder.bind(location);
//        } else if (holder instanceof DeviceViewHolder) {
//            DeviceViewHolder deviceHolder = (DeviceViewHolder) holder;
//            DeviceItem device = (DeviceItem) item;
//            deviceHolder.bind(device);
//        }
//    }
//}
