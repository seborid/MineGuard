package com.example.mineguard.alarm.model;

import java.io.Serializable;

/**
 * 报警数据模型
 */
public class AlarmItem implements Serializable {
    public static final int LEVEL_WARNING = 1;  // 警告级别
    public static final int LEVEL_CRITICAL = 2; // 严重级别

    private String id;
    private String deviceName;
    private String algorithmType;
    private String scene;
    private String area;
    private String status;
    private int level;
    private long timestamp;
    private int imageRes;
    private String processInfo;
    private String processor;
    private long processTime;

    public AlarmItem() {
    }

    public AlarmItem(String id, String deviceName, String algorithmType, String scene, 
                    String area, String status, int level, long timestamp, int imageRes) {
        this.id = id;
        this.deviceName = deviceName;
        this.algorithmType = algorithmType;
        this.scene = scene;
        this.area = area;
        this.status = status;
        this.level = level;
        this.timestamp = timestamp;
        this.imageRes = imageRes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public String getScene() {
        return scene;
    }

    public void setScene(String scene) {
        this.scene = scene;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getProcessInfo() {
        return processInfo;
    }

    public void setProcessInfo(String processInfo) {
        this.processInfo = processInfo;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public long getProcessTime() {
        return processTime;
    }

    public void setProcessTime(long processTime) {
        this.processTime = processTime;
    }

    /**
     * 获取级别描述
     */
    public String getLevelDescription() {
        return level == LEVEL_CRITICAL ? "严重" : "警告";
    }

    /**
     * 获取级别颜色
     */
    public int getLevelColor() {
        return level == LEVEL_CRITICAL ? 0xFFFF4444 : 0xFFFFA726; // 红色或橙色
    }

    /**
     * 是否为严重报警
     */
    public boolean isCritical() {
        return level == LEVEL_CRITICAL;
    }

    /**
     * 是否已处理
     */
    public boolean isProcessed() {
        return "已处理".equals(status);
    }

    /**
     * 是否处理中
     */
    public boolean isProcessing() {
        return "处理中".equals(status);
    }

    /**
     * 是否未处理
     */
    public boolean isUnprocessed() {
        return "未处理".equals(status);
    }
}
