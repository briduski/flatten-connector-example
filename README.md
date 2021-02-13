# flatten-connector-example
Example of how to run Kafka [Kafka Connect JDBC Flatten Connector](https://github.com/gertschouten/kafka-connect-jdbc-flatten)  


1. Run in the directory /docker-compose:  docker-compose up -d

2. Start CLientFlattenSinkv1 sink connector:  
 curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/client-flatten.json http://localhost:8083/connectors   

3. Verify the connector is up&running7
curl -s "http://localhost:8083/connectors?expand=info&expand=status" | jq '. | to_entries[] | [ .value.info.type, .key, .value.status.connector.state,.value.status.tasks[].state,.value.info.config."connector.class"]|join(":|:")' | column -s : -t| sed 's/\"//g'| sort    
curl -s http://localhost:8083/connectors | jQ    
See config: curl -s http://localhost:8083/connectors/CLientFlattenSinkv1 | jq   
 
4. Run the producer under project Event1Producer:
**ClientProducer/src/main/java/bri/kafka/producer/ProducerAvro1.java**  
Output console:  
> Client
> record:key:::: A1
> record:id:::: A1
> Bye!
> Producer => {partition=0, offset=0, topic=client-state, value={"status": "Active", "id": "A1", "unusedid": "A0A1A1", "timestamp": 1613242305076, "pet": ["Cat1", "Dog1"], "name": {"firstname": "Mary", "lastname": "Streep", "middlename": "Louise", "shortname": "Meryl Streep"}, "address": [{"type": "Primary", "address": "Oxford Street", "postalCode": "W1D 1BS", "postalArea": "London", "country": "UK"}, {"type": "Secondary", "address": "Abbey Road", "postalCode": "NW8 0AE", "postalArea": "London", "country": "UK"}], "Email": [{"type": "Primary", "email": "bbbb@gmail.com"}, {"type": "Secondary", "email": "aaaa@gmail.com"}]}, key=A1, timestamp=1613242305433}       

5. Check Topic 'customer-state' using kafkacat  
kafkacat -C -b localhost:29092 -t customer-state -s key=s -s value=avro -r http://localhost:8081

6. Check the postgres database to see the record inserted into the tables     

![Screeshotv2](https://github.com/briduski/flatten-connector-example/blob/main/images/T_CLIENT.png)
![Screeshotv2](https://github.com/briduski/flatten-connector-example/blob/main/images/T_CLIENT_ADDRESS.png)
![Screeshotv2](https://github.com/briduski/flatten-connector-example/blob/main/images/T_CLIENT_EMAIL.png)
![Screeshotv2](https://github.com/briduski/flatten-connector-example/blob/main/images/T_CLIENT_PET.png)
       
# Explanation of configuration   
![see](connector-config/Comments.txt)       
![see](./connector-config/Comments.txt)       
      
# Resources  
- https://github.com/gertschouten/kafka-connect-jdbc-flatten
- https://docs.confluent.io/platform/current/connect/transforms/overview.html
- https://docs.confluent.io/5.3.0/connect/kafka-connect-jdbc/sink-connector/index.html


### Others.

Useful connect-rest-calls: 

**curl -s localhost:8083/connector-plugins | jq**
**curl -s localhost:8083/connectors | jq**
**curl -X DELETE -s --header "Content-Type: application/json" http://localhost:8083/connectors/CLientFlattenSinkv1** 
**curl -X POST -s --header "Content-Type: application/json" --data @../connector-config/client-flatten.json http://localhost:8083/connectors** 
        
       
  