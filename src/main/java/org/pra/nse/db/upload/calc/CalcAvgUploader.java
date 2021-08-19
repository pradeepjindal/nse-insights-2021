package org.pra.nse.db.upload.calc;

import org.pra.nse.ApCo;
import org.pra.nse.calculation.AvgCalculator;
import org.pra.nse.calculation.CalcCons;
import org.pra.nse.csv.data.AvgBean;
import org.pra.nse.db.model.CalcAvgTab;
import org.pra.nse.db.repository.CalcAvgRepo;
import org.pra.nse.db.upload.BaseUploader;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.pra.nse.calculation.CalcCons.AVG_FILE_PREFIX;

@Component
public class CalcAvgUploader extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcAvgUploader.class);

    private final String calc_name = CalcCons.AVG_DATA_NAME;

    private final CalcAvgRepo repo;
    private final AvgCalculator calculator;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;

    public CalcAvgUploader(CalcAvgRepo repo,
                           AvgCalculator calculator,
                           NseFileUtils nseFileUtils,
                           PraFileUtils praFileUtils) {
        super(praFileUtils, CalcCons.AVG_DIR_NAME, AVG_FILE_PREFIX, ApCo.UPLOAD_CALC_FROM_DATE);
        this.repo = repo;
        this.calculator = calculator;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

    public void uploadForDate(LocalDate forDate) {
        //
        String fileName = CalcCons.AVG_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        String fromFile = CalcCons.AVG_FILES_PATH +File.separator+ fileName;
//        LOGGER.info("{} upload | looking for file Name along with path:[{}]",calc_name, fromFile);

        if(!nseFileUtils.isFileExist(fromFile)) {
            LOGGER.warn("{} upload | file does not exist: [{}]", calc_name, fromFile);
            return;
        }

        //
        //int dataCtr = dao.dataCount(forDate);
        int dataCtr = repo.countByTradeDate(forDate);
//        List<AvgBean> beans = calculator.calculateAndReturn(forDate);
//        if (dataCtr == 0) {
//            LOGGER.info("{} upload | uploading | for date:[{}]", calc_name, forDate);
//            upload(beans);
//        } else if (dataCtr == beans.size()) {
//            LOGGER.info("{} | upload skipped, already uploaded", calc_name);
//        } else {
//            LOGGER.warn("{} | upload skipped, discrepancy in data dbRecords={}, dtoSize={}", calc_name, dataCtr, beans.size());
//        }
        if (dataCtr == 0) {
            LOGGER.info("{} upload | uploading - for date:[{}]", calc_name, forDate);
            List<AvgBean> beans = calculator.calculateAndReturn(forDate);
            upload(beans);
        } else if(dataCtr > 0 && ApCo.RE_UPLOAD_CALC && ApCo.RE_UPLOAD_CALC_FROM_DATE.isBefore(forDate)) {
            LOGGER.info("{} upload | RE-UPLOADING - for date:[{}]", calc_name, forDate);
            repo.deleteByTradeDate(forDate);
        } else {
            LOGGER.info("{} upload | SKIPPING (already uploaded) - for date:[{}]", calc_name, forDate);
        }
    }

    private void upload(List<AvgBean> beans) {
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        CalcAvgTab tab = new CalcAvgTab();
        beans.forEach(bean -> {
            tab.reset();
            tab.setSymbol(bean.getSymbol());
            tab.setTradeDate(bean.getTradeDate());

            //tab.setTds(bean.getTradeDate().toString());
            tab.setForDays(bean.getForDays());

            tab.setAtpSma(bean.getAtpSma());
            tab.setTrdSma(bean.getTrdSma());
            tab.setDelSma(bean.getDelSma());
            tab.setFoiSma(bean.getFoiSma());

            try {
                repo.save(tab);
                recordSucceed.incrementAndGet();
            } catch(DataIntegrityViolationException dive) {
                recordFailed.incrementAndGet();
            }
        });
        LOGGER.info("{} upload | record - uploaded {}, failed: [{}]", calc_name, recordSucceed.get(), recordFailed.get());
    }

}

