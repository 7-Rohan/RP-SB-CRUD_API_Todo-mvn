package com.srin.RPSBCRUDAPITodomvn.repository;

import com.srin.RPSBCRUDAPITodomvn.model.TodoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface TodoRepository extends MongoRepository<TodoModel, String> {

    @Query("{'todo': ?0}")
    Optional<TodoModel> findByTodo(String todo);

}
