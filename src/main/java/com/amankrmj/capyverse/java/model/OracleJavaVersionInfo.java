package com.amankrmj.capyverse.java.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Concrete model mapped from the JSON coming from GitHub.
 * Matches fields: version, description, distribution, url.
 */
public class OracleJavaVersionInfo extends AbstractJavaVersionInfo {

    @JsonCreator
    public OracleJavaVersionInfo(@JsonProperty("version") String version,
                                 @JsonProperty("description") String description,
                                 @JsonProperty("distribution") String distribution,
                                 @JsonProperty("url") String url) {
        super(version, description, distribution, url);
    }
}
