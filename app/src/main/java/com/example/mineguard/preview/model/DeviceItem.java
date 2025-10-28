package com.example.mineguard.preview.model;

/**
 * 设备信息模型类
 */
public class DeviceItem {
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_SENSOR = 2;
    public static final int TYPE_ALARM = 3;
    
    public static final int STATUS_ONLINE = 1;
    public static final int STATUS_OFFLINE = 2;
    public static final int STATUS_ERROR = 3;
    
    private String id;
    private String name;
    private String area;
    private String region;
    private int type;
    private int status;
    private String videoUrl;
    private boolean isRecording;
    private boolean isTalking;
    private long lastUpdateTime;
    
    public DeviceItem() {
    }
    
    public DeviceItem(String id, String name, String area, String region, int type, int status) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.region = region;
        this.type = type;
        this.status = status;
        this.lastUpdateTime = System.currentTimeMillis();
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getArea() {
        return area;
    }
    
    public void setArea(String area) {
        this.area = area;
    }
    
    public String getRegion() {
        return region;
    }
    
    public void setRegion(String region) {
        this.region = region;
    }
    
    public int getType() {
        return type;
    }
    
    public void setType(int type) {
        this.type = type;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getVideoUrl() {
        return videoUrl;
    }
    
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    
    public boolean isRecording() {
        return isRecording;
    }
    
    public void setRecording(boolean recording) {
        isRecording = recording;
    }
    
    public boolean isTalking() {
        return isTalking;
    }
    
    public void setTalking(boolean talking) {
        isTalking = talking;
    }
    
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }
    
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public String getTypeName() {
        switch (type) {
            case TYPE_CAMERA:
                return "摄像头";
            case TYPE_SENSOR:
                return "传感器";
            case TYPE_ALARM:
                return "报警器";
            default:
                return "未知设备";
        }
    }
    
    public String getStatusName() {
        switch (status) {
            case STATUS_ONLINE:
                return "在线";
            case STATUS_OFFLINE:
                return "离线";
            case STATUS_ERROR:
                return "故障";
            default:
                return "未知";
        }
    }
    
    public boolean isOnline() {
        return status == STATUS_ONLINE;
    }
}
