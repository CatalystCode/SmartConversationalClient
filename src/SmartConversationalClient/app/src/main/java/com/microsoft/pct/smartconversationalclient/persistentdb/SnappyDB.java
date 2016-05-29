package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

/**
 * Created by abornst on 5/29/2016.
 */
public class SnappyDB implements IPersistentDB {

    private DB _snappydb;
    private Context _context;

    public  SnappyDB (Context context){
        try {
            _context = context;
            _snappydb = DBFactory.open(_context);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, Object value) {
        try {
            _snappydb.put(key, value);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object get(String key) {
        try {
            return _snappydb.get(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void remove(String key) {
        try {
            _snappydb.del(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        try {
            _snappydb.destroy();
            _snappydb = DBFactory.open(_context);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }

    }
}
