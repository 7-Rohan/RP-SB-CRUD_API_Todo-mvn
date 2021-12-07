package com.srin.RPSBCRUDAPITodomvn.service;

import com.srin.RPSBCRUDAPITodomvn.exception.TodoCollectionException;
import com.srin.RPSBCRUDAPITodomvn.model.TodoModel;
import com.srin.RPSBCRUDAPITodomvn.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TodoServiceImpl implements TodoService{

    @Autowired
    private TodoRepository todoRepository;

    @Override
    public void createTodo(TodoModel todo) throws ConstraintViolationException, TodoCollectionException {

        Optional<TodoModel> todoOptional = todoRepository.findByTodo(todo.getTodo());
        if (todoOptional.isPresent()) {
            throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
        } else {
            todo.setCreatedAt(new Date(System.currentTimeMillis()));
            todo.setCompleted(false);
            todoRepository.save(todo);
        }
    }

    @Override
    public List<TodoModel> getAll() {
        List<TodoModel> todos = todoRepository.findAll();
        if (todos.size()>0) {
            return todos;
        } else {
            return new ArrayList<TodoModel>();
        }
    }

    @Override
    public TodoModel getById(String id) throws TodoCollectionException{
        Optional<TodoModel> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
        } else {
            return optionalTodo.get();
        }
    }

    @Override
    public void updateById(String id, TodoModel todo) throws TodoCollectionException {
        Optional<TodoModel> optionalTodo = todoRepository.findById(id);
        Optional<TodoModel> optionalTodoWithSameName = todoRepository.findByTodo(todo.getTodo());
        if (optionalTodo.isPresent()) {

            if (optionalTodoWithSameName.isPresent() && !optionalTodoWithSameName.get().getId().equals(id)){
                throw new TodoCollectionException(TodoCollectionException.TodoAlreadyExists());
            }

            TodoModel todoToUpdate = optionalTodo.get();
            todoToUpdate.setCompleted(todo.getCompleted() != null ? todo.getCompleted() : todoToUpdate.getCompleted());
            todoToUpdate.setTodo(todo.getTodo());
            todoToUpdate.setDescription(todo.getDescription() != null ? todo.getDescription() : todoToUpdate.getDescription());
            todoToUpdate.setUpdatedAt(new Date(System.currentTimeMillis()));
            todoRepository.save(todoToUpdate);
        } else {
            throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
        }
    }

    @Override
    public void deleteById(String id) throws TodoCollectionException {
        Optional<TodoModel> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isEmpty()) {
            throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
        } else {
            todoRepository.deleteById(id);
        }
    }

    @Override
    public void completingTodoById(String id) throws TodoCollectionException {
        Optional<TodoModel> optionalTodo = todoRepository.findById(id);
        if (optionalTodo.isPresent()) {
            TodoModel todoToComplete = optionalTodo.get();
            todoToComplete.setCompleted(true);
            todoToComplete.setCompletedAt(new Date(System.currentTimeMillis()));
            todoRepository.save(todoToComplete);
        } else {
            throw new TodoCollectionException(TodoCollectionException.NotFoundException(id));
        }
    }

}
