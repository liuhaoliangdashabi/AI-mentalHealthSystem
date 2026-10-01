package org.example.aispringboot.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@Table("sys_file_info")
@NoArgsConstructor
@AllArgsConstructor
public class SysFileInfo {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("original_name")
    //存疑——这里我要限定长度吗？但这不是UUID生成的吗？
    private String originalName;

    @TableField("file_path")
    //存疑——这里我要限定长度吗？不然有人搓个雷霆大长串怎办
    private String filePath;

    @TableField("file_size")
    private Integer fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("business_type")
    private String businessType;

    @TableField("business_id")
    private String businessId;

    @TableField("business_field")
    //这是随机生成？怎么又有数字，又有UUID，还有null
    private String businessField;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("is_temp")
    private Integer isTemp;

    @TableField("status")
    private Integer status;

    @TableField("create_time")
    private LocalDateTime createTime;
    @TableField("expire_time")
    private LocalDateTime expireTime;
}
