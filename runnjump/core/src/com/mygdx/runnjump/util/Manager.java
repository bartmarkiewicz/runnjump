package com.mygdx.runnjump.util;

/**
 * Parent asset manager class.
 * @param <T>
 */
public abstract class Manager<T> {
    public abstract void addAsset(String name, String path);
    public abstract void addAssetSet(String name, String[] paths);
    public abstract T getAsset(String name);

}
