package io.github.sabuwalamustafa.models;

import lombok.Getter;
import lombok.Setter;

public class DatabaseConfig {
    @Getter @Setter private String jdbcUrl;
    @Getter @Setter private String username;
    @Getter @Setter private String password;
}
