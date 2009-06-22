@artifact.package@

import javax.jdo.annotations.*;

@PersistenceCapable(table="ou=@artifact.name@s,dc=example,dc=com", schema="top,person,organizationalPerson,inetOrgPerson", detachable="true")
class @artifact.name@ implements Serializable {
	
	@PrimaryKey
	@Column(name="cn")
	String id
	
	@Persistent
	@Column(name="sn")
	String lastName
	
	@Persistent
	@Column(name="givenName")
	String firstName
	
	static constraints = {
	}
}
