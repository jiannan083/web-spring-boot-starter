package cn.bfay.web.result;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 列表返回结果.
 *
 * @param <T> 泛型
 * @author wangjiannan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListResult<T> {
    /**
     * 数据项.
     */
    private List<T> items = new ArrayList<>();
}
