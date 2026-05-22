package br.edu.ifg.med_clinica_api.model.dao;

import br.edu.ifg.med_clinica_api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
}
