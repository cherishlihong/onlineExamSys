package com.ifox.framework.dao.impl;

/**
 * Created by Administrator on 2016/10/8.
 */

import com.ifox.framework.paging.Page;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;

import com.ifox.framework.common.LogUtil;
import com.ifox.framework.dao.BaseDao;
import com.ifox.framework.filter.Filter;
import com.ifox.framework.filter.Filter.Operator;
import com.ifox.framework.ordering.Ordering;
import com.ifox.framework.ordering.Ordering.Direction;
import com.ifox.framework.paging.Pageable;


public abstract class BaseDaoImpl<T, ID extends Serializable> implements BaseDao<T, ID> {

    /**
     * 实体类类型
     */
    private Class<T> entityClass;

    /**
     * 别名数
     */
    private static volatile long aliasCount = 0;

    @PersistenceContext
    protected EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        Type type = getClass().getGenericSuperclass();
        Type[] parameterizedType = ((ParameterizedType) type).getActualTypeArguments();
        entityClass = (Class<T>) parameterizedType[0];
    }

    public T find(ID id) {
        if (id != null) {
            if (LogUtil.isDebugEnabled(BaseDaoImpl.class)) {
                LogUtil.debug(BaseDaoImpl.class, "find", "Fetching entity from database with ID : %d", id);
            }
            return entityManager.find(entityClass, id);
        }
        return null;
    }

    public T find(ID id, LockModeType lockModeType) {
        if (id != null) {
            if (lockModeType != null) {
                return entityManager.find(entityClass, id, lockModeType);
            } else {
                return entityManager.find(entityClass, id);
            }
        }
        return null;
    }

    public List<T> findList(Integer first, Integer count, List<Filter> filters,
                            List<Ordering> orderings) {
        if (LogUtil.isDebugEnabled(BaseDaoImpl.class)) {
            LogUtil
                    .debug(
                            BaseDaoImpl.class,
                            "find",
                            "Fetching entities from database with first : %d, count : %d, filters : %s, orderings : %s",
                            first, count, filters, orderings);
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return findList(criteriaQuery, first, count, filters, orderings);
    }

    public Page<T> findPage(Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return findPage(criteriaQuery, pageable);
    }

    public long count(Filter... filters) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(entityClass);
        criteriaQuery.select(criteriaQuery.from(entityClass));
        return count(criteriaQuery, filters != null ? Arrays.asList(filters) : null);
    }

    public void persist(T entity) {
        Assert.notNull(entity);
        entityManager.persist(entity);
    }

    public void persist(List<T> entities) {
        Assert.notNull(entities, "entities is null.");
        for (T entity : entities) {
            entityManager.persist(entity);
        }
    }

    public T merge(T entity) {
        Assert.notNull(entity);
        return entityManager.merge(entity);
    }

    public void merge(List<T> entities) {
        Assert.notNull(entities);
        for (T entity : entities) {
            entityManager.merge(entity);
        }
    }

    public void remove(T entity) {
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

    public void refresh(T entity) {
        if (entity != null) {
            entityManager.refresh(entity);
        }
    }

    public void refresh(T entity, LockModeType lockModeType) {
        if (entity != null) {
            if (lockModeType != null) {
                entityManager.refresh(entity, lockModeType);
            } else {
                entityManager.refresh(entity);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ID getIdentifier(T entity) {
        Assert.notNull(entity);
        return (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                .getIdentifier(entity);
    }

    public boolean isManaged(T entity) {
        return entityManager.contains(entity);
    }

    public void detach(T entity) {
        entityManager.detach(entity);
    }

    public void lock(T entity, LockModeType lockModeType) {
        if (entity != null && lockModeType != null) {
            entityManager.lock(entity, lockModeType);
        }
    }

    public void clear() {
        entityManager.clear();
    }

    public void flush() {
        entityManager.flush();
    }

    protected List<T> findList(CriteriaQuery<T> criteriaQuery, Integer first, Integer count,
                               List<Filter> filters, List<Ordering> orderings) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Root<T> root = getRoot(criteriaQuery);
        addRestrictions(criteriaQuery, filters);
        addOrderBys(criteriaQuery, orderings);
        if (criteriaQuery.getOrderList().isEmpty()) {

            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Ordering.DEFAULT_ORDERING)));
        }
        TypedQuery<T> query =
                entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
        if (first != null) {
            query.setFirstResult(first);
        }
        if (count != null) {
            query.setMaxResults(count);
        }
        return query.getResultList();
    }

    protected Page<T> findPage(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        if (pageable == null) {
            pageable = new Pageable();
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Root<T> root = getRoot(criteriaQuery);
        addRestrictions(criteriaQuery, pageable);
        addOrders(criteriaQuery, pageable);
        if (criteriaQuery.getOrderList().isEmpty()) {
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get(Ordering.DEFAULT_ORDERING)));
        }
        long total = count(criteriaQuery, null);
        int totalPages = (int) Math.ceil((double) total / (double) pageable.getRows());
        if (totalPages < pageable.getPage()) {
            pageable.setPage(totalPages);
        }
        TypedQuery<T> query =
                entityManager.createQuery(criteriaQuery).setFlushMode(FlushModeType.COMMIT);
        query.setFirstResult((pageable.getPage() - 1) * pageable.getRows());
        query.setMaxResults(pageable.getRows());
        return new Page<T>(query.getResultList(), total, pageable);
    }

    protected Long count(CriteriaQuery<T> criteriaQuery, List<Filter> filters) {
        Assert.notNull(criteriaQuery);
        Assert.notNull(criteriaQuery.getSelection());
        Assert.notEmpty(criteriaQuery.getRoots());

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        addRestrictions(criteriaQuery, filters);

        CriteriaQuery<Long> countCriteriaQuery = criteriaBuilder.createQuery(Long.class);
        for (Root<?> root : criteriaQuery.getRoots()) {
            Root<?> dest = countCriteriaQuery.from(root.getJavaType());
            dest.alias(getAlias(root));
            copyJoins(root, dest);
        }

        Root<?> countRoot = getRoot(countCriteriaQuery, criteriaQuery.getResultType());
        countCriteriaQuery.select(criteriaBuilder.count(countRoot));

        if (criteriaQuery.getGroupList() != null) {
            countCriteriaQuery.groupBy(criteriaQuery.getGroupList());
        }
        if (criteriaQuery.getGroupRestriction() != null) {
            countCriteriaQuery.having(criteriaQuery.getGroupRestriction());
        }
        if (criteriaQuery.getRestriction() != null) {
            countCriteriaQuery.where(criteriaQuery.getRestriction());
        }
        return entityManager.createQuery(countCriteriaQuery).setFlushMode(FlushModeType.COMMIT)
                .getSingleResult();
    }

    private synchronized String getAlias(Selection<?> selection) {
        if (selection != null) {
            String alias = selection.getAlias();
            if (alias == null) {
                if (aliasCount >= 1000) {
                    aliasCount = 0;
                }
                alias = "ovGeneratedAlias" + aliasCount++;
                selection.alias(alias);
            }
            return alias;
        }
        return null;
    }

    private Root<T> getRoot(CriteriaQuery<T> criteriaQuery) {
        if (criteriaQuery != null) {
            return getRoot(criteriaQuery, criteriaQuery.getResultType());
        }
        return null;
    }

    private Root<T> getRoot(CriteriaQuery<?> criteriaQuery, Class<T> clazz) {
        if (criteriaQuery != null && criteriaQuery.getRoots() != null && clazz != null) {
            for (Root<?> root : criteriaQuery.getRoots()) {
                if (clazz.equals(root.getJavaType())) {
                    return (Root<T>) root.as(clazz);
                }
            }
        }
        return null;
    }

    private void copyJoins(From<?, ?> from, From<?, ?> to) {
        for (Join<?, ?> join : from.getJoins()) {
            Join<?, ?> toJoin = to.join(join.getAttribute().getName(), join.getJoinType());
            toJoin.alias(getAlias(join));
            copyJoins(join, toJoin);
        }
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(fetch.getAttribute().getName());
            copyFetches(fetch, toFetch);
        }
    }

    private void copyFetches(Fetch<?, ?> from, Fetch<?, ?> to) {
        for (Fetch<?, ?> fetch : from.getFetches()) {
            Fetch<?, ?> toFetch = to.fetch(fetch.getAttribute().getName());
            copyFetches(fetch, toFetch);
        }
    }

    private void addRestrictions(CriteriaQuery<T> criteriaQuery, List<Filter> filters) {
        if (criteriaQuery == null || filters == null || filters.isEmpty()) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate restrictions =
                criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder
                        .conjunction();
        for (Filter filter : filters) {
            if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
                continue;
            }
            if (filter.getOperator() == Operator.eq && filter.getValue() != null) {
                if (filter.getIgnoreCase() != null && filter.getIgnoreCase()
                        && filter.getValue() instanceof String) {
                    restrictions =
                            criteriaBuilder.and(restrictions, criteriaBuilder.equal(
                                    criteriaBuilder.lower(root.<String>get(filter.getProperty())),
                                    ((String) filter.getValue()).toLowerCase()));
                } else {
                    restrictions =
                            criteriaBuilder.and(restrictions,
                                    criteriaBuilder.equal(root.get(filter.getProperty()), filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.ne && filter.getValue() != null) {
                if (filter.getIgnoreCase() != null && filter.getIgnoreCase()
                        && filter.getValue() instanceof String) {
                    restrictions =
                            criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(
                                    criteriaBuilder.lower(root.<String>get(filter.getProperty())),
                                    ((String) filter.getValue()).toLowerCase()));
                } else {
                    restrictions =
                            criteriaBuilder.and(restrictions,
                                    criteriaBuilder.notEqual(root.get(filter.getProperty()), filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.gt && filter.getValue() != null) {
                if (filter.getValue() instanceof Number) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.gt(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getValue() instanceof Date) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.greaterThan(root.<Date>get(filter.getProperty()),
                                            (Date) filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.lt && filter.getValue() != null) {
                if (filter.getValue() instanceof Number) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.lt(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getValue() instanceof Date) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.lessThan(root.<Date>get(filter.getProperty()),
                                            (Date) filter.getValue()));
                }

            } else if (filter.getOperator() == Operator.ge && filter.getValue() != null) {
                if (filter.getValue() instanceof Number) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.ge(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getValue() instanceof Date) {
                    restrictions =
                            criteriaBuilder.and(restrictions, criteriaBuilder.greaterThanOrEqualTo(
                                    root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.le && filter.getValue() != null) {
                if (filter.getValue() instanceof Date) {
                    restrictions =
                            criteriaBuilder.and(restrictions, criteriaBuilder.lessThanOrEqualTo(
                                    root.<Date>get(filter.getProperty()), (Date) filter.getValue()));
                } else {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.le(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.like && filter.getValue() != null
                    && filter.getValue() instanceof String) {
                restrictions =
                        criteriaBuilder.and(
                                restrictions,
                                criteriaBuilder.like(root.<String>get(filter.getProperty()),
                                        (String) filter.getValue()));
            } else if (filter.getOperator() == Operator.in && filter.getValue() != null) {
//        restrictions =
//            criteriaBuilder.and(restrictions, root.get(filter.getProperty()).in(filter.getValue()));
                if (filter.getValue() instanceof Object[]) {
                    // 传递多个参数 Object... values
                    Object[] objects = (Object[]) filter.getValue();
                    restrictions =
                            criteriaBuilder.and(restrictions, root.get(filter.getProperty()).in(objects));
                } else {
                    restrictions =
                            criteriaBuilder.and(restrictions,
                                    root.get(filter.getProperty()).in(filter.getValue()));
                }
            } else if (filter.getOperator() == Operator.isNull) {
                restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNull());
            } else if (filter.getOperator() == Operator.isNotNull) {
                restrictions =
                        criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNotNull());
            }
        }
        criteriaQuery.where(restrictions);
    }

    private void addRestrictions(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        if (criteriaQuery == null || pageable == null) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        Predicate restrictions =
                criteriaQuery.getRestriction() != null ? criteriaQuery.getRestriction() : criteriaBuilder
                        .conjunction();
        if (StringUtils.isNotEmpty(pageable.getSearchProperty())
                && StringUtils.isNotEmpty(pageable.getSearchValue())) {
            restrictions =
                    criteriaBuilder.and(
                            restrictions,
                            criteriaBuilder.like(root.<String>get(pageable.getSearchProperty()),
                                    "%" + pageable.getSearchValue() + "%"));
        }
        if (pageable.getFilters() != null) {
            for (Filter filter : pageable.getFilters()) {
                if (filter == null || StringUtils.isEmpty(filter.getProperty())) {
                    continue;
                }
                if (filter.getOperator() == Operator.eq && filter.getValue() != null) {
                    if (filter.getIgnoreCase() != null && filter.getIgnoreCase()
                            && filter.getValue() instanceof String) {
                        restrictions =
                                criteriaBuilder.and(restrictions, criteriaBuilder.equal(
                                        criteriaBuilder.lower(root.<String>get(filter.getProperty())),
                                        ((String) filter.getValue()).toLowerCase()));
                    } else {
                        restrictions =
                                criteriaBuilder.and(restrictions,
                                        criteriaBuilder.equal(root.get(filter.getProperty()), filter.getValue()));
                    }
                } else if (filter.getOperator() == Operator.ne && filter.getValue() != null) {
                    if (filter.getIgnoreCase() != null && filter.getIgnoreCase()
                            && filter.getValue() instanceof String) {
                        restrictions =
                                criteriaBuilder.and(restrictions, criteriaBuilder.notEqual(
                                        criteriaBuilder.lower(root.<String>get(filter.getProperty())),
                                        ((String) filter.getValue()).toLowerCase()));
                    } else {
                        restrictions =
                                criteriaBuilder.and(restrictions,
                                        criteriaBuilder.notEqual(root.get(filter.getProperty()), filter.getValue()));
                    }
                } else if (filter.getOperator() == Operator.gt && filter.getValue() != null) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.gt(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.lt && filter.getValue() != null) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.lt(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.ge && filter.getValue() != null) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.ge(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.le && filter.getValue() != null) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.le(root.<Number>get(filter.getProperty()),
                                            (Number) filter.getValue()));
                } else if (filter.getOperator() == Operator.like && filter.getValue() != null
                        && filter.getValue() instanceof String) {
                    restrictions =
                            criteriaBuilder.and(
                                    restrictions,
                                    criteriaBuilder.like(root.<String>get(filter.getProperty()),
                                            (String) filter.getValue()));
                } else if (filter.getOperator() == Operator.in && filter.getValue() != null) {
                    if (filter.getValue() instanceof Object[]) {
                        // 传递多个参数 Object... values
                        Object[] objects = (Object[]) filter.getValue();
                        restrictions =
                                criteriaBuilder.and(restrictions, root.get(filter.getProperty()).in(objects));
                    } else {
                        restrictions =
                                criteriaBuilder.and(restrictions,
                                        root.get(filter.getProperty()).in(filter.getValue()));
                    }
                } else if (filter.getOperator() == Operator.isNull) {
                    restrictions = criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNull());
                } else if (filter.getOperator() == Operator.isNotNull) {
                    restrictions =
                            criteriaBuilder.and(restrictions, root.get(filter.getProperty()).isNotNull());
                }
            }
        }
        criteriaQuery.where(restrictions);
    }

    private void addOrderBys(CriteriaQuery<T> criteriaQuery, List<Ordering> orderings) {
        if (criteriaQuery == null || orderings == null || orderings.isEmpty()) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<javax.persistence.criteria.Order> orderList =
                new ArrayList<javax.persistence.criteria.Order>();
        if (!criteriaQuery.getOrderList().isEmpty()) {
            orderList.addAll(criteriaQuery.getOrderList());
        }
        for (Ordering ordering : orderings) {
            if (ordering.getDirection() == Direction.asc) {
                orderList.add(criteriaBuilder.asc(root.get(ordering.getProperty())));
            } else if (ordering.getDirection() == Direction.desc) {
                orderList.add(criteriaBuilder.desc(root.get(ordering.getProperty())));
            }
        }
        criteriaQuery.orderBy(orderList);
    }

    private void addOrders(CriteriaQuery<T> criteriaQuery, Pageable pageable) {
        if (criteriaQuery == null || pageable == null) {
            return;
        }
        Root<T> root = getRoot(criteriaQuery);
        if (root == null) {
            return;
        }
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        List<javax.persistence.criteria.Order> orderList =
                new ArrayList<javax.persistence.criteria.Order>();
        if (!criteriaQuery.getOrderList().isEmpty()) {
            orderList.addAll(criteriaQuery.getOrderList());
        }
        if (StringUtils.isNotEmpty(pageable.getSort()) && pageable.getOrder() != null) {
            if (pageable.getOrder() == Direction.asc) {
                orderList.add(criteriaBuilder.asc(root.get(pageable.getSort())));
            } else if (pageable.getOrder() == Direction.desc) {
                orderList.add(criteriaBuilder.desc(root.get(pageable.getSort())));
            }
        }
        if (pageable.getOrderings() != null) {
            for (Ordering order : pageable.getOrderings()) {
                if (order.getDirection() == Direction.asc) {
                    orderList.add(criteriaBuilder.asc(root.get(order.getProperty())));
                } else if (order.getDirection() == Direction.desc) {
                    orderList.add(criteriaBuilder.desc(root.get(order.getProperty())));
                }
            }
        }
        // 未设置排序，默认createDate降序
        if ((StringUtils.isEmpty(pageable.getSort()) || pageable.getOrder() == null)
                && pageable.getOrderings() == null) {
            orderList.add(criteriaBuilder.desc(root.get("createDate")));
        }
        criteriaQuery.orderBy(orderList);
    }

    public Page<T> findPageCustomized(Pageable pageable, String jpql,
                                      Map<String, ? extends Object> paramMap) {
        String countJqpl = getCountJpql(jpql);
        TypedQuery<Long> countQuery =
                entityManager.createQuery(countJqpl, Long.class).setFlushMode(FlushModeType.COMMIT);
        for (final Entry<String, ? extends Object> entry : paramMap.entrySet()) {
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        long total = countQuery.getSingleResult();
        int totalPages = (int) Math.ceil((double) total / (double) pageable.getRows());
        if (totalPages < pageable.getPage()) {
            pageable.setPage(totalPages);
        }
        TypedQuery<T> queryEntity =
                entityManager.createQuery(jpql, entityClass).setFlushMode(FlushModeType.COMMIT);
        queryEntity.setFirstResult((pageable.getPage() - 1) * pageable.getRows());
        queryEntity.setMaxResults(pageable.getRows());
        for (final Entry<String, ? extends Object> entry : paramMap.entrySet()) {
            queryEntity.setParameter(entry.getKey(), entry.getValue());
        }
        return new Page<T>(queryEntity.getResultList(), total, pageable);
    }

    public List<T> findListCustomized(String jpql, Map<String, ? extends Object> paramMap) {
        if (LogUtil.isDebugEnabled(BaseDaoImpl.class)) {
            LogUtil.debug(BaseDaoImpl.class, "find", "Fetching entities from database with jpql : %s",
                    jpql);
        }
        TypedQuery<T> queryEntity =
                entityManager.createQuery(jpql, entityClass).setFlushMode(FlushModeType.COMMIT);
        for (final Entry<String, ? extends Object> entry : paramMap.entrySet()) {
            queryEntity.setParameter(entry.getKey(), entry.getValue());
        }
        return queryEntity.getResultList();
    }

    private String getCountJpql(String jpql) {
        StringBuilder stringBuilder = new StringBuilder();
        int from = jpql.toUpperCase().indexOf("FROM");
        stringBuilder.append("SELECT COUNT(*) ");
        stringBuilder.append(jpql.substring(from));
        return stringBuilder.toString();
    }


}

