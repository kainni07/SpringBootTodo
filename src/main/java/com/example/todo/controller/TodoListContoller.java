package com.example.todo.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;

import com.example.todo.repository.TodoRepository;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.example.todo.entity.Todo;
import java.util.List;

@Controller
@AllArgsConstructor
public class TodoListContoller {
  private final TodoRepository todoRepository;
  
  @GetMapping("/todo")
  public ModelAndView showTodoList(ModelAndView mv) {
    // 一覧を検索して表示 
    mv.setViewName("todoList");
    List<Todo> todoList = todoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    mv.addObject("todoList", todoList);
    return mv;
  }
}
