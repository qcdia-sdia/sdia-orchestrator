package nl.uva.qcdis.sdia.rpc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.uva.qcdis.sdia.model.Exceptions.SIDIAExeption;
import nl.uva.qcdis.sdia.model.Message;
import org.springframework.stereotype.Service;

/*
 * Copyright 2019 S. Koulouzis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 *
 * @author S. Koulouzis
 */
@Service
public class SDIACaller implements AutoCloseable {

//    private Connection connection;
//    private Channel channel;
//    private String replyQueueName;
    private String requestQeueName;
    private final ObjectMapper mapper;
    private final ConnectionFactory factory;

    public SDIACaller(ConnectionFactory factory) throws IOException, TimeoutException {
        this.factory = factory;
        Logger.getLogger(SDIACaller.class.getName()).log(Level.INFO, "ConnectionFactory host: {0}", factory.getHost());
        //        factory.setHost(messageBrokerHost);
//        factory.setPort(AMQP.PROTOCOL.PORT);
//        factory.setUsername(username);
//        factory.setPassword(password);
        init();
        this.mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    public void init() throws IOException, TimeoutException {
//        if (connection == null || !connection.isOpen()) {
//            connection = factory.newConnection();
//        }
    }

//    /**
//     * @return the connection
//     */
//    public Connection getConnection() {
//        return connection;
//    }
//    /**
//     * @return the channel
//     */
//    public Channel getChannel() {
//        return channel;
//    }
//    /**
//     * @return the replyQueueName
//     */
//    public String getReplyQueueName() {
//        return replyQueueName;
//    }
    @Override
    public void close() throws IOException, TimeoutException {

//        if (connection != null && connection.isOpen()) {
//            connection.close();
//        }
    }

    public Message call(Message r) throws IOException, TimeoutException, InterruptedException, SIDIAExeption {
        Channel channel = null;
        Connection connection = null;
        try {
            String jsonInString = mapper.writeValueAsString(r);

            int timeOut = 600;
            if (getRequestQeueName().equals("planner")) {
                timeOut = 60;
            }
            if (getRequestQeueName().equals("provisioner")) {
                timeOut = 60;
            }
            connection = factory.newConnection();

            channel = connection.createChannel();
            Map<String, Object> args = new HashMap<>();
            String replyQueueName = channel.queueDeclare().getQueue();

            //Build a correlation ID to distinguish responds 
            final String corrId = UUID.randomUUID().toString();
            AMQP.BasicProperties props = new AMQP.BasicProperties.Builder()
                    .correlationId(corrId)
                    .expiration(String.valueOf(timeOut * 60000))
                    .replyTo(replyQueueName)
                    .build();
            Logger.getLogger(SDIACaller.class.getName()).log(Level.INFO, "Sending: to queue: {1}", new Object[]{getRequestQeueName()});

            channel.basicPublish("", getRequestQeueName(), props, jsonInString.getBytes("UTF-8"));

            final BlockingQueue<String> response = new ArrayBlockingQueue(1);

            channel.basicConsume(replyQueueName, true, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    if (properties.getCorrelationId().equals(corrId)) {
                        response.offer(new String(body, "UTF-8"));
                    }
                }
            });
//        String resp = response.take();

            String resp = response.poll(timeOut, TimeUnit.MINUTES);
            Logger.getLogger(SDIACaller.class.getName()).log(Level.INFO, "Got response from qeue: {0}", getRequestQeueName());
            if (resp == null) {
                throw new TimeoutException("Timeout on qeue: " + getRequestQeueName());
            }
            try {
                Message incomingMessage = mapper.readValue(resp, Message.class);
                if (incomingMessage.getErrorReport() != null){
                    throw new SIDIAExeption(incomingMessage.getErrorReport());
                }
                return incomingMessage;
            } catch (com.fasterxml.jackson.core.JsonParseException ex) {
                if (resp.contains("error")) {
                    Map<String, Object> error = new ObjectMapper().readValue(resp, HashMap.class);
                    throw new SIDIAExeption((String) error.get("error"));
                }
            }
        } finally {
            if (channel != null && channel.isOpen()) {
                channel.close();
            }
            if (connection != null && connection.isOpen()) {
                connection.close();
            }
        }
        return null;
    }

    /**
     * @return the requestQeueName
     */
    public String getRequestQeueName() {
        return requestQeueName;
    }

    /**
     * @param requestQeueName the requestQeueName to set
     */
    public void setRequestQeueName(String requestQeueName) {
        this.requestQeueName = requestQeueName;
    }
}
