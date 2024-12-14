package com.zhao.common.utils.ai;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhao.common.utils.HttpUtils;

/**
 * 百度AI
 * @Author: zhaolianqi
 * @Date: 2021/7/13 11:26
 * @Version: v1.0
 */
public class BaiduAI {

    private static AIAccessToken accessToken = null;

    private static BaiduAI instance = null;

    private BaiduAI(){}

//    private String appid = "24538395";
    private String apiKey = "5YpGtB5AS7jGVz9q6vkk7VGg";
    private String secretKey = "QLX5BasexoTr1My35dp7TAb314cBh5Pk";

    private String getAccessToken(){
        if (accessToken == null || System.currentTimeMillis()/1000 - accessToken.getSt() > accessToken.getExp()){
            String res = HttpUtils.post(
                    "https://aip.baidubce.com/oauth/2.0/token" +
                            "?grant_type=client_credentials" +
                            "&client_id=" + apiKey +
                            "&client_secret=" + secretKey, "");
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (accessToken == null)
                accessToken = new AIAccessToken();
            accessToken.setAccessToken(jsonObject.getString("access_token"));
            accessToken.setExp(jsonObject.getInteger("expires_in"));
        }
        return accessToken.getAccessToken();
    }

    public void lexer(String text){
        JSONObject jsonObject = new JSONObject(2);
        jsonObject.put("text", text);
        String res =HttpUtils.post("https://aip.baidubce.com/rpc/2.0/nlp/v1/lexer?charset=UTF-8&access_token=" + getAccessToken(),
                jsonObject.toJSONString());
        System.out.println(res);
    }

    /**
     * 情感倾向分析
     * @Author zhaolianqi
     * @Date 2021/7/13 16:39
     */
    public BDSentiment sentimentClassify(String text){
        JSONObject jsonObject = new JSONObject(2);
        jsonObject.put("text", text);
        String res =HttpUtils.post("https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify?charset=UTF-8&access_token=" + getAccessToken(),
                jsonObject.toJSONString());
        // {"log_id":5150165905483832365,"text":"大财配资杠杆炒股锂电股卷土重来持续走强","items":[{"positive_prob":0.145559,"sentiment":0,"confidence":0.676536,"negative_prob":0.854441}]}
        jsonObject = JSONObject.parseObject(res);
        JSONArray array = jsonObject.getJSONArray("items");
        if (array == null)
            return new BDSentiment();
        jsonObject = array.getJSONObject(0);

        BDSentiment sentiment = new BDSentiment();
        sentiment.setConfidence(jsonObject.getDouble("confidence"));
        sentiment.setNegativeProb(jsonObject.getDouble("negative_prob"));
        sentiment.setPositiveProb(jsonObject.getDouble("positive_prob"));
        return sentiment;
    }

    public static BaiduAI getInstance(){
        if (instance == null){
            synchronized (BaiduAI.class){
                if (instance == null)
                    instance = new BaiduAI();
            }
        }
        return instance;
    }

}
