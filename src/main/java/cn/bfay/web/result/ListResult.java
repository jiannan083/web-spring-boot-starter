package cn.bfay.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private List<T> list = new ArrayList<>();
}
