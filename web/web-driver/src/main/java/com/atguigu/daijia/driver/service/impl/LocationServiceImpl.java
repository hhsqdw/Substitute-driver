package com.atguigu.daijia.driver.service.impl;

import com.atguigu.daijia.common.execption.GuiguException;
import com.atguigu.daijia.common.result.Result;
import com.atguigu.daijia.common.result.ResultCodeEnum;
import com.atguigu.daijia.driver.client.DriverInfoFeignClient;
import com.atguigu.daijia.driver.service.LocationService;
import com.atguigu.daijia.map.client.LocationFeignClient;
import com.atguigu.daijia.model.entity.driver.DriverSet;
import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressWarnings({"unchecked", "rawtypes"})
public class LocationServiceImpl implements LocationService {

    @Autowired
    private LocationFeignClient locationFeignClient;

    @Autowired
    private DriverInfoFeignClient driverInfoFeignClient;

    // 更新司机消息
    @Override
    public Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm) {
        Long driverId = updateDriverLocationForm.getDriverId();
        Result<DriverSet> driverSet = driverInfoFeignClient.getDriverSet(driverId);
        DriverSet data = driverSet.getData();

        if(data.getServiceStatus() == 1){
            Result<Boolean> booleanResult = locationFeignClient.updateDriverLocation(updateDriverLocationForm);
            return booleanResult.getData();
        }else {
            throw new GuiguException(ResultCodeEnum.NO_START_SERVICE);
        }
    }

    // 司机赶往代驾起始点：更新订单位置到Redis缓存
    @Override
    public Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm) {
        return locationFeignClient.updateOrderLocationToCache(updateOrderLocationForm).getData();
    }
}
