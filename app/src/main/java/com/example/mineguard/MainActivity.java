package com.example.mineguard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mineguard.alarm.AlarmFragment;
import com.example.mineguard.alarm.LocalServer;
import com.example.mineguard.alarm.model.AlarmItem;
import com.example.mineguard.analysis.AnalysisFragment;
import com.example.mineguard.configuration.ConfigurationFragment;
import com.example.mineguard.data.DeviceRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.example.mineguard.data.AlarmRepository; // 导入

public class MainActivity extends AppCompatActivity {

    // ================== 原有变量 (保持不变) ==================
    private final String[] CAMERA_IPS = {
            "192.168.1.64",
            "192.168.1.65",
            "192.168.1.66",
            "192.168.1.67"
    };
    // 对应的 ID（必须与 IP 顺序一致）
    private String[] CAMERA_IDS = {"CAM_01", "CAM_02", "CAM_03", "CAM_04"};
    private Fragment[] fragments = new Fragment[3];
    private int currentIndex = 0;
    private WindowInsetsControllerCompat windowInsetsController;

    // ================== 新增变量 (从 AlarmFragment 移植) ==================
    // 报警相机专用 IP (注意：这与上面的 CAMERA_IPS 是两组不同的设备)
    private static final String ALARM_CAMERA_NORMAL_IP = "192.168.31.64"; // 异物相机
    private static final String ALARM_CAMERA_LIMIT_IP  = "192.168.31.65"; // 四超相机
    private static final String ALARM_CAMERA_COAL_IP   = "192.168.31.66"; // 煤量相机

    private static final int LOCAL_PORT = 8080; // 手机监听端口
    private LocalServer localServer;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable keepAliveRunnable;

    // 全局报警数据列表 (暂时代替数据库)
    private List<AlarmItem> globalAlarmList = new ArrayList<>();

    // 通知相关
    private NotificationManager notificationManager;
    private static final String CHANNEL_ID = "alarm_channel";

    // 1. 【新增】定义 Repository
    private AlarmRepository alarmRepository;

    // 接口：用于通知 Fragment 数据更新
    public interface OnAlarmReceivedListener {
        void onNewAlarm(AlarmItem item);
    }
    // 监听器列表
    private List<OnAlarmReceivedListener> listeners = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceRepository.init(getApplicationContext());

        // ----------------------------------------------------
        // !!! 临时调试代码：执行设备数据重置 !!!
        //DeviceRepository.getInstance().resetData();
        // ----------------------------------------------------

        setContentView(R.layout.activity_main);

