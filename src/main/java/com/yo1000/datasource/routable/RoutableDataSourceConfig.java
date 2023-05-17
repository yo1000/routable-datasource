package com.yo1000.datasource.routable;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class RoutableDataSourceConfig {
    @Primary
    @Bean
    public ReplicatedDataSourceRouter replicatedDataSourceRouter(DataSourceProperties props) {
        return new ReplicatedDataSourceRouter(props);
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(ReplicatedDataSourceRouter dataSourceRouter) {
        return new DataSourceTransactionManager(
                new LazyConnectionDataSourceProxy(dataSourceRouter)
        );
    }
}
