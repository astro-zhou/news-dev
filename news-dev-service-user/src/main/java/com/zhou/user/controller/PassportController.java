package com.zhou.user.controller;

import com.zhou.api.controller.user.HelloControllerApi;
import com.zhou.api.controller.user.PassportControllerApi;
import com.zhou.grace.result.GraceJSONResult;
import com.zhou.utils.SMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author zyh
 * @create 2023-02-03 15:43
 */
@RestController
public class PassportController implements PassportControllerApi {

    final static Logger logger = LoggerFactory.getLogger(PassportController.class);

    @Autowired
    private SMSUtils smsUtils;

    @Override
    public GraceJSONResult getSMSCode() {

        String random = "123789";
        smsUtils.sendSMS("15838786860", random);

        return GraceJSONResult.ok();
    }
}
