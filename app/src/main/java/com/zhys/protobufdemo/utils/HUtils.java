package com.zhys.protobufdemo.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 工具类
 * Created by HDL on 2018/7/4.
 */

public class HUtils {
    public static Map<String, RequestBody> generateRequestBody(HashMap<String, Object> requestDataMap) {
        Map<String, RequestBody> requestBodyMap = new HashMap<>();
        for (String key : requestDataMap.keySet()) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                    TextUtils.isEmpty(requestDataMap.get(key).toString()) ? "" : requestDataMap.get(key).toString());
            requestBodyMap.put(key, requestBody);
        }
        return requestBodyMap;
    }

    public static HashMap<String, Object> parseJSONString(String json) {

        JSONObject obj;

        try {

            obj = new JSONObject(json);

            return parseJSONObject(obj);

        } catch (JSONException e) {

            e.printStackTrace();

        }

        return new HashMap<String, Object>();

    }

    public static HashMap<String, Object> parseJSONObject(JSONObject jsonobj) {

        JSONArray a_name = jsonobj.names();

        HashMap<String, Object> map = new HashMap<String, Object>();

        if (a_name != null) {

            int i = 0;

            while (i < a_name.length()) {

                String key;

                try {

                    key = a_name.getString(i);

                    Object obj = jsonobj.get(key);

                    map.put(key, parseUnknowObjectToJson(obj));

                } catch (JSONException e) {

                    e.printStackTrace();

                }

                i++;

            }

        }
        return map;
    }


    public static File saveBitmap(Bitmap bitmap, String filePath) {
        if (bitmap != null) {

            File file = new File(filePath);
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return file;

        }
        return null;
    }


    public static ArrayList<Object> parseJSONArray(JSONArray jsonarr) {

        ArrayList<Object> list = new ArrayList<Object>();

        int len = jsonarr.length();

        for (int i = 0; i < len; i++) {

            Object o;

            try {

                o = jsonarr.get(i);

                list.add(parseUnknowObjectToJson(o));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

        return list;

    }

    private static Object parseUnknowObjectToJson(Object o) {

        if (o instanceof JSONObject) {

            return parseJSONObject((JSONObject) o);

        } else if (o instanceof JSONArray) {

            return parseJSONArray((JSONArray) o);

        }

        return o;
    }
    /**
     * 判断是否是debug模式
     * @param context
     * @return
     */
    public static boolean isDebug(Context context) {
        boolean isDebug = context.getApplicationInfo() != null &&
                (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        return isDebug;
    }
}
