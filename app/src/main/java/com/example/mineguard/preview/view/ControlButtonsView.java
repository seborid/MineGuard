//package com.example.mineguard.preview.view;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import com.example.mineguard.R;
//import com.example.mineguard.preview.model.DeviceItem;
//
///**
// * 视频控制按钮组件
// */
//public class ControlButtonsView extends FrameLayout {
//
//    private Button btnScreenshot;
//    private Button btnRecord;
//    private Button btnTalk;
//    private Button btnFullscreen;
//    private Button btnClose;
//
//    private DeviceItem currentDevice;
//    private boolean isRecording = false;
//    private boolean isTalking = false;
//
//    public interface OnControlClickListener {
//        void onScreenshotClick(DeviceItem device);
//        void onRecordClick(DeviceItem device, boolean start);
//        void onTalkClick(DeviceItem device, boolean start);
//        void onFullscreenClick(DeviceItem device);
//        void onCloseClick(DeviceItem device);
//    }
//
//    private OnControlClickListener controlListener;
//
//    public ControlButtonsView(@NonNull Context context) {
//        super(context);
//        initView();
//    }
//
//    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initView();
//    }
//
//    public ControlButtonsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initView();
//    }
//
//    private void initView() {
//        LayoutInflater.from(getContext()).inflate(R.layout.view_control_buttons, this);
//
//        btnScreenshot = findViewById(R.id.btn_screenshot);
//        btnRecord = findViewById(R.id.btn_record);
//        btnTalk = findViewById(R.id.btn_talk);
//        btnFullscreen = findViewById(R.id.btn_fullscreen);
//        btnClose = findViewById(R.id.btn_close);
//
//        setupClickListeners();
//    }
//
//    private void setupClickListeners() {
//        btnScreenshot.setOnClickListener(v -> {
//            if (controlListener != null && currentDevice != null) {
//                controlListener.onScreenshotClick(currentDevice);
//            }
//        });
//
//        btnRecord.setOnClickListener(v -> {
//            if (controlListener != null && currentDevice != null) {
//                isRecording = !isRecording;
//                updateRecordButton();
//                controlListener.onRecordClick(currentDevice, isRecording);
//            }
//        });
//
//        btnTalk.setOnClickListener(v -> {
//            if (controlListener != null && currentDevice != null) {
//                isTalking = !isTalking;
//                updateTalkButton();
//                controlListener.onTalkClick(currentDevice, isTalking);
//            }
//        });
//
//        btnFullscreen.setOnClickListener(v -> {
//            if (controlListener != null && currentDevice != null) {
//                controlListener.onFullscreenClick(currentDevice);
//            }
//        });
//
//        btnClose.setOnClickListener(v -> {
//            if (controlListener != null && currentDevice != null) {
//                controlListener.onCloseClick(currentDevice);
//            }
//        });
//    }
//
//    public void setDevice(DeviceItem device) {
//        this.currentDevice = device;
//        updateButtonStates();
//    }
//
//    private void updateButtonStates() {
//        if (currentDevice == null) {
//            setButtonsEnabled(false);
//            return;
//        }
//
//        // 只有在线设备才能使用控制功能
//        boolean isOnline = currentDevice.is_online();
//        setButtonsEnabled(isOnline);
//
//        // 更新录制和对讲状态
//        isRecording = currentDevice.isRecording();
//        isTalking = currentDevice.isTalking();
//
//        updateRecordButton();
//        updateTalkButton();
//    }
//
//    private void setButtonsEnabled(boolean enabled) {
//        btnScreenshot.setEnabled(enabled);
//        btnRecord.setEnabled(enabled);
//        btnTalk.setEnabled(enabled);
//        btnFullscreen.setEnabled(enabled);
//        btnClose.setEnabled(true); // 关闭按钮始终可用
//    }
//
//    private void updateRecordButton() {
//        if (isRecording) {
//            btnRecord.setText("停止录像");
//            btnRecord.setBackgroundColor(getResources().getColor(R.color.primary_red));
//        } else {
//            btnRecord.setText("录像");
//            btnRecord.setBackgroundColor(getResources().getColor(R.color.primary_blue));
//        }
//    }
//
//    private void updateTalkButton() {
//        if (isTalking) {
//            btnTalk.setText("停止对讲");
//            btnTalk.setBackgroundColor(getResources().getColor(R.color.primary_orange));
//        } else {
//            btnTalk.setText("对讲");
//            btnTalk.setBackgroundColor(getResources().getColor(R.color.primary_blue));
//        }
//    }
//
//    public void setOnControlClickListener(OnControlClickListener listener) {
//        this.controlListener = listener;
//    }
//
//    public void setRecordingState(boolean recording) {
//        this.isRecording = recording;
//        updateRecordButton();
//    }
//
//    public void setTalkingState(boolean talking) {
//        this.isTalking = talking;
//        updateTalkButton();
//    }
//
//    public boolean isRecording() {
//        return isRecording;
//    }
//
//    public boolean isTalking() {
//        return isTalking;
//    }
//
//    public DeviceItem getCurrentDevice() {
//        return currentDevice;
//    }
//
//    /**
//     * 显示控制按钮
//     */
//    public void showControls() {
//        setVisibility(VISIBLE);
//        setAlpha(0f);
//        animate()
//                .alpha(1f)
//                .setDuration(300)
//                .start();
//    }
//
//    /**
//     * 隐藏控制按钮
//     */
//    public void hideControls() {
//        animate()
//                .alpha(0f)
//                .setDuration(300)
//                .withEndAction(() -> setVisibility(GONE))
//                .start();
//    }
//
//    /**
//     * 切换控制按钮显示状态
//     */
//    public void toggleControls() {
//        if (getVisibility() == VISIBLE) {
//            hideControls();
//        } else {
//            showControls();
//        }
//    }
//}
