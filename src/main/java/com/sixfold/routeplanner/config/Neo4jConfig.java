package com.sixfold.routeplanner.config;

import lombok.extern.log4j.Log4j2;
import org.neo4j.graphalgo.GetNodeFunc;
import org.neo4j.graphalgo.KShortestPathsProc;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.internal.kernel.api.exceptions.KernelException;
import org.neo4j.kernel.impl.proc.Procedures;
import org.neo4j.kernel.internal.GraphDatabaseAPI;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Paths;

@Configuration
@Log4j2
@EnableNeo4jRepositories("com.sixfold.routeplanner")
@EnableTransactionManagement
public class Neo4jConfig {

    @Bean
    public SessionFactory sessionFactory() {
        try {
            registerProcedure(graphDb(), KShortestPathsProc.class);
            registerFunction(graphDb(), GetNodeFunc.class);
        } catch (KernelException e) {
            log.error("Error registering procedures", e);
        }
        org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration.Builder().build();
        EmbeddedDriver driver = new EmbeddedDriver(graphDb(), configuration);
        return new SessionFactory(driver, "com.sixfold.routeplanner.dto");
    }

    @Bean
    public GraphDatabaseService graphDb() {
        return new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(Paths.get("C:/Temp/graph.db").toFile()).newGraphDatabase();
    }

    @Bean
    public Neo4jTransactionManager transactionManager() {
        return new Neo4jTransactionManager(sessionFactory());
    }

    private void registerProcedure(GraphDatabaseService graphDb, Class<?>... procedures) throws KernelException {
        Procedures proceduresService = ((GraphDatabaseAPI) graphDb)
                .getDependencyResolver().resolveDependency(Procedures.class);
        for (Class<?> procedure : procedures) {
            proceduresService.registerProcedure(procedure);
        }
    }

    private void registerFunction(GraphDatabaseService graphDb, Class<?>... functions) throws KernelException {
        Procedures proceduresService = ((GraphDatabaseAPI) graphDb)
                .getDependencyResolver().resolveDependency(Procedures.class);
        for (Class<?> function : functions) {
            proceduresService.registerFunction(function);
        }
    }
}