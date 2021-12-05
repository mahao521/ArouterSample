package com.example.mahao_annotation.model;

import com.example.mahao_annotation.annotation.Route;
import com.example.mahao_annotation.annotation.RouteType;

import javax.lang.model.element.Element;

public class RouteMeta {
    private RouteType type;
    private Element rawType;
    private Class<?> destination;
    private String path;
    private String group;
    private int priority = -1;

    public RouteMeta() {
    }

    public RouteMeta(RouteType type, Element rawType, Class<?> destination, String path, String group, int priority) {
        this.type = type;
        this.rawType = rawType;
        this.destination = destination;
        this.path = path;
        this.group = group;
        this.priority = priority;
    }

    public RouteMeta(Route route, Element rawType, RouteType type) {
        this(type, rawType, null, route.path(), route.group(), route.priority());
    }

    public static RouteMeta build(RouteType type, Class<?> destination, String path, String group, int priority) {
        return new RouteMeta(type, null, destination, path, group, priority);
    }

    public RouteType getType() {
        return type;
    }

    public void setType(RouteType type) {
        this.type = type;
    }

    public Element getRawType() {
        return rawType;
    }

    public void setRawType(Element rawType) {
        this.rawType = rawType;
    }

    public Class<?> getDestination() {
        return destination;
    }

    public void setDestination(Class<?> destination) {
        this.destination = destination;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
