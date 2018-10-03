package ru.ssnd.demo.vkchat.config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import javax.annotation.Resource;

@Configuration
@ComponentScan("ru.ssnd.demo.vkchat")
@PropertySource("classpath:app.properties")
@EnableMongoRepositories(basePackages = "ru.ssnd.demo.vkchat.repository")
public class DataConfig extends AbstractMongoConfiguration {

    @Resource
    private Environment env;

    @Override
    public String getDatabaseName() {
        return env.getRequiredProperty("mongo.name");
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(
                new ServerAddress(env.getRequiredProperty("mongo.host"), env.getRequiredProperty("mongo.port", Integer.class)),
                new MongoClientOptions.Builder()
                        .connectionsPerHost(300)
                        .build()
        );
    }

}
