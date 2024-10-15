package com.whub507.interfaceforwarder.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.whub507.interfaceforwarder.common.Field;
import com.whub507.interfaceforwarder.common.ResResult;
import com.whub507.interfaceforwarder.config.InterfaceConfig;
import com.whub507.interfaceforwarder.service.InterfaceService;
import com.whub507.interfaceforwarder.vo.FieldsListVO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterfaceServiceImpl implements InterfaceService {

    @Value("${forward-interface.hostname}")
    private String hostname;

    @Value("${forward-interface.paasId}")
    private String paasId;

    @Value("${forward-interface.paasToken}")
    private String paasToken;

    @Resource
    private InterfaceConfig interfaceConfig;

    @Resource
    private RestTemplate restTemplate;

    @Override
    public ResResult<List<FieldsListVO>> tablesByParam(String param, String paramName){
        List<Integer> interfaceList = interfaceConfig.getMap().get(paramName);

        List<FieldsListVO> ret = new ArrayList<>();
        for (Integer i : interfaceList){
            ResResult<List<Object>> a =  interfaceByParams(param, i, paramName);
            ret.add(new FieldsListVO(a.getFields(), a.getData()));
        }

        return ResResult.ok(ret, null, "查询成功");
    }

    @Override
    public ResResult<List<Object>> interfaceByFiles(MultipartFile file, Integer interfaceNum, String paramName) {
        List<Object> res = new ArrayList<>();

        List<String> paramList = new ArrayList<>();
        try {
            InputStream inputStream = file.getInputStream();
            Charset charset =  Charset.forName(new InputStreamReader(inputStream).getEncoding());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            System.out.println("读取文件：");
            while ((line = reader.readLine()) != null) {
                line = new String(line.getBytes(charset), StandardCharsets.UTF_8);
                System.out.println(line);
                paramList.add(line);
            }
            reader.close();
//            for (String s : paramList) {
//                res.addAll(Post(s, interfaceNum, paramName));
//                if (res.size() > 30)
//                    break;
//            }
//        } catch (NoSuchAlgorithmException | IOException e) {
//            return ResResult.error(508, "出现错误,错误信息:"+e.getMessage());
//        }
        }catch (IOException e){
            return ResResult.error(508, "出现错误,错误信息:"+e.getMessage());
        }

//        return ResResult.ok(res.stream().limit(30).collect(Collectors.toList()), interfaceConfig.getInterfaceFields().get(interfaceNum), "查询成功，返回至多前三十条数据");
        StringBuilder msg = new StringBuilder();
        paramList.forEach(msg::append);
        return ResResult.ok(test().getData(), interfaceConfig.getInterfaceFields().get(interfaceNum), "传入参数文件为:"+ msg);
    }

    @Override
    public ResResult<List<Object>> interfaceByParams(String param, Integer interfaceNum, String paramName) {
        param = param == null ? "" : param;
        List<Object> res = new ArrayList<>();
        res = test().getData();
        return ResResult.ok(res, interfaceConfig.getInterfaceFields().get(interfaceNum), "测试：接受参数为（"+paramName+","+param+","+interfaceNum+")");
//        try {
//            res = Post(param, interfaceNum, paramName);
//        } catch (NoSuchAlgorithmException e){
//            return ResResult.error(508, "出现错误,错误信息:"+e.getMessage());
//        }
//
//        //处理数据过多
//        if (res.size() > 30){
//            res = res.subList(0, 30);
//        }
//
//        return ResResult.ok(res, interfaceConfig.getInterfaceFields().get(interfaceNum), "查询成功，返回至多前三十条数据");
    }

    @Override
    public Boolean downloadByFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, Integer interfaceNum, String paramName) {
        List<Object> list = new ArrayList<>();
        try(Workbook workbook=new XSSFWorkbook()){
            // Post(param, interfaceNum, paramName)
//            List<String> paramList = new ArrayList<>();
//
//            InputStream inputStream = file.getInputStream();
//            Charset charset =  Charset.forName(new InputStreamReader(inputStream).getEncoding());
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//            String line;
//            System.out.println("读取文件：");
//            while ((line = reader.readLine()) != null) {
//                line = new String(line.getBytes(charset), StandardCharsets.UTF_8);
//                System.out.println(line);
//                paramList.add(line);
//            }
//            reader.close();
//            for (String s : paramList) {
//                list.addAll(Post(s, interfaceNum, paramName));
//            }
            list = this.test().getData();
            String title = interfaceConfig.getInterfaceTitles().get(interfaceNum);

            createTable(title, interfaceNum, workbook, list);

//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(title+".xlsx", "UTF-8"));
            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            // 将工作簿写入输出流
            workbook.write(outputStream);

            // 关闭工作簿和输出流
            workbook.close();
            outputStream.close();
        }catch (Exception e){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public Boolean downloadByParam(HttpServletRequest request, HttpServletResponse response, String param, Integer interfaceNum, String paramName) {
        List<Object> list = new ArrayList<>();
        try(Workbook workbook=new XSSFWorkbook()){
//            list = Post(param, interfaceNum, paramName);
            list = this.test().getData();
            String title = interfaceConfig.getInterfaceTitles().get(interfaceNum);

            createTable(title, interfaceNum, workbook, list);
            
//            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("content-disposition", "attachment; filename=" + URLEncoder.encode(title+".xlsx", "UTF-8"));
            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            // 将工作簿写入输出流
            workbook.write(outputStream);

            // 关闭工作簿和输出流
            workbook.close();
            outputStream.close();
        }catch (Exception e){
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    public ResResult<List<Object>> test(){
        try {
            // 读取 JSON 文件内容
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("file/json.json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            // 将字符串解析为 JSONObject 对象
            JSONObject jsonObject = JSON.parseObject(jsonString.toString());

            // 获取 data 字段对应的值
            JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("data");

            // 将 data 值转换为 List 对象
            List<Object> dataList = new ArrayList<>(dataArray);
            reader.close();
            // 输出 List 对象中的元素
            return ResResult.ok(dataList, interfaceConfig.getInterfaceFields().get(14), "测试返回");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResResult.error(506, "返回失败");
    }

    private void createTable(String title, Integer interfaceNum, Workbook workbook, List<Object> list){

        Sheet sheet=workbook.createSheet(title);
        Row header=sheet.createRow(0);

        List<Field> fields = interfaceConfig.getInterfaceFields().get(14);

        JSONObject jso = (JSONObject) list.get(0);
        int i = 0;
        for (Map.Entry<String, Object> entry : jso.entrySet()) {
            String key = entry.getKey();
            header.createCell(i).setCellValue(fields.stream().filter(o -> o.getProp().equals(key)).findFirst().get().getLabel());
            i++;
        }

        for (int j = 0; j < list.size(); j++) {
            Row row = sheet.createRow(j+1);
            JSONObject jsonObject = (JSONObject) list.get(j);
            int k = 0;
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                String value = (String) entry.getValue();
                row.createCell(k).setCellValue(value);
                k++;
            }
        }
    }


    private List<Object> Post(String param, Integer interfaceNum, String paramName) throws NoSuchAlgorithmException {
        String url = hostname + interfaceConfig.getInterfaceNames().get(interfaceNum);

        String timestamp = String.valueOf(Instant.now().getEpochSecond());
        String nonce = generateNonce();
        String signature = generateSignature(timestamp + paasToken + nonce + timestamp);

        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.add("x-tif-paasid", paasId);
        httpHeaders.add("x-tif-timestamp", timestamp);
        httpHeaders.add("x-tif-nonce", nonce);
        httpHeaders.add("x-tif-signature", signature);
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Cache-Control", "no-cache");

        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> query = new HashMap<>();
        query.put("pageNo", "1");
        query.put(paramName, param);
        requestBody.put("query", query);

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, httpHeaders);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);

        // 获取响应体
        String responseBody = response.getBody();

        // 将字符串解析为 JSONObject 对象
        JSONObject jsonObject = JSON.parseObject(responseBody);

        // 获取 data 字段对应的值
        JSONArray dataArray = jsonObject.getJSONObject("data").getJSONArray("data");

        return new ArrayList<>(dataArray);
    }

    private String generateNonce() {
        // 使用 Random 类生成随机的nonce值
        Random random = new Random();

        // 使用 StringBuilder 构建随机字符串
        StringBuilder nonceBuilder = new StringBuilder();

        // 随机选择字母和数字，并将其添加到nonceBuilder中
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        int length = 8;
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            nonceBuilder.append(randomChar);
        }

        return nonceBuilder.toString();
    }

    private String generateSignature(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte hashByte : hashBytes) {
            String hex = Integer.toHexString(0xff & hashByte);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
