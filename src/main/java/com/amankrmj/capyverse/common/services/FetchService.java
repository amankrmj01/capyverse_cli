package com.amankrmj.capyverse.common.services;

public interface FetchService {

    void fetchAvailableVersionsList();

    String getDownloadUrl(String version);

    void populateInstalledVersions();

    void listLocalVersions(String path);
}
