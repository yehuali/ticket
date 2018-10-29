package com.ticket.service;

import com.ticket.dao.TicketStationDAO;
import com.ticket.entity.StationNameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QueryTicketService {
    private final String stationNames12306 = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9063";

    @Autowired
    private TicketStationDAO ticketStationDAO;

    public List<StationNameDTO> queryStationName(){
        String sql = "select * from station_code;";
        return ticketStationDAO.queryAllStations(sql);
    }


}
