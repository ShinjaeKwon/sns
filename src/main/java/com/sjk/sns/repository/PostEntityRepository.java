package com.sjk.sns.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjk.sns.model.entity.PostEntity;
import com.sjk.sns.model.entity.UserEntity;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

	Page<PostEntity> findAllByUser(UserEntity entity, Pageable pageable);
}
