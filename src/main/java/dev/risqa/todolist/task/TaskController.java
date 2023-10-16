package dev.risqa.todolist.task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.risqa.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private ITaskRepository taskRepository;

  @PostMapping
  public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
    
    taskModel.setUserId((UUID) request.getAttribute("userId"));

    var now = LocalDateTime.now();
    if (now.isAfter(taskModel.getStartAt()) || now.isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("As datas de início e fim da tarefa devem ser maiores que a data atual.");
    }
    if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("A data de fim deve ser maior que a data de início.");
    }
    var createdTask = this.taskRepository.save(taskModel);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
  }

  @GetMapping
  public List<TaskModel> list(HttpServletRequest request) {
    var userId = request.getAttribute("userId");
    var tasks = taskRepository.findByUserId((UUID) userId);
    return tasks;
  }
  
  @PutMapping("/{taskId}")
  public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID taskId) {

    var credentialUserId = (UUID) request.getAttribute("userId");

    var task = this.taskRepository.findById(taskId).orElse(null);
    // validar se a tarefa não existe

    if (task == null)
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("A tarefa informada não existe.");
    
    if (!(task.getUserId().equals(credentialUserId)))
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para alterar esta tarefa.");

    // fazer as validações de dada para os dados da atualização
    Utils.copyNonNullProperties(taskModel, task);

    var updatedTask = this.taskRepository.save(task);
    return ResponseEntity.status(HttpStatus.OK).body(updatedTask);
  }
}
