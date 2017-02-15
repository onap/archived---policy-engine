This source repository contains the OpenECOMP Policy Engine code. The settings file needs to support the standard Maven repositories (e.g. central = http://repo1.maven.org/maven2/), and any proxy settings needed in your environment. In addition, this code is dependent upon the following OpenECOMP artifacts, which are not part of Policy:

    org.openecomp.ecompsdkos:ecompSDK-project:pom:3.3.9
    org.openecomp.ecompsdkos:ecompSDK-core:jar:3.3.9
    org.openecomp.ecompsdkos:ecompSDK-analytics:jar:3.3.9
    org.openecomp.ecompsdkos:ecompSDK-workflow:jar:3.3.9
    org.openecomp.ecompsdkos:ecompFW:jar:3.3.9

To build it using Maven 3, first build 'policy-common-modules' (which contains dependencies), and then run: mvn -Dmaven.test.failure.ignore=true clean install
