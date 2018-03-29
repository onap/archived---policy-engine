/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.xacml.pdp;

import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.api.pdp.PDPEngineFactory;
import com.att.research.xacml.util.FactoryException;
import com.att.research.xacmlatt.pdp.eval.EvaluationContextFactory;

import java.util.Properties;

import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;

public class OnapPdpEngineFactory extends PDPEngineFactory {
    private Logger logger = FlexLogger.getLogger(this.getClass());

    @Override
    public PDPEngine newEngine() throws FactoryException {
        final EvaluationContextFactory evaluationContextFactory = EvaluationContextFactory.newInstance();
        if (evaluationContextFactory == null) {
            this.logger.error("Null EvaluationContextFactory");
            throw new FactoryException("Null EvaluationContextFactory");
        }
        return new OnapPdpEngine(evaluationContextFactory, this.getDefaultBehavior(), this.getScopeResolver());
    }

    @Override
    public PDPEngine newEngine(final Properties properties) throws FactoryException {
        final EvaluationContextFactory evaluationContextFactory = EvaluationContextFactory.newInstance(properties);
        if (evaluationContextFactory == null) {
            this.logger.error("Null EvaluationContextFactory");
            throw new FactoryException("Null EvaluationContextFactory");
        }
        return new OnapPdpEngine(evaluationContextFactory, this.getDefaultBehavior(), this.getScopeResolver(),
                properties);
    }

}
