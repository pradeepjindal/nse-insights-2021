package org.pra.nse.service;

import org.pra.nse.db.dto.DeliverySpikeDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface DataServiceI {
    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, LocalDate minDate);
    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, LocalDate minDate, String forSymbol);

    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, int forMinusDays);
    public Map<String, List<DeliverySpikeDto>> getRawDataBySymbol(LocalDate forDate, int forMinusDays, String forSymbol) ;

    public Map<String, List<DeliverySpikeDto>> getRichDataBySymbol(LocalDate forDate, int forMinusDays);
    public Map<String, List<DeliverySpikeDto>> getRichDataBySymbol(LocalDate forDate, int forMinusDays, String forSymbol);

    public Map<LocalDate, Map<String, DeliverySpikeDto>> getRichDataByTradeDateAndSymbol(LocalDate forDate, int forMinusDays);
    public Map<LocalDate, Map<String, DeliverySpikeDto>> getRichDataByTradeDateAndSymbol(LocalDate forDate, int forMinusDays, String forSymbol);

}
