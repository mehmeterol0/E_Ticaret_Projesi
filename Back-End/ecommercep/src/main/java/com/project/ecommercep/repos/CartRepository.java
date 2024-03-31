package com.project.ecommercep.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.ecommercep.entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
