package com.amar.repository;

import com.amar.modal.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  DealRepository  extends JpaRepository<Deal,Long> {
    Long id(Long id);
}
