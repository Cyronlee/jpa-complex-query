package io.github.cyronlee.repository;

import io.github.cyronlee.model.Book;
import io.github.cyronlee.util.JsonReader;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SoftDeleteRepositoryTest {

    @Autowired
    BookRepository repo;

    @BeforeEach
    public void deleteAll() {
        repo.deleteAll();
    }

    @Test
    public void shouldNotActive_afterSoftDelete() throws IOException {
        Book book = JsonReader.readObject("json/Book.json", Book.class);

        repo.insert(book);

        assertTrue(repo.existActive(book.getBookId()));
        assertEquals(1, repo.countActive());
        assertEquals(1, repo.findAllActive(Sort.unsorted()).size());
        assertEquals(1, repo.findAllActive(Pageable.unpaged()).getContent().size());
        assertEquals(1, repo.findAllActive(Lists.list(book.getBookId())).size());
        // specification
        assertEquals(1, repo.countActive(nameEqual(book.getName())));
        assertEquals(1, repo.findAllActive(nameEqual(book.getName())).size());
        assertEquals(1, repo.findAllActive(nameEqual(book.getName()), Sort.unsorted()).size());
        assertEquals(1, repo.findAllActive(nameEqual(book.getName()), Pageable.unpaged()).getTotalElements());

        repo.softDelete(book);

        assertFalse(repo.existActive(book.getBookId()));
        assertEquals(0, repo.countActive());
        assertEquals(0, repo.findAllActive(Sort.unsorted()).size());
        assertEquals(0, repo.findAllActive(Pageable.unpaged()).getTotalElements());
        assertEquals(0, repo.findAllActive(Lists.list(book.getBookId())).size());
        // specification
        assertEquals(0, repo.countActive(nameEqual(book.getName())));
        assertEquals(0, repo.findAllActive(nameEqual(book.getName())).size());
        assertEquals(0, repo.findAllActive(nameEqual(book.getName()), Sort.unsorted()).size());
        assertEquals(0, repo.findAllActive(nameEqual(book.getName()), Pageable.unpaged()).getTotalElements());

        assertEquals(1, repo.findAll().size());
    }

    @Test
    public void testSingleInsertAndUpdate() {
        Book book = new Book();
        book.setName("Test");
        book.setCategory("Test");

        repo.insert(book);
        Book inserted = repo.findOneActive(book.getBookId()).get();
        assertNotNull(inserted.getCreatedBy());
        assertNotNull(inserted.getCreatedAt());

        repo.update(book);
        Book updated = repo.findOneActive(book.getBookId()).get();
        assertNotNull(updated.getUpdatedBy());
        assertNotNull(updated.getUpdatedAt());
    }

    @Test
    public void testBatchInsertAndUpdate() {
        Book book1 = new Book();
        book1.setName("Test1");
        book1.setCategory("Test1");

        Book book2 = new Book();
        book2.setName("Test2");
        book2.setCategory("Test2");

        repo.insertAll(Lists.list(book1, book2));
        List<Book> insertedList = repo.findAllActive();
        for (Book inserted : insertedList) {
            assertNotNull(inserted.getCreatedBy());
            assertNotNull(inserted.getCreatedAt());
        }

        repo.updateAll(insertedList);
        List<Book> updatedList = repo.findAllActive();
        for (Book updated : updatedList) {
            assertNotNull(updated.getUpdatedBy());
            assertNotNull(updated.getUpdatedAt());
        }
    }

    private Specification<Book> nameEqual(String name) {
        return (root, query, cb) -> cb.equal(root.get("name"), name);
    }
}
