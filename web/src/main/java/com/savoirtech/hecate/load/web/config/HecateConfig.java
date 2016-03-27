package com.savoirtech.hecate.load.web.config;

import com.codahale.metrics.JmxReporter;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.savoirtech.hecate.core.metrics.HecateMetrics;
import com.savoirtech.hecate.pojo.dao.PojoDaoFactory;
import com.savoirtech.hecate.pojo.dao.def.DefaultPojoDaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HecateConfig {

    private static final String KEYSPACE_NAME = "hecate_load";
    private static final Logger LOGGER = LoggerFactory.getLogger(HecateConfig.class);

    @Bean
    public PojoDaoFactory createPojoDaoFactory() {
        LOGGER.info("Running with Hecate from location {}...", PojoDaoFactory.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm());
        Cluster cluster = Cluster.builder().addContactPoint("localhost").withPort(9142).build();
        Session session = cluster.newSession();
        session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};", KEYSPACE_NAME));
        session.close();
        JmxReporter.forRegistry(HecateMetrics.REGISTRY).inDomain("hecate").build().start();
        session = cluster.connect(KEYSPACE_NAME);
        return new DefaultPojoDaoFactory(session);
    }
}
