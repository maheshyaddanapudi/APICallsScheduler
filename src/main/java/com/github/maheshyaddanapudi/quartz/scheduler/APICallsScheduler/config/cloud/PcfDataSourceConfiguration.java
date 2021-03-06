package com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.config.cloud;

import com.github.maheshyaddanapudi.quartz.scheduler.APICallsScheduler.constants.Constants;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.relational.DataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@Profile(Constants.CF_MYSQL)
public class PcfDataSourceConfiguration {

  private final Logger logger = LoggerFactory.getLogger(PcfDataSourceConfiguration.class.getSimpleName());

  @Bean(Constants.CLOUD)
  public Cloud cloud() {
    return new CloudFactory().getCloud();
  }

  @Bean(Constants.DATASOURCE)
  @DependsOn(Constants.CLOUD)
  public DataSource dataSource() throws SQLException {

    final Properties dataSourceProperties = new Properties();

    dataSourceProperties.setProperty(Constants.CACHE_PREP_STMTS,Constants.TRUE);
    dataSourceProperties.setProperty(Constants.PREP_STMT_CACHE_SIZE,Constants._256);
    dataSourceProperties.setProperty(Constants.PREP_STMT_CACHE_SQL_LIMIT,Constants._2048);
    dataSourceProperties.setProperty(Constants.USER_SERVER_PREP_STMTS,Constants.TRUE);
    dataSourceProperties.setProperty(Constants.USE_LEGACY_DATETIME_CODE,Constants.FALSE);
    dataSourceProperties.setProperty(Constants.SERVER_TIMEZONE,Constants.UTC);
    dataSourceProperties.setProperty(Constants.CONNECTION_COLLATION,Constants.utf8mb4_unicode_ci);
    dataSourceProperties.setProperty(Constants.USE_SSL,Constants.FALSE);
    dataSourceProperties.setProperty(Constants.AUTO_RECONNECT,Constants.TRUE);

    final Map<String, Object> connectionProperties = new HashMap<String, Object>();

    connectionProperties.put(Constants.POOL_NAME, Constants.QUARTZ_HK_DS);
    connectionProperties.put(Constants.MAX_POOL_SIZE, Integer.parseInt(Constants._100));
    connectionProperties.put(Constants.MAX_LIFETIME, Duration.ofMinutes(5).toMillis());
    connectionProperties.put(Constants.CONNECTION_INIT_SQL, Constants.CONNECTION_INIT_SQL_VALUE);
    connectionProperties.put(Constants.DATASOURCE_PROPERTIES, dataSourceProperties);

    final DataSourceConfig serviceConfig = new DataSourceConfig(connectionProperties);

    DataSource cloudDataSource = cloud().getSingletonServiceConnector(DataSource.class, serviceConfig);

    final HikariDataSource hikariDataSource = cloudDataSource.unwrap((HikariDataSource.class));

    return hikariDataSource;
  }

}