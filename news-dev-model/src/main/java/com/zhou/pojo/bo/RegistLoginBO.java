package com.zhou.pojo.bo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 验证 BO 信息 [注册登录]
 * @Author: yuhang
 * @Date: 2023/3/15 17:42
 * @Version: 1.0
 **/
public class RegistLoginBO {

    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "短信验证码不能为空")
    private String SmsCode;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return SmsCode;
    }

    public void setSmsCode(String smsCode) {
        SmsCode = smsCode;
    }

    @Override
    public String toString() {
        return "RegistLoginBO{" +
                "mobile='" + mobile + '\'' +
                ", SmsCode='" + SmsCode + '\'' +
                '}';
    }
}
