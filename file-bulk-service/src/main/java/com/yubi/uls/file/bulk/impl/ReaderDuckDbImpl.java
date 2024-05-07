package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Reader;
import com.yubi.uls.bulk.core.dto.ChunkConfig;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.file.bulk.dto.ReaderItem;
import io.temporal.activity.Activity;
import lombok.extern.slf4j.Slf4j;
import org.duckdb.DuckDBConnection;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ReaderDuckDbImpl implements Reader<ReaderItem> {

    private Connection connection;

    private static final String CHUNK_QUERY = "SELECT to_json(record) as record_json FROM '%s' as record where record_offset>=%s limit %s ";

    @Override
    public List<ReaderItem> read(ChunkConfig chunkConfig) {
        log.info("Reading chunk in Reader: {}", chunkConfig);
        List<ReaderItem> readerItems = readData(chunkConfig);
        log.info("Reading chunk completed in Reader: {}", readerItems.get(0));
        return readerItems;
    }

    private List<ReaderItem> readData(ChunkConfig chunkConfig) {
          String filePath = (String) chunkConfig.getParameters().get(ConfigParams.PARTITION_FILE_PATH.name());
            List<ReaderItem> readerItems = new ArrayList<>();
            try {

                getChunkData(chunkConfig, filePath, readerItems);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return readerItems;
        }

    private void getChunkData(ChunkConfig chunkConfig, String filePath, List<ReaderItem> readerItems) throws SQLException {
        Statement stmt = getConnection().createStatement();
        int size = chunkConfig.getSize();
        int offset = chunkConfig.getOffset();
        ResultSet rs = stmt.executeQuery(String.format(CHUNK_QUERY, filePath, offset*size, chunkConfig.getSize()));
        while (rs.next()) {
            readerItems.add(new ReaderItem(rs.getString("record_json")));
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:duckdb:");
            return connection;
        }
        return ((DuckDBConnection) connection).duplicate();
    }
}
