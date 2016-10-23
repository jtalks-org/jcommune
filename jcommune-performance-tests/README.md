#Gatling performance tests ##

##HOW TO ##

1. Run your application server used for testing.
2. Configure pom.xml
    ```
    <configuration>
        <runMultipleSimulations>false</runMultipleSimulations> (1)
        <simulationClass>org.jtalks.jcommune.performance.tests.OpenTopicPage</simulationClass> (2)
        <jvmArgs>
          <jvmArg>-Durl=http://performance.jtalks.org</jvmArg> (3)
          <jvmArg>-Dport=</jvmArg> (4)
          <jvmArg>-DurlPath=/jcommune</jvmArg> (5)
        </jvmArgs>
    </configuration>
    
    (1) set <runMultipleSimulations> **true** if you want to run all simulations sequentially.
    (2) choose specific simulation to run
    (3) your application server url
    (4) port to connect (can be empty if :80 used)
    (5) path to your applcation (if needed)
3. Run simulations with maven: mvn test.
4. Result charts will be placed in "target" folder in your project root folder.