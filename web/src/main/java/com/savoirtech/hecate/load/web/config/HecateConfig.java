package com.savoirtech.hecate.load.web.config;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.schemabuilder.Create;
import com.savoirtech.hecate.load.web.domain.Person;
import com.savoirtech.hecate.pojo.binding.PojoBindingFactory;
import com.savoirtech.hecate.pojo.binding.def.DefaultPojoBindingFactory;
import com.savoirtech.hecate.pojo.convert.def.DefaultConverterRegistry;
import com.savoirtech.hecate.pojo.dao.PojoDaoFactory;
import com.savoirtech.hecate.pojo.dao.def.DefaultPojoDaoFactory;
import com.savoirtech.hecate.pojo.facet.field.FieldFacetProvider;
import com.savoirtech.hecate.pojo.naming.def.DefaultNamingStrategy;
import com.savoirtech.hecate.pojo.query.def.DefaultPojoQueryContextFactory;
import com.savoirtech.hecate.pojo.statement.def.DefaultPojoStatementFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HecateConfig {
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private static final String KEYSPACE_NAME = "hecate_load";
    private static final Logger LOGGER = LoggerFactory.getLogger(HecateConfig.class);

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Bean
    public PojoDaoFactory createPojoDaoFactory() {
        LOGGER.info("Running with Hecate from location {}...", PojoDaoFactory.class.getProtectionDomain().getCodeSource().getLocation().toExternalForm());
        Cluster cluster = Cluster.builder().addContactPoint("localhost").withPort(9142).build();
        Session session = cluster.newSession();
        session.execute(String.format("CREATE KEYSPACE IF NOT EXISTS %s WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};", KEYSPACE_NAME));
        session.close();
        session = cluster.connect(KEYSPACE_NAME);
        DefaultNamingStrategy namingStrategy = new DefaultNamingStrategy();
        PojoBindingFactory bindingFactory = new DefaultPojoBindingFactory(new FieldFacetProvider(), new DefaultConverterRegistry(), namingStrategy);
        DefaultPojoDaoFactory daoFactory = new DefaultPojoDaoFactory(session, bindingFactory, new DefaultPojoStatementFactory(session), new DefaultPojoQueryContextFactory(session, new DefaultPojoStatementFactory(session)), namingStrategy);
        Create create = bindingFactory.createPojoBinding(Person.class).createTable("person");
        session.execute(create);
        return daoFactory;
    }
}
