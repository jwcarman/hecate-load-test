package com.savoirtech.hecate.load.web.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.savoirtech.hecate.pojo.dao.PojoDaoFactory;
import com.savoirtech.hecate.pojo.dao.def.DefaultPojoDaoFactory;
import com.savoirtech.hecate.pojo.mapping.verify.CreateSchemaVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HecateConfig {

    private static final String KEYSPACE_NAME = "hecate_load";
    @Bean
    public PojoDaoFactory createPojoDaoFactory() {
        Cluster cluster = Cluster.builder().addContactPoint("localhost").withPort(9142).build();
        Session session = cluster.newSession();
        session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};", KEYSPACE_NAME));
        session.close();

        session = cluster.connect(KEYSPACE_NAME);
        return new DefaultPojoDaoFactory(session, new CreateSchemaVerifier(session));
    }
}
