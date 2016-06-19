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

    public DBValue(String value, String valueType ) {
        _value = value;
        _valueType = valueType;
    }

    public DBValue(Object value, String valueType ) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        _valueType = valueType;
        _value = mapper.writeValueAsString(value);
    }

    public DBValue(Object value) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        _valueType = value.getClass().getCanonicalName();
        _value = mapper.writeValueAsString(value);
    }


    public Object getObject() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Class objectType = Class.forName(_valueType);

/*
        switch (_valueType) {
            case "LUISQueryResult":
                objectType = LUISQueryResult.class;
                break;
            case "MockQueryResult":
                objectType = MockQueryResult.class;
                break;
            default:
                throw new IllegalArgumentException("Unsupported DBValue type");
        }
*/
        return mapper.readValue(_value,objectType);
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
