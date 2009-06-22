eventCompileEnd = { kind ->
	ant.taskdef(name:'datanucleusenhancer', classname:'org.datanucleus.enhancer.tools.EnhancerTask')
	ant.datanucleusenhancer(classpathref:"grails.runtime.classpath", persistenceUnit:"jdo-ldap")
}