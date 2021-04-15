package com.shangma.cn.domin.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 开发者：辉哥
 * 特点： 辉哥很帅
 * 开发时间：2021/4/14 16:46
 * 文件说明：
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 管理员id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    /**
     * 创建者
     */

    private Long createBy;

    /**
     * 创建时间
     */

    private LocalDateTime createTime;

    /**
     * 修改者
     */

    private Long updateBy;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;


    public void setData() {
        if (id == null) {
            //添加功能
            this.createBy = 1L;
            this.createTime = LocalDateTime.now();
        } else {
            this.updateBy = 2L;
            this.updateTime = LocalDateTime.now();
        }
    }


}
