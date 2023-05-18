package com.yo1000.datasource.routable;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableConfigurationProperties(RoutableDataSourceConfig.DataSourceRoutingProperties.class)
public class RoutableDataSourceConfig {
    @Primary
    @Bean
    public ReplicatedDataSourceRouter replicatedDataSourceRouter(
            DataSourceProperties dataSourceProps,
            DataSourceRoutingProperties routingProps
    ) {
        return new ReplicatedDataSourceRouter(dataSourceProps, routingProps);
    }

    @Primary
    @Bean
    public PlatformTransactionManager transactionManager(ReplicatedDataSourceRouter dataSourceRouter) {
        return new DataSourceTransactionManager(
                new LazyConnectionDataSourceProxy(dataSourceRouter)
        );
    }

    @ConfigurationProperties(prefix = "app.datasource.routing")
    public static class DataSourceRoutingProperties {
        private PatternProperties pattern = new PatternProperties();

        public PatternProperties getPattern() {
            return pattern;
        }

        public void setPattern(PatternProperties pattern) {
            this.pattern = pattern;
        }

        public static class PatternProperties {
            private String schema = "^jdbc:[^:]+:/*";
            private String databaseOptions = "[/?].*$";

            public String getSchema() {
                return schema;
            }

            public void setSchema(String schema) {
                this.schema = schema;
            }

            public String getDatabaseOptions() {
                return databaseOptions;
            }

            public void setDatabaseOptions(String databaseOptions) {
                this.databaseOptions = databaseOptions;
            }
        }
    }
}
