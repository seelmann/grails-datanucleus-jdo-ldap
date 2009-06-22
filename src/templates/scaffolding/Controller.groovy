<%=packageName ? "package ${packageName}\n\n" : ''%>import javax.jdo.metadata.*
class ${className}Controller {

	def persistenceManagerFactory
	def persistenceManager
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    static allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        params.max = Math.min( params.max ? params.max.toInteger() : 10,  100)
		def ${propertyName}List = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
			return persistenceManager.newQuery( ${className} ).execute()
		}
		def total = 0
		if(  ${propertyName}List &&  ${propertyName}List.size() > 0){
			total =  ${propertyName}List.size()
		}
		[  ${propertyName}List :  ${propertyName}List,  ${propertyName}Total: total ]
    }

    def show = {
		def ${propertyName} = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
			return persistenceManager.getObjectById( ${className}.class, params.id )
		}
        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else { return [ ${propertyName} : ${propertyName} ] }
    }

    def delete = {
		def ${propertyName} = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
			return persistenceManager.getObjectById( ${className}.class, params.id )
		}
        if(${propertyName}) {
            try {
            	org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
            		persistenceManager.deletePersistent(${propertyName})
        		}
                flash.message = "${className} \${params.id} deleted"
                redirect(action:list)
            }
            catch(Exception e) {
                flash.message = "${className} \${params.id} could not be deleted"
                redirect(action:show,id:params.id)
            }
        }
        else {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
		def ${propertyName} = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
			return persistenceManager.getObjectById( ${className}.class, params.id )
		}
		if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
        	return [ ${propertyName} : ${propertyName} ]
        }
    }

    def update = {
		def ${propertyName} = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
			return persistenceManager.getObjectById( ${className}.class, params.id )
		}
    	if(${propertyName}) {
            ${propertyName}.properties = params
            merge(${propertyName}, params)
            if(!${propertyName}.hasErrors()){
				try {
					org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
						persistenceManager.makePersistent(${propertyName})
					}
					flash.message = "${className} \${params.id} updated"
	                redirect(action:show,id:${propertyName}.id)
				} catch( Exception e ){
					${propertyName}.errors.reject('', e.getMessage())
				   	render(view:'edit',model:[${propertyName}:${propertyName}])
				}       
 			}
            else {
                render(view:'edit',model:[${propertyName}:${propertyName}])
            }
        }
        else {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
    }

    def create = {
        def ${propertyName} = new ${className}()
        ${propertyName}.properties = params
        return ['${propertyName}':${propertyName}]
    }

    def save = {
        def ${propertyName} = new ${className}(params)
		${propertyName}.id = params.id
		merge(${propertyName}, params)
		if(!${propertyName}.hasErrors() ) {
			try {
				org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
					persistenceManager.makePersistent(${propertyName})
				}
				flash.message = "${className} \${${propertyName}.id} created"
				redirect(action:show,id:${propertyName}.id)	
			} catch (Exception e) {
				${propertyName}.errors.reject('', e.getMessage())
				render(view:'create',model:[${propertyName}:${propertyName}])
			}
		}
		else{
			render(view:'create',model:[${propertyName}:${propertyName}])
		}
    }
	
	def merge = { instance, params ->
		def classMetadata = persistenceManagerFactory.getMetadata(instance.class.getName())
		def memberMetadatas = classMetadata.getMembers()
	    for(mmd in memberMetadatas)
		{
	    	def collectionMetadata = mmd.getCollectionMetadata()
			def type = collectionMetadata ? collectionMetadata.getElementType() : mmd.getFieldType()
			def fieldClassMetadata = persistenceManagerFactory.getMetadata(type)
			if(fieldClassMetadata)
			{
	        	if(collectionMetadata)
				{
					def collection = instance.properties[mmd.getName()]
					collection.clear();
					
	        		Class clazz = grailsApplication.getArtefactInfo("Domain").getGrailsClass(type).clazz
					def values = params[mmd.getName()]
					if(values instanceof String)
					{
						def obj = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
							def obj =  persistenceManager.getObjectById( clazz, values )
							collection.add(obj)
						}
					}
					else
					{
						def obj = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
							values.each { 
								def obj = persistenceManager.getObjectById( clazz, it )
								collection.add(obj)
			        		}
						}
					}
				}
	        	else
	        	{
					def reference = instance.properties[mmd.getName()]
					if(reference)
					{
						def value = org.grails.plugins.jdo.JdoUtils.runInTransaction(persistenceManager) {
							return persistenceManager.getObjectById( reference.class, reference.id )
						}
						instance.properties[mmd.getName()] = value
					}
	        	}
			}
		}
	}
}
