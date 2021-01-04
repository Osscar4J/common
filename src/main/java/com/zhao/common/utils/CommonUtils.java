package com.zhao.common.utils;

import com.zhao.common.exception.BusinessException;
import com.zhao.common.respvo.ResponseStatus;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {
    private CommonUtils(){}
    private final static String[] agent = { "Android", "iPhone", "iPod", "Windows Phone", "MQQBrowser" };

    /**
     * 判断请求是不是来自手机端
     * @param request
     * @return
     */
    public static boolean checkAgentIsMobile(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        boolean flag = false;
        if (!ua.contains("Windows NT") || (ua.contains("Windows NT") && ua.contains("compatible; MSIE 9.0;"))) {
            // 排除 苹果桌面系统
            if (!ua.contains("Windows NT") && !ua.contains("Macintosh")) {
                for (String item : agent) {
                    if (ua.contains(item)) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    /**
     * 获取请求者的IP
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                // 根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
            // = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }

    public static String getParamtersStrByRequest(HttpServletRequest request){
        try {
            InputStream inStream = request.getInputStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            outStream.close();
            inStream.close();
            return new String(outStream.toByteArray(), "utf-8");
        } catch (Exception e){
            return null;
        }
    }

    public static String getUUIDStr(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static String getRandNumbers(int len){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++){
            sb.append((int)(Math.random() * 10));
        }
        return sb.toString();
    }

    /**
     * 计算IMEI的最后一位数字（校验位）
     * 将每一个偶数位的数字乘2，然后计算出乘积的十位数和个数之和。
     * 将奇数位的数字相加，然后加上第一步得到的和。
     * 果结果的个位是0，则校验位为0，否则为10减去个位数。
     * @param imei
     * @return
     */
    public static String calcImeiLastNumber(String imei){
        if (StringUtils.isEmpty(imei))
            throw new BusinessException(ResponseStatus.INVALIDE_PARAMS);
        if (imei.length() != 14)
            return imei;
        char[] chars = imei.toCharArray();
        String[] temp = new String[7];
        int index = 0;
        int sum = 0;
        for (int i = 0; i < chars.length; i++){
            if ((i & 1) == 0){ // 奇数位
                sum += Integer.parseInt(String.valueOf(chars[i])); // 奇数位之和
            } else { // 偶数位
                temp[index++] = String.valueOf(Integer.parseInt(String.valueOf(chars[i])) * 2);
            }
        }
        int tenSum = 0;
        int oneSum = 0;
        for (String s: temp){
            if (s.length() == 1){
                oneSum += Integer.parseInt(s);
            } else {
                tenSum += Integer.parseInt(String.valueOf(s.charAt(0)));
                oneSum += Integer.parseInt(String.valueOf(s.charAt(1)));
            }
        }
        oneSum += tenSum;
        sum += oneSum;
        String s = String.valueOf(sum);
        StringBuilder res = new StringBuilder();
        res.append(imei);
        if (s.endsWith("0")){
            res.append("0");
        } else {
            res.append(10 - Integer.parseInt(String.valueOf(s.charAt(1))));
        }
        return res.toString();
    }

    private static Pattern dataFormatPattern = Pattern.compile("[a-zA-Z0-9]{16}|\\d{15}|[a-zA-Z0-9]{12}");

    public static List<String> dataFormat(List<String> data){
        List<String> res = dataFormat(data, false);
        if (res.isEmpty()){
            res = dataFormat(data, true);
        }
        return res;
    }
    public static List<String> dataFormat(List<String> data, boolean replaceSpace){
        List<String> res = new ArrayList<>();
        for (String d: data){
            if (replaceSpace)
                d = d.replaceAll("\\s", "");
            Matcher matcher = dataFormatPattern.matcher(d);
            while (matcher.find()){
                res.add(matcher.group());
            }
        }
        return res;
    }

}
