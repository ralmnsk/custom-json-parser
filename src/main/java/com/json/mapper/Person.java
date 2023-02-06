package com.json.mapper;

import java.util.List;

public class Person {
//    {
//        "person":{
//        "mail":"example@gmail.com",
//        "age":30,
//        "address":{
//            "city":"London"
//            },
//        "phones":[123,345,567]
//    }
//    }
     private String mail;
     private Integer age;
     private Address address;
     private List<Integer> phones;

     public Person() {
     }

     public String getMail() {
          return mail;
     }

     public void setMail(String mail) {
          this.mail = mail;
     }

     public Integer getAge() {
          return age;
     }

     public void setAge(Integer age) {
          this.age = age;
     }

     public Address getAddress() {
          return address;
     }

     public void setAddress(Address address) {
          this.address = address;
     }

     public List<Integer> getPhones() {
          return phones;
     }

     public void setPhones(List<Integer> phones) {
          this.phones = phones;
     }
}
