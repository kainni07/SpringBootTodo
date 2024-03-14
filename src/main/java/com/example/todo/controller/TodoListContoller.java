package com.example.todo.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import com.example.todo.entity.Todo;
import com.example.todo.form.TodoData;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class TodoListContoller {
  private final TodoRepository todoRepository;
  private final TodoService todoService;
  
  /*
   * ToDo一覧表示
   */
  @GetMapping("/todo")
  public ModelAndView showTodoList(ModelAndView mv) {
    mv.setViewName("todoList");
    List<Todo> todoList = todoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    mv.addObject("todoList", todoList);
    return mv;
  }
  
  /*
   * ToDo入力フォーム表示
   * 一覧画面で新規追加ボタン押下
   */
  @GetMapping("/todo/create")
  public ModelAndView createTodo(ModelAndView mv) {
    mv.setViewName("todoForm");
    mv.addObject("todoData", new TodoData());
    return mv;
  }
  
  /**
   * ToDo追加処理
   * 入力画面で登録ボタン押下時
   * @param todoData　入力値
   * @param result バリデーションチェックの結果
   * @param mv ModelAndView
   * @return
   */
  @PostMapping("/todo/create")
  public ModelAndView createTodo(@ModelAttribute @Validated TodoData todoData,
                                BindingResult result,
                                ModelAndView mv) {
      boolean isValid = todoService.isValid(todoData, result);
      
      if (!result.hasErrors() && isValid) {
        // エラーなし
        Todo todo = todoData.toEntity(); // エンティティクラスのオブジェクト
        todoRepository.saveAndFlush(todo); // テーブルに追加（INSERT文の発行）
        return showTodoList(mv); // findAll()の代わりにshowTodoList()を呼び出す
        
      } else {
        // エラーあり
        mv.setViewName("todoForm");
        // mv.addObject("todoData", todoData); // @ModelAttrobiteが付与されたオブジェクトのため省略可
        return mv;
      }
  }
  
  /*
   * ToDo一覧画面表示
   * 入力画面でキャンセルボタン押下時
   */
  @PostMapping("/todo/cancel")
  public String cancel() {
      return "redirect:/todo";
  }
}
