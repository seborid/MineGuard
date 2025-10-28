package com.example.mineguard.preview.view;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.mineguard.R;
import com.example.mineguard.preview.model.DeviceItem;
import java.io.IOException;

/**
 * 视频预览组件
 */
public class VideoPreviewView extends FrameLayout implements TextureView.SurfaceTextureListener {
    
    private TextureView textureView;
    private ImageView ivPlaceholder;
    private TextView tvDeviceName;
    private TextView tvDeviceStatus;
    private View statusIndicator;
    
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private DeviceItem currentDevice;
    private boolean isPrepared = false;
    
    public interface OnPreviewClickListener {
        void onPreviewClick(DeviceItem device);
        void onPreviewLongClick(DeviceItem device);
    }
    
    private OnPreviewClickListener clickListener;
    
    public VideoPreviewView(@NonNull Context context) {
        super(context);
        initView();
    }
    
    public VideoPreviewView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    
    public VideoPreviewView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }
    
    private void initView() {
        inflate(getContext(), R.layout.view_video_preview, this);
        
        textureView = findViewById(R.id.texture_view);
        ivPlaceholder = findViewById(R.id.iv_placeholder);
        tvDeviceName = findViewById(R.id.tv_device_name);
        tvDeviceStatus = findViewById(R.id.tv_device_status);
        statusIndicator = findViewById(R.id.status_indicator);
        
        textureView.setSurfaceTextureListener(this);
        
        // 设置点击事件
        setOnClickListener(v -> {
            if (clickListener != null && currentDevice != null) {
                clickListener.onPreviewClick(currentDevice);
            }
        });
        
        setOnLongClickListener(v -> {
            if (clickListener != null && currentDevice != null) {
                clickListener.onPreviewLongClick(currentDevice);
            }
            return true;
        });
    }
    
    public void setDevice(DeviceItem device) {
        this.currentDevice = device;
        updateDeviceInfo();
        
        if (device != null && device.isOnline() && device.getVideoUrl() != null) {
            startVideoPlayback(device.getVideoUrl());
        } else {
            stopVideoPlayback();
        }
    }
    
    private void updateDeviceInfo() {
        if (currentDevice == null) {
            tvDeviceName.setText("");
            tvDeviceStatus.setText("");
            statusIndicator.setBackgroundColor(getResources().getColor(R.color.text_secondary));
            return;
        }
        
        tvDeviceName.setText(currentDevice.getName());
        tvDeviceStatus.setText(currentDevice.getStatusName());
        
        // 设置状态指示器颜色
        switch (currentDevice.getStatus()) {
            case DeviceItem.STATUS_ONLINE:
                statusIndicator.setBackgroundColor(getResources().getColor(R.color.primary_green));
                break;
            case DeviceItem.STATUS_OFFLINE:
                statusIndicator.setBackgroundColor(getResources().getColor(R.color.text_secondary));
                break;
            case DeviceItem.STATUS_ERROR:
                statusIndicator.setBackgroundColor(getResources().getColor(R.color.primary_red));
                break;
        }
    }
    
    private void startVideoPlayback(String videoUrl) {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getContext(), Uri.parse(videoUrl));
            mediaPlayer.setLooping(true);
            
            if (surface != null) {
                mediaPlayer.setSurface(surface);
            }
            
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                isPrepared = true;
                mp.start();
                ivPlaceholder.setVisibility(GONE);
                textureView.setVisibility(VISIBLE);
            });
            
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                ivPlaceholder.setVisibility(VISIBLE);
                textureView.setVisibility(GONE);
                return false;
            });
            
        } catch (IOException e) {
            e.printStackTrace();
            ivPlaceholder.setVisibility(VISIBLE);
            textureView.setVisibility(GONE);
        }
    }
    
    private void stopVideoPlayback() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        isPrepared = false;
        ivPlaceholder.setVisibility(VISIBLE);
        textureView.setVisibility(GONE);
    }
    
    public void setOnPreviewClickListener(OnPreviewClickListener listener) {
        this.clickListener = listener;
    }
    
    public DeviceItem getCurrentDevice() {
        return currentDevice;
    }
    
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
    
    public void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }
    
    public void resumePlayback() {
        if (mediaPlayer != null && isPrepared && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
    
    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        surface = new Surface(surfaceTexture);
        if (mediaPlayer != null && currentDevice != null && currentDevice.getVideoUrl() != null) {
            mediaPlayer.setSurface(surface);
            if (!isPrepared) {
                startVideoPlayback(currentDevice.getVideoUrl());
            }
        }
    }
    
    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int width, int height) {
        // 处理尺寸变化
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        if (surface != null) {
            surface.release();
            surface = null;
        }
        stopVideoPlayback();
        return true;
    }
    
    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
        // 纹理更新
    }
    
    public void release() {
        stopVideoPlayback();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }
}
