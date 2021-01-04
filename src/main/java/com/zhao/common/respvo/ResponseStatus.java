package com.zhao.common.respvo;

public enum ResponseStatus {
    /**操作成功*/
    SUCCESS(0, "操作成功"),
    /**400：操作失败*/
    FAILURE(400, "操作失败"),
    /**401：登陆已失效*/
    UNAUTHORIZED(401, "登陆已失效"),
    /**402:非法参数*/
    INVALIDE_PARAMS(402, "非法参数"),
    /**403:无权限操作*/
    NO_PERMISSION(403, "无权限操作"),
    /**404:对象不存在*/
    OBJECT_NOT_FOUND(404, "对象不存在"),
    /**500：服务异常*/
    SERVER_ERROR(500, "服务异常"),
	/**600：数据有变动，请刷新重试*/
	DATA_CHANGED(600, "数据有变动，请刷新重试"),
	/**700：操作太频繁*/
	OPERATION_TOO_FAST(700, "操作太频繁"),

    /**1001: 账号或密码错误*/
    USER_ACCOUNT_OR_PWD_WRONG(1001, "账号或密码错误")
    /**1002：账号不存在*/
	,USER_ACCOUNT_NOT_EXIST(1002, "账号不存在")
    /**1003：手机号不能为空*/
	,USER_PHONE_NOT_EMPTY(1003, "手机号不能为空")
    /**1004：密码不能为空*/
	,USER_PASSWD_NOT_EMPTY(1004, "密码不能为空")
    /**1005：该手机号已被注册*/
	,USER_PHONE_REGISTERED(1005, "该手机号已被注册")
    /**1006：该账号已被冻结请联系管理员*/
	,USER_FREEZED(1006, "该账号已被冻结请联系管理员")
    /**1007：账号异常请联系管理员*/
	,USER_UNORMAL(1007, "账号异常请联系管理员")
    /**1008：密码错误*/
	,USER_PASSWORD_INCORRECT(1008, "密码错误")
    /**1009：请输入正确的手机号*/
	,USER_PHONE_ERROR(1009, "请输入正确的手机号")
    /**1010：验证码已过期*/
	,USER_SMSCODE_EXPIRED(1010, "验证码已过期")
    /**1011：验证码错误*/
	,USER_SMSCODE_INCORRECT(1011, "验证码错误")
    /**1012：尚未完成实名认证*/
	,USER_NOT_CERTIFICATED(1012, "尚未完成实名认证")
    /**1013：用户不存在*/
	,USER_NOT_FOUND(1013, "用户不存在")
    /**1014：未选择照片*/
	,USER_HASNO_IMAGE(1014, "未选择照片")
    /**1015：该账号已注册*/
	,USER_ACCOUNT_HAS_ECIST(1015, "该账号已注册")
    /**1016：用户不在线*/
	,USER_OFFLINE(1016, "用户不在线")
	/**1017: 密码长度最多6位*/
	,USER_INVALID_PASSWORD_LENGTH(1017, "密码长度最多6位")
	/**1018：无效的openid*/
	,USER_INVALID_OPENID(1018, "无效的openid")
	/**1019: 积分不足*/
	,USER_CREDIT_NOT_ENOUGH(1019, "积分不足")
	/**1020：邮箱不能为空*/
	,USER_EMAIL_CANT_EMPTY(1020, "邮箱不能为空")
	/**1021：无效的key*/
	,USER_INVALID_KEY(1021, "无效的key")
	/**1022：账号不能为空*/
	,USER_ACCOUNT_CANT_BE_EMPTY(1022, "账号不能为空")

	/**1101：服务不存在*/
	,SERVICE_NOT_EXIST(1101, "服务不存在")
	/**1102：服务暂时不可用*/
	,SERVICE_CANT_USE(1102, "服务暂时不可用")
	/**1103：服务维护中*/
	,SERVICE_FIX(1103, "服务维护中")
	/**1104： 该服务不支持批量操作*/
	,SERVICE_NOT_ALLOW_BULK(1104, "该服务不支持批量操作")
	/**1105：该服务不支持数字字母混合*/
	,SERVICE_NOT_ALLOW_ALPHA(1105, "该服务不支持数字字母混合")
	/**1106：数据格式有误*/
	,SERVICE_DATA_ROMAT_ERROR(1106, "数据格式有误")
	/**1107: 供应商服务同步失败*/
	,SERVICE_SYNC_CORRECT(1107, "供应商服务同步失败")

    /**1601：文件不存在*/
	,FILE_NOT_EXSIT(1601, "文件不存在")
	/**1602：未配置OSS信息*/
	,FILE_HAS_NO_BUCKET(1602, "未配置OSS信息")

    /**1701：已失效的价格*/
	, PRODUCT_PRICE_INVALID(1701, "已失效的价格")
	/**1702: 无效的商品*/
	, PRODUCT_INVALID(1702, "无效的商品")
	/**1703: 购买次数受限*/
	, PRODUCT_BUY_LIMITED(1703, "购买次数受限")
	/**1704：库存不足*/
	, PRODUCT_REMAIN_EMPTY(1704, "库存不足")

	/**2101：无效的文件*/
	,FILE_INVALIDATE(2101, "无效的文件")

	/**2201：任务正在进行中*/
	,TASK_EXECUTING(2201, "任务正在进行中")

    /**3001：已存在该邮箱*/
	,MAIL_EXIST(3001, "已存在该邮箱")
	/**3002: 邮箱配置错误*/
	,MAIL_CONFIG_ERROR(3002, "邮箱配置错误")
	/**3003：未开启邮件服务*/
	,MAIL_NOT_ON(3003, "未开启邮件服务")
	/**3004：错误的邮件地址*/
	,MAIL_INVALID(3004, "错误的邮件地址")

    /**4001：该手机号已报过名*/
	,MEETING_USER_EXIST(4001, "该手机号已报过名")

	/**5001：无效的订单*/
	,ORDER_INVALID(5001, "无效的订单")
	/**5002: 查不到订单信息*/
	,ORDER_EMPTY(5002, "查不到订单信息")
	/**5003：当前订单状态不支持退款*/
	,ORDER_NOT_SUPPORT_STATUS(5003, "当前订单状态不支持退款")
	/**5004：当前订单不支持退款*/
	,ORDER_NOT_SUPPORT_REFUND(5004, "当前订单不支持退款")
	/**已有相同的订单正在处理中，请勿重复提交*/
	,ORDER_DUPLICATED(5005, "已有相同的订单正在处理中，请勿重复提交")
            ;

    private final int code;
    private final String msg;

    private ResponseStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
