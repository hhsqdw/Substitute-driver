package com.atguigu.daijia.driver.service;

import com.atguigu.daijia.model.form.map.UpdateDriverLocationForm;
import com.atguigu.daijia.model.form.map.UpdateOrderLocationForm;

public interface LocationService {


    // 更新司机消息
    Boolean updateDriverLocation(UpdateDriverLocationForm updateDriverLocationForm);

    // 司机赶往代驾起始点：更新订单位置到Redis缓存
    Boolean updateOrderLocationToCache(UpdateOrderLocationForm updateOrderLocationForm);
}
