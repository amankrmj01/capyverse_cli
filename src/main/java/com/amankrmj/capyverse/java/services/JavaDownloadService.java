package com.amankrmj.capyverse.java.services;

import com.amankrmj.capyverse.common.services.DownloadService;

import java.io.*;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JavaDownloadService implements DownloadService {
    private final String urlStr;
    private final String filePath;
    private final String installDir;

    public JavaDownloadService(String url, String downloadDir, String installDir, String fileName) {
        this.urlStr = url;
        this.filePath = downloadDir + File.separator + fileName;
        this.installDir = installDir;
    }

    @Override
    public void download() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(java.net.URI.create(urlStr))
                .GET()
                .build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        int fileSize = 0;
        if (response.headers().firstValue("Content-Length").isPresent()) {
            try {
                fileSize = Integer.parseInt(response.headers().firstValue("Content-Length").get());
            } catch (NumberFormatException ignored) {
            }
        }
        try (InputStream in = response.body();
             FileOutputStream out = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int totalRead = 0, lastPercent = -1;
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalRead += bytesRead;
                if (fileSize > 0) {
                    int percent = (int) ((totalRead * 100L) / fileSize);
                    if (percent != lastPercent) {
                        printProgressBar(percent);
                        lastPercent = percent;
                    }
                }
            }
            printProgressBar(100);
            System.out.println();
        }
    }

    @Override
    public void unzipFile() throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outFile = new File(installDir, entry.getName());
                if (entry.isDirectory()) {
                    outFile.mkdirs();
                } else {
                    outFile.getParentFile().mkdirs();
                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        byte[] buffer = new byte[8192];
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    private void printProgressBar(int percent) {
        final int barLength = 50;
        int filled = (percent * barLength) / 100;
        System.out.print("\r[");
        String GREEN = "\u001B[32m";
        String RED = "\u001B[31m";
        String YELLOW = "\u001B[33m";
        String RESET = "\u001B[0m";
        for (int i = 0; i < barLength; i++) {
            if (i < filled) {
                System.out.print(GREEN + "=" + RESET);
            } else {
                System.out.print(RED + "-" + RESET);
            }
        }
        System.out.print("] ");
        System.out.print(YELLOW + percent + "%" + RESET);
    }
}
