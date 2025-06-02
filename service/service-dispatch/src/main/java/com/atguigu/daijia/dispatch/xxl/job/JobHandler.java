package com.atguigu.daijia.dispatch.xxl.job;


import com.atguigu.daijia.dispatch.mapper.XxlJobLogMapper;
import com.atguigu.daijia.dispatch.service.NewOrderService;
import com.atguigu.daijia.model.entity.dispatch.XxlJobLog;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JobHandler {

    @Autowired
    private XxlJobLogMapper xxlJobLogMapper;

    @Autowired
    private NewOrderService newOrderService;

    @XxlJob("newOrderTaskHandler")
    public void newOrderTaskHandler() {
        // 记录任务调度日志
        XxlJobLog xxlJobLog = new XxlJobLog();
        xxlJobLog.setJobId(XxlJobHelper.getJobId());
        long times = System.currentTimeMillis();

        try {
            // 执行任务：搜索附件司机
            newOrderService.executeTask(XxlJobHelper.getJobId());

            // 任务执行成功
            xxlJobLog.setStatus(1);
        }catch (Exception e){
            // 任务执行失败
            xxlJobLog.setStatus(0);
            xxlJobLog.setError(e.getMessage());
            e.printStackTrace();
        }finally {
            long l = System.currentTimeMillis() - times;
            xxlJobLog.setTimes((int)l);
            xxlJobLogMapper.insert(xxlJobLog);
        }
    }
}
