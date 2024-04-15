package com.yubi.uls.bulk.core.dto;

import java.util.Objects;

public class Partition {
    private String fileName;
    private int partitionIndex;

    private int partitionSize;

    public Partition() {
    }

    public Partition(String fileName) {
        this(fileName, 0, 0);
    }

    public Partition(String fileName, int partitionIndex, int limit) {
        this.fileName = fileName;
        this.partitionIndex = partitionIndex;
        this.partitionSize = limit;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPartitionIndex() {
        return partitionIndex;
    }

    public void setPartitionIndex(int partitionIndex) {
        this.partitionIndex = partitionIndex;
    }

    public int getPartitionSize() {
        return partitionSize;
    }

    public void setPartitionSize(int partitionSize) {
        this.partitionSize = partitionSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Partition partition = (Partition) o;
        return partitionIndex == partition.partitionIndex && partitionSize == partition.partitionSize && Objects.equals(fileName, partition.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, partitionIndex, partitionSize);
    }

    @Override
    public String toString() {
        return "Partition{" +
                "fileName='" + fileName + '\'' +
                ", partitionIndex=" + partitionIndex +
                ", partitionSize=" + partitionSize +
                '}';
    }
}
