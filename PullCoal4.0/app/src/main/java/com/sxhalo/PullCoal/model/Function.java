package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * 功能列表
 * Created by amoldZhang on 2016/12/7.
 */
public class Function implements Serializable{

//            "moduleId": "1",
//            "moduleName": "找我拉煤",
//            "imageUrl": "http://172.16.99.248:8080/lameiapiV5/upload/function1.png"

    private String moduleId;
    private String moduleName;
    private String imageUrl;

    public Function(){
    }

    public Function getFunction(Map<String, Object> stringStringMap){
        Function function = new Function();
        if (stringStringMap.size() != 0){
            Gson gson = new Gson();
            String jsonString = gson.toJson(stringStringMap);
            function = gson.fromJson(jsonString,Function.class);
        }
        return function;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
