package io.github.cyronlee.controller;

import io.github.cyronlee.dto.CreateBook;
import io.github.cyronlee.dto.UpdateBook;
import io.github.cyronlee.mapper.BookMapper;
import io.github.cyronlee.model.Book;
import io.github.cyronlee.service.BookService;
import io.github.cyronlee.vo.BookVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "Book Service")
@RestController
@RequestMapping("/api")
public class BookController {

    @Resource
    private BookService bookService;

    @GetMapping("/books/{bookId}")
    @ApiOperation("Get one Book")
    public BookVO getOneBook(Integer bookId) {
        return BookMapper.INSTANCE.bookToBookVO(bookService.findById(bookId));
    }

    @GetMapping("/books")
    @ApiOperation("Get All Books")
    public List<BookVO> getAllBooks() {
        return BookMapper.INSTANCE.booksToBookVOs(bookService.findAll());
    }

    @PostMapping("/books")
    @ApiOperation("Create a Book")
    public ResponseEntity createBook(@RequestBody CreateBook createBook) {
        Book book = bookService.createBook(createBook);
        return ResponseEntity.ok(BookMapper.INSTANCE.bookToBookVO(book));
    }

    @PutMapping("/books/{bookId}")
    @ApiOperation("Update a Book")
    public ResponseEntity updateBook(@RequestBody UpdateBook updateBook) {
        Book book = bookService.updateBook(updateBook);
        return ResponseEntity.ok(BookMapper.INSTANCE.bookToBookVO(book));
    }

    @DeleteMapping("/books/{bookIds}")
    @ApiOperation("Delete Books")
    public ResponseEntity deleteBook(@PathVariable("bookIds") String bookIds) {
        List<Integer> bookIdList = Arrays.stream(bookIds.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        bookService.deleteBook(bookIdList);
        return ResponseEntity.ok().build();
    }
}
