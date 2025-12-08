package com.example.mineguard.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.mineguard.R;

public class ProfileFragment extends Fragment {

    private ImageView ivAvatar;
    private TextView tvUsername;
    private TextView tvPhoneNumber;
    private View llAccountManagement;
    private View llBindPhone;
    private View llUserGuide;
    private View llPrivacyPolicy;
    private View llAbout;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initViews(view);
        setupClickListeners();
        loadUserData();
        
        return view;
    }
    
    private void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvUsername = view.findViewById(R.id.tv_username);
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number);
        llAccountManagement = view.findViewById(R.id.ll_account_management);
        llBindPhone = view.findViewById(R.id.ll_bind_phone);
        llUserGuide = view.findViewById(R.id.ll_user_guide);
        llPrivacyPolicy = view.findViewById(R.id.ll_privacy_policy);
        llAbout = view.findViewById(R.id.ll_about);
    }
    
    private void setupClickListeners() {
        // 头像点击
        ivAvatar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "更换头像功能待实现", Toast.LENGTH_SHORT).show();
        });
        
        // 账户管理
        llAccountManagement.setOnClickListener(v -> {
            Toast.makeText(getContext(), "账户管理功能待实现", Toast.LENGTH_SHORT).show();
        });
        
        // 绑定/换绑手机号
        llBindPhone.setOnClickListener(v -> {
            Toast.makeText(getContext(), "手机号绑定功能待实现", Toast.LENGTH_SHORT).show();
        });
        
        // 使用说明
        llUserGuide.setOnClickListener(v -> {
            showUserGuide();
        });
        
        // 第三方信息收集说明
        llPrivacyPolicy.setOnClickListener(v -> {
            showPrivacyPolicy();
        });
        
        // 关于我们
        llAbout.setOnClickListener(v -> {
            showAbout();
        });
    }
    
    private void loadUserData() {
        tvUsername.setText("张三");
        tvPhoneNumber.setText("138****8888");
        ivAvatar.setImageResource(R.drawable.ic_person);
    }
    
    private void showUserGuide() {
        String guideText = "MineGuard使用说明：\n\n" +
                "1. 首页：查看系统统计数据和报警趋势\n" +
                "2. 预览：实时监控设备状态\n" +
                "3. 报警：查看和处理报警信息\n" +
                "4. 个人中心：管理个人信息和设置\n\n" +
                "如需更多帮助，请联系技术支持。";
        
        showInfoDialog("使用说明", guideText);
    }
    
    private void showPrivacyPolicy() {
        String policyText = "第三方信息收集说明：\n\n" +
                "1. 我们收集的设备信息包括：\n" +
                "   - 设备型号和系统版本\n" +
                "   - 网络状态信息\n" +
                "   - 应用使用统计\n\n" +
                "2. 信息用途：\n" +
                "   - 提供更好的服务体验\n" +
                "   - 优化应用性能\n" +
                "   - 故障诊断和修复\n\n" +
                "3. 我们承诺保护您的隐私安全，不会将个人信息分享给第三方。";
        
        showInfoDialog("隐私政策", policyText);
    }
    
    private void showAbout() {
        String aboutText = "MineGuard v1.0.0\n\n" +
                "专业的矿山安全监控系统\n" +
                "为矿山作业提供全方位的安全保障\n\n" +
                "© 2024 MineGuard Team\n" +
                "技术支持：support@mineguard.com";
        
        showInfoDialog("关于我们", aboutText);
    }

    // === 核心方法：通用美化弹窗 ===
    private void showInfoDialog(String title, String content) {
        if (getContext() == null) return;

        // 1. 加载我们在 XML 里写的漂亮布局
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_common_info, null);

        // 2. 找到布局里的控件
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        TextView tvContent = dialogView.findViewById(R.id.tv_dialog_content); // 这个就是显示长文本的地方
        View btnConfirm = dialogView.findViewById(R.id.btn_dialog_confirm);

        // 3. 【重点】把传入的 title 和 content 设置给控件
        tvTitle.setText(title);     // 设置标题
        tvContent.setText(content); // 设置内容 (这里会显示那一大段文字)

        // 4. 创建弹窗
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        // 5. 设置背景透明（为了让圆角显示出来）
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        // 6. 按钮点击关闭
        btnConfirm.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
}
