package org.zerock.myapp.domain;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecycleListener;

import lombok.Data;

@Data
@EntityListeners(CommonEntityLifecycleListener.class)
@Entity(name = "Member")
@Table(name = "MEMBER")
public class Member implements Serializable{
	@Serial private static final long serialVersionUID = 1L;
	
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MEMBER_ID")
	private Long id;
	
	@Basic(optional = false)
	private String name;
	
	@ManyToOne(optional = true, targetEntity = Team.class)
	@JoinColumn(
			name = "my_team", 
			table = "MEMBER", 
			referencedColumnName = "team_id")
	private Team team;
	
} // end class
