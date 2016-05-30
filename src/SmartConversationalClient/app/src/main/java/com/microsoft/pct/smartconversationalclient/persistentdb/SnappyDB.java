package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.IOException;


/**
 * Created by abornst on 5/29/2016.
 */

public class SnappyDB implements IPersistentDB {

    private DB _snappydb;
    private Context _context;
    private ObjectMapper _mapper;

    public  SnappyDB (Context context){
        try {
            _context = context;
            _snappydb = DBFactory.open(_context);
            _mapper = new ObjectMapper();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void put(String key, IQueryResult value) {
        try {
            try {
                _snappydb.put(key, _mapper.writeValueAsString(value));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IQueryResult get(String key) {
        try {
            try {
                return  _mapper.readValue(_snappydb.get(key),IQueryResult.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
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
