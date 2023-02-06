package com.json.parser.model;

import com.json.parser.exception.ParseException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Node extends AbstractNode {
    private final Map<String, Object> nodes = new LinkedHashMap<>();

    public Node() {
        super();
        super.setNode(true);
    }

    public void addKeyValue(String key, Object value) {
        if (key == null || key.isEmpty()) {
            throw new ParseException("Key could not be null or empty");
        }
        nodes.put(key, value);
    }

    public Map<String, Object> getNodes() {
        return Collections.unmodifiableMap(nodes);
    }

}
