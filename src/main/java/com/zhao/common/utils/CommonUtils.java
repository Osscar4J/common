package com.zhao.common.utils;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.qianxinyao.analysis.jieba.keyword.Keyword;
import com.qianxinyao.analysis.jieba.keyword.TFIDFAnalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private CommonUtils(){}

    private static JiebaSegmenter segmenter = new JiebaSegmenter();

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

    /**
     * 判断当前系统是不是Linux
     * @Author zhaolianqi
     * @Date 2021/7/1 16:35
     */
    public static boolean isLinux(){
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }

//    public static WebDriver getWebDriver(String driverPath){
//        return getWebDriver(driverPath, null, 0);
//    }

//    public static WebDriver getWebDriver(String driverPath, String proxyServer, int port){
//        if (!webDriverPathSystem){
//            System.setProperty("webdriver.chrome.driver", driverPath);
//            webDriverPathSystem = true;
//        }
//        ChromeOptions chromeOptions = new ChromeOptions();
//        chromeOptions.addArguments(
//                "--no-sandbox",
//                "--disable-dev-shm-usage",
//                "window-size=1920x3000",
//                "--disable-gpu",
//                "--hide-scrollbars",
//                "blink-settings=imagesEnabled=false",
//                "--headless"
//        );
//        chromeOptions.setHeadless(true);
//
//        if (proxyServer != null){
//            System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "");
//            org.openqa.selenium.Proxy seProxy = new org.openqa.selenium.Proxy();
//            seProxy.setHttpProxy(proxyServer + ":" + port).setSslProxy(proxyServer + ":" + port);
//            chromeOptions.setProxy(seProxy);
//        }
//
//        return new ChromeDriver(chromeOptions);
//    }

    /**
     * 判断字符串中是否包含中文
     * @param str
     * 待校验字符串
     * @return 是否为中文
     * @warn 不能校验是否为中文标点符号
     */
    public static boolean containsChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static List<String> chineseFenci(String sentences){
        return segmenter.sentenceProcess(sentences);
    }

    public static List<String> chineseFenci(String sentences, int topN) {
        TFIDFAnalyzer analyzer = new TFIDFAnalyzer();
        List<Keyword> keywords = analyzer.analyze(sentences.replaceAll("[^0-9a-zA-Z\u4e00-\u9fa5]", ""), topN);
        double sum = 0D;
        for (Keyword keyword : keywords) {
            sum += keyword.getTfidfvalue();
        }
        double ava = sum / topN;
        int size = topN - topN%2;
        if (size < 2)
            size = 2;
        List<String> res = new ArrayList<>(size);
        for (Keyword keyword : keywords) {
            if (keyword.getTfidfvalue() >= ava)
                res.add(keyword.getName());
        }
        return res;
    }

    /**
     * 计算中位数（整数）
     * @param nums 数组
     * @return
     */
    public static int medianInteger(int[] nums){
        if(nums.length==0)
            return 0;
        int start=0;
        int end=nums.length-1;
        int index= partitionInteger(nums, start, end);
        if(nums.length%2==0){
            while(index!=nums.length/2-1){
                if(index>nums.length/2-1){
                    index= partitionInteger(nums, start, index-1);
                }else{
                    index= partitionInteger(nums, index+1, end);
                }
            }
        }else{
            while(index!=nums.length/2){
                if(index>nums.length/2){
                    index= partitionInteger(nums, start, index-1);
                }else{
                    index= partitionInteger(nums, index+1, end);
                }
            }
        }
        return nums[index];
    }

    private static int partitionInteger(int[] nums, int start, int end){
        int left=start;
        int right=end;
        int pivot=nums[left];
        while(left<right){
            while(left<right&&nums[right]>=pivot){
                right--;
            }
            if(left<right){
                nums[left]=nums[right];
                left++;
            }
            while(left<right&&nums[left]<=pivot){
                left++;
            }
            if(left<right){
                nums[right]=nums[left];
                right--;
            }
        }
        nums[left]=pivot;
        return left;
    }

    /***
     * 计算中位数（浮点数）
     * @param nums 数组
     * @return
     */
    public static float medianFloat(float[] nums){
        if(nums.length==0)
            return 0;
        int start=0;
        int end=nums.length-1;
        int index= partitionFloat(nums, start, end);
        if(nums.length%2==0){
            while(index!=nums.length/2-1){
                if(index>nums.length/2-1){
                    index= partitionFloat(nums, start, index-1);
                }else{
                    index= partitionFloat(nums, index+1, end);
                }
            }
        }else{
            while(index!=nums.length/2){
                if(index>nums.length/2){
                    index= partitionFloat(nums, start, index-1);
                }else{
                    index= partitionFloat(nums, index+1, end);
                }
            }
        }
        return nums[index];
    }

    private static int partitionFloat(float[] nums, int start, int end){
        int left=start;
        int right=end;
        float pivot=nums[left];
        while(left<right){
            while(left<right&&nums[right]>=pivot){
                right--;
            }
            if(left<right){
                nums[left]=nums[right];
                left++;
            }
            while(left<right&&nums[left]<=pivot){
                left++;
            }
            if(left<right){
                nums[right]=nums[left];
                right--;
            }
        }
        nums[left]=pivot;
        return left;
    }

}
