package com.ambacam.repository;

import javax.inject.Named;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ambacam.model.Role;

@Named
public interface RoleRepository extends JpaRepository<Role, Long> {

	int countByNom(String nom);

}
