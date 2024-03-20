package com.example.todo.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import com.example.todo.repository.TodoRepository;
import com.example.todo.service.TodoService;

import ch.qos.logback.core.model.Model;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final HttpSession session;
  
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
    session.setAttribute("mode", "create");
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
  public String createTodo(@ModelAttribute @Validated TodoData todoData,
                                BindingResult result,
                                Model model) {
      boolean isValid = todoService.isValid(todoData, result);
      
      if (!result.hasErrors() && isValid) {
        // エラーなし
        Todo todo = todoData.toEntity(); // エンティティクラスのオブジェクト
        todoRepository.saveAndFlush(todo); // テーブルに追加（INSERT文の発行）
        // return showTodoList(mv); // findAll()の代わりにshowTodoList()を呼び出す
        return "redirect:/todo";
        
      } else {
        // エラーあり
        // mv.setViewName("todoForm");
        // mv.addObject("todoData", todoData); // @ModelAttrobiteが付与されたオブジェクトのため省略可
        return "todoForm";
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
  
  /*
   * ToDo入力(更新)画面表示
   * 一覧画面でid押下時
   */
  @GetMapping("/todo/{id}") // {}URIテンプレート変数：URLパスから値を取り出し、@PathVariableでメソッド変数にセットする
  public ModelAndView todoById(@PathVariable(name = "id") int id, ModelAndView mv) {
    mv.setViewName("todoForm");
    Todo todo = todoRepository.findById(id).get(); // Optional<Todo>型、値があればTodoオブジェクトを返し、なければnull
    mv.addObject("todoData", todo);
    session.setAttribute("mode", "update"); // 機能によって表示ボタンを変えるため、データを書き込む
    return mv;
  }
  
  /*
   * 
   * 入力画面で更新ボタン押下時
   */
  @PostMapping("/todo/update")
  public String updateTodo(@ModelAttribute @Validated TodoData totoData, BindingResult result, ModelAndView mv) {
    boolean isValid = todoService.isValid(totoData, result);
    
    if (!result.hasErrors() && isValid) {
      // エラーなし
      Todo todo = totoData.toEntity();
      todoRepository.saveAndFlush(todo);
      return "redirect:/todo";
    } else {
      // エラーあり
      // model.addAttribute("todoData", totoData);
      return "todoForm";
    }
  }
  
  @PostMapping("/todo/delete")
  public String deleteTodo(@ModelAttribute TodoData todoData) {
    todoRepository.deleteById(todoData.getId());
    return "redirect:/todo";
  }
  
  
}
