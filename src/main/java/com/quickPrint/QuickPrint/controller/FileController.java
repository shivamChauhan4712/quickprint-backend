package com.quickPrint.QuickPrint.controller;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.quickPrint.QuickPrint.exception.MyFileNotFoundException;
import com.quickPrint.QuickPrint.modal.FileStatus;
import com.quickPrint.QuickPrint.modal.UploadedFile;
import com.quickPrint.QuickPrint.service.FileService;

@RestController
@RequestMapping("/api/file")
//@CrossOrigin("*")
public class FileController {
	@Autowired
	private FileService fileService;

	// customer use this after scanning QR code
	@PostMapping("/upload/{uniqueCode}")
	public ResponseEntity<String> uplodaFile(@RequestParam("file") MultipartFile file,
			@PathVariable String uniqueCode) {
		try {

			fileService.storeFile(file, uniqueCode);

			return ResponseEntity.ok("File Uploaded successfully!");
		} catch (Exception e) {

			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}

	// get list of files of your cafe
	@GetMapping("/list/{uniqueCode}")
	public ResponseEntity<List<UploadedFile>> getFilesForCafe(@PathVariable String uniqueCode) {
		List<UploadedFile> uploadedFiles = fileService.getFilesForCafe(uniqueCode);
		if (uploadedFiles.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(uploadedFiles);
	}

	@GetMapping("/download/{id}")
	public ResponseEntity<Resource> downloadFile(@PathVariable UUID id) throws MalformedURLException {
		// 1. for fetching file info from database
		UploadedFile fileInfo = fileService.findById(id)
				.orElseThrow(() -> new MyFileNotFoundException("File not found"));

		// 2. SECURITY CHECK: file status
		if (fileInfo.getStatus() == FileStatus.DELETED) {
			return ResponseEntity.status(HttpStatus.GONE).body(null); // 410 Gone
		}

		Resource resource = fileService.loadFileAsResource(fileInfo.getStoredFileName());

		// sending file download response
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileInfo.getFileType()))
				.header(HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"" + fileInfo.getOriginalFileName() + "\"")
				.body(resource);
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<String> updateFileStatus(@PathVariable UUID id, @RequestParam FileStatus status) {
		fileService.updateStatus(id, status);
		return ResponseEntity.ok("Status successfully patched to: " + status);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> manualDelete(@PathVariable UUID id) {
		fileService.deleteFileManually(id);
		return ResponseEntity.ok("File has been physically deleted and status updated to DELETED.");
	}

}