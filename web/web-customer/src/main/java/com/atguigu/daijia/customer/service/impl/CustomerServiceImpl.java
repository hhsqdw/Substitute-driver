package com.atguigu.daijia.customer.service.impl;

import com.atguigu.daijia.common.constant.RedisConstant;
import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.customer.client.CustomerInfoFeignClient;
import com.atguigu.daijia.customer.service.CustomerService;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerServiceImpl implements CustomerService {

    // 注入远程调用
    @Autowired
    private CustomerInfoFeignClient client;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String login(String code) {

        // 拿着code进行远程调用，返回用户id
        Result<Long> longin = client.longin(code);

        // 如果返回失败了。返回错误信息
        Integer code1 = longin.getCode();
        if(code1 != 200){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        // 获取远程调用的用户id
        Long customerId = longin.getData();

        // 判断返回用户id是否为空，为空则返回错误提示
        if(customerId == null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        // 把用户id放到Redis中，设置过期时间
        String token = UUID.randomUUID().toString();

        // 返回token
        redisTemplate.opsForValue().set(RedisConstant.USER_LOGIN_KEY_PREFIX+token,
                customerId.toString(),
                RedisConstant.USER_LOGIN_KEY_TIMEOUT,
                TimeUnit.SECONDS);
        return token;
    }

    @Override
    public CustomerLoginVo getCustomerLoginInfo(String token) {
        /*
         * 从请求头中获取token字符串
         * 根据token查询redis
         */
        String customerId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX+token);

        // 根据用户id进行远程调用，获取用户信息
        if(!StringUtils.hasText(customerId)){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }

        Result<CustomerLoginVo> customerLoginInfo = client.getCustomerLoginInfo(Long.parseLong(customerId));
        Integer code = customerLoginInfo.getCode();
        if(code != 200){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        CustomerLoginVo data = customerLoginInfo.getData();
        if(data == null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return data;
    }

    // 获取用户信息
    @Override
    public CustomerLoginVo getCustomerInfo(Long customerId) {

        Result<CustomerLoginVo> customerLoginInfo = client.getCustomerLoginInfo(customerId);
        Integer code = customerLoginInfo.getCode();
        if(code != 200){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        CustomerLoginVo data = customerLoginInfo.getData();
        if(data == null){
            throw new GuiguException(ResultCodeEnum.DATA_ERROR);
        }
        return data;
    }

    @Override
    public boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {
        Result<Boolean> booleanResult = client.updateWxPhoneNumber(updateWxPhoneForm);
        return true;
    }

}
