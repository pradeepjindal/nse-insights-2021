package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.pra.nse.db.dto.CmTdrDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@PropertySource(value = "classpath:general-queries.yaml", factory = YamlPropertyLoaderFactory.class)
public class GeneralDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${cmTradeDateRanking}")
    private String cmTradeDateRanking;


    GeneralDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CmTdrDto> getCmTradeDateDesc() {
        List<CmTdrDto> result = jdbcTemplate.query(cmTradeDateRanking, new BeanPropertyRowMapper<CmTdrDto>(CmTdrDto.class));
        return result == null ? Collections.emptyList() : result;
    }


}
