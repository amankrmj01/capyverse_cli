package com.amankrmj.capyverse.common.services;

import java.io.IOException;

public interface DownloadService {
    void download() throws IOException, InterruptedException;

    void unzipFile() throws IOException;
}
