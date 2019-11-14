# Routeplanner

Application to calculate shortest path from A to B with k stops. Takes IATA codes as identifiers for airport.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Java 11

```
- Download https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html
- Add JAVA_HOME to environment
```

### Installing

Clean project and assemble from command line

```
gradlew clean assemble
```

## Running the tests

run

```
gradlew test
```

### Unit tests

Tests json response and error handling
```
RouteControllerTest
```

Tests Haversine distance calculation
```
HaversineDistanceTest
```

### Intergration tests

Tests data downloading, data processing, graph generation and Yen's algorithm.

```
DataServiceTest, RouteServiceTest
```

## Deployment

Parameters to configure

```
application.properties
data:
    - app.data.csv.path.relationships - path where relationships csv is saved
    - app.data.csv.path.nodes - path where nodes csv is saved
    - app.neo4j.db.location - path where Neo4j DB is saved
algorithm:
    - app.algo.k.paths - number of shortest path generated with Yen's algorithm

```

run
```
gradlew bootRun
```

## Example request

```
localhost:8080/shortest-path?startCode=TLL&endCode=LIS&stops=4
```

## Example response

```
{
    "airports": ["TLL","HAM","BRE","CRL","CDG","LIS"],
    "distances": [1110.0,102.0,414.0,211.0,1469.0],
    "totalDistance":3306.0
}
```

## Built With

* [Gradle](https://gradle.org/) - Dependency Management
* [Spring Boot](https://spring.io/projects/spring-boot) - Java framework
* [Neo4j](https://neo4j.com/) - Graph platform
* [Cypher](https://neo4j.com/developer/cypher-query-language/) - Graph query language

## Authors

* **Siim Salin**
