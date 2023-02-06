package com.json.parser;

public class ParseData {
    private int quots = 0;
    private int columns = 0;
    private String strBuffer = null;
    private String key = null;
    private Object value = null;

    public int getQuots() {
        return quots;
    }

    public void setQuots(int quots) {
        this.quots = quots;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public String getStrBuffer() {
        return strBuffer;
    }

    public void setStrBuffer(String strBuffer) {
        this.strBuffer = strBuffer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
