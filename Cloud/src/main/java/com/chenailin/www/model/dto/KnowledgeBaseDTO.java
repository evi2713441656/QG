package com.chenailin.www.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 知识库数据传输对象
 * @author evi
 */
public class KnowledgeBaseDTO {
    private Long id;

    @NotBlank(message = "知识库名称不能为空")
    @Size(min = 2, max = 50, message = "名称长度必须在2-50个字符之间")
    private String name;

    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;

    private String coverUrl;
    private Boolean isPublic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}