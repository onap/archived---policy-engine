package org.openecomp.policy.utils;

import java.lang.reflect.Method;
import java.util.Properties;

public interface AAFPolicyClient {
	/*
	 * Enumeration for the Resource Node Naming. Add here if required. 
	 */
	String AAF_DEFAULT_CLASS = "org.openecomp.policy.utils.AAFPolicyClientImpl";
	public enum Environment{
		DEVL,
		TEST,
		PROD
	}
	public boolean checkAuth(String userName, String pass);
	public void updateProperties(Properties properties) throws AAFPolicyException;
	public boolean checkAuthPerm(String mechID, String pass, String type, String instance, String action);
	public boolean checkPerm(String userName, String pass, String type, String instance, String action);
	public static AAFPolicyClient getInstance(Properties properties) throws AAFPolicyException{
		try {
			Class<?> aafPolicyClient = Class.forName(properties.getProperty("aafClient.impl.className", AAF_DEFAULT_CLASS));
			Method method =  aafPolicyClient.getMethod("getInstance", Properties.class);
			AAFPolicyClient instance = (AAFPolicyClient) method.invoke(null, properties);
			return instance;
		} catch (Exception e) {
			throw new AAFPolicyException(e);
		}
	}
}
