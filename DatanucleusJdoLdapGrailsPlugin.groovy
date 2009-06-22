class DatanucleusJdoLdapGrailsPlugin {
	// the plugin version
	def version = "0.1"
	// the version or versions of Grails the plugin is designed for
	def grailsVersion = "1.1.1 > *"
	// the other plugins this plugin depends on
	def dependsOn = [:]
	// resources that are excluded from plugin packaging
	def pluginExcludes = [
	"grails-app/views/error.gsp"
	]
	
	def author = "Stefan Seelmann"
	def authorEmail = "grails@stefan-seelmann.de"
	def title = "Grails DataNucleus JDO LDAP Plugin"
	def description = '''\\
JDO persistence to an LDAP datastore using DataNucleus.
'''
	
	// URL to the plugin's documentation
	def documentation = ""
	
	def doWithSpring = {
			
		log.info "Configuring JDO PersistenceManager"			
		persistenceManagerFactory(org.springframework.beans.factory.config.MethodInvokingFactoryBean) {
			targetClass = "javax.jdo.JDOHelper"
			targetMethod = "getPersistenceManagerFactory"
			arguments = ["jdo-ldap"]
		}
		persistenceManager(org.springframework.beans.factory.config.MethodInvokingFactoryBean) { bean ->
			bean.scope = "request"
			targetClass = "org.springframework.orm.jdo.PersistenceManagerFactoryUtils"
			targetMethod = "getPersistenceManager"
			arguments = [persistenceManagerFactory,false]				
		}
		transactionManager(org.springframework.orm.jdo.JdoTransactionManager) {
			persistenceManagerFactory = persistenceManagerFactory
		}
		transactionTemplate(org.springframework.transaction.support.TransactionTemplate) {
			transactionManager = transactionManager
		}
		jdoTemplate(org.springframework.orm.jdo.JdoTemplate) {
			persistenceManagerFactory = persistenceManagerFactory
		}
		
		if (manager?.hasGrailsPlugin("controllers")) {
			openPersistenceManagerInViewInterceptor(org.springframework.orm.jdo.support.OpenPersistenceManagerInViewInterceptor) {
				persistenceManagerFactory = persistenceManagerFactory
			}
			if(getSpringConfig().containsBean("grailsUrlHandlerMapping")) {                    
				grailsUrlHandlerMapping.interceptors << openPersistenceManagerInViewInterceptor
			}
		}
	}
	
	def doWithDynamicMethods = { ctx ->
		application.domainClasses.each { domainClass ->
	 		domainClass.metaClass.static.list = {-> 
 				def persistenceManager = ctx.getBean("persistenceManager")
				org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
 					return persistenceManager.newQuery( domainClass.clazz ).execute()
 				}
			}
 		} 
	}
	
}
