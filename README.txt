eSciDoc Browser Documentation

0. if you don't have JRebel installed, change
    ...
        <jetty.scan.sec>0</jetty.scan.sec>
    ...
    to
        <jetty.scan.sec>5</jetty.scan.sec>
        
1. $ mvn eclipse:eclipse

3. $mvn install

4. $mvn jetty:run

5. URL: http://localhost:8084/browser
