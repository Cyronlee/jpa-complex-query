package io.github.cyronlee.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateBook {

    private Long serialNumber;
    private String name;
    private String category;
    private BigDecimal price;
    private LocalDate publishDate;
}
