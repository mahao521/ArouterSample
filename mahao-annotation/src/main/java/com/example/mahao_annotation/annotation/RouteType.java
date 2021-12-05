package com.example.mahao_annotation.annotation;

public enum RouteType {

    ACTIVITY(0,"android.app.activity"),
    SERVICE(1,"android.app.service"),
    PROVIDER(2,"com.example.mahao_api.templete.IProvider");

    int id;
    String className;

    RouteType(int id, String className) {
        this.id = id;
        this.className = className;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public static RouteType parse(String name){
        for (RouteType routeType: RouteType.values()){
            if (routeType.getClassName().equals(name)){
                return routeType;
            }
        }
        return null;
    }

}
