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

    public  SnappyDB (Context context) throws SnappydbException {
        _context = context;
        _snappydb = DBFactory.open(_context);
        _mapper = new ObjectMapper();
    }

    @Override
    public void put(String key, IQueryResult value) throws JsonProcessingException, SnappydbException {
        _snappydb.put(key, _mapper.writeValueAsString(value));
    }

    @Override
    public IQueryResult get(String key) throws SnappydbException, IOException {
        return  _mapper.readValue(_snappydb.get(key),IQueryResult.class);
    }

    @Override
    public void remove(String key) throws SnappydbException {
        _snappydb.del(key);
    }

    @Override
    public void clear() throws SnappydbException {
        _snappydb.destroy();
        _snappydb = DBFactory.open(_context);
    }

    @Override
    public int getSize() throws Exception {
        return _snappydb.countKeys("");
    }
}
