package com.app.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "builds")
public class Builds {
	
	@Id
	@Column(name="build_id")
	public long buildId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private Users user;
	
	@Column(name = "build_status")
	public buildStatusEnum finalBuildStatus;
	
	@Column(nullable = false)
	private LocalDateTime timestamp = LocalDateTime.now();

	@Column(nullable = false)
	private String docekerUser;
	
}
