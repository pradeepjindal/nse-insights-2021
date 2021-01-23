package org.pra.nse.service;

import org.pra.nse.db.dao.GeneralDao;
import org.pra.nse.db.dto.CmTdrDto;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DateService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DateService.class);

    private final GeneralDao generalDao;
    private final PraFileUtils praFileUtils;

    private LocalDate latestDbDate;
    private List<LocalDate> dbDatesListDesc;
    private Map<LocalDate, Long> dbDatesMap;

    public DateService(GeneralDao generalDao, PraFileUtils praFileUtils) {
        this.generalDao = generalDao;
        this.praFileUtils = praFileUtils;
    }


    public LocalDate getLatestDbDate() {
        if (latestDbDate == null) prepareData();
        if (latestDbDate.isBefore(praFileUtils.getLatestNseDateCDF())) prepareData();
        return latestDbDate;
    }

    public boolean validateTradeDate(LocalDate forDate) {
        if (latestDbDate == null) prepareData();
        if (latestDbDate.isBefore(praFileUtils.getLatestNseDateCDF())) prepareData();
        return dbDatesMap.containsKey(forDate);
    }

    public List<LocalDate> getTradeDatesListDesc(LocalDate forDate, int forMinusDays) {
        if (latestDbDate == null) prepareData();
        if (latestDbDate.isBefore(praFileUtils.getLatestNseDateCDF())) prepareData();

        int fromIndex = dbDatesListDesc.indexOf(forDate);
        int toIndexDesc = fromIndex + forMinusDays -1;
        if (toIndexDesc >= dbDatesListDesc.size()) return Collections.emptyList();

        List<LocalDate> localList = new ArrayList<>();
        for(int i=fromIndex; i<=toIndexDesc; i++) {
            localList.add(dbDatesListDesc.get(i));
        }
        return localList;
    }

    public LocalDate getMinTradeDate(LocalDate forDate, int forMinusDays) {
        if (latestDbDate == null) prepareData();
        if (latestDbDate.isBefore(praFileUtils.getLatestNseDateCDF())) prepareData();

        int fromIndex = dbDatesListDesc.indexOf(forDate);
        int toIndexDesc = fromIndex + forMinusDays -1;
        if (toIndexDesc >= dbDatesListDesc.size()) return null;

        LocalDate minDate = dbDatesListDesc.get(toIndexDesc);
        LOGGER.info("forDate:{}, minusDays:{}, minDate:{}, maxDate:{}", forDate, forMinusDays, minDate, forDate);
        return minDate;
    }


    private void prepareData() {
        List<CmTdrDto> dbResults = generalDao.getCmTradeDateDesc();
        dbDatesListDesc = dbResults.stream().map( dto->dto.getTradeDate() ).collect(Collectors.toList());
        latestDbDate = dbDatesListDesc.get(0);
        dbDatesMap = dbResults.stream().collect(
                Collectors.toMap(
                    cmTdrDto -> cmTdrDto.getTradeDate(), cmTdrDto -> cmTdrDto.getRank()
                )
        );
    }

}
