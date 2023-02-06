package com.json.parser;

import com.json.parser.exception.ParseException;
import com.json.parser.model.AbstractNode;
import com.json.parser.model.ListNode;
import com.json.parser.model.Node;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonParser implements IParser {
    private int countLeftCurlyBrackets = 0;
    private int countRightCurlyBrackets = 0;
    private int countLeftSquareBrackets = 0;
    private int countRightSquareBrackets = 0;
    private int countQuotations = 0;
    private int pointer = 0;
    private boolean isNodeAddedToList = false;
    private final List<Character> characters = new ArrayList<>();
    private static final char LEFT_CURLY_BRACKETS = '{';
    private static final char RIGHT_CURLY_BRACKETS = '}';
    private static final char LEFT_SQUARE_BRACKETS = '[';
    private static final char RIGHT_SQUARE_BRACKETS = ']';
    private static final char QUOTATIONS = '"';
    private static final char COLUMN = ':';
    private static final char COMMA = ',';
    private static final char N = '\n';
    private static final char T = '\t';
    private static final String BOOLEAN_TRUE = "true";
    private static final String BOOLEAN_FALSE = "false";
    private static final String INTEGER_CLASS = "INTEGER";
    private static final String DOUBLE_CLASS = "DOUBLE";
    private static final String BOOLEAN_CLASS = "BOOLEAN";
    private static final String NULL_CLASS = "NULL";

    @Override
    public AbstractNode parse(InputStream byteInput) {
        List<Character> chars = getParsedString(byteInput);
        validateBrackets(chars);
        AbstractNode node = null;
        if (chars.get(0).equals(LEFT_CURLY_BRACKETS)) {
            node = parseNode(null, chars);
        }
        if (chars.get(0).equals(LEFT_SQUARE_BRACKETS)) {
            node = parseListNode(null, chars);
        }

        return node;
    }

    private void addNodeKeyValue(ParseData data, Node node) {
        if (data.getQuots() == 2) {
            data.setValue(data.getStrBuffer());
            data.setQuots(0);
            data.setColumns(0);
            node.addKeyValue(data.getKey(), data.getValue());
            data.setKey(null);
            data.setValue(null);
        }
    }

    private void addNodeKeySimpleValueIfNeeded(ParseData data, Node node) {
        rightCurlyBracketsOrCommaInNode(node, data);
    }

    private void addListElement(ParseData data, ListNode listNode) {
        data.setKey(data.getStrBuffer());
        listNode.add(data.getKey());
        data.setKey(null);
        data.setQuots(0);
        data.setColumns(0);
    }

    private void addNodeToList(Node node, ListNode listNode, ParseData data) {
        listNode.add(node);
        isNodeAddedToList = true;
        data.setQuots(0);
        data.setColumns(0);
    }

    private int createListNodeInsideValue(ParseData data, Node node, List<Character> chars, int oldPointer) {
        if (data.getQuots() != 1 && data.getColumns() == 1) {
            data.setValue(new ListNode());
            data.setColumns(0);
            node.addKeyValue(data.getKey(), data.getValue());
            parseListNode((ListNode) data.getValue(), chars);
            data.setKey(null);
            data.setValue(null);
            return pointer;
        }
        return oldPointer;
    }

    private int createNodeInsideValue(ParseData data, Node node, List<Character> chars, int oldPointer) {
        if (data.getQuots() != 1 && data.getColumns() == 1) {
            data.setValue(new Node());
            data.setColumns(0);
            node.addKeyValue(data.getKey(), data.getValue());
            parseNode((Node) data.getValue(), chars);
            data.setKey(null);
            data.setValue(null);
            return pointer;
        }
        return oldPointer;
    }

    private Node createNodeIfNull(Node node, ParseData data) {
        if (data.getQuots() != 1 && node == null) {
            return new Node();
        }
        return node;
    }

    private ListNode createListNodeIfNull(ListNode listNode, ParseData data) {
        if (data.getQuots() != 1 && data.getColumns() == 0 && listNode == null) {
            listNode = new ListNode();
        }
        return listNode;
    }

    private void addCharacterIfNeeded(char ch, ParseData data) {
        if (data.getQuots() != 1) {
            return;
        }
        characters.add(ch);
    }

    private void column(ParseData data) {
        if (data.getQuots() != 1) {
            data.setColumns(data.getColumns() + 1);
            data.setKey(data.getStrBuffer());
            data.setQuots(0);
        }
    }

    private ListNode rightSquareBracketsOrCommaInListNode(ListNode listNode, Node node, ParseData data) {
        if (data.getQuots() == 1) {
            return listNode;
        }
        if (data.getQuots() == 2 && node == null) {
            addListElement(data, listNode);
            return listNode;
        }
        if (data.getQuots() == 0 && !characters.isEmpty() && isInteger(characters) && data.getKey() == null) {
            addSimpleListElement(listNode, data, INTEGER_CLASS);
            return listNode;
        }
        if (data.getQuots() == 0 && !characters.isEmpty() && characters.contains('.')) {
            addSimpleListElement(listNode, data, DOUBLE_CLASS);
            return listNode;
        }
        if (data.getQuots() == 0 && !characters.isEmpty() && isContainsBoolean(characters)) {
            addSimpleListElement(listNode, data, BOOLEAN_CLASS);
            return listNode;
        }
        if (data.getQuots() == 0 && !characters.isEmpty() && isNullValue(characters)) {
            addSimpleListElement(listNode, data, NULL_CLASS);
            return listNode;
        }
        if (data.getQuots() == 0 && data.getStrBuffer() == null) {
            return listNode;
        }
        if ((data.getQuots() == 0 || data.getQuots() == 2) && data.getColumns() == 1 && data.getKey() != null) {
            addNodeKeySimpleValueIfNeeded(data, node);
            return listNode;
        }
        addNodeToList(node, listNode, data);
        return listNode;
    }

    private Node rightCurlyBracketsOrCommaInNode(Node node, ParseData data) {
        if (data.getQuots() == 0 && !characters.isEmpty() && isInteger(characters)) {
            addSimpleNodeValue(node, data, INTEGER_CLASS);
        } else if (data.getQuots() == 0 && !characters.isEmpty() && characters.contains('.')) {
            addSimpleNodeValue(node, data, DOUBLE_CLASS);
        } else if (data.getQuots() == 0 && !characters.isEmpty() && isContainsBoolean(characters)) {
            addSimpleNodeValue(node, data, BOOLEAN_CLASS);
        } else if (data.getQuots() == 0 && !characters.isEmpty() && isNullValue(characters)) {
            addSimpleNodeValue(node, data, NULL_CLASS);
        } else if (data.getQuots() == 0 && data.getStrBuffer() == null) {
            return node;
        } else if (data.getQuots() == 0 && data.getKey() != null && characters.isEmpty()) {
            throw new ParseException("Missing value for key: {\"key\": ... }");
        }
        addNodeKeyValue(data, node);

        return node;
    }

    private boolean isInteger(List<Character> characters) {
        try {
            Integer.valueOf(normalize(characters));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private boolean isNullValue(List<Character> characters) {
        String str = normalize(characters);
        return str.contains("null");
    }

    //integer, double, boolean element without " "
    private void addSimpleListElement(ListNode listNode, ParseData data, String className) {
        Object value;
        String str = normalize(characters);
        value = switch (className) {
            case INTEGER_CLASS -> Integer.valueOf(str);
            case DOUBLE_CLASS -> Double.valueOf(str);
            case BOOLEAN_CLASS -> Boolean.valueOf(str);
            default -> null;
        };
        data.setValue(value);
        listNode.add(data.getValue());
        data.setValue(null);
        characters.clear();
    }

    //integer, double, boolean element without " "
    private void addSimpleNodeValue(Node node, ParseData data, String className) {
        Object value = null;
        String str = normalize(characters);
        value = switch (className) {
            case INTEGER_CLASS -> Integer.valueOf(str);
            case DOUBLE_CLASS -> Double.valueOf(str);
            case BOOLEAN_CLASS -> Boolean.valueOf(str);
            default -> null;
        };
        data.setValue(value);
        node.addKeyValue(data.getKey(), data.getValue());
        data.setKey(null);
        data.setValue(null);
        data.setColumns(0);
        characters.clear();
    }

    private Node parseNode(Node parentNode, List<Character> chars) {
        Node node = parentNode;
        ParseData data = new ParseData();
        for (int i = pointer; i < chars.size(); i++) {
            pointer++;
            char ch = chars.get(i);
            switch (ch) {
                case LEFT_CURLY_BRACKETS -> {
                    i = createNodeInsideValue(data, node, chars, i);
                    node = createNodeIfNull(node, data);
                    addCharacterIfNeeded(LEFT_CURLY_BRACKETS, data);
                }
                case RIGHT_CURLY_BRACKETS -> {
                    if (data.getQuots() != 1) {
                        return rightCurlyBracketsOrCommaInNode(node, data);
                    }
                    addCharacterIfNeeded(RIGHT_CURLY_BRACKETS, data);
                }
                case LEFT_SQUARE_BRACKETS -> {
                    i = createListNodeInsideValue(data, node, chars, i);
                    addCharacterIfNeeded(LEFT_SQUARE_BRACKETS, data);
                }
                case QUOTATIONS -> {
                    data.setQuots(data.getQuots() + 1);
                    updateStrBufferIfNeeded(data);
                }
                case COLUMN -> {
                    column(data);
                    addCharacterIfNeeded(COLUMN, data);
                }
                case N, T -> {
                    //just skip them
                }
                case COMMA -> {
                    rightCurlyBracketsOrCommaInNode(node, data);
                    addCharacterIfNeeded(COMMA, data);
                }
                default -> characters.add(ch);
            }
        }
        return node;
    }


    private ListNode parseListNode(ListNode parentListNode, List<Character> chars) {
        ListNode listNode = parentListNode;
        Node node = null;
        ParseData data = new ParseData();
        for (int i = pointer; i < chars.size(); i++) {
            pointer++;
            char ch = chars.get(i);
            switch (ch) {
                case LEFT_CURLY_BRACKETS -> {
                    i = createNodeInsideValue(data, node, chars, i);
                    node = createNodeIfNull(node, data);
                    addCharacterIfNeeded(LEFT_CURLY_BRACKETS, data);
                }
                case RIGHT_CURLY_BRACKETS -> {
                    addNodeKeyValue(data, node);
                    addCharacterIfNeeded(RIGHT_CURLY_BRACKETS, data);
                }
                case LEFT_SQUARE_BRACKETS -> {
                    i = createListNodeInsideValue(data, node, chars, i);
                    listNode = createListNodeIfNull(listNode, data);
                    addCharacterIfNeeded(LEFT_SQUARE_BRACKETS, data);
                }
                case RIGHT_SQUARE_BRACKETS -> {
                    if (data.getQuots() != 1) {
                        return rightSquareBracketsOrCommaInListNode(listNode, node, data);
                    }
                    addCharacterIfNeeded(RIGHT_SQUARE_BRACKETS, data);
                }
                case QUOTATIONS -> {
                    data.setQuots(data.getQuots() + 1);
                    updateStrBufferIfNeeded(data);
                }
                case COLUMN -> {
                    column(data);
                    addCharacterIfNeeded(COLUMN, data);
                }
                case N, T -> {
                    //just skip them
                }
                case COMMA -> {
                    rightSquareBracketsOrCommaInListNode(listNode, node, data);
                    addCharacterIfNeeded(COMMA, data);
                    if (isNodeAddedToList) {
                        node = null;
                        isNodeAddedToList = false;
                    }
                }
                default -> characters.add(ch);
            }
        }
        return listNode;
    }

    private boolean isContainsBoolean(List<Character> characters) {
        String s = normalize(characters);//.toLowerCase(); //NEW
        return s.equals(BOOLEAN_TRUE) || s.equals(BOOLEAN_FALSE);
    }

    private void updateStrBufferIfNeeded(ParseData data) {
        // "a":[{"b": "c"},{"d":"e"}]
        if (data.getQuots() == 2) {
            data.setStrBuffer(normalize(characters));
            characters.clear();
        }
    }

    private String normalize(List<Character> characters) {
        StringBuilder builder = new StringBuilder();
        characters.forEach(builder::append);
        return builder.toString().trim();
    }

    private void validateBrackets(List<Character> chars) {
        chars.forEach(c -> {
            switch (c) {
                case LEFT_CURLY_BRACKETS -> countLeftCurlyBrackets++;
                case RIGHT_CURLY_BRACKETS -> countRightCurlyBrackets++;
                case LEFT_SQUARE_BRACKETS -> countLeftSquareBrackets++;
                case RIGHT_SQUARE_BRACKETS -> countRightSquareBrackets++;
                case QUOTATIONS -> countQuotations++;
                default -> {
                    //skip
                }
            }
        });
        if (countLeftCurlyBrackets != countRightCurlyBrackets) {
            throw new ParseException("There are incorrect { } brackets");
        }
        if (countLeftSquareBrackets != countRightSquareBrackets) {
            throw new ParseException("There are incorrect [ ] brackets");
        }
        int i = countQuotations % 2;
        if (i != 0) {
            throw new ParseException("There are incorrect \" symbols");
        }
    }

    public List<Character> getParsedString(InputStream byteInput) {
        var isError = false;
        List<Character> chars = new ArrayList<>();
        while (!isError) {
            int read;
            try {
                read = byteInput.read();
            } catch (IOException e) {
                throw new ParseException("InputStream reading exception.", e);
            }
            if (read < 0) {
                isError = true;
            } else {
                char ch = (char) read;
                if (isValid(ch)) {
                    chars.add(ch);
                }
            }
        }
        return chars;
    }

    private boolean isValid(char ch) {
        return ch != '\n' && ch != '\t';
    }
}
