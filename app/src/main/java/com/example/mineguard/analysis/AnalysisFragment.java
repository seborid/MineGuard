package com.example.mineguard.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mineguard.R;

import java.util.ArrayList;
import java.util.List;

public class AnalysisFragment extends Fragment {

    private View grid1View;
    private View grid4View;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analysis, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. 初始化视频区域视图
        grid1View = view.findViewById(R.id.grid_1_view);
        grid4View = view.findViewById(R.id.grid_4_view);

        // 2. 配置左侧设备列表 RecyclerView
        RecyclerView rvDeviceList = view.findViewById(R.id.rv_device_list);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建模拟设备数据
        List<String> mockDevices = new ArrayList<>();
        mockDevices.add("主井皮带机CAM01");
        mockDevices.add("破碎站入口CAM02");
        mockDevices.add("通风井口CAM03");
        mockDevices.add("变电所CAM04");

        // 使用独立的 SimpleDeviceAdapter
        SimpleDeviceAdapter deviceAdapter = new SimpleDeviceAdapter(mockDevices);
        rvDeviceList.setAdapter(deviceAdapter);

        // 3. 配置右侧报警列表 RecyclerView
        RecyclerView rvAlarmList = view.findViewById(R.id.rv_alarm_list);
        rvAlarmList.setLayoutManager(new LinearLayoutManager(getContext()));

        // 创建模拟报警数据
        List<String> mockAlarms = new ArrayList<>();
        mockAlarms.add("皮带跑偏告警 #1");
        mockAlarms.add("人员入侵检测 #2");
        mockAlarms.add("温度异常升高 #3");

        // 使用独立的 AlarmAdapter
        AlarmAdapter alarmAdapter = new AlarmAdapter(mockAlarms);
        rvAlarmList.setAdapter(alarmAdapter);

        // 4. 初始化控制按钮
        ImageButton btnGrid1 = view.findViewById(R.id.btn_grid_1);
        ImageButton btnGrid4 = view.findViewById(R.id.btn_grid_4);
        Button btnDisarm = view.findViewById(R.id.btn_disarm);
        Button btnClose = view.findViewById(R.id.btn_close);
        Button btnIntercom = view.findViewById(R.id.btn_intercom);

        // 5. 设置事件监听
        btnGrid1.setOnClickListener(v -> {
            grid1View.setVisibility(View.VISIBLE);
            grid4View.setVisibility(View.GONE);
            Toast.makeText(getContext(), "切换至单路视频", Toast.LENGTH_SHORT).show();
        });

        btnGrid4.setOnClickListener(v -> {
            grid1View.setVisibility(View.GONE);
            grid4View.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "切换至四路视频", Toast.LENGTH_SHORT).show();
        });

        btnDisarm.setOnClickListener(v -> Toast.makeText(getContext(), "撤防指令已发送", Toast.LENGTH_SHORT).show());
        btnClose.setOnClickListener(v -> Toast.makeText(getContext(), "关闭操作", Toast.LENGTH_SHORT).show());
        btnIntercom.setOnClickListener(v -> Toast.makeText(getContext(), "开启对讲", Toast.LENGTH_SHORT).show());
    }
}