package io.github.cyronlee.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Book extends BaseModel {

    @Id
    @GeneratedValue
    private Integer bookId;
    private UUID uuid = UUID.randomUUID();
    private Long serialNumber;
    private String name;
    private String category;
    private BigDecimal price;
    private LocalDate publishDate;
}
