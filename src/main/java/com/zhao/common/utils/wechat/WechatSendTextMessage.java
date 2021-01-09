package com.zhao.common.utils.wechat;

public class WechatSendTextMessage {
	private String touser;
	private String msgtype;
	private Text text;
	
	public class Text {
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
	
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	public String getMsgtype() {
		return msgtype;
	}
	public void setMsgtype(String msgtype) {
		this.msgtype = msgtype;
	}
	public String getTouser() {
		return touser;
	}
	public void setTouser(String touser) {
		this.touser = touser;
	}
}
