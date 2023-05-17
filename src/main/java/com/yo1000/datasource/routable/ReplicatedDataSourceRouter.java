package com.yo1000.datasource.routable;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ReplicatedDataSourceRouter extends AbstractRoutingDataSource {
    private static final boolean TRANSACTION_READ_ONLY = true;
    private static final boolean TRANSACTION_READ_WRITE = false;

    public ReplicatedDataSourceRouter(DataSourceProperties props) {
        String url = props.getUrl();

        String connSchema = url
                .substring(0, url.length() - url.replaceFirst("^jdbc:[^:]+:/*", "").length());

        String connHosts = url
                .substring(connSchema.length())
                .replaceFirst("[/?].*$", "");

        String connOptions = url
                .substring(connSchema.length() + connHosts.length());

        String[] hosts = connHosts.split("\\s*,\\s*");

        DataSource writerDataSource = props.initializeDataSourceBuilder().build();

        if (hosts.length == 1) {
            setTargetDataSources(Map.ofEntries(
                    Map.entry(TRANSACTION_READ_ONLY, writerDataSource),
                    Map.entry(TRANSACTION_READ_WRITE, writerDataSource)
            ));
        } else {
            String readerHosts = Arrays.stream(hosts)
                    .skip(1)
                    .collect(Collectors.joining(","))
                    + "," + hosts[0];

            DataSource readerDataSource = props.initializeDataSourceBuilder()
                    .url(connSchema + readerHosts + connOptions).build();

            setTargetDataSources(Map.ofEntries(
                    Map.entry(TRANSACTION_READ_ONLY, readerDataSource),
                    Map.entry(TRANSACTION_READ_WRITE, writerDataSource)
            ));
        }
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly();
    }
}
