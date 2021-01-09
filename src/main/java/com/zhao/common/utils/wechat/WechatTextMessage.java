package com.zhao.common.utils.wechat;

/**
 * 微信文本消息
 * @author ZhaoLian
 *
 */
public class WechatTextMessage extends WechatBaseMessage {
	/**
     * 文本消息内容
     */
    public String Content;
    /**
     * 消息id，64位整型
     */
    public long MsgId ;
     
    public String getContent() {
        return Content;
    }
    public void setContent(String content) {
        Content = content;
    }
    public long getMsgId() {
        return MsgId;
    }
    public void setMsgId(long msgId) {
        MsgId = msgId;
    }
}
