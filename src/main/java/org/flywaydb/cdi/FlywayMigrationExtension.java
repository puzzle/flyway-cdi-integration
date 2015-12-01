package org.flywaydb.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.*;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This CDI Extension triggers the FlywayMigrationService during Context Startup
 */
public class FlywayMigrationExtension implements Extension {

    private Bean<FlywayMigrationService> instance;

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        AnnotatedType<FlywayMigrationService> at = bm.createAnnotatedType(FlywayMigrationService.class);
        final InjectionTarget<FlywayMigrationService> it = bm.createInjectionTarget(at);
        instance = new Bean<FlywayMigrationService>() {

            @Override
            public Set<Type> getTypes() {
                Set<Type> types = new HashSet<Type>();
                types.add(FlywayMigrationService.class);
                types.add(Object.class);
                return types;
            }

            @Override
            public Set<Annotation> getQualifiers() {
                Set<Annotation> qualifiers = new HashSet<Annotation>();
                qualifiers.add( new AnnotationLiteral<Default>() {} );
                qualifiers.add( new AnnotationLiteral<Any>() {} );
                return qualifiers;
            }

            @Override
            public Class<? extends Annotation> getScope() {
                return ApplicationScoped.class;
            }

            @Override
            public String getName() {
                return "flywayMigrationService";
            }

            @Override
            public Set<Class<? extends Annotation>> getStereotypes() {
                return Collections.emptySet();
            }

            @Override
            public Class<?> getBeanClass() {
                return FlywayMigrationService.class;
            }

            @Override
            public boolean isAlternative() {
                return false;
            }

            @Override
            public boolean isNullable() {
                return false;
            }

            @Override
            public Set<InjectionPoint> getInjectionPoints() {
                return it.getInjectionPoints();
            }

            @Override
            public FlywayMigrationService create(CreationalContext<FlywayMigrationService> ctx) {
                FlywayMigrationService instance = it.produce(ctx);
                it.inject(instance, ctx);
                it.postConstruct(instance);
                return instance;
            }

            @Override
            public void destroy(FlywayMigrationService instance, CreationalContext<FlywayMigrationService> ctx) {
                it.preDestroy(instance);
                it.dispose(instance);
                ctx.release();
            }
        };
        abd.addBean(instance);
    }

    void afterDeploymentValidation(@Observes AfterDeploymentValidation event, BeanManager manager) {
        try {
            manager.getReference(instance, instance.getBeanClass(), manager.createCreationalContext(instance)).toString();
        } catch (Exception ex) {
            event.addDeploymentProblem(ex);
        }
    }

}
