package com.zhao.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 敏感词过滤器
 * @Author: zhaolianqi
 * @Date: 2020/12/22 17:19
 * @Version: v1.0
 */
public class SensitiveWordFilter {

    private Logger logger = LoggerFactory.getLogger(SensitiveWordFilter.class);

    private static SensitiveWordFilter instance = null;

    private int matchMode = MatchMode.MAX_MATCH;

    /** 匹配模式 */
    interface MatchMode {
        /** 1：最小匹配原则 */
        int MIN_MATCH = 1;
        /** 2：最大匹配原则 */
        int MAX_MATCH = 2;
    }

    private Map<String, SensitiveNode> sensitiveMap = null;

    private SensitiveWordFilter(){

    }

    public void setMatchMode(int matchMode){
        this.matchMode = matchMode;
    }

    private void initSensitiveWords(){
        sensitiveMap = new HashMap<>(1024);
        BufferedReader reader = null;
        try {
            InputStream is = new FileInputStream(new File("sensi_words.txt"));
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            for (String line = reader.readLine(); line != null; line = reader.readLine()){
                addSensitiveWord(line);
            }
        } catch (IOException e){
            e.printStackTrace();
            logger.error("初始化敏感词异常：{}", e.getLocalizedMessage());
        } finally {
            try {
                if (reader != null){
                    reader.close();
                    reader = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加敏感词
     * @param words 敏感词列表
     * @Author zhaolianqi
     */
    public void addSensitiveWords(List<String> words){
        for (String w: words)
            addSensitiveWord(w);
    }

    /**
     * 添加敏感词
     * @param word 敏感词
     * @Author zhaolianqi
     */
    public void addSensitiveWord(String word){
        SensitiveNode node = null;
        for (int i = 0; i < word.length(); i++){
            SensitiveNode n = new SensitiveNode(String.valueOf(word.charAt(i)));
            if (i == word.length() - 1)
                n.isEnd = true;

            if (i == 0){
                node = sensitiveMap.get(n.word);
                if (node == null){
                    sensitiveMap.put(n.word, n);
                    node = n;
                }
            } else {
                if (node.subWords == null)
                    node.subWords = new ArrayList<>(6);
                int index = -1;
                for (int j = 0; j < node.subWords.size(); j++){
                    if (node.subWords.get(j).word.equalsIgnoreCase(n.word)){
                        index = j;
                        break;
                    }
                }
                if (index < 0){
                    n.parent = node;
                    node.subWords.add(n);
                    node = n;
                } else {
                    node = node.subWords.get(index);
                    node.isEnd = n.isEnd;
                }
            }
        }
    }

    /**
     * 提取敏感词
     * @param sequence 目标文本
     * @Author zhaolianqi
     * @return 敏感词列表
     */
    public List<String> filter(String sequence){
        List<String> res = new ArrayList<>(6);
        SensitiveNode node = null;
        SensitiveNode parentNode = null;
        for (int i = 0; i < sequence.length(); i++){
            String c = String.valueOf(sequence.charAt(i));
            if (node == null){
                node = sensitiveMap.get(c);
            } else {
                parentNode = node;
                if (node.subWords != null){
                    SensitiveNode tempNode = null;
                    for (int j = 0; j < node.subWords.size(); j++){
                        if (node.subWords.get(j).word.equalsIgnoreCase(c)){
                            tempNode = node.subWords.get(j);
                            break;
                        }
                    }
                    node = tempNode;
                } else {
                    node = null;
                }
            }
            if (node != null){
                if (node.isEnd && matchMode == MatchMode.MIN_MATCH){ // 最小匹配原则
                    res.add(getWords(node));
                    node = null;
                    parentNode = null;
                }
            } else if (matchMode == MatchMode.MAX_MATCH) { // 最大匹配原则
                SensitiveNode lastEndNode = getLatestEndNode(parentNode);
                if (lastEndNode != null){
                    res.add(getWords(lastEndNode));
                }
                parentNode = null;
            }
        }
        if (matchMode == 2 && node != null){
            SensitiveNode lastEndNode = getLatestEndNode(node);
            if (lastEndNode != null){
                res.add(getWords(lastEndNode));
            }
        }
        return res;
    }

    private SensitiveNode getLatestEndNode(SensitiveNode node){
        if (node == null)
            return null;
        if (node.isEnd)
            return node;
        return getLatestEndNode(node.parent);
    }

    private String getWords(SensitiveNode node){
        if (node.parent == null)
            return node.word;
        String w = node.word;
        node = node.parent;
        return getWords(node) + w;
    }

    public static SensitiveWordFilter getInstance(){
        if (instance == null){
            synchronized (SensitiveWordFilter.class){
                if (instance == null){
                    instance = new SensitiveWordFilter();
                    instance.initSensitiveWords();
                }
            }
        }
        return instance;
    }

    static class SensitiveNode {
        SensitiveNode(String word){
            this.word = word;
        }
        boolean isEnd = false;
        String word = null;
        SensitiveNode parent = null;
        List<SensitiveNode> subWords = null;

        @Override
        public String toString() {
            return "SensitiveNode{" +
                    "word='" + word + '}';
        }
    }

}
