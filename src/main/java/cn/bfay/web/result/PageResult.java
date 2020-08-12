package cn.bfay.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回结果.
 *
 * @param <T> 泛型
 * @author wangjiannan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    /**
     * 页码
     */
    private int currentPage;

    /**
     * 页面记录数
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPage;

    /**
     * 总记录数
     */
    private long totalCount;

    /**
     * 分页数据
     */
    private List<T> list = new ArrayList<>();
}
