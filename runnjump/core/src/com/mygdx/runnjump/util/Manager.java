package com.mygdx.runnjump.util;

public abstract class Manager<T> {
    public abstract void addAsset(String name, String path);
    public abstract void addAssetSet(String name, String[] paths);
    public abstract T getAsset(String name);

}
