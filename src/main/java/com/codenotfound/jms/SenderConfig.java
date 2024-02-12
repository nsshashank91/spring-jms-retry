package com.codenotfound.jms;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
//@EnableAsync
public class SenderConfig {

  @Value("${activemq.broker-url}")
  private String brokerUrl;



  @Bean
  public ActiveMQConnectionFactory senderActiveMQConnectionFactory() {
    ActiveMQConnectionFactory activeMQConnectionFactory =
        new ActiveMQConnectionFactory();
    activeMQConnectionFactory.setBrokerURL(brokerUrl);
    return activeMQConnectionFactory;
  }

  @Bean
  public CachingConnectionFactory cachingConnectionFactory() {
    CachingConnectionFactory factory=  new CachingConnectionFactory(
        );
    factory.setTargetConnectionFactory(senderActiveMQConnectionFactory());
    factory.setReconnectOnException(true);
    factory.setSessionCacheSize(5);
    return factory;
  }


  /*@Bean
  public RetryTemplate retryTemplate() {
    RetryTemplate retryTemplate = new RetryTemplateBuilder()
            .maxAttempts(5)
            .exponentialBackoff(2000, 2.0, 5000)
            .build();

    RetryPolicy retryPolicy = new SimpleRetryPolicy(3); // Number of retries
    retryTemplate.setRetryPolicy(retryPolicy);

    ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
    backOffPolicy.setInitialInterval(1000); // Initial interval in milliseconds
    backOffPolicy.setMultiplier(2.0); // Backoff multiplier
    retryTemplate.setBackOffPolicy(backOffPolicy);

    return retryTemplate;
  }*/



  @Bean
  public JmsTemplate jmsTemplate() {
    return new JmsTemplate(cachingConnectionFactory());
  }

  @Bean
  public Sender sender() {
    return new Sender();
  }
}
