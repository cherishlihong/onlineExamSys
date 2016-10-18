package com.ifox.framework.paging;

/**
 * Created by Administrator on 2016/10/8.
 */

import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

    private static final long serialVersionUID = -2053800594583879853L;

    /**
     * 内容
     */
    private final List<T> rows = new ArrayList<T>();

    /**
     * 总记录数
     */
    private final long total;


    /**
     * 初始化一个新创建的Page对象
     */
    public Page() {
        this.total = 0L;
    }

    /**
     * @param rows     内容
     * @param total    总记录数
     * @param pageable 分页信息
     */
    public Page(List<T> rows, long total, Pageable pageable) {
        this.rows.addAll(rows);
        this.total = total;
    }


    /**
     * 获取内容
     *
     * @return 内容
     */
    public List<T> getRows() {
        return rows;
    }

    /**
     * 获取总记录数
     *
     * @return 总记录数
     */
    public long getTotal() {
        return total;
    }


}
