package io.github.cyronlee.repository;

import io.github.cyronlee.model.BaseModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@NoRepositoryBean
public interface SoftDeleteRepository<T extends BaseModel, ID extends Serializable> extends JpaRepository<T, ID> {

    <S extends T> S insert(S entity);

    <S extends T> S insert(S entity, String createdBy);

    <S extends T> List<S> insertAll(Iterable<S> entities);

    <S extends T> List<S> insertAll(Iterable<S> entities, String createdBy);

    @Modifying
    <S extends T> S update(S entity);

    @Modifying
    <S extends T> S update(S entity, String updatedBy);

    @Modifying
    <S extends T> List<S> updateAll(Iterable<S> entities);

    @Modifying
    <S extends T> List<S> updateAll(Iterable<S> entities, String updatedBy);

    Optional<T> findOneActive(Specification<T> spec);

    Optional<T> findOneActive(ID id);

    List<T> findAllActive();

    List<T> findAllActive(Sort sort);

    List<T> findAllActive(Iterable<ID> ids);

    Page<T> findAllActive(Pageable pageable);

    List<T> findAllActive(Specification<T> spec);

    List<T> findAllActive(Specification<T> spec, Sort sort);

    Page<T> findAllActive(Specification<T> spec, Pageable pageable);

    boolean existActive(ID id);

    long countActive();

    long countActive(Specification<T> spec);

    @Modifying
    void softDelete(T entity);

    @Modifying
    void softDelete(ID id);

    @Modifying
    void softDelete(ID id, String deletedBy, LocalDateTime deletedAt);
}
