package com.microsoft.pct.smartconversationalclient.persistentdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by abornst on 6/19/2016.
 */
public class DBValue {

    @com.fasterxml.jackson.annotation.JsonProperty("value")
    private String _value;

    @com.fasterxml.jackson.annotation.JsonProperty("valueType")
    private String _valueType;

    public  DBValue() {
    }

    public DBValue(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        _value = mapper.writeValueAsString(value);
        _valueType = value.getClass().getCanonicalName();
    }

    public DBValue(String value, String valueType ) {
        _value = value;
        _valueType = valueType;
    }
    public Object getObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper.readValue(_value,Class.forName(_valueType));
    }

    @com.fasterxml.jackson.annotation.JsonSetter("value")
    public void setValue(String _value) {
        this._value = _value;
    }

    @com.fasterxml.jackson.annotation.JsonSetter("valueType")
    public void setValueType(String _valueType) {
        this._valueType = _valueType;
    }

}