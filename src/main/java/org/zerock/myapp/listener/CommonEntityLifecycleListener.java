package org.zerock.myapp.listener;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
public class CommonEntityLifecycleListener {

	@PostLoad
	void postLoad(Object entity) {
		log.trace("1. postLoad({}) invoked.", entity);
	} // postLoad

	@PrePersist
	void prePersist(Object entity) {
		log.trace("2. prePersist({}) invoked.", entity);
	} // prePersist

	
	@PostPersist
	void postPersist(Object entity) {
		log.trace("3. postPersist({}) invoked.", entity);

	} // postPersist

	@PreUpdate
	void preUpdate(Object entity) {
		log.trace("4. preUpdate({}) invoked.", entity);

	} // preUpdate

	@PostUpdate
	void postUpdate(Object entity) {
		log.trace("5. postUpdate({}) invoked.", entity);

	} // postUpdate

	@PreRemove
	void preRemove(Object entity) {
		log.trace("6. preRemove({}) invoked.", entity);

	} // preRemove

	@PostRemove
	void postRemove(Object entity) {
		log.trace("7. postRemove({}) invoked.", entity);

	} // postRemove
} // end class
