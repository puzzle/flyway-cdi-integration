package org.flywaydb.cdi;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * The MigrationService calls the DB Migrations during startup of the Application
 */
@ApplicationScoped
public class FlywayMigrationService implements Extension {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayMigrationService.class);

    @Inject
    private Event<AfterDatabaseMigration> eventManager;

    @PostConstruct
    public void onStartup() {
        LOG.info("Setting Up Flyway ");

        Flyway flyway = new Flyway();
        try {
            flyway.setDataSource(getDataSource());
            flyway.migrate();
            eventManager.fire(new AfterDatabaseMigration());
        } catch (NamingException e) {
            LOG.error("Database migration failed");
        }
    }

    /**
     * Read the Datasource from InitialContext instead of
     *
     * @return
     *
     * Alternative:
     * @Resource(lookup = "java:jboss/datasources/defaultDS")
     * private DataSource dataSource;
     */
    private DataSource getDataSource() throws NamingException {
        DataSource ds = null;
        String datasourceName = getDatasourceSystemConfigurtaiton();
        try {
            Context initCtx = new InitialContext();
            ds = (DataSource) initCtx.lookup("java:jboss/datasources/" + datasourceName);
        } catch (NamingException e) {
            LOG.error("Flyway Datasource was not configured under java:jboss/datasources/" + datasourceName);
            throw e;
        }
        return ds;
    }

    private String getDatasourceSystemConfigurtaiton() {
        return System.getProperties().getProperty("flywaydb.cdi.integration.datasourcename", "defaultDS");
    }
}
