/*
 * Copyright (C) 2022 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
