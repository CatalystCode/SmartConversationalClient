package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.io.IOException;


/**
 * Created by abornst on 5/29/2016.
 */

public class SnappyDB implements IPersistentDB {

    private static final String DB_NAME = "PersistentDB";
    private DB _snappydb;
    private Context _context;
    private ObjectMapper _mapper;
    private int _size;

    public SnappyDB (Context context) {
        _context = context;
        _mapper = new ObjectMapper();
        _mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    @Override
    public synchronized void open() throws Exception {
        _snappydb = DBFactory.open(_context, DB_NAME);
        _size = this.count();
    }

    @Override
    public synchronized void put(String key, DBValue value) throws JsonProcessingException, SnappydbException {
        _snappydb.put(key,_mapper.writeValueAsString(value));
        _size += 1;
    }

    @Override
    public synchronized DBValue getValue(String key) throws SnappydbException, IOException {
        if (!_snappydb.exists(key)) {
            return null;
        }

        String value = _snappydb.get(key);

        return _mapper.readValue(value, DBValue.class);
    }

    @Override
    public synchronized void remove(String key) throws SnappydbException {
        _snappydb.del(key);
        _size -= 1;
    }

    @Override
    public synchronized void clear() throws SnappydbException {
        _snappydb.destroy();
        _snappydb = DBFactory.open(_context, DB_NAME);
        _size = 0;
    }

    @Override
    public synchronized void close() throws Exception {
        if(_snappydb.isOpen()) {
            _snappydb.close();
        }
    }

    @Override
    public synchronized int getSize() throws Exception {
        return _size;
    }

    @Override
    public String[] getAllKeys() throws Exception {
        KeyIterator it = _snappydb.allKeysIterator();
        String[] keys = it.next(_size);
        it.close();
        return keys;
    }

    public String[] getNKeys(int n) throws Exception {
        KeyIterator it = _snappydb.allKeysIterator();
        String[] keys = {};

        if (_size > 0) {
            if (_size > n) {
                keys = it.next(_size);
            } else {
                keys = it.next(n);
            }
        }

        it.close();
        return keys;
    }

    private int count() throws Exception {
        int index = 0;
        KeyIterator it = _snappydb.allKeysIterator();
        while (it.hasNext()) {
            it.next(1);
            index += 1;
        }

        it.close();
        return index;
    }

}
