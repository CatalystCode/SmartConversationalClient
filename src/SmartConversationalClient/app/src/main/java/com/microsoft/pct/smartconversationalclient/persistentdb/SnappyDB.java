package com.microsoft.pct.smartconversationalclient.persistentdb;

import android.content.Context;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.pct.smartconversationalclient.common.IQueryResult;
import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.KeyIterator;
import com.snappydb.SnappydbException;

import java.io.IOException;
import java.util.Iterator;


/**
 * Created by abornst on 5/29/2016.
 */

public class SnappyDB implements IPersistentDB {

    private DB _snappydb;
    private Context _context;
    private ObjectMapper _mapper;
    private int _size;

    public  SnappyDB (Context context) throws Exception {
        _context = context;
        _mapper = new ObjectMapper();
        this.open();
        _size=this.count();
    }

    @Override
    public void open() throws Exception {
        _snappydb = DBFactory.open(_context,"PersistentDB");
    }

    @Override
    public void put(String key, IQueryResult value) throws JsonProcessingException, SnappydbException {
        _snappydb.put(key, _mapper.writeValueAsString(value));
        _size+=1;
    }

    @Override
    public IQueryResult getObject(String key, Class<? extends IQueryResult> objectType) throws SnappydbException, IOException {
        String s =_snappydb.get(key);
        return   _mapper.readValue(_snappydb.get(key),objectType);

    }

    @Override
    public void remove(String key) throws SnappydbException {
        _snappydb.del(key);
        _size-=1;
    }

    @Override
    public void clear() throws SnappydbException {
        _snappydb.destroy();
        _snappydb = DBFactory.open(_context,"PersistentDB");
        _size =0;
    }

    @Override
    public void close() throws Exception {
      if( _snappydb.isOpen()){
            _snappydb.close();
        }
    }

    @Override
    public int getSize() throws Exception {
        return this.count();
    }

    public int count() throws Exception {
        int index =0;
        KeyIterator it = _snappydb.allKeysIterator();
        while ( it.hasNext()) {
            it.next(1);
            index+=1;
        }
        it.close();
        return index;
    }



}
