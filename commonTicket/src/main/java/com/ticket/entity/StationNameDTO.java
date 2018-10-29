package com.ticket.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StationNameDTO implements RowMapper<StationNameDTO> {

    private String stationCode;
    private String stationName;
    private String stationSpell;

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationSpell() {
        return stationSpell;
    }

    public void setStationSpell(String stationSpell) {
        this.stationSpell = stationSpell;
    }


    public StationNameDTO mapRow(ResultSet rs, int num) throws SQLException {
        //从结果集里把数据得到
        String stationCode = rs.getString("station_code");
        String stationvalue = rs.getString("station_value");
        String stationSpell = rs.getString("station_spell");
        //把数据封装到对象里
        StationNameDTO stationNameDTO = new StationNameDTO();
        stationNameDTO.setStationSpell(stationSpell);
        stationNameDTO.setStationCode(stationCode);
        stationNameDTO.setStationName(stationvalue);
        return stationNameDTO;
    }


}
