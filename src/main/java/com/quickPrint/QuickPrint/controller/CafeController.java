package com.quickPrint.QuickPrint.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quickPrint.QuickPrint.config.JwtUtils;
import com.quickPrint.QuickPrint.modal.Cafe;
import com.quickPrint.QuickPrint.service.CafeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@RestController
//@CrossOrigin("*")
@RequestMapping("/api/cafes")
public class CafeController {

	@Autowired
	private CafeService cafeService;
	@Autowired
	private JwtUtils jwtUtils;

	@PostMapping("/register")
	public ResponseEntity<Cafe> registerCafe(@Valid @RequestBody Cafe cafe) {
		Cafe saveCafe = cafeService.saveCafe(cafe);

		return ResponseEntity.ok(saveCafe);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
	    String email = credentials.get("email");
	    String password = credentials.get("password");

	    // 1. Password check (CafeService se)
	    Cafe cafe = cafeService.loginCafe(email, password);

	    // 2. Token generate karein
	    String token = jwtUtils.generateToken(email);

	    // 3. Response mein token aur cafe ki details bhejien
	    Map<String, Object> response = new HashMap<>();
	    response.put("token", token);
	    response.put("uniqueCode", cafe.getUniqueCode());
	    response.put("cafeName", cafe.getCafeName());

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/{uniqueCode}")
	public ResponseEntity<Cafe> getCafeByUnicode(@PathVariable @Size(min = 5, max = 50) String uniqueCode){
		return cafeService.getCafeByUnicode(uniqueCode)
				.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}
	@GetMapping("/allCafes")
	public ResponseEntity<List<Cafe>> getAllCafes(){
		List<Cafe> cafes = cafeService.getAllCafes();
		
		if(cafes.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		
		return ResponseEntity.ok(cafes);
	}

}