package com.json.mapper;

import com.json.parser.IParser;
import com.json.parser.JsonParser;
import com.json.parser.model.AbstractNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapperTest {
    private IParser parser;
    private String jsonString;
    private AbstractNode abstractNode;
    private IMapper mapper;
    private Person person;
    private List<Person> persons;

    @BeforeEach
    void setUp() {
        parser = new JsonParser();
        mapper = new Mapper();
    }

    private Person createTestPerson(String mail, int age, String city, int phone1, int phone2, int phone3) {
        Person person = new Person();
        person.setMail(mail);
        person.setAge(age);
        Address address = new Address();
        address.setCity(city);
        person.setAddress(address);
        List<Integer> phones = new ArrayList<>();
        phones.add(phone1);
        phones.add(phone2);
        phones.add(phone3);
        person.setPhones(phones);

        return person;
    }

    @Test
    void map() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        jsonString = """
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
        abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        person = createTestPerson("example@gmail.com", 30, "London", 123, 345, 567);

        Person testPerson = (Person) mapper.map(abstractNode, Person.class);
        assertEquals(person.getMail(), testPerson.getMail());
        assertEquals(person.getAge(), testPerson.getAge());
        assertEquals(person.getAddress().getCity(), testPerson.getAddress().getCity());
        assertEquals(person.getPhones(), testPerson.getPhones());
    }

    @Test
    void mapList() {
        jsonString = """
                [
                    {
                        "mail": "example@gmail.com",
                        "age": 30,
                        "address": {
                            "city": "London"
                        },
                        "phones": [
                            123,
                            345,
                            567
                        ]
                    },
                    {
                        "mail": "example2@gmail.com",
                        "age": 32,
                        "address": {
                            "city": "London2"
                        },
                        "phones": [
                            222,
                            333,
                            444
                        ]
                    },
                    {
                        "mail": "example3@gmail.com",
                        "age": 33,
                        "address": {
                            "city": "London3"
                        },
                        "phones": [
                            555,
                            666,
                            777
                        ]
                    }
                ]
                """;
        abstractNode = parser.parse(new ByteArrayInputStream(jsonString.getBytes()));
        Person person = createTestPerson("example@gmail.com", 30, "London", 123, 345, 567);
        Person person2 = createTestPerson("example2@gmail.com", 32, "London2", 222, 333, 444);
        Person person3 = createTestPerson("example3@gmail.com", 33, "London3", 555, 666, 777);

        persons = new LinkedList<>();
        persons.add(person);
        persons.add(person2);
        persons.add(person3);

        List<Person> testPersons = (List<Person>) mapper.mapList(abstractNode, Person.class);

        assertEquals(person.getMail(), testPersons.get(0).getMail());
        assertEquals(person.getAge(), testPersons.get(0).getAge());
        assertEquals(person.getAddress().getCity(), testPersons.get(0).getAddress().getCity());
        assertEquals(person.getPhones(), testPersons.get(0).getPhones());

        assertEquals(person2.getMail(), testPersons.get(1).getMail());
        assertEquals(person2.getAge(), testPersons.get(1).getAge());
        assertEquals(person2.getAddress().getCity(), testPersons.get(1).getAddress().getCity());
        assertEquals(person2.getPhones(), testPersons.get(1).getPhones());

        assertEquals(person3.getMail(), testPersons.get(2).getMail());
        assertEquals(person3.getAge(), testPersons.get(2).getAge());
        assertEquals(person3.getAddress().getCity(), testPersons.get(2).getAddress().getCity());
        assertEquals(person3.getPhones(), testPersons.get(2).getPhones());
    }
}