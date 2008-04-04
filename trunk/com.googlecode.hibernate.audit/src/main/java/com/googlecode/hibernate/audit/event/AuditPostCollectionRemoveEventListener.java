package com.googlecode.hibernate.audit.event;

import org.apache.log4j.Logger;
import org.hibernate.StatelessSession;
import org.hibernate.event.PostCollectionRecreateEvent;
import org.hibernate.persister.entity.EntityPersister;

import com.googlecode.hibernate.audit.model.AuditOperation;

@SuppressWarnings("serial")
public class AuditPostCollectionRemoveEventListener extends
		AuditAbstractEventListener {
	private Logger LOG = Logger
			.getLogger(AuditPostCollectionRemoveEventListener.class);

	@Override
	protected StatelessSession openStatelessSession(Object object) {
		PostCollectionRecreateEvent event = (PostCollectionRecreateEvent) object;
		return event.getSession().getFactory().openStatelessSession(
				event.getSession().connection());

	}

	@Override
	protected Object getEntity(Object object) {
		PostCollectionRecreateEvent event = (PostCollectionRecreateEvent) object;
		return event.getCollection().getOwner();
	}

	@Override
	protected EntityPersister getEntityPersister(Object object) {
		PostCollectionRecreateEvent event = (PostCollectionRecreateEvent) object;
		Object entity = event.getCollection().getOwner();
		return event.getSession().getEntityPersister(
				event.getSession().bestGuessEntityName(entity), entity);
	}

	@Override
	protected AuditOperation getAuditEntityOperation(Object object) {
		return AuditOperation.UPDATE;
	}

	/*
	 * protected void doAuditEntityProperties(StatelessSession session, Object
	 * object, AuditTransaction auditTransaction, AuditObject auditEntity) {
	 * PostCollectionRecreateEvent event = (PostCollectionRecreateEvent) object;
	 * Object entity = getEntity(object); String entityName =
	 * entity.getClass().getName().toString(); EntityPersister persister =
	 * getEntityPersister(object); EntityMode entityMode =
	 * persister.guessEntityMode(entity);
	 * 
	 * CollectionPersister collectionPersister = event.getSession()
	 * .getPersistenceContext().getCollectionEntry(
	 * event.getCollection()).getCurrentPersister();
	 * 
	 * String role = collectionPersister.getCollectionMetadata().getRole();
	 * 
	 * String propertyName = role.substring(role.lastIndexOf('.') != -1 ? role
	 * .lastIndexOf('.') + 1 : 0, role.length());
	 * 
	 * Object propertyValue = persister.getPropertyValue(entity, propertyName,
	 * entityMode);
	 * 
	 * AuditEntityProperty auditEntityProperty = createAuditEntityProperty(
	 * session, auditEntity, entityName, propertyName, propertyValue == null ?
	 * AuditEntityPropertyOperation.SET_NULL_COLLECTION :
	 * AuditEntityPropertyOperation.SET_EMPTY_COLLECTION);
	 * 
	 * if (LOG.isDebugEnabled()) { LOG.debug("Add audit entity property with id " +
	 * auditEntityProperty.getId()); } }
	 */
}
