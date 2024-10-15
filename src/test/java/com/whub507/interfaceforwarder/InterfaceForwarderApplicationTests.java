package com.whub507.interfaceforwarder;

import com.alibaba.fastjson.JSONObject;
import com.whub507.interfaceforwarder.common.Field;
import com.whub507.interfaceforwarder.config.InterfaceConfig;
import jdk.jfr.internal.tool.Main;
import netscape.javascript.JSObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

@SpringBootTest
class InterfaceForwarderApplicationTests {
    @Autowired
    InterfaceConfig interfaceConfig;

    @Test
    void contextLoads() throws IOException {
        try {
            // read json
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("file/json.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            // parse string to JSONObject
            JSONObject jsonObject = JSON.parseObject(jsonString.toString());

            // get data value
            JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("data");

            List<Field> fields = interfaceConfig.getInterfaceFields().get(14);
            // parse data to List
            List<Object> dataList = new ArrayList<>(dataArray);

            List<String> aa = new ArrayList<>();

            JSONObject jso = (JSONObject) dataList.get(0);
            for (Map.Entry<String, Object> entry : jso.entrySet()) {
                String key = entry.getKey();
                aa.add(fields.stream().filter(o -> o.getProp().equals(key)).findFirst().get().getLabel());
            }
            // sout dataList
            for (Object obj : dataList) {
                System.out.println(obj);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//
    @Test
    void test2(){
        HashMap<String, List<Integer>> map = interfaceConfig.getMap();
        HashSet<Field> set = interfaceConfig.getRequestFields();
        System.out.println("aa");
    }

}
