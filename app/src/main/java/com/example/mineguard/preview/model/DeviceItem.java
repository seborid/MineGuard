//package com.example.mineguard.preview.model;
//
///**
// * 设备信息模型类
// */
//public class DeviceItem {
//
//    private int id;
//    private String name;
//    private String ip;
//    private String admin;
//    private String passwd;
//    private String flow;
//    private boolean is_online;
//    private String location;
//    private String board_ip;
//    private String algorithm;
//
//
//
//
//    private boolean isRecording; //设备是否正在录像的标志
//    private boolean isTalking;//设备是否正在对讲的标志
//    private long lastUpdateTime;//设备最后一次更新状态的时间
//
//    public DeviceItem() {
//    }
//
//    public DeviceItem(int id, String name, String ip, String admin,String passwd,
//                      String flow, boolean is_online, String location,
//                      String board_ip,  String algorithm) {
//        this.id = id;
//        this.name = name;
//        this.ip = ip;
//        this.admin = admin;
//        this.passwd = passwd;
//        this.flow = flow;
//        this.is_online = is_online;
//        this.location = location;
//        this.board_ip = board_ip;
//        this.algorithm = algorithm;
//    }
//
//    public int getId() {
//        return id;
//    }
//
//    public void setId(int id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getIp() {
//        return ip;
//
//    }
//
//    public void setIp(String ip) {
//        this.ip = ip;
//    }
//
//    public String getAdmin() {
//        return admin;
//    }
//
//    public void setAdmin(String admin) {
//        this.admin = admin;
//    }
//
//    public String getPasswd() {
//        return passwd;
//    }
//
//    public void setPasswd(String passwd) {
//        this.passwd = passwd;
//    }
//
//    public String getFlow() {
//        return flow;
//    }
//
//    public void setFlow(String flow) {
//        this.flow = flow;
//    }
//
//    public boolean is_online() {
//        return is_online;
//    }
//
//    public void set_online(boolean _online) {
//        is_online = _online;
//    }
//
//    public String getLocation() {
//        return location;
//    }
//
//    public void setLocation(String location) {
//        this.location = location;
//    }
//
//    public String getBoard_ip() {
//        return board_ip;
//    }
//
//    public void setBoard_ip(String board_ip) {
//        this.board_ip = board_ip;
//    }
//
//    public String getAlgorithm() {
//        return algorithm;
//    }
//
//    public void setAlgorithm(String algorithm) {
//        this.algorithm = algorithm;
//    }
//
//    public boolean isRecording() {
//        return isRecording;
//    }
//
//    public void setRecording(boolean recording) {
//        isRecording = recording;
//    }
//
//    public boolean isTalking() {
//        return isTalking;
//    }
//
//    public void setTalking(boolean talking) {
//        isTalking = talking;
//    }
//
//    public long getLastUpdateTime() {
//        return lastUpdateTime;
//    }
//
//    public void setLastUpdateTime(long lastUpdateTime) {
//        this.lastUpdateTime = lastUpdateTime;
//    }
//
//    // public String getTypeName() {
//    //     switch (type) {
//    //         case TYPE_CAMERA:
//    //             return "摄像头";
//    //         case TYPE_SENSOR:
//    //             return "传感器";
//    //         case TYPE_ALARM:
//    //             return "报警器";
//    //         default:
//    //             return "未知设备";
//    //     }
//    // }
//
//    // public String getStatusName() {
//    //     switch (status) {
//    //         case STATUS_ONLINE:
//    //             return "在线";
//    //         case STATUS_OFFLINE:
//    //             return "离线";
//    //         case STATUS_ERROR:
//    //             return "故障";
//    //         default:
//    //             return "未知";
//    //     }
//    // }
//
//    // public boolean isOnline() {
//    //     return status == STATUS_ONLINE;
//    // }
//}
