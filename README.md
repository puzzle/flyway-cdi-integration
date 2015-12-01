# Flyway CDI Integration

This Maven Project integrates [Flyway](http://flywaydb.org/) Database Management Tool as CDI Extension into a Java EE 7 project and triggers flyway DB migration during the startup of the Application.

# Usage

**Work in Progress**

## add as Maven dependency
```
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-cdi-integration</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

# Configuration

The Flyway Migration Services expects a configured Datasource as JNDI Service usually provided by the Container or the Application Server.

The Name of the Datasource can therefore be configured via the Java System Property

```
flywaydb.cdi.integration.datasourcename
```

Defaultvalue of the property is: defaultDS
