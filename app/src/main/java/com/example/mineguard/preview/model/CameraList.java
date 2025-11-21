package com.example.mineguard.preview.model;

import java.util.List;

public class CameraList {

    private int code;
    private Data data;
    private Meta meta;
    private String message;

    public static class Data {
        private List<Camera> list;
        private Meta meta;

        public List<Camera> getList() {
            return list;
        }

        public void setList(List<Camera> list) {
            this.list = list;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta(Meta meta) {
            this.meta = meta;
        }
    }

    public static class Camera {
        private int id;
        private String name;
        private String ip;
        private String admin;
        private String passwd;
        private String flow;
        private boolean is_online;
        private String location;
        private String board_ip;
        private String algorithm;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getPasswd() {
            return passwd;
        }

        public void setPasswd(String passwd) {
            this.passwd = passwd;
        }

        public String getFlow() {
            return flow;
        }

        public void setFlow(String flow) {
            this.flow = flow;
        }

        public boolean isIs_online() {
            return is_online;
        }

        public void setIs_online(boolean is_online) {
            this.is_online = is_online;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getBoard_ip() {
            return board_ip;
        }

        public void setBoard_ip(String board_ip) {
            this.board_ip = board_ip;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }
    }

    public static class Meta {
        private int page;
        private int per_page;
        private int total;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
    public Meta getMeta() {
        return meta;
    }
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
