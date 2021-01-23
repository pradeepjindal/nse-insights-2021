package org.pra.nse.csv.download;

import org.pra.nse.ApCo;
import org.pra.nse.util.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class DownloadHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadHelper.class);

    public boolean shouldDownload(List<String> urlListToBeDownloaded) {
        if(urlListToBeDownloaded.size() == 1 && dayOrTimeIsNotValid()) {
            LOGGER.info("skipping file | Date: {} , Time: {} | {}", LocalDate.now(), LocalTime.now(), urlListToBeDownloaded.get(0));
            return false;
        }
        return true;
    }

    public boolean dayOrTimeIsNotValid() {
        return DateUtils.isWeekend(LocalDate.now()) || LocalTime.now().isBefore(ApCo.DAILY_DOWNLOAD_TIME);
    }

    public boolean timeFilter(LocalDate forDate) {
        return forDate.isBefore(LocalDate.now()) || LocalTime.now().isAfter(ApCo.DAILY_DOWNLOAD_TIME);
    }

    void downloadFile(String fromUrl, String toDir, Supplier<String> inputFileSupplier, Consumer<String> outputFileConsumer) {
        String outputDirAndFileName = inputFileSupplier.get();
        LOGGER.info("FROM URL: " + fromUrl);
        LOGGER.info("TO   DIR: " + outputDirAndFileName);
        try (BufferedInputStream inputStream = new BufferedInputStream(new URL(fromUrl).openStream());
             FileOutputStream fileOS = new FileOutputStream(outputDirAndFileName)) {
            byte[] data = new byte[1024];
            int byteContent;
            while ((byteContent = inputStream.read(data, 0, 1024)) != -1) {
                fileOS.write(data, 0, byteContent);
            }
            //unzip(outputDirAndFileName);
            outputFileConsumer.accept(outputDirAndFileName);
        } catch (IOException e) {
            LOGGER.warn("Error while downloading file: {}", e.getMessage());
            //LOGGER.warn("Error while downloading file: {}", e);
        }
    }
}
