package io.urdego.urdego_content_service.domain.entity.repository;

import io.urdego.urdego_content_service.domain.entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentRepository extends JpaRepository<Content, Long>, ContentRepositoryCustom {

}