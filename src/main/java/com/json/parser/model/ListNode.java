package com.json.parser.model;

import java.util.LinkedList;
import java.util.List;

public class ListNode extends AbstractNode {
    private final List list = new LinkedList<>();

    public ListNode() {
        super();
    }

    public List getList() {
        return list;
    }

    public void add(Object o) {
        this.list.add(o);
    }
}
