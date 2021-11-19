package io.github.cyronlee.mapper;

import io.github.cyronlee.dto.CreateBook;
import io.github.cyronlee.dto.UpdateBook;
import io.github.cyronlee.model.Book;
import io.github.cyronlee.vo.BookVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book createBookToBook(CreateBook createBook);

    @Mapping(source = "id", target = "bookId")
    Book updateBookToBook(UpdateBook createBook);

    @Mapping(source = "bookId", target = "id")
    BookVO bookToBookVO(Book book);

    @Mapping(source = "bookId", target = "id")
    List<BookVO> booksToBookVOs(List<Book> books);
}
