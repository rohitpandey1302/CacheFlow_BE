package com.rohitpandey.cacheflow.post_service.repositories;

import com.rohitpandey.cacheflow.post_service.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByOrderByIdAsc(Pageable pageable);

    @Query("SELECT p.id FROM Post p")
    List<Long> findAllIds();

}
