package com.quickPrint.QuickPrint.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quickPrint.QuickPrint.modal.Cafe;

@Repository
public interface CafeRepository extends JpaRepository<Cafe, Long>{
	Optional<Cafe> findByUniqueCode(String uniqueCode);
	boolean existsByOwnerEmail(String ownerEmail);
	Optional<Cafe> findByOwnerEmail(String email);
}
