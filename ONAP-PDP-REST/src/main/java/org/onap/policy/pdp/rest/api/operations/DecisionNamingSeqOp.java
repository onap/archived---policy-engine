/*-
 * ============LICENSE_START=======================================================
 * ONAP-PDP-REST
 * ================================================================================
 * Copyright (C) 2018 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pdp.rest.api.operations;

import java.math.BigInteger;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.onap.policy.common.logging.flexlogger.FlexLogger;
import org.onap.policy.common.logging.flexlogger.Logger;
import org.onap.policy.pdp.rest.api.models.IncrementSequence;
import org.onap.policy.pdp.rest.api.models.NamingProperty;
import org.onap.policy.pdp.rest.api.operations.DecisionOperationType;
import org.onap.policy.rest.daoimpl.PolicyValidationDaoImpl;
import org.onap.policy.rest.jpa.NamingSequences;
import org.onap.policy.xacml.api.XACMLErrorConstants;


/**
 * DecisionNamingSeqOp - Retrieves the next sequence in this range.
 */
public class DecisionNamingSeqOp extends DecisionBaseOperation {

    private static final Logger LOGGER = FlexLogger.getLogger(DecisionNamingSeqOp.class.getName());

    private String namingType = null;

    private NamingProperty namingProp = null;

    private String seqKey = null;

    private String seqType = null;

    /** The generated next seq. */
    private long generatedSeq;

    public static final String NAMING_TYPE = "namingType";
    public static final String INCREMENT_SEQUENCE = "incrementSeq";
    public static final String NAMING_PROP = "namingProp";
    public static final String SEQ_KEY = "sequenceKey";


