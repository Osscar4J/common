package com.zhao.common.utils.wechat;

import com.alibaba.fastjson.JSONObject;
import com.zhao.common.utils.HttpUtils;
import com.zhao.common.utils.wechat.menu.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeChatUtils {

    private static AccessToken accessToken = null;
    private static AccessToken jsApiTicket = null;
    private static Logger logger = LoggerFactory.getLogger(WeChatUtils.class);
    private WeChatUtils(){}

    /**
     * 发送模板消息
     * @param jsonObject
     * @return
     */
    public static String sendTemplateMsg(JSONObject jsonObject, String accessToken){
        return HttpUtils.post(
                "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=" + accessToken,
                jsonObject.toJSONString()
        );
    }

    public static String addTempMedia(String appId, String appSecret, String filePath) throws Exception {
        return addTempMedia(appId, appSecret, new File(filePath));
    }

    public static String addTempMedia(String appId, String appSecret, File file) throws Exception {
        String res = HttpUtils.upload("https://api.weixin.qq.com/cgi-bin/media/upload" +
                "?access_token=" + getAccessToken(appId, appSecret) +
                "&type=image", file, "media.jpg");
        return JSONObject.parseObject(res).getString("media_id");
    }

    // {"ticket":"gQEy8TwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyYVFzcnN6WGJlWjMxMDAwME0wMzkAAgS7EcRdAwQAAAAA","url":"http:\/\/weixin.qq.com\/q\/02aQsrszXbeZ310000M039"}
    public static String getTicket(String appId, String appSecret, long userId){
        JSONObject params = new JSONObject();
        params.put("action_name", "QR_SCENE");
        params.put("expire_seconds", 2592000); // 30天

        JSONObject actionInfo = new JSONObject();
        JSONObject scene = new JSONObject();
        scene.put("scene_id", userId);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);

        String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + WeChatUtils.getAccessToken(appId, appSecret), params.toJSONString());
        actionInfo = JSONObject.parseObject(res);
        String ticket = actionInfo.getString("ticket");
        if (ticket == null)
            logger.error(res);
        return ticket;
    }

    public static JSONObject getUserInfo(String accessToken, String openid){
        String res = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/user/info" +
                "?access_token=" + accessToken +
                "&openid=" + openid +
                "&lang=zh_CN");
        return JSONObject.parseObject(res);
    }

    public static String getAccessToken(String appId, String appSecret){
        synchronized (WeChatUtils.class){
            if (accessToken == null
                    || System.currentTimeMillis() - accessToken.getStartTime() > 7000000
                    || accessToken.getAccessToken() == null){ // 两小时内有效
                String res = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/token" +
                        "?grant_type=client_credential" +
                        "&appid=" + appId +
                        "&secret=" + appSecret);
                JSONObject resJson = JSONObject.parseObject(res);
                if (resJson.getString("access_token") == null){
                    logger.info("获取accessToken失败: {}", res);
                    return null;
                }
                if (accessToken == null)
                    accessToken = new AccessToken();

                accessToken.setAccessToken(resJson.getString("access_token"));
                accessToken.setStartTime(System.currentTimeMillis());
                logger.info("获取accessToken: {}", res);
            }
        }
        return accessToken.getAccessToken();
    }

    /**
     * 创建菜单
     * @param menu
     * @param accessToken
     */
    public static void createMenus(Menu menu, String accessToken){
        String res = HttpUtils.post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken,
                JSONObject.toJSONString(menu));
        JSONObject resJson = JSONObject.parseObject(res);
        if (resJson != null){
            if (resJson.getInteger("errcode") != 0){
                throw new RuntimeException("菜单创建失败！code:" + resJson.getInteger("errcode"));
            }
        }
    }

    /**
     * 查询菜单信息
     * @param accessToken
     * @return
     */
    public static String getMenus(String accessToken){
        return HttpUtils.get("https://api.weixin.qq.com/cgi-bin/menu/get?access_token=" + accessToken);
    }

    /**
     * 获取jsApiTicket
     * @Author zhaolianqi
     * @Date 2020/12/29 20:25
     */
    public static String getJSApiTicket(String appId, String appSecret){
        synchronized (WeChatUtils.class){
            if (jsApiTicket == null
                    || System.currentTimeMillis() - jsApiTicket.getStartTime() > 7000000
                    || jsApiTicket.getAccessToken() == null){ // 两小时内有效
                String accessToken = getAccessToken(appId, appSecret);
                String res = HttpUtils.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi");

                JSONObject resJson = JSONObject.parseObject(res);
                if (resJson.getString("ticket") == null){
                    logger.info("获取jsApiTicket失败: {}", res);
                    return null;
                }

                if (jsApiTicket == null)
                    jsApiTicket = new AccessToken();

                jsApiTicket.setAccessToken(resJson.getString("ticket"));
                jsApiTicket.setStartTime(System.currentTimeMillis());
                logger.info("获取accessToken: {}", res);
            }
        }
        return jsApiTicket.getAccessToken();
    }

    /**
     * 生成小程序码
     * @param accessToken accessToken
     * @param page 小程序页面路径
     * @param outputStream 输出流
     * @Author zhaolianqi
     * @Date 2021/1/6 16:11
     */
    public static void createMiniProgramCode(String accessToken, String page, OutputStream outputStream){
        try {
            URL url = new URL("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=" + accessToken);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            // 发送请求参数
            JSONObject paramJson = new JSONObject();
            paramJson.put("scene", "1234567890");
//            paramJson.put("page", page); //小程序页面
            paramJson.put("width", 430);
            paramJson.put("is_hyaline", true);
            paramJson.put("auto_color", true);
//            设置颜色
//             paramJson.put("auto_color", false);
//             JSONObject lineColor = new JSONObject();
//             lineColor.put("r", 0);
//             lineColor.put("g", 0);
//             lineColor.put("b", 0);
//             paramJson.put("line_color", lineColor);

            printWriter.write(paramJson.toString());
            // flush输出流的缓冲
            printWriter.flush();
            //开始获取数据
            BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
            int len;
            byte[] arr = new byte[1024];
            while ((len = bis.read(arr)) != -1){
                outputStream.write(arr, 0, len);
                outputStream.flush();
            }
            outputStream.close();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    static class AccessToken {
        private String accessToken;
        private long startTime;

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }
    }

}
