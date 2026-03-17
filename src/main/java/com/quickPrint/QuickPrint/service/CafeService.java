package com.quickPrint.QuickPrint.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.quickPrint.QuickPrint.exception.CafeAlreadyExistsException;
import com.quickPrint.QuickPrint.modal.Cafe;
import com.quickPrint.QuickPrint.repository.CafeRepository;

@Service
public class CafeService {
	@Autowired
	private CafeRepository cafeRepo;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public Cafe loginCafe(String email, String rawPassword) {
	    // 1. Email se cafe dhoondo
	    Cafe cafe = cafeRepo.findByOwnerEmail(email)
	            .orElseThrow(() -> new RuntimeException("Cafe not found with this email!"));

	    // 2. Password match karo (Matches method hashed password ko check karta hai)
	    if (!passwordEncoder.matches(rawPassword, cafe.getPassword())) {
	        throw new RuntimeException("Invalid Password! Please try again.");
	    }else {
	    	System.out.println("login successful");
	    }

	    return cafe; // Login success
	}
	public Cafe saveCafe(Cafe cafe) {
		
		if(cafeRepo.existsByOwnerEmail(cafe.getOwnerEmail())) {
			throw new CafeAlreadyExistsException("Cafe with this email already exists!");
		}
		// it encode the plain password by making its hash
		String encodedPassword = passwordEncoder.encode(cafe.getPassword());
		cafe.setPassword(encodedPassword);
		return cafeRepo.save(cafe);
	}
	public Optional<Cafe> getCafeByUnicode(String unicode) {
		return cafeRepo.findByUniqueCode(unicode);
	}
	public List<Cafe> getAllCafes() {
		return cafeRepo.findAll();
	}
}