package com.amankrmj.capyverse.java.services;

import com.amankrmj.capyverse.common.model.VersionInfo;
import com.amankrmj.capyverse.java.model.OracleJavaVersionInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.List;

/**
 * Fetches a raw GitHub JSON (array) and maps it into JavaVersionInfo models.
 * JSON is expected to be a list of objects with fields:
 * version (String), description (String), distribution (String), url (String)
 */
public class JavaVersionAvailableFetchService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public JavaVersionAvailableFetchService() {
        this(HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .build(),
                new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    }

    public JavaVersionAvailableFetchService(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Fetch and parse the JSON array from a raw GitHub URL.
     * <p>
     * Example raw URL:
     * https://raw.githubusercontent.com/<owner>/<repo>/<branch>/path/to/file.json
     *
     * @param rawGithubJsonUrl URL to the raw JSON file
     * @return list of parsed OracleJavaVersionInfo entries
     * @throws IOException if network or parsing fails
     */
    public List<OracleJavaVersionInfo> fetchOracleJavaVersions(String rawGithubJsonUrl) throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(rawGithubJsonUrl))
                .header("Accept", "application/json")
                .timeout(Duration.ofSeconds(20))
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (HttpTimeoutException e) {
            throw new IOException("Request timed out fetching: " + rawGithubJsonUrl, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Request interrupted fetching: " + rawGithubJsonUrl, e);
        }

        if (response.statusCode() != 200) {
            throw new IOException("Unexpected status " + response.statusCode() + " for: " + rawGithubJsonUrl);
        }

        return objectMapper.readValue(
                response.body(),
                new TypeReference<List<OracleJavaVersionInfo>>() {
                }
        );
    }

    /**
     * Convenience method if you want the interface type for downstream use.
     */
    public List<? extends VersionInfo> fetchAsModel(String rawGithubJsonUrl) throws IOException {
        return fetchOracleJavaVersions(rawGithubJsonUrl);
    }
}
