package com.whub507.interfaceforwarder.service;

import com.whub507.interfaceforwarder.common.ResResult;
import com.whub507.interfaceforwarder.vo.FieldsListVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface InterfaceService {

    ResResult<List<Object>> interfaceByFiles(MultipartFile file, Integer interfaceNum, String paramName);

    ResResult<List<Object>> interfaceByParams(String param, Integer interfaceNum, String paramName);

    ResResult<List<FieldsListVO>> tablesByParam(String param, String paramName);

    Boolean downloadByFile(HttpServletRequest request, HttpServletResponse response, MultipartFile file, Integer interfaceNum, String paramName);

    Boolean downloadByParam(HttpServletRequest request, HttpServletResponse response, String param, Integer interfaceNum, String paramName);

    ResResult<List<Object>> test();

}
