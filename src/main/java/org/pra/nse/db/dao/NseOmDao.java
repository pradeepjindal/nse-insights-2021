package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.pra.nse.db.dto.LotSizeDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@PropertySource(value = "classpath:upload-queries.yaml", factory = YamlPropertyLoaderFactory.class)
@PropertySource(value = "classpath:option-queries.yaml", factory = YamlPropertyLoaderFactory.class)
public class NseOmDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${omDataCountForDateSql}")
    private String rowsCountForTradeDateSql;

    @Value("${omDeleteForDateSql}")
    private String rowsDeleteForTradeDateSql;

    @Value("${activeOptionScriptsForGivenDateSql}")
    private String activeFutureScriptsForGivenDateSql;

    @Value("${lotSizeSql}")
    private String lotSizeSql;


    NseOmDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public int dataCount(LocalDate tradeDate) {
        return jdbcTemplate.queryForObject(
                rowsCountForTradeDateSql,
                Integer.class,
                tradeDate.toString());
    }

    public List<String> activeScripts(LocalDate tradeDate) {
        Object[] args = new Object[] {tradeDate.toString()};
        return jdbcTemplate.queryForList(activeFutureScriptsForGivenDateSql, args, String.class);
    }

    public int dataDelete(LocalDate tradeDate) {
        return jdbcTemplate.update(
                rowsDeleteForTradeDateSql,
                Integer.class,
                tradeDate.toString()
        );
    }

    public List<LotSizeDto> getLotSizeList() {
        List<LotSizeDto> result = jdbcTemplate.query(lotSizeSql, new BeanPropertyRowMapper<LotSizeDto>(LotSizeDto.class));
        return result;
    }

}
