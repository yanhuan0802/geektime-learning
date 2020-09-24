package com.yanhuan.authorization.util;

import java.util.Map;
import java.util.Set;

/**
 * @author Wang
 */
public class URLParamsUtil {


    public static String appendParams(String url, Map<String, String> params) {
        if (null == url) {
            return "";
        } else if (params.isEmpty()) {
            return url.trim();
        } else {
            String s = URLParamsUtil.mapToStr(params);

            url = url.trim();
            int length = url.length();
            int index = url.indexOf("?");
            if (index > -1) {
                //url最后一个符号为？，如：http://wwww.yy.com?
                if ((length - 1) == index) {
                    url += s;
                    //情况为：http://wwww.baidu.com?aa=11
                } else {
                    url += "&" + s;
                }
                //url后面没有问号，如：http://wwww.baidu.com
            } else {
                url += "?" + s;
            }
            return url;
        }
    }


    public static String mapToStr(Map<String, String> params) {
        StringBuilder sb = new StringBuilder("");
        Set<String> keys = params.keySet();
        for (String key : keys) {
            sb.append(key).append("=").append(params.get(key)).append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}


