package com.quickPrint.QuickPrint.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickPrint.QuickPrint.modal.FileStatus;
import com.quickPrint.QuickPrint.modal.UploadedFile;

@Repository
public interface FileRepository extends JpaRepository<UploadedFile, UUID> {
	//find By [Cafe] Entity's [UniqueCode] variable
    // OrderByUploadTimeDesc: to show new field at the top
	List<UploadedFile> findByCafeUniqueCodeOrderByUploadTimeDesc(String uniqueCode);
	
	// this search for those files which are old from threshold time and have status "PRINTED"
	List<UploadedFile> findAllByUploadTimeBeforeAndStatus(LocalDateTime threshold, FileStatus status);
}
