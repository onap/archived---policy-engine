/*-
 * ============LICENSE_START=======================================================
 * ONAP-PAP-REST
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.pap.xacml.rest.daoimpl;

import java.util.List;
import java.util.Map;

import javax.script.SimpleBindings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.onap.policy.rest.dao.CommonClassDao;
import org.onap.policy.rest.jpa.ClosedLoops;
import org.onap.policy.rest.jpa.GroupPolicyScopeList;
import org.onap.policy.rest.jpa.PolicyRoles;
import org.onap.policy.xacml.api.XACMLErrorConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service("CommonClassDao")
@Primary
public class CommonClassDaoImpl implements CommonClassDao{

    private static final Log LOGGER = LogFactory.getLog(CommonClassDaoImpl.class);


    private static SessionFactory sessionFactory;


    @Autowired
    private CommonClassDaoImpl(SessionFactory sessionFactory){
        CommonClassDaoImpl.sessionFactory = sessionFactory;
    }

    public CommonClassDaoImpl(){
        //Default Constructor
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Object> getData(Class className) {
        Session session = sessionFactory.openSession();
        List<Object> data = null;
        try{
            Criteria cr = session.createCriteria(className);
            data = cr.list();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e);
            }
        }
        return data;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public List<Object> getDataById(Class className, String columnName, String key) {
        Session session = sessionFactory.openSession();
        List<Object> data = null;
        try {
            Criteria cr = session.createCriteria(className);
            if(columnName.contains(":") && key.contains(":")){
                String[] columns = columnName.split(":");
                String[] keys = key.split(":");
                for(int i=0; i < columns.length; i++){
                    cr.add(Restrictions.eq(columns[i], keys[i]));
                }
            }else{
                cr.add(Restrictions.eq(columnName, key));
            }
            data = cr.list();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
        return data;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<String> getDataByColumn(Class className, String columnName) {
        Session session = sessionFactory.openSession();
        List<String> data = null;
        try{
            Criteria cr = session.createCriteria(className);
            cr.setProjection(Projections.property(columnName));
            data = cr.list();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e);
            }
        }
        return data;
    }

    @Override
    public void save(Object entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.persist(entity);
            tx.commit();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Saving  data to Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }

    }

    @Override
    public void delete(Object entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.delete(entity);
            tx.commit();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Deleting data from Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }

    }


    @Override
    public void update(Object entity) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.update(entity);
            tx.commit();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating data to Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }

    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<Object> checkDuplicateEntry(String value, String columnName, Class className) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<Object> data = null;

        String[] columnNames = null;
        if(columnName != null && columnName.contains(":")){
            columnNames = columnName.split(":");
        }
        String[] values = null;
        if(value != null && value.contains(":")){
            values = value.split(":");
        }
        try {
            Criteria cr = session.createCriteria(className);
            if(columnNames != null && values != null && columnNames.length == values.length){
                for (int i = 0; i < columnNames.length; i++){
                    cr.add(Restrictions.eq(columnNames[i],values[i]));
                }
            }else{
                cr.add(Restrictions.eq(columnName,value));
            }

            data = cr.list();
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying for Duplicate Entries for Table"+e + className);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
        return data;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<Object> getDataByQuery(String query, SimpleBindings params) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<Object> data = null;
        try {
            Query hbquery = session.createQuery(query);
            for (Map.Entry<String, Object> paramPair : params.entrySet()) {
                if(paramPair.getValue() instanceof java.lang.Long){
                    hbquery.setLong(paramPair.getKey(), (long) paramPair.getValue());
                }
                else{
                    hbquery.setParameter(paramPair.getKey(), paramPair.getValue());
                }
            }
            data = hbquery.list();
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Database Table"+e);
            throw e;
        }finally{
            try{
                session.close();
            }catch(HibernateException e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement",e1);
            }
        }
        return data;
    }


    @Override
    public void updateQuery(String query) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try {
            Query hbquery = session.createQuery(query);
            hbquery.executeUpdate();
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating Database Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Object getEntityItem(Class className, String columnName, String key) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Object data = null;
        try {
            Criteria cr = session.createCriteria(className);
            if(columnName.contains(":") && key.contains(":")){
                String[] columns = columnName.split(":");
                String[] keys = key.split(":");
                for(int i=0; i < columns.length; i++){
                    cr.add(Restrictions.eq(columns[i], keys[i]));
                }
            }else{
                cr.add(Restrictions.eq(columnName, key));
            }
            data = cr.list().get(0);
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying Database Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
        return data;
    }


    @SuppressWarnings("unchecked")
    @Override
    public List<PolicyRoles> getUserRoles() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<PolicyRoles> rolesData = null;
        try {
            Criteria cr = session.createCriteria(PolicyRoles.class);
            Disjunction disjunction = Restrictions.disjunction();
            Conjunction conjunction1  = Restrictions.conjunction();
            conjunction1.add(Restrictions.eq("role", "admin"));
            Conjunction conjunction2  = Restrictions.conjunction();
            conjunction2.add(Restrictions.eq("role", "editor"));
            Conjunction conjunction3  = Restrictions.conjunction();
            conjunction3.add(Restrictions.eq("role", "guest"));
            disjunction.add(conjunction1);
            disjunction.add(conjunction2);
            disjunction.add(conjunction3);
            rolesData = cr.add(disjunction).list();
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying PolicyRoles Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
        return rolesData;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void updateClAlarms(String clName, String alarms) {
        Session session = sessionFactory.openSession();
        List<ClosedLoops> closedloopsdata = null;
        Transaction tx = session.beginTransaction();
        try {
            Criteria cr = session.createCriteria(ClosedLoops.class);
            cr.add(Restrictions.eq("closedLoopControlName",clName));
            closedloopsdata = cr.list();
            ClosedLoops closedloop = closedloopsdata.get(0);
            closedloop.setAlarmConditions(alarms);
            session.update(closedloop);
            tx.commit();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating ClosedLoops Table"+e);
        }finally{
            session.close();
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void updateClYaml(String clName, String yaml) {
        Session session = sessionFactory.openSession();
        List<ClosedLoops> closedloopsdata = null;
        Transaction tx = session.beginTransaction();
        try {
            Criteria cr = session.createCriteria(ClosedLoops.class);
            cr.add(Restrictions.eq("closedLoopControlName",clName));
            closedloopsdata = cr.list();
            ClosedLoops closedloop = closedloopsdata.get(0);
            closedloop.setYaml(yaml);
            session.update(closedloop);
            tx.commit();
        }catch(Exception e){
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Updating ClosedLoops Table"+e);
        }finally{
            session.close();
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public void deleteAll() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<ClosedLoops> closedloopsdata = null;
        try {
            Criteria cr = session.createCriteria(ClosedLoops.class);
            closedloopsdata = cr.list();

            if(closedloopsdata!=null && !closedloopsdata.isEmpty()) {
                LOGGER.info("ClosedLoops exist in the database, we need to delete them in our first step to buildCache().");
                for(int i=0; i < closedloopsdata.size(); i++) {
                    ClosedLoops cl = closedloopsdata.get(i);
                    session.delete(cl);
                }
            } else {
                LOGGER.info("No ClosedLoops exist in the database, no need to delete.");
            }

            tx.commit();
        }catch(Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error while deleting ClosedLoops from the table"+e);
        }finally{
            session.close();
        }
    }

    @SuppressWarnings({ "unchecked"})
    @Override
    public List<Object> checkExistingGroupListforUpdate(String groupListValue, String groupNameValue) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        List<Object> data = null;
        try {
            Criteria cr = session.createCriteria(GroupPolicyScopeList.class);
            cr.add(Restrictions.eq("groupList",groupListValue));
            Criterion expression = Restrictions.eq("name", groupNameValue);
            cr.add(Restrictions.not(expression));
            data = cr.list();
            tx.commit();
        } catch (Exception e) {
            LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Querying for Duplicate Entries for GroupPolicyScopeList Table"+e);
        }finally{
            try{
                session.close();
            }catch(Exception e1){
                LOGGER.error(XACMLErrorConstants.ERROR_PROCESS_FLOW + "Error While Closing Connection/Statement"+e1);
            }
        }
        return data;
    }


    @Override
    public List<Object> getMultipleDataOnAddingConjunction(@SuppressWarnings("rawtypes") Class className, String columnName, List<String> data) {
        return null;
    }

    public static void setSessionfactory(SessionFactory sessionfactory) {
        sessionFactory = sessionfactory;
    }

}