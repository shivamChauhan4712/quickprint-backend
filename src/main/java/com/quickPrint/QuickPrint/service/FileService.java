package com.quickPrint.QuickPrint.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.quickPrint.QuickPrint.exception.FileSizeExceededException;
import com.quickPrint.QuickPrint.exception.FileStorageException;
import com.quickPrint.QuickPrint.exception.InvalidFileTypeException;
import com.quickPrint.QuickPrint.exception.MyFileNotFoundException;
import com.quickPrint.QuickPrint.modal.Cafe;
import com.quickPrint.QuickPrint.modal.FileStatus;
import com.quickPrint.QuickPrint.modal.UploadedFile;
import com.quickPrint.QuickPrint.repository.CafeRepository;
import com.quickPrint.QuickPrint.repository.FileRepository;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileService {
	@Autowired
	private FileRepository fileRepo;
	@Autowired
	private CafeRepository cafeRepo;
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	// folder to save files
	private final Path root = Paths.get("uploads");

	@PostConstruct
	public void init() {
		try {
			// when project starts it checks for 'uploads' folder, if not available then
			// create
			if (!Files.exists(root)) {
				Files.createDirectory(root);
			}
		} catch (IOException e) {
			throw new RuntimeException("Could not initialize folder for upload!");
		}
	}

	// to store file in a uploads folder and other file info to database
	public String storeFile(MultipartFile file, String cafeCode) throws IOException {
		// 1. check cafe
		Cafe cafe = cafeRepo.findByUniqueCode(cafeCode).orElseThrow(() -> new RuntimeException("Cafe not found"));
		
		if (file.isEmpty()) {
	        throw new FileStorageException("Cannot store an empty file.");
	    }
		// to check file content-type
		if(!isAllowedType(file.getContentType())) {
			throw new InvalidFileTypeException("Invalid file type! Only PDF, Images, Text, Word, Excel aur PPT allowed.");
		}
		// to check file size
		if(file.getSize()>20*1024*1024) {
			throw new FileSizeExceededException("File size exceeds the limit. Maximum allowed size is 20MB.");
		}
		
		// 2. make unique file name
		String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
		Path targetPath = root.resolve(fileName);

		// 3. save physical file
		Files.createDirectories(targetPath.getParent());
		Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

		// 4. save record in the database
		UploadedFile upload = new UploadedFile();
		upload.setOriginalFileName(file.getOriginalFilename());
		upload.setStoredFileName(fileName);
		upload.setStatus(FileStatus.PENDING); // default status-> PENDING
		upload.setCafe(cafe);
		upload.setFileType(file.getContentType());
		upload.setFileSize(file.getSize());

		UploadedFile savedFile = fileRepo.save(upload);

		// 5. notify cafe owner using webSocket
//		messagingTemplate.convertAndSend("/topic/cafe/" + cafeCode, "New File: " + file.getOriginalFilename());
		messagingTemplate.convertAndSend("/topic/cafe/" + cafeCode, savedFile);

		return fileName;
	}

	// get list of files of your cafe
	public List<UploadedFile> getFilesForCafe(String cafeCode) {
		return fileRepo.findByCafeUniqueCodeOrderByUploadTimeDesc(cafeCode);
	}

	public Optional<UploadedFile> findById(UUID id) {
		return fileRepo.findById(id);
	}

	public void deleteFileManually(UUID id) {
		// fetching file details from database
		UploadedFile file = fileRepo.findById(id)
				.orElseThrow(() -> new MyFileNotFoundException("File not found with id: " + id));

		try {
			// deleting physical file
			Path filePath = this.root.resolve(file.getStoredFileName());
			Files.deleteIfExists(filePath);

			// updating the status to DELETED and save the file
			file.setStatus(FileStatus.DELETED);
			fileRepo.save(file);

			System.out.println("Manual Delete Success: File removed from folder, marked DELETED in DB.");
		} catch (IOException e) {
			throw new RuntimeException("Could not delete physical file: " + e.getMessage());
		}
	}

	public void updateStatus(UUID id, FileStatus status) {
		UploadedFile file = fileRepo.findById(id).orElseThrow(() -> new RuntimeException("File not found"));
		// validation: to check the status of deleted file could not be changed
		if (file.getStatus() == FileStatus.DELETED) {
			throw new RuntimeException("Cannot change status! This file is already physically DELETED.");
		}
		file.setStatus(status);
		fileRepo.save(file);
	}

	@Scheduled(fixedRate = 3600000) // runs automatically in every 1 hr
	public void autoDeletePrintedFiles() {
		// threshold of 2hr
		LocalDateTime threshold = LocalDateTime.now().minusHours(2);

		// fetch all the PRINTED files which are older than threshold
		List<UploadedFile> oldPrintedFiles = fileRepo.findAllByUploadTimeBeforeAndStatus(threshold, FileStatus.PRINTED);

		for (UploadedFile file : oldPrintedFiles) {
			try {
				// delete file physically
				Files.deleteIfExists(this.root.resolve(file.getStoredFileName()));
				// setting status to deleted
				file.setStatus(FileStatus.DELETED);
				fileRepo.save(file);
				System.out.println("Auto-deleted printed file: " + file.getOriginalFileName());
			} catch (IOException e) {
				System.err.println("Failed to auto-delete file: " + file.getStoredFileName());
			}
		}
	}

	// to check valid file types
	private boolean isAllowedType(String contentType) {
		List<String> allowedMimeTypes = Arrays.asList(
				
				//Images
				"image/jpeg", "image/png", "image/webp","image/avif", // Images
				
				// PDF and text files
				"application/pdf",
				"text/plain",
				"application/rtf",
				"text/markdown",
				
				// Microsoft Word
				"application/vnd.openxmlformats-officedocument.wordprocessingml.document", // Word (.docx)
				"application/msword", // Word (.doc)
				
				// Microsoft Excel
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // Excel (.xlsx)
				"application/vnd.ms-excel", // Excel (.xls)
				"text/csv",
				
				// Microsoft PowerPoint
				"application/vnd.openxmlformats-officedocument.presentationml.presentation", // PPT (.pptx)
				"application/vnd.ms-powerpoint" // PPT (.ppt)
		);
		return contentType!=null && allowedMimeTypes.contains(contentType);
	}
	
	public Resource loadFileAsResource(String fileName) {
	    try {
	        // 1. normalizing and resolving the path (important for Security)
	        Path filePath = this.root.resolve(fileName).normalize();
	        
	        // 2. making object of Resource 
	        Resource resource = new UrlResource(filePath.toUri());
	        
	        // 3. Check if file exist in the folder or not
	        if (resource.exists() && resource.isReadable()) {
	            return resource;
	        } else {
	            // if it is not present in physical storage (Scheduler deleted it)
	            throw new MyFileNotFoundException("File not found or not readable: " + fileName);
	        }
	    } catch (MalformedURLException ex) {
	        // if any error in making URL 
	        throw new MyFileNotFoundException("Could not determine file path for: " + fileName);
	    }
	}

	@Transactional 
	public void deleteFilesBulk(List<UUID> ids) {
	    // 1. fetching data if all file
	    List<UploadedFile> files = fileRepo.findAllById(ids);

	    for (UploadedFile file : files) {
	        try {
	            // 2. Physical file delete 
	            Path filePath = this.root.resolve(file.getStoredFileName());
	            Files.deleteIfExists(filePath);

	            // 3. Status update 
	            file.setStatus(FileStatus.DELETED);
	        } catch (IOException e) {
	            System.err.println("Could not delete file: " + file.getOriginalFileName());
	        }
	    }

	    // 4. saving all files (Efficiency!)
	    fileRepo.saveAll(files);
	    System.out.println("Bulk Delete Success: " + ids.size() + " files processed.");
	}
}
