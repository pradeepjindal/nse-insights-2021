package org.pra.nse.db.upload.calc;

import org.pra.nse.ApCo;
import org.pra.nse.calculation.CalcCons;
import org.pra.nse.calculation.MfiCalculatorNew;
import org.pra.nse.csv.data.MfiBeanNew;
import org.pra.nse.db.model.CalcMfiTabNew;
import org.pra.nse.db.repository.CalcMfiRepoNew;
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

@Component
public class CalcMfiUploaderNew extends BaseUploader {
    private static final Logger LOGGER = LoggerFactory.getLogger(CalcMfiUploaderNew.class);

    private final String calc_name = CalcCons.MFI_DATA_NAME;

    private final MfiCalculatorNew calculator;
    private final CalcMfiRepoNew repo;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;

    public CalcMfiUploaderNew(MfiCalculatorNew calculator,
                              CalcMfiRepoNew calcMfiRepoNew,
                              NseFileUtils nseFileUtils, PraFileUtils praFileUtils) {
        super(praFileUtils, CalcCons.MFI_DIR_NAME_NEW, CalcCons.MFI_FILE_PREFIX);
        this.calculator = calculator;
        this.repo = calcMfiRepoNew;
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
    }

    public void uploadForDate(LocalDate forDate) {
        //
        String fileName = CalcCons.MFI_FILE_PREFIX +forDate+ ApCo.DATA_FILE_EXT;
        String fromFile = CalcCons.MFI_FILES_PATH_NEW +File.separator+ fileName;
        LOGGER.info("{} upload | looking for file Name along with path:[{}]",calc_name, fromFile);

        if(!nseFileUtils.isFileExist(fromFile)) {
            LOGGER.warn("{} upload | file does not exist: [{}]", calc_name, fromFile);
            return;
        }

        //
        //int dataCtr = dao.dataCount(forDate);
        int dataCtr = repo.countByTradeDate(forDate);
//        List<MfiBean> beans = calculator.calculateAndReturn(forDate);
//        if (dataCtr == 0) {
//            LOGGER.info("{} upload | uploading | for date:[{}]", calc_name, forDate);
//            upload(beans);
//        } else if (dataCtr == beans.size()) {
//            LOGGER.info("{} | upload skipped, already uploaded", calc_name);
//        } else {
//            LOGGER.warn("{} | upload skipped, discrepancy in data dbRecords={}, dtoSize={}", calc_name, dataCtr, beans.size());
//        }
        if (dataCtr == 0) {
            LOGGER.info("{} upload | uploading | for date:[{}]", calc_name, forDate);
            List<MfiBeanNew> beans = calculator.calculateAndReturn(forDate);
            upload(beans);
        }
    }

    private void upload(List<MfiBeanNew> beans) {
        AtomicInteger recordSucceed = new AtomicInteger();
        AtomicInteger recordFailed = new AtomicInteger();

        CalcMfiTabNew tab = new CalcMfiTabNew();
        beans.forEach(bean -> {
            tab.reset();
            tab.setSymbol(bean.getSymbol());
            tab.setTradeDate(bean.getTradeDate());

            //tab.setTds(bean.getTradeDate().toString());
            tab.setForDays(bean.getForDays());

            tab.setVolAtpMfiSma(bean.getVolMfi());
            tab.setDelAtpMfiSma(bean.getDelMfi());

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

