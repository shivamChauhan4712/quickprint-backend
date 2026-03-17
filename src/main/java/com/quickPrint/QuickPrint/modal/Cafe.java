package com.quickPrint.QuickPrint.modal;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cafe", indexes = {@Index(name = "idx_unique_code", columnList = "uniqueCode", unique = true)})
public class Cafe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotBlank(message = "Cafe name is required")
	private String cafeName;
	
	@Email(message = "Please provide a valid email address")
	@NotBlank(message = "Email is required")
	private String ownerEmail;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;
	
	@Column(unique = true, nullable = false, length = 50)
	private String uniqueCode; // QR code link
	
	@OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL)
	@JsonIgnore
	private List<UploadedFile> files;
	
	// this method runs every time automatically when a new cafe is registered
	@PrePersist
	public void generateUniqueCode() {
	    if (this.uniqueCode == null || this.uniqueCode.isEmpty()) {
	        // this removes the spaces from the cafe name and attach short UUID to it
	        String cleanName = this.cafeName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
	        this.uniqueCode = cleanName + "-" + UUID.randomUUID().toString().substring(0, 5);
	    }
	}
	
}
