package cn.kmbeast.pojo.dto.query.extend;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class HealthModelConfigQueryDto extends QueryDto {

    /**
     * model ID
     */
    private Integer id;

    private Integer visibleTo;

    /**
     * user ID
     */
    private Integer userId;

    /**
     * global variable
     */
    private Boolean isGlobal;

    /**
     * configuration list
     */
    private String name;
    /**
     * configuration overview
     */
    private String detail;
    /**
     * model allocation unit
     */
    private String unit;
    /**
     * model configuration symbol
     */
    private String symbol;

}