        // 1. 原有初始化逻辑
        windowInsetsController = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());

        // 2. 【新增】初始化 Repository
        alarmRepository = new AlarmRepository(getApplication());

        // 2. 新增初始化逻辑 (通知、服务器、心跳)
        initNotificationChannel();
        startLocalServer();
        startKeepAlive();

        // Fragment 初始化
        fragments[0] = new AnalysisFragment();
        fragments[1] = new AlarmFragment();
        fragments[2] = new ConfigurationFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragments[0])
                .commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            int newIndex = -1;

            if (id == R.id.nav_analysis) newIndex = 0;
            else if (id == R.id.nav_alarms) newIndex = 1;
            else if (id == R.id.nav_configuration) newIndex = 2;

            if (newIndex != -1 && newIndex != currentIndex) {
                switchFragment(currentIndex, newIndex);
                currentIndex = newIndex;
                return true;
            }
            return false;
        });
        // ---------------- 测试代码开始 ----------------
        // 5秒后，自己给自己发一个模拟报警，看看界面会不会更新
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            AlarmItem testItem = new AlarmItem();
            testItem.setId(999);
            testItem.setName("测试报警");
            testItem.setLevel("1"); // 严重
            testItem.setSolve_time("2023-10-12 12:00:00");
            testItem.setStatus(0); // 未处理

            // 直接调用监听回调，模拟收到数据
            // 注意：这里是模拟 LocalServer 解析数据后回调的过程

            // 1. 加到全局列表
            globalAlarmList.add(0, testItem);
            // 2. 发通知
            // sendNotification(testItem); // 如果你想测通知栏就取消注释
            // 3. 通知 Fragment
            for (OnAlarmReceivedListener listener : listeners) {
                listener.onNewAlarm(testItem);
            }

            Toast.makeText(MainActivity.this, "已发送模拟测试报警", Toast.LENGTH_SHORT).show();
        }, 5000); // 延迟5秒执行
        // ---------------- 测试代码结束 ----------------
    }

    // ================== 原有方法 (保持不变) ==================

    private void switchFragment(int oldIndex, int newIndex) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment oldFrag = fragments[oldIndex];
        Fragment newFrag = fragments[newIndex];

        if (!newFrag.isAdded()) {
            transaction.hide(oldFrag).add(R.id.fragment_container, newFrag);
        } else {
            transaction.hide(oldFrag).show(newFrag);
        }
        transaction.commit();
    }

    /**
     * 原有的配置方法 (针对 192.168.1.x 设备)
     */
    private void configureAllCameras() {
        String myIp ="192.168.1.2";
        int myPort = 8888;
        String myUri = "/alarm/info";

        OkHttpClient client = new OkHttpClient();

        for (int i = 0; i < CAMERA_IPS.length; i++) {
            String ip = CAMERA_IPS[i];
            String id = CAMERA_IDS[i];

            String url = "http://" + ip + ":5002/server/set/smart/event/on/";

            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("protocol", "http")
                    .addFormDataPart("port", String.valueOf(myPort))
                    .addFormDataPart("uri", myUri)
                    .addFormDataPart("extend", id)
                    .build();

            Request request = new Request.Builder().url(url).post(body).build();

            final String currentIp = ip;
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "配置失败: " + currentIp, Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "配置成功: " + currentIp, Toast.LENGTH_SHORT).show());
                }
            });
        }
    }

    // ================== 新增方法 (从 AlarmFragment 移植的服务器逻辑) ==================

    /**
     * 获取当前的全局报警列表
     */
    public List<AlarmItem> getGlobalAlarmList() {
        return globalAlarmList;
    }

    /**
     * 注册监听器 (Fragment调用)
     */
    public void addAlarmListener(OnAlarmReceivedListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * 移除监听器
     */
    public void removeAlarmListener(OnAlarmReceivedListener listener) {
        listeners.remove(listener);
    }

    private void initNotificationChannel() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "报警通知", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void startLocalServer() {
        try {
            localServer = new LocalServer(LOCAL_PORT, this);
            localServer.setListener(alarmItem -> {
                handler.post(() -> {
                    // 3. 【修改】收到报警 -> 存入数据库
                    alarmRepository.insert(alarmItem);

                    // 4. 【保留】依然发通知
                    sendNotification(alarmItem);

                    // 5. 【保留】依然通知 AnalysisFragment (因为它看实时的)
                    for (OnAlarmReceivedListener listener : listeners) {
                        listener.onNewAlarm(alarmItem);
                    }
                });
            });
            localServer.start();
            Log.i("MainActivity", "全局服务器已启动，端口: " + LOCAL_PORT);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "服务器启动失败，端口可能被占用", Toast.LENGTH_LONG).show();
        }
    }

    // 暴露给 Fragment 手动刷新用 (针对 192.168.31.x 设备)
    public void manualRefreshAlarmConfig() {
        configureAlarmCamera(ALARM_CAMERA_NORMAL_IP, "/api/recv/alarmInfo", "/server/on/alarm/info/upload/");
        configureAlarmCamera(ALARM_CAMERA_LIMIT_IP, "/api/recv/fourLimit", "/server/on/four/limit/alarm/upload/");
        configureAlarmCamera(ALARM_CAMERA_COAL_IP, "/api/recv/coalWeight", "/server/on/volum/weight/upload/");
    }

    private void startKeepAlive() {
        keepAliveRunnable = new Runnable() {
            @Override
            public void run() {
                // 维持 192.168.31.x 设备的连接
                configureAlarmCamera(ALARM_CAMERA_NORMAL_IP, LocalServer.URI_ALARM_1, "/server/on/alarm/info/upload/");
                configureAlarmCamera(ALARM_CAMERA_LIMIT_IP, LocalServer.URI_ALARM_3, "/server/on/four/limit/alarm/upload/");
                configureAlarmCamera(ALARM_CAMERA_COAL_IP, LocalServer.URI_COAL_6, "/server/on/volum/weight/upload/");

                handler.postDelayed(this, 60 * 1000);
            }
        };
        handler.post(keepAliveRunnable);
    }

    // 专门用于报警相机的配置方法 (与原 configureAllCameras 区分开)
    private void configureAlarmCamera(String deviceIp, String targetUri, String openUrlPath) {
        String myIp = getPhoneIpAddress();
        if (myIp == null) return;

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("protocol", "http")
                .add("port", String.valueOf(LOCAL_PORT))
                .add("uri", targetUri)
                .add("extend", deviceIp)
                .build();

        String url = "http://" + deviceIp + ":5002" + openUrlPath;
        Request request = new Request.Builder().url(url).post(body).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Connect", "连接报警相机失败: " + deviceIp);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (!response.isSuccessful()) {
                    Log.e("Connect", "报警相机拒绝连接: " + response.code());
                }
            }
        });
    }

    private void sendNotification(AlarmItem item) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("新报警: " + item.getName())
                .setContentText("时间: " + item.getSolve_time())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);
        notificationManager.notify((int)System.currentTimeMillis(), builder.build());
    }

    private String getPhoneIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (localServer != null) localServer.stop();
        if (keepAliveRunnable != null) handler.removeCallbacks(keepAliveRunnable);
    }
}