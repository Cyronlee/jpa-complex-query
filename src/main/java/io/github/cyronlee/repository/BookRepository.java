package io.github.cyronlee.repository;

import io.github.cyronlee.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends SoftDeleteRepository<Book, Integer> {

}
