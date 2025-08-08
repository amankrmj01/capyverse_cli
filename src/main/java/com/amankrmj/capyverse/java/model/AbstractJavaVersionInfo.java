package com.amankrmj.capyverse.java.model;

import com.amankrmj.capyverse.common.model.VersionInfo;

import java.util.Objects;

public abstract class AbstractJavaVersionInfo implements VersionInfo {

    protected final String version;
    protected final String description;
    protected final String distribution;
    protected final String url;

    protected AbstractJavaVersionInfo(String version,
                                      String description,
                                      String distribution,
                                      String url) {
        this.version = Objects.requireNonNull(version, "version");
        this.description = Objects.requireNonNull(description, "description");
        this.distribution = Objects.requireNonNull(distribution, "distribution");
        this.url = Objects.requireNonNull(url, "url");
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDistribution() {
        return distribution;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "JavaVersionInfo{" +
                "version='" + version + '\'' +
                ", description='" + description + '\'' +
                ", distribution='" + distribution + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractJavaVersionInfo other)) return false;
        return Objects.equals(version, other.version)
                && Objects.equals(url, other.url)
                && Objects.equals(description, other.description)
                && Objects.equals(distribution, other.distribution);
    }
}
