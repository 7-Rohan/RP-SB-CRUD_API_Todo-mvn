package com.srin.RPSBCRUDAPITodomvn.service;

import com.srin.RPSBCRUDAPITodomvn.exception.TodoCollectionException;
import com.srin.RPSBCRUDAPITodomvn.model.TodoModel;

import javax.validation.ConstraintViolationException;
import java.util.List;

public interface TodoService {

    public void createTodo(TodoModel todo) throws ConstraintViolationException, TodoCollectionException;

    public List<TodoModel> getAll();

    public TodoModel getById(String id) throws TodoCollectionException;

    public void updateById(String id, TodoModel todo) throws TodoCollectionException;

    public void deleteById(String id) throws TodoCollectionException;

    public void completingTodoById(String id) throws TodoCollectionException;

}
