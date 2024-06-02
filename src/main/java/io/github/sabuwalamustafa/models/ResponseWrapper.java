package io.github.sabuwalamustafa.models;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class ResponseWrapper<T> {
    private boolean isSuccessful = false;
    private double numberResponse;
    private String strResponse;
    private JsonNode jsonResponse;
    private Object miscResponse;
    private T tResponse;

    private List<T> listResponse;
    private Map<T, T> mapResponse;
}
