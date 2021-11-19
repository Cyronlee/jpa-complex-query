package io.github.cyronlee.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookVO {

    private Integer id;
    private String name;
    private String category;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
