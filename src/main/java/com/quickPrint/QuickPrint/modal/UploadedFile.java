package com.quickPrint.QuickPrint.modal;


import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize; // Size in bytes
    private LocalDateTime uploadTime;
    @Enumerated(EnumType.STRING)
    private FileStatus status;
    
    @ManyToOne
    @JoinColumn(name = "cafe_id")
    @JsonIgnoreProperties("files")
    private Cafe cafe;
    
    // this method runs every time automatically when a new file is uploaded
    @PrePersist
    protected void onCreate() {
        this.uploadTime = LocalDateTime.now();
    }
}