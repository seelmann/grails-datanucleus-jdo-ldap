
ant.mkdir(dir:"${basedir}/src/templates")
ant.copy(todir:"${basedir}/src/templates") {
	fileset(dir:"${datanucleusJdoLdapPluginDir}/src/templates")
}

ant.mkdir(dir:"${basedir}/grails-app/conf/META-INF")
ant.copy(todir:"${basedir}/grails-app/conf/META-INF", overwrite:false) {
	fileset(dir:"${datanucleusJdoLdapPluginDir}/src/META-INF")
}
