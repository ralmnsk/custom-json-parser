package com.json.mapper;

import com.json.mapper.exception.MapperException;
import com.json.parser.model.AbstractNode;
import com.json.parser.model.ListNode;
import com.json.parser.model.Node;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Mapper implements IMapper {
    private static final String NODE_CLASS_NAME = "Node";

    @Override
    public Object map(AbstractNode abstractNode, Class clazz) {
        if (!abstractNode.isNode()) {
            throw new MapperException("There is ListNode instead of Node in map method.");
        }
        try {
            return nextFieldValue(abstractNode, clazz);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            throw new MapperException("Node mapping exception. ", e);
        }
    }

    @Override
    public Object mapList(AbstractNode abstractNode, Class clazz) {
        if (abstractNode.isNode()) {
            throw new MapperException("There is Node instead of ListNode in map method.");
        }
        return nextListValue(abstractNode, clazz);
    }

    private Object nextListValue(AbstractNode abstractNode, Class<?> clazz) {
        Object listObject = new LinkedList<>();
        ListNode listNode = (ListNode) abstractNode;
        if (listNode.getList().isEmpty()) {
            return listObject;
        }
        String className = listNode.getList().get(0).getClass().getSimpleName();
        if (!NODE_CLASS_NAME.equals(className)) {
            //logic for simple values
        } else {
            List<Node> nodes = (List<Node>) listNode.getList();
            listObject = nodes.stream().map(l -> map(l, clazz)).toList();
        }

        return listObject;
    }

    private Object nextFieldValue(AbstractNode abstractNode, Class<?> clazz) throws IllegalAccessException,
            ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException {
        if (Objects.equals(clazz, Map.class)) {
            Node classNode = (Node) abstractNode;
            return classNode.getNodes();
        }
        Node classNode = getClassNode((Node) abstractNode, clazz);
        Object classObject = Class.forName(clazz.getName()).getConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String key = field.getName();
            Object value = classNode.getNodes().get(key);
            String valueClassName = value.getClass().getSimpleName();
            switch (valueClassName) {
                case "Node" -> {
                    Class<?> fieldClass = field.getType();
                    AbstractNode fieldAbstractNode = (AbstractNode) value;
                    value = nextFieldValue(fieldAbstractNode, fieldClass);
                }
                case "ListNode" -> {
                    ListNode listNode = (ListNode) value;
                    value = listNode.getList();
                }
                default -> {
                    //String,Integer classes
                }
            }
            field.set(classObject, value);
            field.setAccessible(false);
        }
        return classObject;
    }

    private Node getClassNode(Node abstractNode, Class<?> clazz) {
        Node node = abstractNode;
        String className = clazz.getSimpleName().toLowerCase();
        Node classNode = (Node) node.getNodes().get(className);
        if (classNode == null) {
            return node;
        }
        return classNode;
    }
}
