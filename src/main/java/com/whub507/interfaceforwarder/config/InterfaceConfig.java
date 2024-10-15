package com.whub507.interfaceforwarder.config;

import com.whub507.interfaceforwarder.common.Field;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
public class InterfaceConfig {
    private List<String> interfaceTitles = new ArrayList<>();
    private List<String> interfaceNames = new ArrayList<>();
    private List<List<Field>> interfaceFields = new ArrayList<>();

    HashSet<Field> requestFields = new HashSet<>();
    private HashMap<String, List<Integer>> map = new HashMap<>();

    public void initConfig() throws IOException {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("file/InterfaceTable.xlsx");
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(in);
        for (int i = 1; i < 25; i++) {
            Sheet sheet = xssfWorkbook.getSheetAt(i);
            String theInterface = sheet.getRow(1).getCell(1).toString();
            String title = sheet.getRow(0).getCell(0).toString();
            List<Field> fields = new ArrayList<>();
            for (int j = 3; j <= sheet.getLastRowNum(); j++) {
                if (sheet.getRow(j).getCell(0).toString().equals("请求参数") || sheet.getRow(j).getCell(0).toString().equals(""))
                    continue;
                String prop = sheet.getRow(j).getCell(1).toString().toUpperCase();
                String label = sheet.getRow(j).getCell(3).toString();
                Field field = new Field(prop, label);
                fields.add(field);
            }
            interfaceNames.add(theInterface);
            interfaceFields.add(fields);
            interfaceTitles.add(title);
        }

        for (int i = 1; i < 25; i++) {
            Sheet sheet = xssfWorkbook.getSheetAt(i);
            for (int j = 3; j <= sheet.getLastRowNum(); j++) {
                if (sheet.getRow(j).getCell(0).toString().equals("返回参数"))
                    break;
                String prop = sheet.getRow(j).getCell(1).toString();
                String label = sheet.getRow(j).getCell(3).toString();
                Field field = new Field(prop, label);
                if(!map.containsKey(prop)){
                    map.put(prop, new ArrayList<>());
                }
                map.get(prop).add(i-1);
                requestFields.add(field);
            }
        }

        map.remove("pageNo");
        requestFields.remove(new Field("pageNo", "页码"));

    }

    public List<String> getInterfaceNames() {
        return interfaceNames;
    }


    public List<List<Field>> getInterfaceFields() {
        return interfaceFields;
    }

    public List<String> getInterfaceTitles() {
        return interfaceTitles;
    }

    public HashMap<String, List<Integer>> getMap() {return map;}

    public HashSet<Field> getRequestFields(){
        return requestFields;
    }
}
