package io.github.sabuwalamustafa.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class MyHttpResponse {
    @Getter boolean isSuccessful;
    @Getter String response;

    @Getter @Setter List<String> cookies;

    private MyHttpResponse() {
    }

    public static MyHttpResponse getInstance() {
        return new MyHttpResponse();
    }

    public MyHttpResponse setIsSuccessful(boolean isSuccessful) {
        this.isSuccessful = isSuccessful;
        return this;
    }

    public MyHttpResponse setResponse(String response) {
        this.response = response;
        return this;
    }
}
