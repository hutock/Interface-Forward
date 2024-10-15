package com.whub507.interfaceforwarder.controller;

import com.whub507.interfaceforwarder.common.Field;
import com.whub507.interfaceforwarder.common.ResResult;
import com.whub507.interfaceforwarder.config.InterfaceConfig;
import com.whub507.interfaceforwarder.service.InterfaceService;
import com.whub507.interfaceforwarder.vo.FieldsListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Api(tags = "Interface")
@RestController
@RequestMapping("/interface")
public class interfaceController {

    @Resource
    InterfaceService interfaceService;

    @Resource
    InterfaceConfig interfaceConfig;



    @PostMapping("/forwarderFile")
    @ApiOperation("接口转发器(文件)")
    public ResResult<List<Object>> getByForwardFile(MultipartFile file, String paramName, Integer interfaceNum) {
        if (file!=null)
            return interfaceService.interfaceByFiles(file, interfaceNum, paramName);

        return ResResult.error(502, "无参调用，请传入参数文件列表！");
    }


    @GetMapping("/forwarderParam")
    @ApiOperation("接口转发器(参数)")
    public ResResult<List<Object>> getByForwardParam(@ApiParam(value = "参数值") @RequestParam(required = false) String param, @ApiParam(value = "参数名字") String paramName,
                                                     @ApiParam(value = "接口序号") Integer interfaceNum) throws NoSuchAlgorithmException {
        return interfaceService.interfaceByParams(param, interfaceNum, paramName);
    }

    @PostMapping("/downloadFile")
    @ApiOperation(value = "下载查询excel(文件)")
    public ResResult<Boolean> downloladFile(HttpServletRequest request, HttpServletResponse response, @RequestBody MultipartFile file,
                                            @RequestParam("paramName") @ApiParam(value = "参数名字") String paramName,
                                            @RequestParam("interfaceNum") @ApiParam(value = "接口序号") Integer interfaceNum) throws IOException {
        return ResResult.ok(interfaceService.downloadByFile(request, response, file, interfaceNum, paramName),"查询信息下载成功");
    }

    @GetMapping("/downloadParam")
    @ApiOperation(value = "下载查询excel(参数)")
    public ResResult<Boolean> downloladParam(HttpServletRequest request, HttpServletResponse response, @ApiParam(value = "参数值") String param,
                                             @ApiParam(value = "接口序号") Integer interfaceNum, @ApiParam(value = "参数名字") String paramName) throws IOException {
        Boolean flag = interfaceService.downloadByParam(request, response, param, interfaceNum, paramName);
        return ResResult.ok(flag, flag ? "下载成功" : "下载失败");
    }

    @GetMapping("/getHeader")
    @ApiOperation(value = "获取各表表头")
    public ResResult<List<Field>> getHeader(@ApiParam(value = "接口序号") Integer interfaceNum){
        if (interfaceNum == null || interfaceNum < 0 || interfaceNum > 23){
            return ResResult.error(407, "接口序号为空或越界");
        }
        return ResResult.ok(interfaceConfig.getInterfaceFields().get(interfaceNum), interfaceConfig.getInterfaceFields().get(interfaceNum), "查询表头成功");
    }

    @GetMapping("/getParams")
    @ApiOperation(value = "获取可选参数")
    public ResResult<List<Field>> getParams(){
        HashSet<Field> requestFields = interfaceConfig.getRequestFields();
        return ResResult.ok(new ArrayList<>(requestFields), new ArrayList<>(requestFields), "返回可选参数列表");
    }

    @GetMapping("/getTablesByParam")
    @ApiOperation(value = "获取所选参数下所有接口的返回值")
    public ResResult<List<FieldsListVO>> getTablesByParam(@ApiParam(value = "参数值") @RequestParam(required = false) String param,
                                                          @ApiParam(value = "参数名字") String paramName){
        return interfaceService.tablesByParam(param, paramName);
    }

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public ResResult<List<Object>> test(){
        return interfaceService.test();
    }
}
