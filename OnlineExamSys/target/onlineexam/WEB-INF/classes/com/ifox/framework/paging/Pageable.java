package com.ifox.framework.paging;

/**
 * Created by Administrator on 2016/10/8.
 */

import org.apache.commons.lang.builder.EqualsBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.HashCodeBuilder;
import com.ifox.framework.filter.Filter;
import com.ifox.framework.ordering.Ordering;
import com.ifox.framework.ordering.Ordering.Direction;

public class Pageable implements Serializable {

    private static final long serialVersionUID = -3930180379790344299L;

    /**
     * 默认页码
     */
    private static final int DEFAULT_PAGE_NUMBER = 1;

    /**
     * 默认每页记录数
     */
    private static final int DEFAULT_PAGE_SIZE = 40;

    /**
     * 最大每页记录数
     */
    private static final int MAX_PAGE_SIZE = 1000;

    /**
     * 页码
     */
    private int page = DEFAULT_PAGE_NUMBER;

    /**
     * 每页记录数
     */
    private int rows = DEFAULT_PAGE_SIZE;

    /**
     * 搜索属性
     */
    private String searchProperty;

    /**
     * 搜索值
     */
    private String searchValue;

    /**
     * 排序属性
     */
    private String sort;

    /**
     * 排序方向
     */
    private Direction order;

    /**
     * 筛选
     */
    private List<Filter> filters = new ArrayList<Filter>();

    /**
     * 排序
     */
    private List<Ordering> orderings = new ArrayList<Ordering>();

    /**
     * 初始化一个新创建的Pageable对象
     */
    public Pageable() {
    }

    /**
     * 初始化一个新创建的Pageable对象
     *
     * @param page 页码
     * @param rows 每页记录数
     */
    public Pageable(Integer page, Integer rows) {
        if (page != null && page >= 1) {
            this.page = page;
        }
        if (rows != null && rows >= 1 && rows <= MAX_PAGE_SIZE) {
            this.rows = rows;
        }
    }

    /**
     * 获取页码
     *
     * @return 页码
     */
    public int getPage() {
        return page;
    }

    /**
     * 设置页码
     *
     * @param
     *
     */
    public void setPage(int page) {
        if (page < 1) {
            page = DEFAULT_PAGE_NUMBER;
        }
        this.page = page;
    }

    /**
     * 获取每页记录数
     *
     * @return 每页记录数
     */
    public int getRows() {
        return rows;
    }

    /**
     * 设置每页记录数
     *
     * @param rows 每页记录数
     */
    public void setRows(int rows) {
        if (rows < 1 || rows > MAX_PAGE_SIZE) {
            rows = DEFAULT_PAGE_SIZE;
        }
        this.rows = rows;
    }

    /**
     * 获取搜索属性
     *
     * @return 搜索属性
     */
    public String getSearchProperty() {
        return searchProperty;
    }

    /**
     * 设置搜索属性
     *
     * @param searchProperty 搜索属性
     */
    public void setSearchProperty(String searchProperty) {
        this.searchProperty = searchProperty;
    }

    /**
     * 获取搜索值
     *
     * @return 搜索值
     */
    public String getSearchValue() {
        return searchValue;
    }

    /**
     * 设置搜索值
     *
     * @param searchValue 搜索值
     */
    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    /**
     * 获取排序属性
     *
     * @return 排序属性
     */
    public String getSort() {
        return sort;
    }

    /**
     * 设置排序属性
     *
     * @paramorderProperty 排序属性
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * 获取排序方向
     *
     * @return 排序方向
     */
    public Direction getOrder() {
        return order;
    }

    /**
     * 设置排序方向
     *
     * @paramorderDirection 排序方向
     */
    public void setOrder(Direction order) {
        this.order = order;
    }

    /**
     * 获取筛选
     *
     * @return 筛选
     */
    public List<Filter> getFilters() {
        return filters;
    }

    /**
     * 设置筛选
     *
     * @param filters 筛选
     */
    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public List<Ordering> getOrderings() {
        return orderings;
    }

    /**
     * 设置排序
     *
     * @paramorders 排序
     */
    public void setOrders(List<Ordering> orderings) {
        this.orderings = orderings;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        Pageable other = (Pageable) obj;
        return new EqualsBuilder().append(getPage(), other.getPage())
                .append(getRows(), other.getRows())
                .append(getSearchProperty(), other.getSearchProperty())
                .append(getSearchValue(), other.getSearchValue())
                .append(getSort(), other.getSort())
                .append(getOrder(), other.getOrder())
                .append(getFilters(), other.getFilters()).append(getOrderings(), other.getOrderings())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getPage()).append(getRows())
                .append(getSearchProperty()).append(getSearchValue()).append(getSort())
                .append(getOrder()).append(getFilters()).append(getOrderings()).toHashCode();
    }

}

