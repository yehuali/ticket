package com.ticket.controller;

import com.alibaba.fastjson.JSON;
import com.ticket.entity.StationNameDTO;
import com.ticket.service.QueryTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class QueryTicketController {

    @Autowired
    private QueryTicketService queryTicketService;

    @RequestMapping(value = "api/query/queryStationName")
    public String queryStationName() {
        List<StationNameDTO> stationNameList =  queryTicketService.queryStationName();
        return JSON.toJSONString(stationNameList);
    }
}
