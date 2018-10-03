package ru.ssnd.demo.vkchat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import ru.ssnd.demo.vkchat.entity.Message;

@Component
public interface MessagesRepository extends MongoRepository<Message, String> {

}
