package com.fanflow.domain.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

	Optional<Channel> findBySlug(String slug);

	boolean existsBySlug(String slug);

	List<Channel> findByActiveTrueOrderByNameAsc();

	List<Channel> findAllByOrderByCreatedAtDesc();
}