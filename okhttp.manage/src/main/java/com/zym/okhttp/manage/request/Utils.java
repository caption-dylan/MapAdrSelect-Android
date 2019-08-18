package com.zym.okhttp.manage.request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by zymapp on 2016/9/26.
 */

public class Utils {

    /**
     * is null or its length is 0 or it is made by space
     *
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0 or it is made by space, return true, else return false.
     */
    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0);
    }
    /**
     * is null or its length is 0
     *
     * <pre>
     * isEmpty(null) = true;
     * isEmpty(&quot;&quot;) = true;
     * isEmpty(&quot;  &quot;) = false;
     * </pre>
     *
     * @param str
     * @return if string is null or its size is 0, return true, else return false.
     */
    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0) && isBlank(str);
    }

    public static String getFullUrl(String url, List<Part> params, boolean urlEncoder) {
        StringBuffer urlFull = new StringBuffer();
        urlFull.append(url);
        if (urlFull.indexOf("?", 0) < 0 && params.size() > 0) {
            urlFull.append("?");
        }
        int flag = 0;
        for (Part part:params){
            String key = part.getKey();
            String value = part.getValue();
            if(urlEncoder){//只对key和value编码
                try {
                    key = URLEncoder.encode(key, "UTF-8");
                    value = URLEncoder.encode(value, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            urlFull.append(key).append("=").append(value);
            if (++flag != params.size()){
                urlFull.append("&");
            }
        }

        return urlFull.toString();
    }
}
