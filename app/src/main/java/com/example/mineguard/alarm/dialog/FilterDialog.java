package com.example.mineguard.alarm.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.mineguard.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * 筛选条件对话框
 */
public class FilterDialog extends BottomSheetDialogFragment {
    
    private Spinner spinnerDevice;
    private Spinner spinnerAlgorithm;
    private Spinner spinnerScene;
    private Spinner spinnerArea;
    private Spinner spinnerStatus;
    private Spinner spinnerTimeRange;
    private Button btnReset;
    private Button btnConfirm;
    
    private String selectedDevice;
    private String selectedAlgorithm;
    private String selectedScene;
    private String selectedArea;
    private String selectedStatus;
    private String selectedTimeRange;
    
    private OnFilterChangeListener listener;

    public interface OnFilterChangeListener {
        void onFilterChanged(String device, String algorithm, String scene, 
                           String area, String status, String timeRange);
    }

    public static FilterDialog newInstance(String device, String algorithm, String scene, 
                                         String area, String status, String timeRange) {
        FilterDialog dialog = new FilterDialog();
        Bundle args = new Bundle();
        args.putString("device", device);
        args.putString("algorithm", algorithm);
        args.putString("scene", scene);
        args.putString("area", area);
        args.putString("status", status);
        args.putString("timeRange", timeRange);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            selectedDevice = args.getString("device", "");
            selectedAlgorithm = args.getString("algorithm", "");
            selectedScene = args.getString("scene", "");
            selectedArea = args.getString("area", "");
            selectedStatus = args.getString("status", "");
            selectedTimeRange = args.getString("timeRange", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_filter, container, false);
        
        initViews(view);
        setupSpinners();
        setupClickListeners();
        setSelectedValues();
        
        return view;
    }

    private void initViews(View view) {
        spinnerDevice = view.findViewById(R.id.spinnerDevice);
        spinnerAlgorithm = view.findViewById(R.id.spinnerAlgorithm);
        spinnerScene = view.findViewById(R.id.spinnerScene);
        spinnerArea = view.findViewById(R.id.spinnerArea);
        spinnerStatus = view.findViewById(R.id.spinnerStatus);
        spinnerTimeRange = view.findViewById(R.id.spinnerTimeRange);
        btnReset = view.findViewById(R.id.btnReset);
        btnConfirm = view.findViewById(R.id.btnConfirm);
    }

    private void setupSpinners() {
        // 设备名称
        String[] devices = {"全部", "掘进机A1", "采煤机B2", "运输机C3", "风机D4", "水泵E5"};
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, devices);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(deviceAdapter);
        
        // 算法类型
        String[] algorithms = {"全部", "人员检测", "设备异常", "瓦斯超标", "温度异常", "振动异常"};
        ArrayAdapter<String> algorithmAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, algorithms);
        algorithmAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlgorithm.setAdapter(algorithmAdapter);
        
        // 场景
        String[] scenes = {"全部", "采煤工作面", "掘进工作面", "运输巷道", "通风系统", "排水系统"};
        ArrayAdapter<String> sceneAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, scenes);
        sceneAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerScene.setAdapter(sceneAdapter);
        
        // 区域
        String[] areas = {"全部", "东区", "西区", "南区", "北区", "中央区"};
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, areas);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);
        
        // 处理状态
        String[] statuses = {"全部", "未处理", "处理中", "已处理"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, statuses);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(statusAdapter);
        
        // 时间范围
        String[] timeRanges = {"全部", "最近1小时", "最近6小时", "最近24小时", "最近3天", "最近7天"};
        ArrayAdapter<String> timeRangeAdapter = new ArrayAdapter<>(requireContext(), 
                android.R.layout.simple_spinner_item, timeRanges);
        timeRangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeRange.setAdapter(timeRangeAdapter);
    }

    private void setupClickListeners() {
        btnReset.setOnClickListener(v -> {
            spinnerDevice.setSelection(0);
            spinnerAlgorithm.setSelection(0);
            spinnerScene.setSelection(0);
            spinnerArea.setSelection(0);
            spinnerStatus.setSelection(0);
            spinnerTimeRange.setSelection(0);
        });
        
        btnConfirm.setOnClickListener(v -> {
            String device = spinnerDevice.getSelectedItem().toString();
            String algorithm = spinnerAlgorithm.getSelectedItem().toString();
            String scene = spinnerScene.getSelectedItem().toString();
            String area = spinnerArea.getSelectedItem().toString();
            String status = spinnerStatus.getSelectedItem().toString();
            String timeRange = spinnerTimeRange.getSelectedItem().toString();
            
            // 转换"全部"为空字符串
            device = "全部".equals(device) ? "" : device;
            algorithm = "全部".equals(algorithm) ? "" : algorithm;
            scene = "全部".equals(scene) ? "" : scene;
            area = "全部".equals(area) ? "" : area;
            status = "全部".equals(status) ? "" : status;
            timeRange = "全部".equals(timeRange) ? "" : timeRange;
            
            if (listener != null) {
                listener.onFilterChanged(device, algorithm, scene, area, status, timeRange);
            }
            dismiss();
        });
    }

    private void setSelectedValues() {
        // 设置之前选择的值
        if (!selectedDevice.isEmpty()) {
            setSpinnerSelection(spinnerDevice, selectedDevice);
        }
        if (!selectedAlgorithm.isEmpty()) {
            setSpinnerSelection(spinnerAlgorithm, selectedAlgorithm);
        }
        if (!selectedScene.isEmpty()) {
            setSpinnerSelection(spinnerScene, selectedScene);
        }
        if (!selectedArea.isEmpty()) {
            setSpinnerSelection(spinnerArea, selectedArea);
        }
        if (!selectedStatus.isEmpty()) {
            setSpinnerSelection(spinnerStatus, selectedStatus);
        }
        if (!selectedTimeRange.isEmpty()) {
            setSpinnerSelection(spinnerTimeRange, selectedTimeRange);
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public void setOnFilterChangeListener(OnFilterChangeListener listener) {
        this.listener = listener;
    }
}
