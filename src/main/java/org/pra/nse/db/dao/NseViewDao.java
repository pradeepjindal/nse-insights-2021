package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.pra.nse.db.dto.PivotOiDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@PropertySource(value = "classpath:future-queries.yaml", factory = YamlPropertyLoaderFactory.class)
public class NseViewDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${sqlPivotOi}")
    private String sqlPivotOi;

    NseViewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PivotOiDto> getPivotOi() {
        List<PivotOiDto> result = jdbcTemplate.query(sqlPivotOi, new BeanPropertyRowMapper<PivotOiDto>(PivotOiDto.class));
        return result;
    }

}
