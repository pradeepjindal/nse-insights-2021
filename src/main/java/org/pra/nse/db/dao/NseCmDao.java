package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@PropertySource(value = "classpath:upload-queries.yaml", factory = YamlPropertyLoaderFactory.class)
public class NseCmDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${cmDataCountForDateSql}")
    private String rowsCountForTradeDateSql;

    @Value("${cmDeleteForDateSql}")
    private String rowsDeleteForTradeDateSql;

    NseCmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public int dataCount(LocalDate tradeDate) {
        return jdbcTemplate.queryForObject(
                rowsCountForTradeDateSql,
                Integer.class,
                tradeDate.toString()
                );
    }

    public int dataDelete(LocalDate tradeDate) {
        return jdbcTemplate.update(
                rowsDeleteForTradeDateSql,
                Integer.class,
                tradeDate.toString()
        );
    }

}
