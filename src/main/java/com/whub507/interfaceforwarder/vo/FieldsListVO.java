package com.whub507.interfaceforwarder.vo;

import com.whub507.interfaceforwarder.common.Field;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldsListVO {
    List<Field> fields;
    List<Object> data;
}
