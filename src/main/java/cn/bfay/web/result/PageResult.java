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
    //@ApiModelProperty(value = "页码")
    private int currentPage;

    //@ApiModelProperty(value = "页面记录数")
    private int pageSize;

    //@ApiModelProperty(value = "总页数")
    private int totalPage;

    //@ApiModelProperty(value = "总记录数")
    private long totalCount;

    //@ApiModelProperty(value = "分页数据")
    private List<T> list = new ArrayList<>();
}
