package io.github.cyronlee.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseModel {

    @Column(updatable = false)
    private String createdBy;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String deletedBy;
    private LocalDateTime deletedAt;

    public void setCreateInfo(String createdBy, LocalDateTime createdAt) {
        if (this.createdBy == null) this.createdBy = createdBy;
        if (this.createdAt == null) this.createdAt = createdAt;
        if (this.updatedBy == null) this.updatedBy = createdBy;
        if (this.updatedAt == null) this.updatedAt = createdAt;
    }

    public void setUpdateInfo(String updatedBy, LocalDateTime updatedAt) {
        this.updatedBy = createdBy;
        this.updatedAt = createdAt;
    }
}
