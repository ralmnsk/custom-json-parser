package com.json.parser;

import com.json.parser.model.AbstractNode;

import java.io.InputStream;

public interface IParser {
    AbstractNode parse(InputStream in);
}
