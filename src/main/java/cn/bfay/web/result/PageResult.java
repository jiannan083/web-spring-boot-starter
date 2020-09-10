package cn.bfay.web.result;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

/**
 * 分页返回结果.
 *
 * @author wangjiannan
 * @since 2020/7/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<E> {
    /**
     * 页码.
     */
    private long currentPage;
    /**
     * 页面记录数.
     */
    private long pageSize;

    /**
     * 总页数.
     */
    private long totalPage;

    /**
     * 总记录数.
     */
    private long totalCount;

    /**
     * 分页数据.
     */
    private List<E> items = new ArrayList<>();

    /**
     * PageResult.
     *
     * @param currentPage 页码
     * @param pageSize    页面记录数
     * @param totalPage   总页数
     * @param totalCount  总记录数
     * @param records     分页数据
     * @param clz         结果数据类型
     * @param <T>         参数数据类型
     */
    public <T> PageResult(long currentPage, long pageSize, long totalPage,
                          long totalCount, List<T> records, Class<E> clz) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalPage = totalPage;
        this.totalCount = totalCount;

        // List<T> records = iPage.getRecords();
        List<E> result = new ArrayList<>(records.size());
        for (T t : records) {
            E e;
            try {
                e = clz.newInstance();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            BeanUtils.copyProperties(t, e);
            result.add(e);
        }
        this.items = result;
    }
}
