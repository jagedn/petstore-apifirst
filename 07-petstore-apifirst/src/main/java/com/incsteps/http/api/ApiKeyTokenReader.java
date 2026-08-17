package com.incsteps.http.api;

import io.micronaut.security.token.reader.HttpHeaderTokenReader;
import jakarta.inject.Singleton;

@Singleton
public class ApiKeyTokenReader extends HttpHeaderTokenReader {
    private static final String X_API_TOKEN = "X-API-KEY";

    @Override
    protected String getPrefix() {
        return null;
    }

    @Override
    protected String getHeaderName() {
        return X_API_TOKEN;
    }
}