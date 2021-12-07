package com.srin.RPSBCRUDAPITodomvn.controller;

import com.srin.RPSBCRUDAPITodomvn.exception.TodoCollectionException;
import com.srin.RPSBCRUDAPITodomvn.model.TodoModel;
import com.srin.RPSBCRUDAPITodomvn.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.handling.Context;
import ratpack.jackson.Jackson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TodoController {

    @Autowired
    private TodoService todoService;

    //	@Bean
//	Action<RatpackServerSpec> ratpackServerSpecAction() {
//		return ratpackServerSpec -> ratpackServerSpec
//				.serverConfig(serverConfigBuilder -> serverConfigBuilder
//						.env()
//						.build()
//				)
//				.registry(
//						Guice.registry(bindingsSpec -> bindingsSpec
//								.bind(ServerErrorHandler.class, ErrorHandler.class)
//						)
//				)
//				.handlers(chain());
//	}

    @Bean
    Action<Chain> chain() {
        return chain ->chain
                .path("todos", ctx -> ctx.byMethod(action -> action
                        .get(this::getAll)
                        .post(this::saveTodo)))
                .path("todos/:id", ctx -> ctx.byMethod(action -> action
                        .get(this::getById)
                        .put(this::updateTodo)
                        .delete(this::deleteTodo)))
                .path("todos/completed/:id", ctx -> ctx.byMethod(action -> action
                        .put(this::completingTodo)))
                .get(ctx -> ctx.render("Hello from Ratpack in Spring Boot!"));
    }

    private void getAll(Context ctx) {
        List<TodoModel> todos = todoService.getAll();
        Map<String, List<TodoModel>> map = new HashMap<>();
        map.put("data", todos);
        ctx.render(Jackson.json(map));
    }

    private void saveTodo(Context ctx) {
        ctx.parse(TodoModel.class)
                .onError(error -> ctx.getResponse().status(500)
                        .send(error.getMessage()))
                .then(todoModel -> {
                    todoService.createTodo(todoModel);
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", "Data created.");
                    map.put("data", todoModel);
                    ctx.render(Jackson.json(map));
                });
    }

    private void getById(Context ctx) {
        String id = ctx.getPathTokens().get("id");
        try {
            TodoModel byId = todoService.getById(id);
            ctx.render(Jackson.json(byId));
        } catch (TodoCollectionException e) {
            ctx.getResponse().status(404).send(e.getMessage());
        }
    }

    private void updateTodo(Context ctx) {
        String id = ctx.getPathTokens().get("id");
        ctx.parse(TodoModel.class)
                .onError(error -> ctx.getResponse().status(500).send(error.getMessage()))
                .then(todoModel -> {
                    todoService.updateById(id, todoModel);
                    Map<String, Object> map = new HashMap<>();
                    map.put("message", "Data updated.");
                    ctx.render(Jackson.json(map));
                });
    }

    private void deleteTodo(Context ctx) {
        String id = ctx.getPathTokens().get("id");
        try {
            todoService.deleteById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Data deleted.");
            ctx.render(Jackson.json(map));
        } catch (TodoCollectionException e){
            ctx.getResponse().status(404).send(e.getMessage());
        }
    }

    private void completingTodo(Context ctx) {
        String id = ctx.getPathTokens().get("id");
        try {
            todoService.completingTodoById(id);
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Todo with id " +id + " completed");
            ctx.render(Jackson.json(map));
        } catch (TodoCollectionException e) {
            ctx.getResponse().status(404).send(e.getMessage());
        }
    }
}
