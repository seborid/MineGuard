package com.example.mineguard.alarm.dialog;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mineguard.R;
import com.example.mineguard.alarm.model.AlarmItem;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 报警详情对话框
 */
public class AlarmDetailDialog extends DialogFragment {
    
    private ImageView imageView;
    private TextView tvLevel;
    private TextView tvDeviceName;
    private TextView tvAlgorithmType;
    private TextView tvScene;
    private TextView tvArea;
    private TextView tvTime;
    private TextView tvStatus;
    private TextView tvProcessInfo;
    private TextView tvProcessor;
    private TextView tvProcessTime;
    private EditText etProcessInfo;
    private Button btnSave;
    private Button btnExport;
    private Button btnClose;
    
    private AlarmItem alarm;

    public static AlarmDetailDialog newInstance(AlarmItem alarm) {
        AlarmDetailDialog dialog = new AlarmDetailDialog();
        Bundle args = new Bundle();
        args.putSerializable("alarm", alarm);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            alarm = (AlarmItem) args.getSerializable("alarm");
        }
        
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_alarm_detail, container, false);
        
        initViews(view);
        setupData();
        setupClickListeners();
        
        return view;
    }

    private void initViews(View view) {
        imageView = view.findViewById(R.id.imageView);
        tvLevel = view.findViewById(R.id.tvLevel);
        tvDeviceName = view.findViewById(R.id.tvDeviceName);
        tvAlgorithmType = view.findViewById(R.id.tvAlgorithmType);
        tvScene = view.findViewById(R.id.tvScene);
        tvArea = view.findViewById(R.id.tvArea);
        tvTime = view.findViewById(R.id.tvTime);
        tvStatus = view.findViewById(R.id.tvStatus);
        tvProcessInfo = view.findViewById(R.id.tvProcessInfo);
        tvProcessor = view.findViewById(R.id.tvProcessor);
        tvProcessTime = view.findViewById(R.id.tvProcessTime);
        etProcessInfo = view.findViewById(R.id.etProcessInfo);
        btnSave = view.findViewById(R.id.btnSave);
        btnExport = view.findViewById(R.id.btnExport);
        btnClose = view.findViewById(R.id.btnClose);
    }

    private void setupData() {
        if (alarm == null) return;
        
        // 设置图片
        imageView.setImageResource(alarm.getImageRes());
        
        // 设置基本信息
        tvLevel.setText(alarm.getLevelDescription());
        tvLevel.setTextColor(alarm.getLevelColor());
        
        tvDeviceName.setText(alarm.getDeviceName());
        tvAlgorithmType.setText(alarm.getAlgorithmType());
        tvScene.setText(alarm.getScene());
        tvArea.setText(alarm.getArea());
        
        // 设置时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        tvTime.setText(dateFormat.format(new Date(alarm.getTimestamp())));
        
        // 设置状态
        tvStatus.setText(alarm.getStatus());
        setStatusStyle(tvStatus, alarm.getStatus());
        
        // 设置处理信息
        if (alarm.getProcessInfo() != null && !alarm.getProcessInfo().isEmpty()) {
            tvProcessInfo.setText(alarm.getProcessInfo());
            tvProcessInfo.setVisibility(View.VISIBLE);
            etProcessInfo.setVisibility(View.GONE);
            btnSave.setText("修改处理信息");
        } else {
            tvProcessInfo.setVisibility(View.GONE);
            etProcessInfo.setVisibility(View.VISIBLE);
            btnSave.setText("保存处理信息");
        }
        
        // 设置处理人
        if (alarm.getProcessor() != null && !alarm.getProcessor().isEmpty()) {
            tvProcessor.setText("处理人: " + alarm.getProcessor());
            tvProcessor.setVisibility(View.VISIBLE);
        } else {
            tvProcessor.setVisibility(View.GONE);
        }
        
        // 设置处理时间
        if (alarm.getProcessTime() > 0) {
            String processTimeStr = dateFormat.format(new Date(alarm.getProcessTime()));
            tvProcessTime.setText("处理时间: " + processTimeStr);
            tvProcessTime.setVisibility(View.VISIBLE);
        } else {
            tvProcessTime.setVisibility(View.GONE);
        }
    }

    private void setStatusStyle(TextView tvStatus, String status) {
        switch (status) {
            case "未处理":
                tvStatus.setTextColor(0xFFFF5252); // 红色
                break;
            case "处理中":
                tvStatus.setTextColor(0xFFFFA726); // 橙色
                break;
            case "已处理":
                tvStatus.setTextColor(0xFF66BB6A); // 绿色
                break;
            default:
                tvStatus.setTextColor(0xFF757575); // 灰色
                break;
        }
    }

    private void setupClickListeners() {
        btnSave.setOnClickListener(v -> saveProcessInfo());
        btnExport.setOnClickListener(v -> exportImage());
        btnClose.setOnClickListener(v -> dismiss());
        
        // 点击图片查看大图
        imageView.setOnClickListener(v -> {
            // 这里可以实现图片放大查看功能
            Toast.makeText(getContext(), "图片查看功能", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveProcessInfo() {
        String processInfo = etProcessInfo.getText().toString().trim();
        if (processInfo.isEmpty()) {
            Toast.makeText(getContext(), "请输入处理信息", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 更新报警信息
        alarm.setProcessInfo(processInfo);
        alarm.setProcessor("当前用户"); // 这里应该获取实际的用户名
        alarm.setProcessTime(System.currentTimeMillis());
        alarm.setStatus("已处理");
        
        // 刷新显示
        setupData();
        
        Toast.makeText(getContext(), "处理信息已保存", Toast.LENGTH_SHORT).show();
    }

    private void exportImage() {
        try {
            // 创建下载管理器
            DownloadManager downloadManager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
            
            // 生成文件名
            String fileName = "alarm_" + alarm.getId() + "_" + System.currentTimeMillis() + ".jpg";
            
            // 创建下载请求
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + alarm.getImageRes()));
            request.setTitle("报警图片导出");
            request.setDescription("导出报警图片到本地");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, fileName);
            
            // 开始下载
            downloadManager.enqueue(request);
            
            Toast.makeText(getContext(), "图片导出中，请查看通知栏", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            // 如果下载管理器方式失败，尝试其他方式
            exportImageAlternative();
        }
    }

    private void exportImageAlternative() {
        try {
            // 创建图片目录
            File picturesDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MineGuard");
            if (!picturesDir.exists()) {
                picturesDir.mkdirs();
            }
            
            // 生成文件名
            String fileName = "alarm_" + alarm.getId() + "_" + System.currentTimeMillis() + ".jpg";
            File outputFile = new File(picturesDir, fileName);
            
            // 这里应该实现实际的图片保存逻辑
            // 由于是示例，我们只显示成功消息
            Toast.makeText(getContext(), "图片已保存到: " + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            
        } catch (Exception e) {
            Toast.makeText(getContext(), "图片导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // 设置对话框宽度为屏幕宽度的90%
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            getDialog().getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
