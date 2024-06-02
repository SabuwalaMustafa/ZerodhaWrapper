package io.github.sabuwalamustafa.models;

import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyHttpRequest {
    @Getter private String url;
    @Getter private List<String> headers;
    @Getter private List<String> urlQueryParams;
    @Getter private Map<String, String> body;

    private MyHttpRequest() {
    }

    public static MyHttpRequest getInstance() {
        return new MyHttpRequest();
    }

    public MyHttpRequest setUrl(String url) {
        this.url = url;
        return this;
    }

    public MyHttpRequest setHeaders(List<String> headers) {
        this.headers = headers;
        return this;
    }

    public MyHttpRequest setUrlQueryParams(List<String> urlQueryParams) {
        this.urlQueryParams = urlQueryParams;
        return this;
    }

    public MyHttpRequest setBody(Map<String, String> body) {
        this.body = body;
        return this;
    }

    public MyHttpRequest addHeaders(String key, String value) {
        if (headers == null) {
            headers = new ArrayList<>();
        }
        headers.add(key);
        headers.add(value);
        return this;
    }

    public MyHttpRequest addQueryParams(String key, String value) {
        if (urlQueryParams == null) {
            urlQueryParams = new ArrayList<>();
        }
        urlQueryParams.add(key);
        urlQueryParams.add(value);
        return this;
    }

    public MyHttpRequest addBodyData(String key, String value) {
        if (body == null) {
            body = new HashMap<>();
        }
        body.put(key, value);
        return this;
    }
}
