package com.ls.dsbr.jsnc.trans;

/**
 * Created by joe on 8/22/16.
 */
public interface ObjectHelper {
    PropertyReader getPropertyReader(Object object, String name);
    PropertyWriter getPropertyWriter(Object object, String name);
}
