//package com.example.mineguard.preview.view;
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.GridLayout;
//import android.widget.LinearLayout;
//import android.widget.FrameLayout;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.fragment.app.FragmentManager;
//import com.example.mineguard.preview.model.DeviceItem;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 分屏管理器，负责管理多画面预览布局
// */
//public class SplitScreenManager {
//
//    public static final int MODE_SINGLE = 1;  // 单画面
//    public static final int MODE_VERTICAL_THREE = 3;  // 三行一列（2-3个设备）
//    public static final int MODE_QUAD = 4;    // 四画面
//    public static final int MODE_NINE = 9;    // 九画面
//
//    private Context context;
//    private FragmentManager fragmentManager;
//    private ViewGroup container;
//    private int currentMode = MODE_SINGLE;
//    private List<VideoPreviewView> previewViews;
//    private List<DeviceItem> currentDevices;
//    private List<DeviceItem> allDevices; // 所有设备列表
//    private int currentPage = 0; // 当前页码
//    private int devicesPerPage = 4; // 每页显示的设备数量
//    private OnSplitScreenChangeListener listener;
//    private boolean splitModeLocked = false; // 单设备锁定模式
//    private List<DeviceItem> backupDevices; // 备份的设备列表
//
//    public interface OnSplitScreenChangeListener {
//        void onModeChanged(int newMode);
//        void onPreviewClick(DeviceItem device, int position);
//        void onPreviewLongClick(DeviceItem device, int position);
//    }
//
//    public SplitScreenManager(Context context, FragmentManager fragmentManager, ViewGroup container) {
//        this.context = context;
//        this.fragmentManager = fragmentManager;
//        this.container = container;
//        this.previewViews = new ArrayList<>();
//        this.currentDevices = new ArrayList<>();
//        this.allDevices = new ArrayList<>();
//    }
//
//    public void setOnSplitScreenChangeListener(OnSplitScreenChangeListener listener) {
//        this.listener = listener;
//    }
//
//    /**
//     * 设置分屏模式
//     */
//    public void setSplitMode(int mode) {
//        if (splitModeLocked) {
//            return;
//        }
//        if (currentMode == mode) {
//            return;
//        }
//
//        currentMode = mode;
//        // 不在此处修改 devicesPerPage，交由调用方根据业务规则设置
//        recreateLayout();
//
//        if (listener != null) {
//            listener.onModeChanged(mode);
//        }
//    }
//
//    /**
//     * 获取当前分屏模式
//     */
//    public int getCurrentMode() {
//        return currentMode;
//    }
//
//    /**
//     * 重新创建布局
//     */
//    private void recreateLayout() {
//        // 清理现有视图
//        container.removeAllViews();
//        previewViews.clear();
//
//        // 根据模式创建新的布局
//        switch (currentMode) {
//            case MODE_SINGLE:
//                createSingleLayout();
//                break;
//            case MODE_VERTICAL_THREE:
//                createVerticalLayout();
//                break;
//            case MODE_QUAD:
//                createGridLayout(2, 2);
//                break;
//            case MODE_NINE:
//                createGridLayout(3, 3);
//                break;
//        }
//
//        // 重新设置设备
//        if (!currentDevices.isEmpty()) {
//            // 注意：不要调用 setDevices(currentDevices)  — 这会把 currentDevices 误当作完整设备列表并覆盖 allDevices，导致分页信息丢失。
//            // 仅刷新预览视图，让已有的 allDevices/currentDevices 保持不变。
//            updatePreviewViews();
//        }
//    }
//
//    /**
//     * 创建单画面布局
//     */
//    private void createSingleLayout() {
//        VideoPreviewView previewView = createPreviewView();
//        previewViews.add(previewView);
//
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        );
//        container.addView(previewView, params);
//    }
//
//    /**
//     * 创建垂直布局（用于2-3个设备，三行一列）
//     */
//    private void createVerticalLayout() {
//        LinearLayout verticalLayout = new LinearLayout(context);
//        verticalLayout.setOrientation(LinearLayout.VERTICAL);
//
//        FrameLayout.LayoutParams containerParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        );
//
//        for (int i = 0; i < 3; i++) {
//            VideoPreviewView previewView = createPreviewView();
//            previewViews.add(previewView);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    0,
//                    1.0f  // 权重相等，平均分配高度
//            );
//            params.setMargins(2, 2, 2, 2);
//            verticalLayout.addView(previewView, params);
//        }
//
//        container.addView(verticalLayout, containerParams);
//    }
//
//    /**
//     * 创建网格布局
//     */
//    private void createGridLayout(int rows, int cols) {
//        GridLayout gridLayout = new GridLayout(context);
//        gridLayout.setRowCount(rows);
//        gridLayout.setColumnCount(cols);
//        gridLayout.setOrientation(GridLayout.VERTICAL);
//        // 为 GridLayout 本身创建正确的 LayoutParams，假设其父容器是 FrameLayout
//        FrameLayout.LayoutParams gridParams = new FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//        );
//        for (int i = 0; i < rows * cols; i++) {
//            VideoPreviewView previewView = createPreviewView();
//            previewViews.add(previewView);
//            // 为 GridLayout 的子视图设置布局参数
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.width = 0;
//            params.height = 0;
//            params.columnSpec = GridLayout.spec(i % cols, 1f);
//            params.rowSpec = GridLayout.spec(i / cols, 1f);
//            params.setMargins(2, 2, 2, 2);
//            gridLayout.addView(previewView, params);
//        }
//        // 将 GridLayout 添加到父容器，并应用正确的布局参数
//        container.addView(gridLayout, gridParams);
//    }
//
//    /**
//     * 创建预览视图
//     */
//    private VideoPreviewView createPreviewView() {
//        VideoPreviewView previewView = new VideoPreviewView(context);
//
//        previewView.setOnPreviewClickListener(new VideoPreviewView.OnPreviewClickListener() {
//            @Override
//            public void onPreviewClick(DeviceItem device) {
//                int position = getPreviewPosition(previewView);
//                if (listener != null && position >= 0) {
//                    listener.onPreviewClick(device, position);
//                }
//            }
//
//            @Override
//            public void onPreviewLongClick(DeviceItem device) {
//                int position = getPreviewPosition(previewView);
//                if (listener != null && position >= 0) {
//                    listener.onPreviewLongClick(device, position);
//                }
//            }
//        });
//
//        return previewView;
//    }
//
//    /**
//     * 获取预览视图的位置
//     */
//    private int getPreviewPosition(VideoPreviewView previewView) {
//        return previewViews.indexOf(previewView);
//    }
//
//    /**
//     * 设置设备列表
//     */
//    public void setDevices(List<DeviceItem> devices) {
//        Log.d("SplitScreenManager", "setDevices called with " + devices.size() + " devices."); // 添加这行日志
//        this.allDevices = new ArrayList<>(devices);
//        this.currentPage = 0;
//        updateCurrentPageDevices();
//    }
//
//    /**
//     * 设置所有设备列表（用于分页）
//     */
//    public void setAllDevices(List<DeviceItem> devices) {
//        this.allDevices = new ArrayList<>(devices);
//        this.currentPage = 0;
//        updateCurrentPageDevices();
//    }
//
//    /**
//     * 快速查看：默认四画面，只在2-3个摄像头时使用垂直布局
//     * 1个设备：单画面
//     * 2-3个设备：三行一列（垂直布局）
//     * 4个及以上设备：四画面（2x2网格）
//     */
//    public void applyQuickView(List<DeviceItem> devices) {
//        if (devices == null) return;
//        if (splitModeLocked) return;
//        // 过滤在线设备
//        List<DeviceItem> online = new ArrayList<>();
//        for (DeviceItem d : devices) {
//            if (d != null && d.is_online()) {
//                online.add(d);
//            }
//        }
//        if (online.isEmpty()) {
//            setAllDevices(new ArrayList<>());
//            return;
//        }
//
//        // 根据在线设备数量选择布局
//        if (online.size() == 1) {
//            // 1个设备：单画面
//            devicesPerPage = 1;
//            setSplitMode(MODE_SINGLE);
//            setAllDevices(online);
//        } else if (online.size() == 2 || online.size() == 3) {
//            // 2-3个设备：三行一列布局
//            devicesPerPage = online.size();
//            setSplitMode(MODE_VERTICAL_THREE);
//            setAllDevices(online);
//        } else {
//            // 4个及以上设备：四画面布局（默认）
//            devicesPerPage = 4;
//            setSplitMode(MODE_QUAD);
//            setAllDevices(online);
//        }
//    }
//
//    /**
//     * 多画面：根据数量展示，最多九画面；超过九台按9分页。
//     */
//    public void applyMulti(List<DeviceItem> devices) {
//        if (devices == null) return;
//        if (splitModeLocked) return;
//        // 过滤在线设备
//        List<DeviceItem> online = new ArrayList<>();
//        for (DeviceItem d : devices) {
//            if (d != null && d.is_online()) {
//                online.add(d);
//            }
//        }
//        if (online.isEmpty()) {
//            setAllDevices(new ArrayList<>());
//            return;
//        }
//        devicesPerPage = Math.min(online.size(), 9);
//        setSplitMode(MODE_NINE);
//        setAllDevices(online);
//    }
//
//    /**
//     * 四画面：强制四画面展示，超过4台按4分页，不足4台直接展示全部（无分页）。
//     */
//    public void applyQuad(List<DeviceItem> devices) {
//        if (devices == null) return;
//        if (splitModeLocked) return;
//        // 过滤在线设备
//        List<DeviceItem> online = new ArrayList<>();
//        for (DeviceItem d : devices) {
//            if (d != null && d.is_online()) {
//                online.add(d);
//            }
//        }
//        if (online.isEmpty()) {
//            setAllDevices(new ArrayList<>());
//            return;
//        }
//        devicesPerPage = Math.min(online.size(), 4);
//        setSplitMode(MODE_QUAD);
//        setAllDevices(online);
//    }
//
//    public void enterSingleDeviceMode(DeviceItem device) {
//        // 进入单设备锁定模式
//        this.splitModeLocked = true;
//        // 备份当前设备列表
//        this.backupDevices = new ArrayList<>(this.allDevices);
//        // 设置为单画面并只显示该设备
//        this.currentMode = MODE_SINGLE;
//        this.currentDevices.clear();
//        this.currentDevices.add(device);
//        // 重新创建布局并更新视图
//        recreateLayout();
//    }
//
//    public void unlockSplitMode() {
//        // 解除锁定，恢复原设备列表和布局
//        this.splitModeLocked = false;
//        if (this.backupDevices != null) {
//            this.allDevices = new ArrayList<>(this.backupDevices);
//            this.currentPage = 0;
//            updateCurrentPageDevices();
//        } else {
//            // 如果没有备份，则保持当前设备
//            updatePreviewViews();
//        }
//    }
//
//    public boolean isSplitModeLocked() {
//        return this.splitModeLocked;
//    }
//
//    /**
//     * 更新当前页的设备列表
//     */
//    private void updateCurrentPageDevices() {
//        Log.d("SplitScreenManager", "updateCurrentPageDevices: currentPage=" + currentPage +
//                ", devicesPerPage=" + devicesPerPage + ", total devices=" + allDevices.size());
//
//        currentDevices.clear();
//
//        if (devicesPerPage <= 0) {
//            Log.w("SplitScreenManager", "devicesPerPage is invalid: " + devicesPerPage);
//            return;
//        }
//
//        int startIndex = currentPage * devicesPerPage;
//        int endIndex = Math.min(startIndex + devicesPerPage, allDevices.size());
//
//        Log.d("SplitScreenManager", "Loading devices from " + startIndex + " to " + endIndex);
//
//        for (int i = startIndex; i < endIndex; i++) {
//            if (i >= 0 && i < allDevices.size()) {
//                currentDevices.add(allDevices.get(i));
//            }
//        }
//
//        // 根据当前页的设备数量动态调整布局
//        int currentPageDeviceCount = currentDevices.size();
//        Log.d("SplitScreenManager", "Current page device count: " + currentPageDeviceCount +
//                ", current mode: " + currentMode);
//
//        int newMode = currentMode;  // 记录需要切换到的新模式
//
//        // 如果当前页只有2-3个设备，且不是单页面模式，则需要切换到垂直布局
//        if ((currentPageDeviceCount == 2 || currentPageDeviceCount == 3) &&
//            currentMode != MODE_SINGLE && currentMode != MODE_VERTICAL_THREE) {
//            Log.d("SplitScreenManager", "Current page has " + currentPageDeviceCount +
//                    " devices, switching to VERTICAL_THREE layout");
//            newMode = MODE_VERTICAL_THREE;
//        }
//        // 如果当前页有4个或以上设备，且当前是垂直布局，则需要切换回四画面
//        else if (currentPageDeviceCount >= 4 && currentMode == MODE_VERTICAL_THREE) {
//            Log.d("SplitScreenManager", "Current page has " + currentPageDeviceCount +
//                    " devices, switching back to QUAD layout");
//            newMode = MODE_QUAD;
//        }
//
//        // 如果需要切换模式，则调用 setSplitMode 来安全地切换
//        if (newMode != currentMode) {
//            currentMode = newMode;
//            recreateLayout();
//            // recreateLayout 内部会调用 updatePreviewViews()
//            if (listener != null) {
//                listener.onModeChanged(currentMode);
//            }
//        } else {
//            // 不需要切换模式，仅更新预览视图
//            updatePreviewViews();
//        }
//
//        // 注意：不再调用 smartLoadVisibleVideos()，因为它会错误地暂停正在播放的视频
//        // smartLoadVisibleVideos() 应该只在需要时手动调用，例如在 Fragment 可见性改变时
//    }
//
//    /**
//     * 切换到下一页
//     */
//    public boolean nextPage() {
//        if (splitModeLocked) {
//            return false;
//        }
//
//        // 防守检查
//        if (devicesPerPage <= 0) {
//            Log.w("SplitScreenManager", "devicesPerPage is invalid: " + devicesPerPage);
//            return false;
//        }
//
//        int maxPage = (int) Math.ceil((double) allDevices.size() / devicesPerPage) - 1;
//        Log.d("SplitScreenManager", "nextPage: currentPage=" + currentPage + ", maxPage=" + maxPage +
//                ", devicesPerPage=" + devicesPerPage + ", total devices=" + allDevices.size());
//
//        if (currentPage < maxPage) {
//            currentPage++;
//            updateCurrentPageDevices();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 切换到上一页
//     */
//    public boolean previousPage() {
//        if (splitModeLocked) {
//            return false;
//        }
//        if (currentPage > 0) {
//            currentPage--;
//            updateCurrentPageDevices();
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 获取当前页码
//     */
//    public int getCurrentPage() {
//        return currentPage;
//    }
//
//    /**
//     * 获取总页数
//     */
//    public int getTotalPages() {
//        if (devicesPerPage <= 0) {
//            Log.w("SplitScreenManager", "devicesPerPage is " + devicesPerPage + ", returning 1");
//            return 1;
//        }
//        return (int) Math.ceil((double) allDevices.size() / devicesPerPage);
//    }
//
//    /**
//     * 设置每页显示的设备数量
//     */
//    public void setDevicesPerPage(int count) {
//        this.devicesPerPage = count;
//        this.currentPage = 0;
//        if (!allDevices.isEmpty()) {
//            updateCurrentPageDevices();
//        }
//    }
//
//    /**
//     * 更新预览视图
//     */
//    private void updatePreviewViews() {
//        // 防御性检查
//        if (previewViews == null || previewViews.isEmpty()) {
//            Log.w("SplitScreenManager", "previewViews is null or empty!");
//            return;
//        }
//
//        int maxViews = Math.min(previewViews.size(), currentDevices.size());
//
//        for (int i = 0; i < previewViews.size(); i++) {
//            VideoPreviewView view = previewViews.get(i);
//            if (view == null) {
//                Log.w("SplitScreenManager", "PreviewView at index " + i + " is null!");
//                continue;
//            }
//
//            if (i < maxViews) {
//                DeviceItem device = currentDevices.get(i);
//                if (device != null) {
//                    view.setDevice(device);
//                    view.setVisibility(View.VISIBLE);
//                    Log.d("SplitScreenManager", "Updating view at index " + i + " for device: " + device.getName());
//                }
//            } else {
//                view.setDevice(null);
//                view.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    /**
//     * 添加设备
//     */
//    public void addDevice(DeviceItem device) {
//        currentDevices.add(device);
//        updatePreviewViews();
//    }
//
//    /**
//     * 移除设备
//     */
//    public void removeDevice(DeviceItem device) {
//        currentDevices.remove(device);
//        updatePreviewViews();
//    }
//
//    /**
//     * 更新设备
//     */
//    public void updateDevice(DeviceItem device) {
//        int index = -1;
//        for (int i = 0; i < currentDevices.size(); i++) {
//            if (currentDevices.get(i).getId()==device.getId()) {
//                index = i;
//                break;
//            }
//        }
//
//        if (index >= 0) {
//            currentDevices.set(index, device);
//            updatePreviewViews();
//        }
//    }
//
//    /**
//     * 获取指定位置的设备
//     */
//    public DeviceItem getDeviceAt(int position) {
//        if (position >= 0 && position < currentDevices.size()) {
//            return currentDevices.get(position);
//        }
//        return null;
//    }
//
//    /**
//     * 获取指定位置的预览视图
//     */
//    public VideoPreviewView getPreviewViewAt(int position) {
//        if (position >= 0 && position < previewViews.size()) {
//            return previewViews.get(position);
//        }
//        return null;
//    }
//
//    /**
//     * 暂停所有预览（节省系统资源）
//     */
//    public void pauseAllPreviews() {
//        Log.d("SplitScreenManager", "pauseAllPreviews called, pausing " + previewViews.size() + " views");
//        for (VideoPreviewView previewView : previewViews) {
//            if (previewView != null) {
//                previewView.pausePlayback();
//            }
//        }
//    }
//
//    /**
//     * 恢复所有预览
//     */
//    public void resumeAllPreviews() {
//        Log.d("SplitScreenManager", "resumeAllPreviews called, resuming " + previewViews.size() + " views");
//        for (VideoPreviewView previewView : previewViews) {
//            if (previewView != null) {
//                previewView.resumePlayback();
//            }
//        }
//    }
//
//    /**
//     * 智能加载：只加载当前页可见的视频，暂停不可见的视频
//     * 这有助于在多画面模式下节省系统资源
//     * 注意：这个方法应该在 Fragment 可见性改变或需要时手动调用，而不是在每次更新时调用
//     */
//    public void smartLoadVisibleVideos() {
//        Log.d("SplitScreenManager", "smartLoadVisibleVideos called, currentDevices size: " +
//                (currentDevices != null ? currentDevices.size() : 0) + ", previewViews size: " + previewViews.size());
//
//        if (currentDevices == null || currentDevices.isEmpty()) {
//            Log.d("SplitScreenManager", "No current devices, pausing all");
//            // 没有设备时，暂停所有播放
//            pauseAllPreviews();
//            return;
//        }
//
//        for (int i = 0; i < previewViews.size(); i++) {
//            VideoPreviewView previewView = previewViews.get(i);
//            if (previewView == null) continue;
//
//            // 检查这个视图是否对应当前页的设备
//            boolean isVisible = i < currentDevices.size();
//
//            if (isVisible) {
//                Log.d("SplitScreenManager", "Resuming video at position " + i);
//                previewView.resumePlayback();
//            } else {
//                Log.d("SplitScreenManager", "Pausing video at position " + i);
//                previewView.pausePlayback();
//            }
//        }
//    }
//
//    /**
//     * 释放所有资源
//     */
//    public void release() {
//        Log.d("SplitScreenManager", "Releasing all resources. Total views: " + previewViews.size());
//        for (VideoPreviewView previewView : previewViews) {
//            if (previewView != null) {
//                previewView.release();
//            }
//        }
//        previewViews.clear();
//        currentDevices.clear();
//        allDevices.clear();
//    }
//
//    /**
//     * 获取当前显示的设备数量
//     */
//    public int getVisibleDeviceCount() {
//        return Math.min(previewViews.size(), currentDevices.size());
//    }
//
//    /**
//     * 获取最大可显示的设备数量
//     */
//    public int getMaxDeviceCount() {
//        return previewViews.size();
//    }
//
//    /**
//     * 检查是否可以添加更多设备
//     */
//    public boolean canAddMoreDevices() {
//        return currentDevices.size() < previewViews.size();
//    }
//}
