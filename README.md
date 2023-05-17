Routable DataSource demo
========================================

Demo routing data sources based on `readOnly` property of `@Transatcional` annotations.


How to Run
----------------------------------------

Step1. 

```shell
docker compose up
```

Step2.

```shell
./mvnw clean spring-boot:run
```

Step3.

```shell
curl 'http://localhost:8080/'
curl -XPOST 'http://localhost:8080/'
```

Monitoring the console where Docker was launched,
can confirm Slave is called when a read-only endpoint is invoked,
and Master is called when a read-write endpoint is invoked.
