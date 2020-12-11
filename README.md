#set up config/server.properties
    advertised.host.name = localhost
    advertised.port = 9092

# Start the ZooKeeper service
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start the Kafka broker service
bin/kafka-server-start.sh config/server.properties

#Create a topic manually
bin/kafka-topics.sh --create --topic quickstart-events --bootstrap-server localhost:9092
