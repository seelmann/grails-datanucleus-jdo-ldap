/**
 * 
 */
package org.grails.plugins.jdo

import javax.jdo.annotations.*;
import javax.jdo.annotations.PersistenceCapable

/**
 * @author Stefan Seelmann
 *
 */
public class JdoUtils{
	
	public static boolean isRelationshipOneSide(prop) {
		def name = prop.name
		def field = prop.domainClass.clazz.getDeclaredField(name)
		def isFieldPersistent = field.isAnnotationPresent(Persistent.class)
		def isFieldTypePersistenceCapable = field.type.isAnnotationPresent(PersistenceCapable.class)
		def isRef = isFieldPersistent && isFieldTypePersistenceCapable
		return isRef;
	}
	public static boolean isRelationshipManySide(prop) {
		def name = prop.name
		def field = prop.domainClass.clazz.getDeclaredField(name)
		def isFieldPersistent = field.isAnnotationPresent(Persistent.class)
		def isFieldTypeCollection = Collection.class.isAssignableFrom(field.type)
		def isRef = isFieldPersistent && isFieldTypeCollection
		return isRef;
	}
	public static Class getReferencedDomainClass(prop) {
		def name = prop.name
		def field = prop.domainClass.clazz.getDeclaredField(name)
		def isFieldPersistent = field.isAnnotationPresent(Persistent.class)
		def isFieldTypePersistenceCapable = field.type.isAnnotationPresent(PersistenceCapable.class)
		def isFieldTypeCollection = Collection.class.isAssignableFrom(field.type)
		if(isFieldTypePersistenceCapable)
		{
			return field.type
		}
		else if(isFieldTypeCollection)
		{
			// TODO: non-parameterized collection
			if(field.genericType)
			{
				return field.genericType.actualTypeArguments[0]
			}
		}
		return null;
	}
	public static Object runInTransaction(persistenceManager, Closure worker) {
		def tx = persistenceManager.currentTransaction()
		try {
			tx.begin()
			def result = worker()
			tx.commit()
			return result
		}
		finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}
	}
}
