package com.ticket.dao;

import com.ticket.entity.StationNameDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TicketStationDAO extends BaseDao {
        @Autowired
        private JdbcTemplate jdbcTemplate;

        public void insertAllStations(String sql,final List<StationNameDTO> stationNames){
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter()
            {
                public void setValues(PreparedStatement ps, int i)throws SQLException
                {
                    String stationName=stationNames.get(i).getStationName();
                    String stationCode=stationNames.get(i).getStationCode();
                    String stationSpell=stationNames.get(i).getStationSpell();
                    ps.setString(1, stationCode);
                    ps.setString(2, stationName);
                    ps.setString(3, stationSpell);
                }
                public int getBatchSize()
                {
                    return stationNames.size();
                }
            });
        }

        public List<StationNameDTO> queryAllStations(String sql){
            return jdbcTemplate.query(sql,new StationNameDTO());
        }
}
