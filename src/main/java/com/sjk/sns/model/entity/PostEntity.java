package com.sjk.sns.model.entity;

import java.sql.Timestamp;
import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "\"post\"")
@SQLDelete(sql = "UPDATE \"post\" SET deleted_at = NOW() where id =?")
@Where(clause = "deleted_at is NULL")
@Entity
public class PostEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "title")
	private String title;

	@Column(name = "body", columnDefinition = "TEXT")
	private String body;

	@JoinColumn(name = "user_id")
	@ManyToOne
	private UserEntity user;

	@Column(name = "registered_at")
	private Timestamp registeredAt;

	@Column(name = "updated_at")
	private Timestamp updatedAt;

	@Column(name = "deleted_at")
	private Timestamp deletedAt;

	@PrePersist
	void registeredAt() {
		this.registeredAt = Timestamp.from(Instant.now());
	}

	@PreUpdate
	void updatedAt() {
		this.updatedAt = Timestamp.from(Instant.now());
	}

	public static PostEntity of(String title, String body, UserEntity userEntity) {
		PostEntity entity = new PostEntity();
		entity.setTitle(title);
		entity.setBody(body);
		entity.setUser(userEntity);
		return entity;
	}

}
