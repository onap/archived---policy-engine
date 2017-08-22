package org.onap.policyengine;

import java.io.File;
import java.util.UUID;

import org.onap.policy.api.ImportParameters;
import org.onap.policy.api.PolicyChangeResponse;
import org.onap.policy.api.PolicyEngine;
import org.onap.policy.api.ImportParameters.IMPORT_TYPE;

public class ImportBRMSTemplate {
	public static void main(String[] args) {
		try{	
	        PolicyEngine policyEngine = new PolicyEngine("config.properties");
	        ImportParameters importParameters = new ImportParameters();
	        importParameters.setFilePath("testResources"+File.separator+"test.drl");
	        importParameters.setServiceName("testTemplate");
	        importParameters.setRequestID(UUID.randomUUID());
	        importParameters.setServiceType(IMPORT_TYPE.BRMSPARAM);

            // API method to create Policy or update policy
	        PolicyChangeResponse response = null;
            response = policyEngine.policyEngineImport(importParameters);
            System.out.println(response.getResponseMessage());

	    } catch (Exception e) {
	        System.err.println(e.getMessage());
	    }
	}
}
