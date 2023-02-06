package com.json.parser;

import com.json.parser.exception.ParseException;
import com.json.parser.model.AbstractNode;
import com.json.parser.model.ListNode;
import com.json.parser.model.Node;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonParserTest {

    @Test
    void parse() {
        String jsonString = """
                {
                 "email":"exampl2e@gmail.com",
                 "info":{"address": "city"},
                 "a":[{"b": "c"},{"d":"e"}],
                 "password": "123"
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals("exampl2e@gmail.com", node.getNodes().get("email"));
        assertEquals("city", ((Node) node.getNodes().get("info")).getNodes().get("address"));
        assertEquals("e", ((Node) ((ListNode) node.getNodes().get("a")).getList().get(1)).getNodes().get("d"));
        assertEquals("123", node.getNodes().get("password"));
    }

    @Test
    void parseList() {
        String jsonString = """
                ["a","b","c"]
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        ListNode listNode = null;

        if (!abstractNode.isNode()) {
            listNode = (ListNode) abstractNode;
        }
        assertEquals("a", listNode.getList().get(0));
        assertEquals("b", listNode.getList().get(1));
        assertEquals("c", listNode.getList().get(2));
    }

    @Test
    void parseListInt() {
        String jsonString = """
                [1,2,3]
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        ListNode listNode = null;

        if (!abstractNode.isNode()) {
            listNode = (ListNode) abstractNode;
        }
        assertEquals(1, listNode.getList().get(0));
        assertEquals(2, listNode.getList().get(1));
        assertEquals(3, listNode.getList().get(2));
    }

    @Test
    void parseListDouble() {
        String jsonString = """
                [1.0,2.12,3.00]
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        ListNode listNode = null;

        if (!abstractNode.isNode()) {
            listNode = (ListNode) abstractNode;
        }
        assertEquals(1.0, listNode.getList().get(0));
        assertEquals(2.12, listNode.getList().get(1));
        assertEquals(3.00, listNode.getList().get(2));
    }

    @Test
    void parseListBoolean() {
        String jsonString = """
                [false,false,true]
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        ListNode listNode = null;

        if (!abstractNode.isNode()) {
            listNode = (ListNode) abstractNode;
        }
        assertEquals(false, listNode.getList().get(0));
        assertEquals(false, listNode.getList().get(1));
        assertEquals(true, listNode.getList().get(2));
    }

    @Test
    void parseListNull() {
        String jsonString = """
                [null,null,null]
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        ListNode listNode = null;

        if (!abstractNode.isNode()) {
            listNode = (ListNode) abstractNode;
        }
        assertNull(listNode.getList().get(0));
        assertNull(listNode.getList().get(1));
        assertNull(listNode.getList().get(2));
    }

    @Test
    void parseNodeValueNumber() {
        String jsonString = """
                {
                 "a":1,
                 "b":2,
                 "c":3
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals(1, node.getNodes().get("a"));
        assertEquals(2, node.getNodes().get("b"));
        assertEquals(3, node.getNodes().get("c"));
    }

    @Test
    void parseNodeValueBoolean() {
        String jsonString = """
                {
                 "a":false,
                 "b":true,
                 "c":false,
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals(false, node.getNodes().get("a"));
        assertEquals(true, node.getNodes().get("b"));
        assertEquals(false, node.getNodes().get("c"));
    }

    @Test
    void parseNodeValueDouble() {
        String jsonString = """
                {
                 "a":1.00,
                 "b":2.123,
                 "c":3.1
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals(1.00, node.getNodes().get("a"));
        assertEquals(2.123, node.getNodes().get("b"));
        assertEquals(3.1, node.getNodes().get("c"));
    }

    @Test
    void parseNodeValueNull() {
        String jsonString = """
                {
                 "a":null,
                 "b":null,
                 "c":null
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertNull(node.getNodes().get("a"));
        assertNull(node.getNodes().get("b"));
        assertNull(node.getNodes().get("c"));
    }

    @Test
    void parseNodeValueInsideNodeValue() {
        String jsonString = """
                {
                 	"a": {
                 		"b": {
                 			"c": "d"
                 		}
                 	}
                 }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        Node b = (Node) node.getNodes().get("a");
        Node c = (Node) b.getNodes().get("b");
        Object d = c.getNodes().get("c");
        assertEquals("d", d);
    }

    @Test
    void parseNodeListInsideNodeValue() {
        String jsonString = """
                {
                	"a": {
                		"b": ["c","d"]
                	}
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        Node b = (Node) node.getNodes().get("a");
        ListNode listNode = (ListNode) b.getNodes().get("b");
        assertEquals("c", listNode.getList().get(0));
        assertEquals("d", listNode.getList().get(1));
    }

    @Test
    void parseNodeValueAsText() {
        String jsonString = """
                {
                 "a": "Qq1 .,!:{}[]'",
                 "b": ["Aa2 .,!:{}[]'","Bb3 .,!:{}[]'"]
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals("Qq1 .,!:{}[]'", node.getNodes().get("a"));
        assertEquals("Aa2 .,!:{}[]'", ((ListNode) node.getNodes().get("b")).getList().get(0));
        assertEquals("Bb3 .,!:{}[]'", ((ListNode) node.getNodes().get("b")).getList().get(1));
    }

    @Test
    void parseNodeValueEmpty() {
        String jsonString = """
                {
                 "a": "",
                 "b": ["",""]
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals("", node.getNodes().get("a"));
        assertEquals("", ((ListNode) node.getNodes().get("b")).getList().get(0));
        assertEquals("", ((ListNode) node.getNodes().get("b")).getList().get(1));
    }

    @Test
    void parseEmptyList() {
        String jsonString = """
                {
                 "a": [],
                 "b": [],
                 "c": []
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertTrue(((ListNode) (node.getNodes().get("a"))).getList().isEmpty());
        assertTrue(((ListNode) (node.getNodes().get("b"))).getList().isEmpty());
        assertTrue(((ListNode) (node.getNodes().get("c"))).getList().isEmpty());
    }

    @Test
    void parseEmptyNode() {
        String jsonString = """
                {
                 "a": {},
                 "b": {},
                 "c": {}
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertTrue(((Node) (node.getNodes().get("a"))).getNodes().isEmpty());
        assertTrue(((Node) (node.getNodes().get("b"))).getNodes().isEmpty());
        assertTrue(((Node) (node.getNodes().get("c"))).getNodes().isEmpty());
    }

    @Test
    void parseEmptyNodeInsideEmptyNode() {
        String jsonString = """
                {
                 "a": {"b":{}}
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        Node bValue = (Node) ((Node) node.getNodes().get("a")).getNodes().get("b");
        assertTrue(bValue.getNodes().isEmpty());
    }

    @Test
    void parsePerson() {
        String jsonString = """
                {
                	"person":{
                		"mail":"example@gmail.com",
                		"age":30,
                		"address":{
                			"city":"London"
                		},
                		"phones":[123,345,567]
                	}
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        Node person = (Node) node.getNodes().get("person");
        String mail = (String) person.getNodes().get("mail");
        Integer age = (Integer) person.getNodes().get("age");
        Node address = (Node) person.getNodes().get("address");
        String city = (String) address.getNodes().get("city");
        ListNode phones = (ListNode) person.getNodes().get("phones");
        Integer phone1 = (Integer) phones.getList().get(0);
        Integer phone2 = (Integer) phones.getList().get(1);
        Integer phone3 = (Integer) phones.getList().get(2);

        assertEquals("example@gmail.com", mail);
        assertEquals(30, age);
        assertEquals("London", city);
        assertEquals(123, phone1);
        assertEquals(345, phone2);
        assertEquals(567, phone3);
    }

    @Test
    void parseNoQuotationsThrowException() {
        String jsonString = """
                {
                 email:"exampl2e@gmail.com"
                }
                """;
        IParser parser = new JsonParser();
        ParseException exception = assertThrows(ParseException.class, () -> {
            parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        });
        assertEquals("Key could not be null or empty", exception.getMessage());
    }

    @Test
    void parseUnnecessaryComma() {
        String jsonString = """
                {
                 "email":"exampl2e@gmail.com",,
                }
                """;
        IParser parser = new JsonParser();
        AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Node node = null;

        if (abstractNode.isNode()) {
            node = (Node) abstractNode;
        }
        assertEquals("exampl2e@gmail.com", node.getNodes().get("email"));
    }

    @Test
    void parseMissingValueForKey() {
        String jsonString = """
                {
                 "a":,
                 "b":
                }
                """;
        IParser parser = new JsonParser();
        ParseException exception = assertThrows(ParseException.class, () -> {
            AbstractNode abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        });
        assertEquals("Missing value for key: {\"key\": ... }", exception.getMessage());
    }
}