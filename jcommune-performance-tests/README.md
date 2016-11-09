Gatling performance tests
-------------------------

# Running Performance Tests

1. Run your application server used for testing.
2. Configure pom.xml
    ```
    <configuration>
        <runMultipleSimulations>true</runMultipleSimulations> (1)
        <simulationClass>org.jtalks.jcommune.performance.tests.OpenTopicPage</simulationClass> (2)
    </configuration>

    Choose one:
    (1) set <runMultipleSimulations> **true** if you want to run all simulations sequentially.
    (2) choose specific simulation to run
3. Pass your server address as argument to Maven: `-Dperformance.url=http://yourserveraddress.com` otherwise 
`http://performance.jtalks.org/jcommune` will be taken.
4. Run simulations with maven: 
`mvn test -pl jcommune-performance-tests -Dperformance-test.skip=false -Dperformance.url=http://yourserveraddress.com`
5. Result charts will be placed in "target" folder in your project root folder.