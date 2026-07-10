package com.fanflow.domain.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	Optional<User> findByNickname(String nickname);

	boolean existsByEmail(String email);

	boolean existsByNickname(String nickname);

	@Query("""
			SELECT u
			FROM User u
			WHERE (:status IS NULL OR u.status = :status)
			  AND (
			        :keyword IS NULL
			        OR u.email LIKE CONCAT('%', :keyword, '%')
			        OR u.nickname LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	Page<User> searchAdminUsers(@Param("status") UserStatus status, @Param("keyword") String keyword, Pageable pageable);
}