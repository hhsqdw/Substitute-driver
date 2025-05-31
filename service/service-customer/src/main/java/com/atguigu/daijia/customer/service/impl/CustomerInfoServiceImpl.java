package com.atguigu.daijia.customer.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.atguigu.daijia.customer.config.WxConfigProperties;
import com.atguigu.daijia.customer.mapper.CustomerInfoMapper;
import com.atguigu.daijia.customer.mapper.CustomerLoginLogMapper;
import com.atguigu.daijia.customer.service.CustomerInfoService;
import com.atguigu.daijia.model.entity.customer.CustomerInfo;
import com.atguigu.daijia.model.entity.customer.CustomerLoginLog;
import com.atguigu.daijia.model.form.customer.UpdateWxPhoneForm;
import com.atguigu.daijia.model.vo.customer.CustomerLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class CustomerInfoServiceImpl extends ServiceImpl<CustomerInfoMapper, CustomerInfo> implements CustomerInfoService {

    @Autowired
    private WxMaService wxMaService;

    @Autowired
    private CustomerInfoMapper customerInfoMapper;

    @Autowired
    private CustomerLoginLogMapper customerLoginLogMapper;

    //微信小程序登录
    @Override
    public Long login(String code) {
        String openid = null;
        // 获取code值，通过微信工具包对象，获取唯一标识openId
        try {
            WxMaJscode2SessionResult sessionInfo = wxMaService.getUserService().getSessionInfo(code);
             openid = sessionInfo.getOpenid();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

        // 用openId查询数据库是否存在
        // 如果openId不存在返回null，如果存在返回记录
        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper();
        wrapper.eq(CustomerInfo::getWxOpenId, openid);
        CustomerInfo customerInfo = customerInfoMapper.selectOne(wrapper);

        // 如果不存在，也就是第一次登陆，添加信息到用户表
        if (customerInfo == null) {
            customerInfo = new CustomerInfo();
            //用当前时间戳作为昵称
            customerInfo.setNickname(String.valueOf(System.currentTimeMillis()));
            //设置默认头像
            customerInfo.setAvatarUrl("https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
            //把openId存入用户表
            customerInfo.setWxOpenId(openid);
            customerInfoMapper.insert(customerInfo);
        }

        // 记录登录日志
        CustomerLoginLog loginLog = new CustomerLoginLog();
        loginLog.setCustomerId(customerInfo.getId());
        loginLog.setMsg("小程序登录");
        customerLoginLogMapper.insert(loginLog);

        // 最后返回用户id
        return customerInfo.getId();
    }

    // 获取客户端登录信息
    @Override
    public CustomerLoginVo getCustomerInfoService(Long customerId) {

        // 根据用户id查询用户信息
//        LambdaQueryWrapper<CustomerInfo> wrapper = new LambdaQueryWrapper();
//        wrapper.eq(CustomerInfo::getId, customerId);
//        CustomerInfo customerInfo = customerInfoMapper.selectOne(wrapper);
        CustomerInfo info = customerInfoMapper.selectById(customerId);
        if (info == null) {
            throw new RuntimeException("用户不存在");
        }

        // 封装到customerLoginVo对象
        CustomerLoginVo customerLoginVo = new CustomerLoginVo();

        // springboot的工具类，可以把第一个对象的属性复制到第二个对象的同名属性中
        BeanUtils.copyProperties(info, customerLoginVo);

        /*
        * 还有一个属性需要手动拷贝，判断是否存在手机号
        * StringUtils.hasText(info.getPhone())判断字符串是否有值
        */
        customerLoginVo.setIsBindPhone(StringUtils.hasText(info.getPhone()));

        // 返回对象
        return customerLoginVo;
    }

    // 更新客户微信手机号码
    @Override
    public Boolean updateWxPhoneNumber(UpdateWxPhoneForm updateWxPhoneForm) {

        // 根据code查询用户信息
        try {
            WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(updateWxPhoneForm.getCode());

            // 更新用户信息
            Long customerId = updateWxPhoneForm.getCustomerId();
            CustomerInfo customerInfo = customerInfoMapper.selectById(customerId);
            customerInfo.setPhone(phoneNoInfo.getPhoneNumber());
            customerInfoMapper.updateById(customerInfo);

            return true;
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
