package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@PropertySource(value = "classpath:upload-queries.yaml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:future-queries.yaml", factory = YamlPropertyLoaderFactory.class)
public class FmDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${fmDataCountForDateSql}")
    private String fmDataCountForDateSql;

    @Value("${activeFutureScriptsForGivenDateSql}")
    private String activeFutureScriptsForGivenDateSql;

    FmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public int dataCount(LocalDate tradeDate) {
        Object[] args = new Object[] {tradeDate.toString()};
        return jdbcTemplate.queryForObject(fmDataCountForDateSql, args, Integer.class);
    }

    public List<String> activeScripts(LocalDate tradeDate) {
        Object[] args = new Object[] {tradeDate.toString()};
        return jdbcTemplate.queryForList(activeFutureScriptsForGivenDateSql, args, String.class);
    }

}
