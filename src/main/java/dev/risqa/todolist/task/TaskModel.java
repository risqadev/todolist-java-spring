package dev.risqa.todolist.task;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "tb_tasks")
public class TaskModel {

  private static final int titleMaxLength = 50;
  
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;
  private UUID userId;
  
  @Column(length = titleMaxLength)
  private String title;
  private String description;
  private String priority;
  private LocalDateTime startAt;
  private LocalDateTime endAt;

  @CreationTimestamp
  private LocalDateTime createdAt;

  public void setTitle(String title) throws Exception {
    if (title.length() > titleMaxLength)
      throw new Exception("O título deve ter no máximo " + titleMaxLength + " caracteres.");
    this.title = title;
  }
}
