package com.ticket.service;

import com.ticket.dao.TicketStationDAO;
import com.ticket.entity.StationNameDTO;
import com.ticket.util.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QueryTicketService {
    private final String stationNames12306 = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=1.9063";

    @Autowired
    private TicketStationDAO ticketStationDAO;

    public void queryStationName(){
        String stations12306 =  HttpClientUtil.get(stationNames12306);
        final List<StationNameDTO> stationNames = new ArrayList<StationNameDTO>();
        String[] stationArray =  stations12306.split("\\|");
        for(int i=0;i<stationArray.length;i=i+5){
            if(i+3>= stationArray.length){
                break;
            }
            StationNameDTO stationName  = new StationNameDTO();
            stationName.setStationCode(stationArray[i+2]);
            stationName.setStationName(stationArray[i+1]);
            stationName.setStationSpell(stationArray[i+3]);
            stationNames.add(stationName);
        }
        String sql="insert into station_code(station_code,station_value,station_spell) values(?,?,?)";
        ticketStationDAO.insertAllStations(sql,stationNames);
    }
}
