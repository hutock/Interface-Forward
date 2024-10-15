package com.whub507.interfaceforwarder.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Field {
    private String prop;
    private String label;

    @Override
    public boolean equals(Object o){
        if (o instanceof Field){
            Field obj = (Field) o;

            return this.prop.equals(obj.getProp());
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hash(prop);
    }
}
