package com.example.mineguard.preview;

import static com.example.mineguard.MyApplication.globalIP;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineguard.R;
import com.example.mineguard.preview.adapter.DeviceAdapter;
import com.example.mineguard.preview.model.DeviceItem;
import com.example.mineguard.preview.model.CameraList;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.example.mineguard.preview.view.ControlButtonsView;
import com.example.mineguard.preview.view.SplitScreenManager;
import com.example.mineguard.preview.view.VideoPreviewView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

/**
 * 实时预览Fragment
 * 实现设备列表展示、视频预览、分屏功能、截图录像对讲等功能
 */
public class PreviewFragment extends Fragment implements
        DeviceAdapter.OnDeviceClickListener,
        SplitScreenManager.OnSplitScreenChangeListener,
        ControlButtonsView.OnControlClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    // UI组件
    private RecyclerView recyclerViewDevices;
    private LinearLayout layoutDeviceList;
    private FrameLayout layoutVideoPreview;
    private LinearLayout layoutEmpty;
    private LinearLayout splitModeSelector;
    private LinearLayout videoSwitchControls;
    private ViewGroup videoContainer;
    private ControlButtonsView controlButtons;
    private TextView tvStatusInfo;
    private TextView tvDeviceCount;
    private TextView tvBottomDeviceCount;
    private TextView tvPageInfo;
    private Button btnSplitMode;
    private Button btnRefresh;
    private Button btnPreviousPage;
    private Button btnNextPage;

    // 适配器和管理器
    private DeviceAdapter deviceAdapter;
    private SplitScreenManager splitScreenManager;

    // 数据
    private CameraList cameraList;
    private List<DeviceItem> deviceList;
    private boolean isVideoMode = false;
    private DeviceItem currentSelectedDevice;
    private List<DeviceItem> currentPreviewDevices = new ArrayList<>();

    // Handler用于定时更新
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;
    // 控制按钮自动隐藏相关
    private Runnable autoHideControlsRunnable;
    private static final long AUTO_HIDE_DELAY_MS = 5000;

    public PreviewFragment() {
        // Required empty public constructor
    }

    public static PreviewFragment newInstance(String param1, String param2) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);
        
        initViews(view);
        setupRecyclerView();
        setupSplitScreenManager();
        setupClickListeners();
        
        // 初始化cameraList避免空指针
        if (cameraList == null) {
            cameraList = new CameraList();
        }
        
        getCameras();
        // splitScreenManager.setSplitMode(SplitScreenManager.MODE_QUAD); // <--- 添加这一行，设置一个默认的单分屏模式
        return view;
    }

    private void initViews(View view) {
        recyclerViewDevices = view.findViewById(R.id.recyclerView_devices);
        layoutDeviceList = view.findViewById(R.id.layout_device_list);
        layoutVideoPreview = view.findViewById(R.id.layout_video_preview);
        layoutEmpty = view.findViewById(R.id.layout_empty);
        splitModeSelector = view.findViewById(R.id.split_mode_selector);
        videoSwitchControls = view.findViewById(R.id.video_switch_controls);
        videoContainer = view.findViewById(R.id.video_container);
        controlButtons = view.findViewById(R.id.control_buttons);
        // 添加这一行，将当前Fragment设置为controlButtons的监听器
        controlButtons.setOnControlClickListener(this);
        tvStatusInfo = view.findViewById(R.id.tv_status_info);
        tvDeviceCount = view.findViewById(R.id.tv_device_count);
        tvBottomDeviceCount = view.findViewById(R.id.tv_bottom_device_count);
        tvPageInfo = view.findViewById(R.id.tv_page_info);
        btnSplitMode = view.findViewById(R.id.btn_split_mode);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnPreviousPage = view.findViewById(R.id.btn_previous_page);
        btnNextPage = view.findViewById(R.id.btn_next_page);

        // 单画面按钮点击事件
        view.findViewById(R.id.btn_mode_single).setOnClickListener(v -> {
            // 切到单画面时，按照在线设备进行分页，确保上一页/下一页可用
            List<DeviceItem> source = currentPreviewDevices != null ? currentPreviewDevices : deviceList;
            List<DeviceItem> online = new ArrayList<>();
            if (source != null) {
                for (DeviceItem d : source) {
                    if (d.is_online()) online.add(d);
                }
            }
            if (online.isEmpty()) {
                Toast.makeText(requireContext(), "没有在线摄像头", Toast.LENGTH_SHORT).show();
                return;
            }
            splitScreenManager.setAllDevices(online);
            splitScreenManager.setDevicesPerPage(1); // 单画面每页显示1台设备
            splitScreenManager.setSplitMode(SplitScreenManager.MODE_SINGLE);
            updatePageInfo();
            showVideoSwitchControls();
            splitModeSelector.setVisibility(View.GONE);
        });

        // 四画面按钮点击事件
        view.findViewById(R.id.btn_mode_quad).setOnClickListener(v -> {
            // 使用当前预览设备列表
            List<DeviceItem> source = currentPreviewDevices != null ? currentPreviewDevices : deviceList;
            List<DeviceItem> online = new ArrayList<>();
            if (source != null) {
                for (DeviceItem d : source) {
                    if (d.is_online()) online.add(d);
                }
            }
            if (online.isEmpty()) {
                Toast.makeText(requireContext(), "没有在线摄像头", Toast.LENGTH_SHORT).show();
                return;
            }
            splitScreenManager.setAllDevices(online);
            // 不超过4台则一次性展示完（无分页），否则按4分页
            int per = Math.min(online.size(), 4);
            splitScreenManager.setDevicesPerPage(per);
            splitScreenManager.setSplitMode(SplitScreenManager.MODE_QUAD);
            updatePageInfo();
            showVideoSwitchControls();
            splitModeSelector.setVisibility(View.GONE);
        });
       // 九画面按钮点击事件
        view.findViewById(R.id.btn_mode_nine).setOnClickListener(v -> {
            List<DeviceItem> source = currentPreviewDevices != null ? currentPreviewDevices : deviceList;
            List<DeviceItem> online = new ArrayList<>();
            if (source != null) {
                for (DeviceItem d : source) {
                    if (d.is_online()) online.add(d);
                }
            }
            if (online.isEmpty()) {
                Toast.makeText(requireContext(), "没有在线摄像头", Toast.LENGTH_SHORT).show();
                return;
            }
            splitScreenManager.applyMulti(online);
            updatePageInfo();
            showVideoSwitchControls();
            splitModeSelector.setVisibility(View.GONE);
        });

    }

    private void setupRecyclerView() {
        deviceList = new ArrayList<>();
        deviceAdapter = new DeviceAdapter(this);
        
        recyclerViewDevices.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerViewDevices.setAdapter(deviceAdapter);
    }

    private void setupSplitScreenManager() {
        splitScreenManager = new SplitScreenManager(requireContext(), 
                getChildFragmentManager(), videoContainer);
        splitScreenManager.setOnSplitScreenChangeListener(this);
        if (deviceAdapter != null) {
            deviceAdapter.setSplitScreenManager(splitScreenManager);
        }
    }

    private void setupClickListeners() {
        btnSplitMode.setOnClickListener(v -> {
            if (splitScreenManager.isSplitModeLocked()) {
                Toast.makeText(requireContext(), "已锁定至单设备预览，分屏模式不可用", Toast.LENGTH_SHORT).show();
                return;
            }
            if (isVideoMode) {
                toggleSplitModeSelector();
            } else {
                // 切换到视频预览模式
                Log.d(TAG, "setupClickListeners: 切换到视频预览模式 ");
                switchToVideoMode();
            }
        });

        btnRefresh.setOnClickListener(v -> {
            if(isVideoMode){
                Toast.makeText(requireContext(), "视频预览模式下无法刷新设备列表", Toast.LENGTH_SHORT).show();
                return;
            }
            getCameras(); 
            Toast.makeText(requireContext(), "设备列表已刷新", Toast.LENGTH_SHORT).show();
        });

        // 视频切换按钮事件
        btnPreviousPage.setOnClickListener(v -> {
            if (splitScreenManager.previousPage()) {
                updatePageInfo();
                Toast.makeText(requireContext(), "切换到上一页", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "已经是第一页", Toast.LENGTH_SHORT).show();
            }
        });

        btnNextPage.setOnClickListener(v -> {
            if (splitScreenManager.nextPage()) {
                updatePageInfo();
                Toast.makeText(requireContext(), "切换到下一页", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "已经是最后一页", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDeviceData() {
        deviceAdapter.updateDevices(deviceList);
        updateDeviceCount();
        updateEmptyState();
    }

    private void getCameras() {
        getCameras(null, null, null);
    }

    private void getCameras(String id, Integer page, Integer limitNum) {
        okhttp3.OkHttpClient client = new okhttp3.OkHttpClient();

        // 构建URL和参数
        okhttp3.HttpUrl.Builder urlBuilder = okhttp3.HttpUrl.parse("http://" + globalIP + ":5004/data/cameras").newBuilder();
        if (id != null && !id.isEmpty()) {
            urlBuilder.addQueryParameter("id", id);
        }
        if (page != null) {
            urlBuilder.addQueryParameter("page", String.valueOf(page));
        }
        if (limitNum != null) {
            urlBuilder.addQueryParameter("limitNum", String.valueOf(limitNum));
        }
        String url = urlBuilder.build().toString();

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, java.io.IOException e) {
                Log.e(TAG, "请求摄像头列表失败: " + e.getMessage());
                if (isAdded()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "请求摄像头列表失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws java.io.IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "摄像头列表返回数据: " + responseBody);
                    try {
                        Gson gson = new Gson();
                        CameraList parsed = gson.fromJson(responseBody, CameraList.class);
                        cameraList = parsed;

                        List<DeviceItem> items = new ArrayList<>();
                        if (parsed != null && parsed.getData() != null && parsed.getData().getList() != null) {
                            for (CameraList.Camera c : parsed.getData().getList()) {
                                DeviceItem item = new DeviceItem(
                                        c.getId(),
                                        c.getName() != null ? c.getName() : "未命名",
                                        c.getIp()!= null? c.getIp() : "未设置Ip",
                                        c.getAdmin()!= null? c.getAdmin() : "未设置管理员",
                                        c.getPasswd()!= null? c.getPasswd() : "未设置密码",
                                        c.getFlow()!= null? c.getFlow() : "未设置rtsp流",
                                        c.isIs_online(),
                                        c.getLocation()!= null? c.getLocation() : "未设置位置",
                                        c.getBoard_ip()!= null? c.getBoard_ip() : "未设置板卡Ip",
                                        c.getAlgorithm()!= null? c.getAlgorithm() : "未设置算法"
                                        );
                                items.add(item);
                            }
                        }

                        if (isAdded()) {
                            requireActivity().runOnUiThread(() -> {
                                if (deviceList == null) {
                                    deviceList = new ArrayList<>();
                                }
                                deviceList.clear();
                                deviceList.addAll(items);
                                
                                // 更新适配器
                                if (deviceAdapter != null) {
                                    deviceAdapter.updateDevices(deviceList);
                                }
                                
                                // 更新设备计数（基于cameraList的meta信息）
                                if (tvDeviceCount != null && tvBottomDeviceCount != null) {
                                    updateDeviceCount();
                                }
                                
                                // 更新空状态显示
                                if (layoutEmpty != null && layoutDeviceList != null) {
                                    updateEmptyState();
                                }
                                
                                // 如果在视频预览模式，更新分屏管理器
                                if (isVideoMode && splitScreenManager != null) {
                                    splitScreenManager.setAllDevices(deviceList);
                                    updatePageInfo();
                                    showVideoSwitchControls();
                                }
                            });
                        }
                    } catch (JsonSyntaxException je) {
                        Log.e(TAG, "摄像头列表解析失败: " + je.getMessage());
                    } catch (Exception ex) {
                        Log.e(TAG, "处理摄像头列表时发生错误: " + ex.getMessage());
                    }
                } else {
                    Log.e(TAG, "请求摄像头列表失败，状态码: " + response.code());
                    if (isAdded()) {
                        final int code = response.code();
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "摄像头列表请求失败，状态码: " + code, Toast.LENGTH_SHORT).show()
                        );
                    }
                }
            }
        });
    }


    private void updateDeviceCount() {
        // 确保TextView已初始化
        if (tvDeviceCount == null || tvBottomDeviceCount == null) {
            return;
        }
        
        // 计算在线设备数和总设备数
        int totalCount = 0;
        int onlineCount = 0;
        
        if (deviceList != null && !deviceList.isEmpty()) {
            totalCount = deviceList.size();
            for (DeviceItem device : deviceList) {
                if (device.is_online()) {
                    onlineCount++;
                }
            }
        } else if (cameraList != null && cameraList.getMeta() != null) {
            // 如果deviceList为空，使用cameraList的meta信息
            totalCount = cameraList.getMeta().getTotal();
            onlineCount = 0;
        }
        
        String countText = onlineCount + "/" + totalCount + " 在线";
        tvDeviceCount.setText(countText);
        tvBottomDeviceCount.setText(countText);
        
        Log.d(TAG, "updateDeviceCount: online=" + onlineCount + ", total=" + totalCount);
    }
    private void updateEmptyState() {
        if (deviceList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutDeviceList.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            layoutDeviceList.setVisibility(View.VISIBLE);
        }
    }

    private void switchToVideoMode() {
        isVideoMode = true;
        layoutDeviceList.setVisibility(View.GONE);
        layoutVideoPreview.setVisibility(View.VISIBLE);
        btnSplitMode.setText("分屏模式");
        tvStatusInfo.setText("视频预览模式");
        // 默认启用分屏按钮
        btnSplitMode.setEnabled(true);

        // 设置默认设备到分屏管理器
        List<DeviceItem> allOnline = new ArrayList<>();
        for (DeviceItem device : deviceList) {
            if (device.is_online()) {
                allOnline.add(device);
            }
        }
        currentPreviewDevices = allOnline;
        List<DeviceItem> onlineDevices = new ArrayList<>();
        for (int i = 0; i < Math.min(4, allOnline.size()); i++) {
            onlineDevices.add(allOnline.get(i));
        }

        if (!onlineDevices.isEmpty()) {
            splitScreenManager.setAllDevices(onlineDevices);
            updatePageInfo();
            showVideoSwitchControls();
            showControlsWithAutoHide();
        }
    }

    private void switchToDeviceList() {
        isVideoMode = false;
        layoutDeviceList.setVisibility(View.VISIBLE);
        layoutVideoPreview.setVisibility(View.GONE);
        btnSplitMode.setText("视频预览");
        tvStatusInfo.setText("设备列表");
        // 返回列表时恢复分屏按钮可用
        btnSplitMode.setEnabled(true);
        if (autoHideControlsRunnable != null) {
            handler.removeCallbacks(autoHideControlsRunnable);
        }
        controlButtons.hideControls();
        splitModeSelector.setVisibility(View.GONE);
        videoSwitchControls.setVisibility(View.GONE);
    }

    private void showVideoSwitchControls() {
        int totalPages = splitScreenManager.getTotalPages();
        if (totalPages > 1) {
            videoSwitchControls.setVisibility(View.VISIBLE);
            showControlsWithAutoHide();
        } else {
            videoSwitchControls.setVisibility(View.GONE);
        }
    }


    private void updatePageInfo() {
        int currentPage = splitScreenManager.getCurrentPage() + 1; // 页码从1开始显示
        int totalPages = splitScreenManager.getTotalPages();
        tvPageInfo.setText(currentPage + "/" + totalPages);
    }

    private void toggleSplitModeSelector() {
        if (splitModeSelector.getVisibility() == View.VISIBLE) {
            splitModeSelector.setVisibility(View.GONE);
        } else {
            splitModeSelector.setVisibility(View.VISIBLE);
        }
    }

    private void showControlsWithAutoHide() {
        if (controlButtons == null) return;
        if (autoHideControlsRunnable == null) {
            autoHideControlsRunnable = new Runnable() {
                @Override
                public void run() {
                    controlButtons.hideControls();
                }
            };
        }
        handler.removeCallbacks(autoHideControlsRunnable);
        controlButtons.bringToFront();
        controlButtons.showControls();
        handler.postDelayed(autoHideControlsRunnable, AUTO_HIDE_DELAY_MS);
    }

    // private void startPeriodicUpdate() {
    //     updateRunnable = new Runnable() {
    //         @Override
    //         public void run() {
    //             // 模拟设备状态更新
    //             updateDeviceStatus();
    //             handler.postDelayed(this, 30000); // 每30秒更新一次
    //         }
    //     };
    //     handler.post(updateRunnable);
    // }

    // private void updateDeviceStatus() {
    //     getCameras();
    //     if (splitScreenManager != null && isVideoMode) {
    //         // 更新分屏管理器中的设备状态
    //         for (int i = 0; i < splitScreenManager.getVisibleDeviceCount(); i++) {
    //             DeviceItem device = splitScreenManager.getDeviceAt(i);
    //             if (device != null) {
    //                 for (DeviceItem updatedDevice : deviceList) {
    //                     if (updatedDevice.getId().equals(device.getId())) {
    //                         splitScreenManager.updateDevice(updatedDevice);
    //                         break;
    //                     }
    //                 }
    //             }
    //         }
    //     }   
    // }

    // DeviceAdapter.OnDeviceClickListener 实现
    @Override
    public void onDeviceClick(DeviceItem device) {
        currentSelectedDevice = device;
        if (device.is_online()) {
            switchToVideoMode();
            // 单设备锁定预览，禁用分屏按钮并隐藏分页控件
            splitScreenManager.enterSingleDeviceMode(device);
            btnSplitMode.setEnabled(false);
            videoSwitchControls.setVisibility(View.GONE);
        } else {
            Toast.makeText(requireContext(), "该设备不支持视频预览", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDeviceLongClick(DeviceItem device) {
        // 长按显示设备详情
        Toast.makeText(requireContext(), "设备: " + device.getName() + 
                "\n状态: " + device.is_online() +
                "\n位置: " + device.getLocation(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onQuickViewClick(String region, List<DeviceItem> devices) {
        // 过滤出在线的摄像头设备
        List<DeviceItem> onlineDevices = new ArrayList<>();
        for (DeviceItem device : devices) {
            if (device.is_online()) {
                onlineDevices.add(device);
            }
        }
        if (onlineDevices.isEmpty()) {
            Toast.makeText(requireContext(), "该区域没有在线的摄像头设备", Toast.LENGTH_SHORT).show();
            return;
        }
        // 解除单设备锁定并启用分屏按钮
        splitScreenManager.unlockSplitMode();
        btnSplitMode.setEnabled(true);
        // 切换到视频预览模式
        switchToVideoMode();
        // 记录当前区域的预览设备列表，便于“多画面”使用
        currentPreviewDevices = onlineDevices;
        // 应用快速查看规则
        splitScreenManager.applyQuickView(onlineDevices);
        updatePageInfo();
        showVideoSwitchControls();
        showControlsWithAutoHide();
        Toast.makeText(requireContext(), "正在查看 " + region + " 的摄像头", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onModeChanged(int newMode) {
        String modeText = "";
        switch (newMode) {
            case SplitScreenManager.MODE_SINGLE:
                modeText = "单画面";
                break;
            case SplitScreenManager.MODE_QUAD:
                modeText = "四画面";
                break;
            case SplitScreenManager.MODE_NINE:
                modeText = "九画面";
                break;
        }
        Toast.makeText(requireContext(), "切换到" + modeText + "模式", Toast.LENGTH_SHORT).show();

        showVideoSwitchControls();
        showControlsWithAutoHide();
    }

    @Override
    public void onPreviewClick(DeviceItem device, int position) {
        currentSelectedDevice = device;
        controlButtons.setDevice(device);
        showControlsWithAutoHide();
    }

    @Override
    public void onPreviewLongClick(DeviceItem device, int position) {
        Toast.makeText(requireContext(), "设备: " + device.getName() + 
                "\n状态: " + device.is_online(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onScreenshotClick(DeviceItem device) {
        Toast.makeText(requireContext(), "截图: " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRecordClick(DeviceItem device, boolean start) {
        String message = start ? "开始录像: " + device.getName() : "停止录像: " + device.getName();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        device.setRecording(start);
    }

    @Override
    public void onTalkClick(DeviceItem device, boolean start) {
        String message = start ? "开始对讲: " + device.getName() : "停止对讲: " + device.getName();
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        device.setTalking(start);
    }

    @Override
    public void onFullscreenClick(DeviceItem device) {
        Toast.makeText(requireContext(), "全屏: " + device.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCloseClick(DeviceItem device) {
        controlButtons.hideControls();
        switchToDeviceList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
        if (splitScreenManager != null) {
            splitScreenManager.release();
        }
    }
}
