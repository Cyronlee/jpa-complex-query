package io.github.cyronlee.service;

import io.github.cyronlee.dto.CreateBook;
import io.github.cyronlee.dto.UpdateBook;
import io.github.cyronlee.mapper.BookMapper;
import io.github.cyronlee.model.Book;
import io.github.cyronlee.repository.BookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Resource
    private BookRepository bookRepo;

    public Book findById(Integer bookId) {
        Optional<Book> bookOptional = bookRepo.findOneActive(bookId);
        if (bookOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
        return bookOptional.get();
    }

    public List<Book> findAll() {
        return bookRepo.findAllActive();
    }

    public Book createBook(CreateBook createBook) {
        Book book = BookMapper.INSTANCE.createBookToBook(createBook);
        return bookRepo.insert(book);
    }

    public Book updateBook(UpdateBook updateBook) {
        Book book = BookMapper.INSTANCE.updateBookToBook(updateBook);
        return bookRepo.update(book);
    }

    public void deleteBook(List<Integer> bookIds) {
        for (Integer bookId : bookIds) {
            bookRepo.softDelete(bookId);
        }
    }
}
