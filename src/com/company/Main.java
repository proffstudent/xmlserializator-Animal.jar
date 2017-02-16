package com.company;

import datamanager.DataManager;
import java.util.LinkedList;
import java.util.List;
import datamanager.URLClassLoader;
import org.w3c.dom.Document;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws Exception {
        People people = new People("John",201,1111);
        People people1 = new People("John1",2011,10);

       List<People> list = new LinkedList<>();
       list.add(people);
       list.add(people1);


        DataManager.serializeCollection(list);
        DataManager.serialize(people);
        People people11 = DataManager.deserialize("test1.xml");
        System.out.println("name " +people11.getName()+"; age "+ people11.getAge() +"; salary "+ people11.getSalary());

        //CustomClassLoader customClassLoader = new CustomClassLoader();
        //Class animal =  customClassLoader.loadClass("Animal");

        //Object obj = animal.newInstance();

        //DataManager.serialize(obj);



    }
    private static void testWrite(String filepath, Document doc) throws
            ParserConfigurationException, IllegalAccessException,
            TransformerException, IOException, NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, InstantiationException {
        final File file;

        URLClassLoader loader=new URLClassLoader(ClassLoader
                .getSystemClassLoader());

        Class<?> hereBeAnimal=loader.loadClass("Animal");
        Object iAmAnimal=hereBeAnimal.newInstance();

        file = new File(filepath);
        // person = new Person("John Connor", 20, 7.5);

        doc = DataManager.serialize(iAmAnimal, doc);
        write(doc, file);
    }
    public static void write(Document doc, File file)
            throws TransformerException, IOException {
        DOMSource source = new DOMSource(doc);
        FileWriter writer = new FileWriter(file);
        StreamResult result = new StreamResult(writer);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
    }
}
