package com.json.mapper;

import com.json.parser.model.AbstractNode;

import java.lang.reflect.InvocationTargetException;

public interface IMapper {
    Object map(AbstractNode node, Class clazz) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException;

    Object mapList(AbstractNode abstractNode, Class clazz);
}
