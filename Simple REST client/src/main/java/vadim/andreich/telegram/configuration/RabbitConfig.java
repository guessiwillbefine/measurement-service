package vadim.andreich.telegram.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    public DirectExchange direct() {
        return new DirectExchange("alert-queue");
    }

    @Bean
    public Queue mailQueue() {
        return new Queue("queue");
    }


    @Bean
    public Binding binding1a(DirectExchange direct,
                             Queue mailQueue) {
        return BindingBuilder.bind(mailQueue).to(direct)
                .with("telegram-notifier");
    }

}
