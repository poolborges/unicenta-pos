/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.openbravo.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pauloborges
 */
public class ResultSetMapper<T> {

    private static final Logger LOGGER = Logger.getLogger(ResultSetMapper.class.getName());

    Class<T> clazz;

    public ResultSetMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<T> map(ResultSet resultSet) {

        List<T> list = new ArrayList<>();
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());

        try {
            fields.forEach(field -> {
                field.setAccessible(true);
            });
            while (resultSet.next()) {

                T dto = clazz.getConstructor().newInstance();

                for (Field field : fields) {

                    String name = field.getName();
                    TableColumn col = field.getAnnotation(TableColumn.class);
                    if (col != null) {
                        name = col.name();
                    }

                    String value = resultSet.getString(name);
                    field.set(dto, field.getType().getConstructor(String.class).newInstance(value));

                }

                list.add(dto);

            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Exception while perfom object mapping", ex);
        }
        return list;
    }
}

@Retention(RetentionPolicy.RUNTIME)
@interface TableColumn {

    String name();
}
/*
class Person {

   @Col(name = "person_name")
   private String personName;

   public Person() {}
}


ResultSetMapper rsm = new ResultSetMapper(Person.class);
 */
