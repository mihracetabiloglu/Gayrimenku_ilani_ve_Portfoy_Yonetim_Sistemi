package com.gayrimenkul.system.repository;

import com.gayrimenkul.system.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloorRepository extends JpaRepository<Floor, Long> {
    Optional<Floor> findByNameIgnoreCase(String name);
}
