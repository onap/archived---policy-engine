package org.onap.policy.xacml.api.pap;

import java.util.Properties;

import com.att.research.xacml.api.pap.PAPException;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacml.util.FactoryFinder;

public abstract class ONAPPapEngineFactory{
	
	/**
	 * Creates a new <code>PAPEngineFactory</code> instance using the given class name and the default thread class loader.
	 * 
	 * @param factoryClassName the <code>String</code> name of the factory class to instantiate
	 * @return an instance of an object that extends <code>ONAPPapEngineFactory</code> to use in creating <code>PAPPolicyEngine</code> objects.
	 */
	public static ONAPPapEngineFactory newInstance(String factoryClassName) throws FactoryException {
		return FactoryFinder.newInstance(factoryClassName, ONAPPapEngineFactory.class, null, true);
	}

	/**
	 * Creates a new <code>PAPPolicyEngine</code> based on the configured <code>ONAPPapEngineFactory</code>.
	 * 
	 * @return a new <code>PAPPolicyEngine</code>
	 * @throws PAPException 
	 */
	public abstract PAPPolicyEngine newEngine() throws FactoryException, PAPException;

	/**
	 * Creates a new <code>PAPPolicyEngine</code> based on the configured <code>ONAPPapEngineFactory</code>.
	 * 
	 * @return a new <code>PAPPolicyEngine</code>
	 * @throws PAPException 
	 */
	public abstract PAPPolicyEngine newEngine(Properties properties) throws FactoryException, PAPException;


}
