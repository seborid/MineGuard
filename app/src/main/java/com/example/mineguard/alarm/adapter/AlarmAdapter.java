package com.example.mineguard.alarm.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mineguard.R;
import com.example.mineguard.alarm.model.AlarmItem;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 报警列表适配器
 */
public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    
    private List<AlarmItem> alarmList;
    private OnAlarmClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnAlarmClickListener {
        void onAlarmClick(AlarmItem alarm);
        void onAlarmLongClick(AlarmItem alarm);
    }

    public AlarmAdapter(List<AlarmItem> alarmList, OnAlarmClickListener listener) {
        this.alarmList = alarmList;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alarm_card, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        AlarmItem alarm = alarmList.get(position);
        holder.bind(alarm);
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tvLevel;
        private TextView tvDeviceName;
        private TextView tvAlgorithmType;
        private TextView tvScene;
        private TextView tvTime;
        private TextView tvStatus;
        private View levelIndicator;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvDeviceName = itemView.findViewById(R.id.tvDeviceName);
            tvAlgorithmType = itemView.findViewById(R.id.tvAlgorithmType);
            tvScene = itemView.findViewById(R.id.tvScene);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            levelIndicator = itemView.findViewById(R.id.levelIndicator);
        }

        public void bind(AlarmItem alarm) {
            // 设置图片
            imageView.setImageResource(alarm.getImageRes());
            
            // 设置级别
            tvLevel.setText(alarm.getLevelDescription());
            tvLevel.setTextColor(alarm.getLevelColor());
            levelIndicator.setBackgroundColor(alarm.getLevelColor());
            
            // 设置设备名称
            tvDeviceName.setText(alarm.getDeviceName());
            
            // 设置算法类型
            tvAlgorithmType.setText(alarm.getAlgorithmType());
            
            // 设置场景
            tvScene.setText(alarm.getScene());
            
            // 设置时间
            String timeStr = dateFormat.format(new Date(alarm.getTimestamp()));
            tvTime.setText(timeStr);
            
            // 设置状态
            tvStatus.setText(alarm.getStatus());
            setStatusStyle(tvStatus, alarm.getStatus());
            
            // 设置点击事件
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onAlarmClick(alarm);
                }
            });
            
            // 设置长按事件
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onAlarmLongClick(alarm);
                }
                return true;
            });
        }

        private void setStatusStyle(TextView tvStatus, String status) {
            switch (status) {
                case "未处理":
                    tvStatus.setTextColor(0xFFFF5252); // 红色
                    tvStatus.setBackgroundResource(R.drawable.bg_status_unprocessed);
                    break;
                case "处理中":
                    tvStatus.setTextColor(0xFFFFA726); // 橙色
                    tvStatus.setBackgroundResource(R.drawable.bg_status_processing);
                    break;
                case "已处理":
                    tvStatus.setTextColor(0xFF66BB6A); // 绿色
                    tvStatus.setBackgroundResource(R.drawable.bg_status_processed);
                    break;
                default:
                    tvStatus.setTextColor(0xFF757575); // 灰色
                    tvStatus.setBackgroundResource(R.drawable.bg_status_default);
                    break;
            }
        }
    }
}
