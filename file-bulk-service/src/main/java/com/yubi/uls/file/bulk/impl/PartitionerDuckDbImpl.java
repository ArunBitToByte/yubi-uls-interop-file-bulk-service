package com.yubi.uls.file.bulk.impl;

import com.yubi.uls.bulk.core.client.Partitioner;
import com.yubi.uls.bulk.core.dto.ConfigParams;
import com.yubi.uls.bulk.core.dto.JobConfiguration;
import com.yubi.uls.bulk.core.dto.Partition;
import com.yubi.uls.bulk.core.dto.State;
import lombok.Builder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.duckdb.DuckDBConnection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class PartitionerDuckDbImpl implements Partitioner {

    private String filePath;
    private String basePath;

    private String name;
    private int partitionSize;
    int offset = 1;

    List<String> files;

    private static final String PARTITION_QUERY =
                """
                   COPY (
                    SELECT *, CAST(FLOOR((ROW_NUMBER() OVER () - 1) / %s) AS INTEGER) AS partition_group,
                    (ROW_NUMBER() OVER () - 1) %% %s AS record_offset
                   FROM read_csv_auto('%s', HEADER=TRUE))
                   TO '%s'(FORMAT 'CSV', PARTITION_BY partition_group,  OVERWRITE_OR_IGNORE TRUE)
                    """;

    private Connection connection;

    @SneakyThrows
    @Override
    public void init(JobConfiguration jobConfiguration) {
        log.info("Initializing partitioner");
        filePath = (String) jobConfiguration.getParameters().get(ConfigParams.FILE_PATH.name());
        partitionSize = (int) jobConfiguration.getParameters().get(ConfigParams.PARTITION_SIZE.name());
        File sourceFile = new File(filePath);
        basePath = sourceFile.getParent();
        name = sourceFile.getName();
        offset = 1;

        //download file
        // partition file to db
        partitionFile();
        // upload to s3
        initFileIterator();
        log.info("Partitioner initialized");
    }

    private void initFileIterator() throws IOException {
        Path startPath = Paths.get(getPartitionPath());
        files = Files.find( startPath, Integer.MAX_VALUE, ( path, attributes ) -> path.toFile().isFile() )
                .map( p -> startPath.relativize( p ).toString() ).collect( Collectors.toList() );
        removeTempFiles();

    }

    private void removeTempFiles() {
        for( int i = 0; i < files.size(); i++) {
            if(files.get(i).contains(".DS_Store")) {
                files.remove(i);
            }
        }
    }

    private String getPartitionPath() {
       return String.format("%s/%s_partition/", basePath, name);
    }

    private void partitionFile() {
        try {
            Connection connection = getConnection();
            String sql = buildQuery();
            connection.createStatement().execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
    }
}

    private String buildQuery() {
        String partitionDir = getPartitionPath();
       return String.format(PARTITION_QUERY, partitionSize, partitionSize, filePath, partitionDir);
    }

    @SneakyThrows
    public Partition getNextPartition() {

        log.info("Creating partition: {}", offset);
        String partitionFilePath = getFilePath();
        if(partitionFilePath == null) {
            return null;
        }
        Map<String,Integer> countMap = getCount(partitionFilePath);
        log.info("Partition size and partition group: {}", countMap);
        Partition partition = Partition.builder().
                parameters(new HashMap<>()).
                status(State.PENDING).
                partitionIndex(countMap.get(ConfigParams.PARTITION_GROUP.name())).
                partitionSize(countMap.get(ConfigParams.PARTITION_SIZE.name()))
                .build();
        partition.getParameters().put(ConfigParams.PARTITION_FILE_PATH.name(), partitionFilePath);
        offset++;
        return partition;
    }

    private Map<String ,Integer> getCount(String partitionFilePath) {
        Map<String, Integer> countMap = new HashMap<>();
        try {
            Connection connection = getConnection();
            String sql = "SELECT COUNT(*), MAX(partition_group) FROM '" + partitionFilePath+"'";
            ResultSet rs = connection.createStatement().executeQuery(sql);
            rs.next();
            countMap.put(ConfigParams.PARTITION_SIZE.name(), rs.getInt(1));
            countMap.put(ConfigParams.PARTITION_GROUP.name(), rs.getInt(2));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countMap;
    }

    private String getFilePath() {
        if(offset >= files.size()) {
            return null;
        }
        return getPartitionPath()+files.get(offset - 1);
    }

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:duckdb:");
            return connection;
        }
        return ((DuckDBConnection) connection).duplicate();
    }

}
