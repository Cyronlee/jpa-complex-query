package io.github.cyronlee.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateBook {

    private Integer id;
    private Long serialNumber;
    private String name;
    private String category;
    private BigDecimal price;
    private LocalDate publishDate;
}
