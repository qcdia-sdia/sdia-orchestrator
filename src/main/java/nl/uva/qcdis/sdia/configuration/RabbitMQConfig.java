/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.uva.qcdis.sdia.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author S. Koulouzis
 */
@Configuration
public class RabbitMQConfig {

    @Value("${message.broker.host}")
    private String messageBrokerHost;
    @Value("${message.broker.username}")
    private String messageBrokerUsername;
    @Value("${message.broker.password}")
    private String messageBrokerPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(messageBrokerHost);
        connectionFactory.setUsername(messageBrokerUsername);
        connectionFactory.setPassword(messageBrokerPassword);
        Logger.getLogger(RabbitMQConfig.class.getName()).log(Level.INFO, "ConnectionFactory host: {0}", connectionFactory.getHost());
        return connectionFactory;
    }

}
