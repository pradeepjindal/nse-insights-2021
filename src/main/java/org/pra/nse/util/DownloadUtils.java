package org.pra.nse.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
public class DownloadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadUtils.class);

    public void downloadFile(String fromUrl, String toDir, Supplier<String> inputFileSupplier, Consumer<String> outputFileConsumer) {
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

    public void downloadFileOld(String fromUrl, String toDir, Supplier<String> inputFileSupplier, Consumer<String> outputFileConsumer) {
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
