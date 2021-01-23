package org.pra.nse.csv.download;

import org.pra.nse.ApCo;
import org.pra.nse.NseCons;
import org.pra.nse.util.DateUtils;
import org.pra.nse.util.NseFileUtils;
import org.pra.nse.util.PraFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class EqDownloader {
    private static final Logger LOGGER = LoggerFactory.getLogger(EqDownloader.class);

    private final String Data_Dir = ApCo.ROOT_DIR + File.separator + NseCons.EQ_DIR_NAME;
    private final String File_Prefix = NseCons.NSE_CM_FILE_PREFIX;
    private final String File_Suffix = NseCons.NSE_CM_FILE_SUFFIX;
    private final String File_Ext = NseCons.NSE_CM_FILE_EXT;
    private final String File_Date_Regex = NseCons.NSE_CM_FILE_NAME_DATE_REGEX;
    private final String File_Date_Format = NseCons.NSE_CM_FILE_NAME_DATE_FORMAT;
    private final DateTimeFormatter File_Date_Dtf = NseCons.CM_FILE_NAME_DTF;
    private final String Data_Date_Regex = null;
    private final String Data_Date_Format = null;
    private final DateTimeFormatter Data_Date_Dtf = null;

    private final DownloadHelper downloadHelper;

    private final NseFileUtils nseFileUtils;
    private final PraFileUtils praFileUtils;


    public EqDownloader(NseFileUtils nseFileUtils, PraFileUtils praFileUtils, DownloadHelper downloadHelper) {
        this.nseFileUtils = nseFileUtils;
        this.praFileUtils = praFileUtils;
        this.downloadHelper = downloadHelper;
    }


    public void downloadBySymbol(String symbol) {
        List<String> filesDownloadUrls = prepareFileUrls(symbol);
        looper(filesDownloadUrls, symbol);
    }

    private List<String> prepareFileUrls(String symbol) {
        String original_url = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=sbin&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2015&toDate=31-12-2015&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2001 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2001&toDate=31-12-2001&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2002 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2002&toDate=31-12-2002&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2003 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2003&toDate=31-12-2003&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2004 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2004&toDate=31-12-2004&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2005 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2005&toDate=31-12-2005&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2006 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2006&toDate=31-12-2006&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2007 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2007&toDate=31-12-2007&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2008 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2008&toDate=31-12-2008&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2009 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2009&toDate=31-12-2009&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2010 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2010&toDate=31-12-2010&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2011 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2011&toDate=31-12-2011&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2012 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2012&toDate=31-12-2012&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2013 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2013&toDate=31-12-2013&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2014 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2014&toDate=31-12-2014&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2015 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2015&toDate=31-12-2015&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2016 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2016&toDate=31-12-2016&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2017 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2017&toDate=31-12-2017&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2018 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2018&toDate=31-12-2018&dataType=PRICEVOLUMEDELIVERABLE";
        String url_2019 = "https://www1.nseindia.com/products/dynaContent/common/productsSymbolMapping.jsp?symbol=SYMBOL_NAME_PARAM&segmentLink=3&symbolCount=1&series=EQ&dateRange=+&fromDate=01-01-2019&toDate=31-12-2019&dataType=PRICEVOLUMEDELIVERABLE";

        List<String> urls = new ArrayList<>();
        urls.add(url_2001);
        urls.add(url_2002);
        urls.add(url_2003);
        urls.add(url_2004);
        urls.add(url_2005);
        urls.add(url_2006);
        urls.add(url_2007);
        urls.add(url_2008);
        urls.add(url_2009);
        urls.add(url_2010);
        urls.add(url_2011);
        urls.add(url_2012);
        urls.add(url_2013);
        urls.add(url_2014);
        urls.add(url_2015);
        urls.add(url_2016);
        urls.add(url_2017);
        urls.add(url_2018);
        urls.add(url_2019);

        String tmp;
        for(int i=0; i<urls.size(); i++) {
            tmp = urls.get(i).replace("SYMBOL_NAME_PARAM", symbol.toLowerCase());
            urls.set(i, tmp);
            LOGGER.info("{}", urls.get(i));
        }
        LOGGER.info("===");
        return urls;
    }

    private void looper(List<String> urlListToBeDownloaded, String symbol) {
        urlListToBeDownloaded.stream().forEach( filteredFileUrl -> {
            LOGGER.info("EQ | download - {}", filteredFileUrl);
            downloadFromUrl(filteredFileUrl, symbol);
        });
    }

    private void downloadFromUrl(String fileUrl, String symbol) {
        downloadHelper.downloadFile(fileUrl, Data_Dir,
                () -> (Data_Dir + File.separator + "eq-" + symbol + "-" + fileUrl.substring(fileUrl.indexOf("fromDate")+15, 161)),
                downloadedFilePathAndName -> {
                    LOGGER.info("PASSING: transformation of - {}", downloadedFilePathAndName);
                });
    }

}
