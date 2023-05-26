package com.vd.backend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

    /**
     * Check if data is a fhir resource
     * @param data
     * @return
     */
    public static boolean isResource(String data) {
        try {
            JSONObject jsonObject = JSON.parseObject(data);
            if (jsonObject.containsKey("resourceType")) {
                return true;
            }
        } catch (JSONException e) {
            return false;
        }

        return false;
    }

    public static void main(String[] args) {
        boolean rel = JsonUtil.isResource("666");

        System.out.println(rel);
    }

    public static JSONObject getFakeCode(String type) {
        JSONObject codeObject = new JSONObject();
        JSONArray codingArray = new JSONArray();
        JSONObject element = new JSONObject();

        element.put("system", "http://loinc.org");
        element.put("code", "29463-7");
        element.put("display", type);                         // Should be changed
        codingArray.add(element);
        codeObject.put("coding", codingArray);

        return codeObject;
    }

}
