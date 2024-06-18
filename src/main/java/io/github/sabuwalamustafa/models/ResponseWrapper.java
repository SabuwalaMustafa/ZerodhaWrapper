package io.github.sabuwalamustafa.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ResponseWrapper<T> {
    private boolean isSuccessful;
    private T tResponse;
}
