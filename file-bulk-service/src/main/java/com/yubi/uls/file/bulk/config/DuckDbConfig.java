package com.yubi.uls.file.bulk.config;

import org.duckdb.DuckDBConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DuckDbConfig {
    @Value("${duckdb.url}")
    private String url;
    @Bean
    private Connection getConnection() throws SQLException {
        return (DuckDBConnection)DriverManager.getConnection(url);
        }
    }
