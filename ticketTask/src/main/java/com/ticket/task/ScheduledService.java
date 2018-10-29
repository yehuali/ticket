package com.ticket.task;


import com.ticket.service.QueryTicketService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 *  fixedRate：定义一个按一定频率执行的定时任务
 * fixedDelay：定义一个按一定频率执行的定时任务，与上面不同的是，改属性可以配合initialDelay， 定义该任务延迟执行时间。
 * cron：通过表达式来配置任务执行时间
 */
@Slf4j
@Component
public class ScheduledService {
    private  final Logger log = LoggerFactory.getLogger(ScheduledService.class);

    @Autowired
    private QueryTicketService queryTicketService;

    @Scheduled(cron = "0 45 0 ? * *")
    public void scheduled(){
        log.info("开始爬取12306站点码值信息,开始时间为：" + System.currentTimeMillis());
        queryTicketService.queryStationName();
        log.info("开始爬取12306站点码值信息,结束时间为：" + System.currentTimeMillis());
    }
}
