package com.sjk.sns.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sjk.sns.exception.model.entity.PostEntity;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {
}