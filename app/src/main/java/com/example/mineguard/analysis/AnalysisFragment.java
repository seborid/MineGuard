package com.example.mineguard.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mineguard.MainActivity; // 导入 MainActivity
import com.example.mineguard.R;
import com.example.mineguard.alarm.model.AlarmItem; // 导入 AlarmItem
import com.example.mineguard.data.DeviceViewModel;

import java.util.ArrayList;
import java.util.List;

// 1. 实现监听接口 MainActivity.OnAlarmReceivedListener
public class AnalysisFragment extends Fragment implements MainActivity.OnAlarmReceivedListener {

    private View grid1View;
    private View grid4View;

    private PlayerView playerView;
    private ExoPlayer player;
    private String rtspUrl = "rtsp://192.168.31.64/live/raw";

    private SurfaceView[] gridSurfaceViews = new SurfaceView[4];
    private ExoPlayer[] gridPlayers = new ExoPlayer[4];
    private String[] gridUrls;

    private DeviceViewModel deviceViewModel;
    private SimpleDeviceAdapter deviceAdapter;

    // === 2. 新增报警列表相关变量 ===
    private RecyclerView rvAlarmList;
    private AlarmAdapter alarmAdapter; // 注意这里用的是 analysis 包下的 Adapter
    private List<AlarmItem> displayAlarmList = new ArrayList<>(); // 真实数据列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 初始化视图控件
        playerView = view.findViewById(R.id.player_view_main);
        gridSurfaceViews[0] = view.findViewById(R.id.sv_cam_01);
        gridSurfaceViews[1] = view.findViewById(R.id.sv_cam_02);
        gridSurfaceViews[2] = view.findViewById(R.id.sv_cam_03);
        gridSurfaceViews[3] = view.findViewById(R.id.sv_cam_04);
        gridUrls = new String[] { rtspUrl, rtspUrl, rtspUrl, rtspUrl };
        grid1View = view.findViewById(R.id.grid_1_view);
        grid4View = view.findViewById(R.id.grid_4_view);

        // 初始化设备列表 (ViewModel)
        deviceViewModel = new ViewModelProvider(requireActivity()).get(DeviceViewModel.class);
        RecyclerView rvDeviceList = view.findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));
        deviceAdapter = new SimpleDeviceAdapter(new ArrayList<>());
        rvDeviceList.setAdapter(deviceAdapter);
        deviceViewModel.getLiveDeviceList().observe(getViewLifecycleOwner(), deviceItems -> {
            deviceAdapter.setDeviceList(deviceItems);
        });

        // === 3. 初始化报警列表 (关键修改) ===
        rvAlarmList = view.findViewById(R.id.rv_alarm_list);
        rvAlarmList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 这里的 displayAlarmList 一开始是空的，稍后在 onResume 加载
        alarmAdapter = new AlarmAdapter(displayAlarmList);
        rvAlarmList.setAdapter(alarmAdapter);

        // 按钮事件绑定
        setupClickListeners(view);
    }

    // === 4. 生命周期管理：注册/注销监听 ===

    @Override
    public void onResume() {
        super.onResume();
        // 只有当宿主是 MainActivity 时才执行
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();

            // 注册监听器：有新报警时通知我
            mainActivity.addAlarmListener(this);

            // 每次页面显示时，从全局列表同步一次最新数据 (防止漏掉)
            loadDataFromActivity(mainActivity);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 页面不可见时，取消监听，节省资源
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).removeAlarmListener(this);
        }
    }

    // === 5. 从 MainActivity 获取历史数据 ===
    private void loadDataFromActivity(MainActivity mainActivity) {
        List<AlarmItem> globalList = mainActivity.getGlobalAlarmList();
        if (globalList != null) {
            displayAlarmList.clear();
            // 复制一份，避免直接操作源数据
            displayAlarmList.addAll(globalList);
            alarmAdapter.notifyDataSetChanged();
        }
    }

    // === 6. 实现接口回调：收到新报警 ===
    @Override
    public void onNewAlarm(AlarmItem item) {
        // 在 UI 线程更新
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                // 将新报警添加到列表头部
                displayAlarmList.add(0, item);
                alarmAdapter.notifyItemInserted(0);
                // 滚动到顶部，确保用户看到最新消息
                rvAlarmList.scrollToPosition(0);
            });
        }
    }

    // --- 以下是原有的播放器和按钮逻辑 (保持不变) ---

    private void setupClickListeners(View view) {
        ImageButton btnGrid1 = view.findViewById(R.id.btn_grid_1);
        ImageButton btnGrid4 = view.findViewById(R.id.btn_grid_4);
        Button btnDisarm = view.findViewById(R.id.btn_disarm);
        Button btnClose = view.findViewById(R.id.btn_close);
        Button btnIntercom = view.findViewById(R.id.btn_intercom);

        btnGrid1.setOnClickListener(v -> {
            grid1View.setVisibility(View.VISIBLE);
            grid4View.setVisibility(View.GONE);
            initializePlayer();
            if (player != null) player.play();
            stopGridPlayers();
            Toast.makeText(getContext(), "切换至单路视频", Toast.LENGTH_SHORT).show();
        });

        btnGrid4.setOnClickListener(v -> {
            grid1View.setVisibility(View.GONE);
            grid4View.setVisibility(View.VISIBLE);
            if (player != null) player.pause();
            initGridPlayers();
            Toast.makeText(getContext(), "切换至四路视频", Toast.LENGTH_SHORT).show();
        });

        btnDisarm.setOnClickListener(v -> Toast.makeText(getContext(), "撤防指令已发送", Toast.LENGTH_SHORT).show());
        btnClose.setOnClickListener(v -> Toast.makeText(getContext(), "关闭操作", Toast.LENGTH_SHORT).show());
        btnIntercom.setOnClickListener(v -> Toast.makeText(getContext(), "开启对讲", Toast.LENGTH_SHORT).show());
    }

    private void initializePlayer() {
        if (player == null) {
            player = new ExoPlayer.Builder(requireContext()).build();
            playerView.setPlayer(player);
            MediaItem mediaItem = MediaItem.fromUri(rtspUrl);
            player.setMediaItem(mediaItem);
            player.prepare();
        }
        if (grid1View.getVisibility() == View.VISIBLE) {
            player.play();
        }
    }

    private void initGridPlayers() {
        for (int i = 0; i < 4; i++) {
            if (gridPlayers[i] == null) {
                ExoPlayer.Builder builder = new ExoPlayer.Builder(requireContext());
                gridPlayers[i] = builder.build();
                gridPlayers[i].setVideoSurfaceView(gridSurfaceViews[i]);
                gridPlayers[i].setVolume(0f);
            }
            if (!gridPlayers[i].isPlaying()) {
                MediaItem mediaItem = MediaItem.fromUri(gridUrls[i]);
                MediaSource mediaSource = new RtspMediaSource.Factory()
                        .setForceUseRtpTcp(true)
                        .createMediaSource(mediaItem);
                gridPlayers[i].setMediaSource(mediaSource);
                gridPlayers[i].prepare();
                gridPlayers[i].play();
            }
        }
    }

    private void stopGridPlayers() {
        for (int i = 0; i < 4; i++) {
            if (gridPlayers[i] != null) {
                gridPlayers[i].stop();
                gridPlayers[i].release();
                gridPlayers[i] = null;
            }
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();
        stopGridPlayers();
    }
}