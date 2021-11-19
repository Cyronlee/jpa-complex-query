package io.github.cyronlee.repository;

import io.github.cyronlee.exception.RecordNotFoundException;
import io.github.cyronlee.model.BaseModel;
import io.github.cyronlee.util.CopyUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.util.Streamable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class SoftDeleteRepositoryImpl<T extends BaseModel, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements SoftDeleteRepository<T, ID> {

    private static final String DELETED_BY = "deletedBy";
    private static final String DELETED_AT = "deletedAt";
    private static final String DEFAULT_OPERATOR = "System";

    private static final String ID_NOT_NULL = "ID must not be null!";
    private static final String IDS_NOT_NULL = "IDs must not be null!";
    private static final String ENTITY_NOT_NULL = "Entity must not be null!";
    private static final String SORT_NOT_NULL = "Sort must not be null!";
    private static final String PAGE_NOT_NULL = "Page must not be null!";
    private static final String SPEC_NOT_NULL = "Specification must not be null!";

    private final JpaEntityInformation<T, ?> entityInformation;
    private final EntityManager entityManager;
    private final Class<T> domainClass;

    public SoftDeleteRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
        this.domainClass = getDomainClass();
    }

    @Override
    public <S extends T> S insert(S entity) {
        return insert(entity, DEFAULT_OPERATOR);
    }

    @Override
    public <S extends T> S insert(S entity, String createdBy) {
        entity.setCreateInfo(createdBy, LocalDateTime.now());
        return save(entity);
    }

    @Override
    public <S extends T> List<S> insertAll(Iterable<S> entities) {
        return insertAll(entities, DEFAULT_OPERATOR);
    }

    @Override
    public <S extends T> List<S> insertAll(Iterable<S> entities, String createdBy) {
        LocalDateTime now = LocalDateTime.now();
        entities.forEach(entity -> entity.setCreateInfo(createdBy, now));
        return saveAll(entities);
    }

    @Override
    public <S extends T> S update(S entity) {
        return update(entity, DEFAULT_OPERATOR);
    }

    @Override
    public <S extends T> S update(S entity, String updatedBy) {
        Optional<T> optionalOne = findOneActive((ID) entityInformation.getId(entity));
        if (optionalOne.isEmpty()) {
            throw new RecordNotFoundException();
        }

        S record = (S) optionalOne.get();
        CopyUtil.copyIgnoreNull(entity, record);
        record.setUpdateInfo(updatedBy, LocalDateTime.now());

        return save(record);
    }

    @Override
    public <S extends T> List<S> updateAll(Iterable<S> entities) {
        return updateAll(entities, DEFAULT_OPERATOR);
    }

    @Override
    public <S extends T> List<S> updateAll(Iterable<S> entities, String updatedBy) {
        LocalDateTime now = LocalDateTime.now();

        List<S> result = new ArrayList<>();
        for (S entity : entities) {
            Optional<T> optionalOne = findOneActive((ID) entityInformation.getId(entity));
            if (optionalOne.isEmpty()) {
                throw new RecordNotFoundException();
            }

            S record = (S) optionalOne.get();
            CopyUtil.copyIgnoreNull(entity, record);
            record.setUpdateInfo(updatedBy, now);

            result.add(save(record));
        }

        return result;
    }

    @Override
    public Optional<T> findOneActive(Specification<T> spec) {
        Assert.notNull(spec, SPEC_NOT_NULL);

        return findOne(spec.and(notDeleted()));
    }

    @Override
    public Optional<T> findOneActive(ID id) {
        Assert.notNull(id, ID_NOT_NULL);

        return findOneActive(new ByIdSpecification<>(entityInformation, id));
    }

    @Override
    public List<T> findAllActive() {
        return findAll(notDeleted());
    }

    @Override
    public List<T> findAllActive(Sort sort) {
        Assert.notNull(sort, SORT_NOT_NULL);

        return findAll(notDeleted(), sort);
    }

    @Override
    public List<T> findAllActive(Iterable<ID> ids) {
        Assert.notNull(ids, IDS_NOT_NULL);

        if (!ids.iterator().hasNext()) {
            return Collections.emptyList();
        }

        Collection<ID> idCollection = Streamable.of(ids).toList();

        ByIdsSpecification<T> specification = new ByIdsSpecification<>(entityInformation);
        TypedQuery<T> query = getQuery(Specification.where(specification).and(notDeleted()), Sort.unsorted());

        return query.setParameter(specification.parameter, idCollection).getResultList();
    }

    @Override
    public Page<T> findAllActive(Pageable pageable) {
        Assert.notNull(pageable, PAGE_NOT_NULL);

        return findAll(notDeleted(), pageable);
    }

    @Override
    public List<T> findAllActive(Specification<T> spec) {
        Assert.notNull(spec, SPEC_NOT_NULL);

        return findAll(spec.and(notDeleted()));
    }

    @Override
    public List<T> findAllActive(Specification<T> spec, Sort sort) {
        Assert.notNull(spec, SPEC_NOT_NULL);
        Assert.notNull(sort, SORT_NOT_NULL);

        return findAll(spec.and(notDeleted()), sort);
    }

    @Override
    public Page<T> findAllActive(Specification<T> spec, Pageable pageable) {
        Assert.notNull(spec, SPEC_NOT_NULL);
        Assert.notNull(pageable, PAGE_NOT_NULL);

        return findAll(spec.and(notDeleted()), pageable);
    }

    @Override
    public boolean existActive(ID id) {
        Assert.notNull(id, ID_NOT_NULL);

        return findOneActive(id).isPresent();
    }

    @Override
    public long countActive() {
        return count(notDeleted());
    }

    @Override
    public long countActive(Specification<T> spec) {
        Assert.notNull(spec, SPEC_NOT_NULL);

        return count(spec.and(notDeleted()));
    }

    @Override
    public void softDelete(T entity) {
        Assert.notNull(entity, ENTITY_NOT_NULL);

        ID id = (ID) entityInformation.getId(entity);
        softDelete(id);
    }

    @Override
    public void softDelete(ID id) {
        Assert.notNull(id, ID_NOT_NULL);

        softDelete(id, DEFAULT_OPERATOR, LocalDateTime.now());
    }

    @Override
    public void softDelete(ID id, String deletedBy, LocalDateTime deletedAt) {
        Assert.notNull(id, ID_NOT_NULL);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(domainClass);
        Root<T> root = update.from(domainClass);

        update.set(DELETED_BY, deletedBy);
        update.set(DELETED_AT, deletedAt);

        update.where(cb.equal(root.<ID>get(entityInformation.getIdAttribute().getName()), id),
                cb.isNull(root.<String>get(DELETED_BY)));

        entityManager.createQuery(update).executeUpdate();
    }


    private static final class DeletedAtIsNull<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.isNull(root.<LocalDateTime>get(DELETED_AT));
        }
    }

    private static final class DeletedAtAfterNow<T> implements Specification<T> {
        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.greaterThan(root.get(DELETED_AT), LocalDateTime.now());
        }
    }

    private static final class ByIdSpecification<T, ID extends Serializable> implements Specification<T> {

        private final JpaEntityInformation<T, ?> entityInformation;
        private final ID id;

        ByIdSpecification(JpaEntityInformation<T, ?> entityInformation, ID id) {
            this.entityInformation = entityInformation;
            this.id = id;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            return cb.equal(root.<ID>get(entityInformation.getIdAttribute().getName()), id);
        }
    }

    private static final class ByIdsSpecification<T> implements Specification<T> {

        private final JpaEntityInformation<T, ?> entityInformation;
        ParameterExpression<Collection<?>> parameter;

        ByIdsSpecification(JpaEntityInformation<T, ?> entityInformation) {
            this.entityInformation = entityInformation;
        }

        @Override
        public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<?> path = root.get(entityInformation.getIdAttribute());
            parameter = (ParameterExpression<Collection<?>>) (ParameterExpression) cb.parameter(Collection.class);
            return path.in(parameter);
        }
    }

    private static <T> Specification<T> notDeleted() {
        return Specification.where(new DeletedAtIsNull<T>()).or(new DeletedAtAfterNow<T>());
    }
}

