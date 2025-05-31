package com.atguigu.daijia.customer.service;

import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;

public interface CustomerService {

    // 微信登录
    String login(String code);

    // 获取用户信息
    CustomerLoginVo getCustomerLoginInfo(String token);

    // 获取用户信息
    CustomerLoginVo getCustomerInfo(Long userId);

    // 更新微信手机号
    boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm);
}
