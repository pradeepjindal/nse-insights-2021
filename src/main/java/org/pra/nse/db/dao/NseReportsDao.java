package org.pra.nse.db.dao;

import org.pra.nse.config.YamlPropertyLoaderFactory;
import org.pra.nse.db.dto.DeliverySpikeDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@PropertySource(value = "classpath:reports-query.yaml", factory = YamlPropertyLoaderFactory.class)
public class NseReportsDao {
    private final JdbcTemplate jdbcTemplate;

    @Value("${sqlDeliverySpike}")
    private String deliverySpikeSql;
    @Value("${sqlDeliverySpikeTwo}")
    private String deliverySpikeTwoSql;
    @Value("${sqlDeliverySpikeThreeFor30DayzOrderBySymbolAndTradeDate}")
    private String deliverySpikeThree;
    @Value("${sqlPastPresentFuture}")
    private String pastPresentFutureSql;

    private final String SQL_AND_CLAUSE = " and tdy.trade_date < to_date(?, 'yyyy-MM-dd')";

    NseReportsDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<DeliverySpikeDto> getDeliverySpike() {
        List<DeliverySpikeDto> result = jdbcTemplate.query(deliverySpikeSql, new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
    public List<DeliverySpikeDto> getDeliverySpikeTwo() {
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW cm_trade_date_ranking_mv WITH DATA ");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW cfd_data_cd_left_join_f_mv WITH DATA ");
        jdbcTemplate.execute("REFRESH MATERIALIZED VIEW cfd_data_cd_left_join_f_mv2 WITH DATA ");
        List<DeliverySpikeDto> result = jdbcTemplate.query(deliverySpikeThree, new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
//    public List<DeliverySpikeDto> getDeliverySpike(LocalDate forDate) {
//        //String param = "'" + forDate.plusDays(1) + "'";
//        String param = forDate.plusDays(1).toString();
//        List<DeliverySpikeDto> result = jdbcTemplate.query(
//                deliverySpikeSql,
//                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class),
//                param);
//        return result;
//    }
    public List<DeliverySpikeDto> getDeliverySpike(LocalDate forDate) {
        String param = "'" + forDate.plusDays(1) + "'";
        String effective_and_clause = SQL_AND_CLAUSE.replace("?", param);
        String effective_sql = deliverySpikeSql + effective_and_clause;
        List<DeliverySpikeDto> result = jdbcTemplate.query(
                effective_sql,
                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
    public List<DeliverySpikeDto> getDeliverySpike(LocalDate fromDate, LocalDate toDate) {
        Object[] sql_args = new Object[] {fromDate.toString(), toDate.plusDays(1).toString()};
        List<DeliverySpikeDto> result = jdbcTemplate.query(
                deliverySpikeSql, sql_args,
                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }


    public List<DeliverySpikeDto> getPpfData() {
        List<DeliverySpikeDto> result = jdbcTemplate.query(pastPresentFutureSql, new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
//    public List<DeliverySpikeDto> getPpfData(LocalDate forDate) {
//        //String param = "'" + forDate.plusDays(1) + "'";
//        String param = forDate.plusDays(1).toString();
//        List<DeliverySpikeDto> result = jdbcTemplate.query(
//                pastPresentFutureSql,
//                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class),
//                param);
//        return result;
//    }
    public List<DeliverySpikeDto> getPpfData(LocalDate forDate) {
        String param = "'" + forDate.plusDays(1) + "'";
        String effective_and_clause = SQL_AND_CLAUSE.replace("?", param);
        String effective_sql = pastPresentFutureSql + effective_and_clause;
        List<DeliverySpikeDto> result = jdbcTemplate.query(
                effective_sql,
                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }
    public List<DeliverySpikeDto> getPpfData(LocalDate fromDate, LocalDate toDate) {
        Object[] sql_args = new Object[] {fromDate.toString(), toDate.plusDays(1).toString()};
        List<DeliverySpikeDto> result = jdbcTemplate.query(
                pastPresentFutureSql, sql_args,
                new BeanPropertyRowMapper<DeliverySpikeDto>(DeliverySpikeDto.class));
        return result;
    }

}