    public DecisionNamingSeqOp(DecisionOperationType opType) {
        super(opType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation#populateOperation(java.util.Map)
     */
    @Override
    public boolean populateOperation(Map<String, Object> opInputData) {
        boolean ret = true;
        namingType = (String) opInputData.get(NAMING_TYPE);
        namingProp = (NamingProperty) opInputData.get(NAMING_PROP);
        seqKey = (String) opInputData.get(SEQ_KEY);
        seqType = namingProp.getIncrementSequence().getSequenceType();

        if (!validateOpData()) {
            ret = false;
        }
        LOGGER.info("DecisionNamingSeqOp - populateOperation - namingType: " + namingType + ", Naming Property: "
                + namingProp);
        return ret;
    }

    /**
     * Validate op data.
     *
     * @return true, if successful
     */
    private boolean validateOpData() {
        boolean ret = true;
        if (StringUtils.isBlank(namingType) || namingProp == null) {
            LOGGER.error(XACMLErrorConstants.ERROR_DATA_ISSUE + " - DecisionNamingSeqOp - validateOpData failed");
            ret = false;
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation#performOperation()
     */
    @Override
    public boolean performOperation() {
        boolean ret = true;
        NamingSequences namingSeq = getNextSeqFromDb();
        if (namingSeq == null) {
            // we would not find next seq num in db
            // would be due to range exhausted or first time
            // let us try to start the ranges from the policy
            namingSeq = startNewRange();
        }

        if (namingSeq != null) {
            generatedSeq = namingSeq.getCurrentseq();
        } else {
            LOGGER.info("Sequence Operation Failure - for namingType: " + namingType + ", seqKey: " + seqKey
                    + ", namingProp: " + namingProp);
            ret = false;
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.onap.policy.pdp.rest.api.operations.DecisionOperation#getResult()
     */
    @Override
    public String getResult() {
        return StringUtils.leftPad(Long.toString(generatedSeq, calcBase()),
                (int) namingProp.getIncrementSequence().getLength(), "0");
    }

    /**
     * Gets the next seq from DB.
     *
     * @return the next seq from DB
     */
    private NamingSequences getNextSeqFromDb() {
        String scope = namingProp.getIncrementSequence().getScope();
        long step = Long.parseLong(namingProp.getIncrementSequence().getIncrement(), calcBase());

        // we maintain a table of used sequence numbers for this
        // namingType+scope+seqKey
        // First, we try to get next sequence number from the table.
        // If this is the first time, then we use the start Value.
        // if we get the next seq number from table or first time,
        // we insert the used seq into the table.
        // if the range is used up, then we check whether there is another range
        // available
        String queryString =
             "INSERT INTO NamingSequences (namingtype, scope, sequencekey, startrange, endrange, steprange, currentseq)"
                        + " select a.namingtype, a.scope, a.sequencekey, a.startrange, a.endrange, a.steprange,"
                        + "(a.currentseq + :increment) from NamingSequences a "
                        + " where a.sequencekey = :seqKey and a.namingtype = :nameType and a.scope = :scope"
                        + " and a.currentseq >= a.startrange - :increment"
                        + " and a.currentseq <= a.endrange - :increment  and not exists"
                        + " (select 1 from NamingSequences b where b.sequencekey = :seqKey and b.namingtype = :nameType"
                        + " and b.currentseq >= b.startrange and b.currentseq <= b.endrange"
                        + " and b.scope = :scope and b.currentseq = a.currentseq + :increment )"
                        + " order by currentseq LIMIT 1";

        Session session = PolicyValidationDaoImpl.getSessionfactory().openSession();
        Transaction tx = session.beginTransaction();
        NamingSequences namingSeq = null;
        try {
            SQLQuery hbquery = session.createSQLQuery(queryString);
            hbquery.setMaxResults(1);
            hbquery.setParameter("scope", scope);
            hbquery.setLong("increment", step);
            hbquery.setParameter("nameType", namingType);
            hbquery.setParameter("seqKey", seqKey);
            int ret = hbquery.executeUpdate();

            if (ret > 0) {
                BigInteger lastId = (BigInteger) session.createSQLQuery("SELECT LAST_INSERT_ID()").uniqueResult();
                namingSeq = (NamingSequences) session.get(NamingSequences.class, lastId.intValue());
                generatedSeq = namingSeq.getCurrentseq();
                LOGGER.info("Next sequence number found in DB for this namingType - " + namingType + ", scope: " + scope
                        + ", sequencekey: " + seqKey + "lastId - " + lastId + ", NamingSequence - " + namingSeq);

            } else {
                // either range exhausted or new range starting
                LOGGER.info("No sequence number found in DB for this namingType - " + namingType + ", scope: " + scope
                        + ", sequencekey: " + seqKey);
            }

            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Retrieving next sequence number from DB "
                    + e);
        } finally {
            try {
                session.close();
            } catch (Exception e1) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW
                        + PolicyValidationDaoImpl.DB_CONNECTION_CLOSING_ERROR + e1);
            }
        }
        return namingSeq;
    }

    /**
     * Start new range.
     *
     * @return the naming sequences
     */
    private NamingSequences startNewRange() {
        boolean ret = true;
        NamingSequences namingSeq = null;
        String scope = namingProp.getIncrementSequence().getScope();
        long start = Long.parseLong(namingProp.getIncrementSequence().getStartValue(), calcBase());
        long end = Long.parseLong(namingProp.getIncrementSequence().getEndValue(), calcBase());
        long step = Long.parseLong(namingProp.getIncrementSequence().getIncrement(), calcBase());

        // if this is the first time with this range, we use the first
        // value in the range and persist it to DB
        // The requirement is that previously used seq numbers can be reused
        // Our query to get the next seq number from DB relies on the initial
        // start value being there
        // To allow the start value of the range to be reused, we insert a dummy
        // seq num that is before the start
        NamingSequences namingSeqStart =
                new NamingSequences(namingType, scope, seqKey, null, start, end, step, start, null, null);

        LOGGER.info("Going to try to start New Range - " + namingSeqStart);

        ret = saveNamingSequences(namingSeqStart);

        if (!ret) {
            // continue and try the next range? model currently does not support multiple ranges
            LOGGER.info("Failed to save range into DB. Range possibly exists and exhausted  - " + namingSeqStart);
        } else {
            // we will use the start of this range as the seq num
            namingSeq = namingSeqStart;
            LOGGER.info("Started New Range - " + namingSeqStart);
        }

        NamingSequences namingSeqDummy =
                new NamingSequences(namingType, scope, seqKey, null, start, end, step, start - step, null, null);

        // how to handle Contiguous ranges.
        ret = saveNamingSequences(namingSeqDummy);

        if (!ret) {
            LOGGER.info("Failed to save dummy into DB. Possibly contiguous ranges  - " + namingSeqDummy);
        }

        return namingSeq;
    }

    /**
     * Save naming sequences - used when a new range is started.
     *
     * @param namingSeq the naming seq
     * @return true, if successful
     */
    private boolean saveNamingSequences(NamingSequences namingSeq) {
        boolean ret = true;

        Session session = PolicyValidationDaoImpl.getSessionfactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(namingSeq);
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving data to Table" + e);
            ret = false;
        } finally {
            try {
                session.close();
            } catch (Exception e1) {
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW
                        + PolicyValidationDaoImpl.DB_CONNECTION_CLOSING_ERROR + e1);
                ret = false;
            }
        }

        return ret;
    }

    /**
     * Calculate the base to use depending on sequenceType. if numeric range, base 10 if alpha-numeric range, base 36
     *
     * @return the namingType
     */
    private int calcBase() {
        int base = 10;

        // convert to base-36 for alpha
        if (StringUtils.containsIgnoreCase(seqType, IncrementSequence.SEQUENCE_TYPE_ALPHA)) {
            base = 36;
        }
        return base;
    }

    /**
     * Gets the naming type.
     *
     * @return the namingType
     */
    public String getNamingType() {
        return namingType;
    }

    /**
     * Sets the naming type.
     *
     * @param namingType the namingType to set
     */
    public void setNamingType(String namingType) {
        this.namingType = namingType;
    }

    /**
     * Gets the naming prop.
     *
     * @return the namingProp
     */
    public NamingProperty getNamingProp() {
        return namingProp;
    }

    /**
     * Sets the naming prop.
     *
     * @param namingProp the namingProp to set
     */
    public void setNamingProp(NamingProperty namingProp) {
        this.namingProp = namingProp;
    }

    /**
     * Gets the seq key.
     *
     * @return the seqKey
     */
    public String getSeqKey() {
        return seqKey;
    }

    /**
     * Sets the seq key.
     *
     * @param seqKey the seqKey to set
     */
    public void setSeqKey(String seqKey) {
        this.seqKey = seqKey;
    }

    /**
     * Gets the seq type.
     *
     * @return the seqType
     */
    public String getSeqType() {
        return seqType;
    }

    /**
     * Sets the seq type.
     *
     * @param seqType the seqType to set
     */
    public void setSeqType(String seqType) {
        this.seqType = seqType;
    }

    /**
     * Gets the generated seq.
     *
     * @return the generatedSeq
     */
    public long getGeneratedSeq() {
        return generatedSeq;
    }

    /**
     * Sets the generated seq.
     *
     * @param generatedSeq the generatedSeq to set
     */
    public void setGeneratedSeq(int generatedSeq) {
        this.generatedSeq = generatedSeq;
    }
}
