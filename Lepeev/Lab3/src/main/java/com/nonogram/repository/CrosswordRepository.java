package com.nonogram.repository;

import com.nonogram.entity.Crossword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrosswordRepository extends JpaRepository<Crossword, Long> {
}