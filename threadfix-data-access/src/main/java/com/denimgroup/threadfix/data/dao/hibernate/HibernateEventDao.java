////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2015 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////

package com.denimgroup.threadfix.data.dao.hibernate;

import com.denimgroup.threadfix.data.dao.AbstractObjectDao;
import com.denimgroup.threadfix.data.dao.EventDao;
import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.data.enums.EventAction;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

import static com.denimgroup.threadfix.CollectionUtils.list;

@Repository
public class HibernateEventDao extends AbstractObjectDao<Event> implements EventDao {

    @Autowired
    public HibernateEventDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    protected Class<Event> getClassReference() {
        return Event.class;
    }

    @Override
    public List<Event> retrieveAllByScan(Scan scan) {

        Criteria criteria = getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("scan", scan));

        Order order = getOrder();
        if (order != null) {
            criteria.addOrder(order);
        }

        return criteria.list();
    }

    @Override
    public List<Event> retrieveAllByVulnerability(Vulnerability vulnerability) {

        Criteria criteria = getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("vulnerability", vulnerability));

        Order order = getOrder();
        if (order != null) {
            criteria.addOrder(order);
        }

        return criteria.list();
    }

    @Override
    public List<Event> retrieveAllByDefect(Defect defect) {

        Criteria criteria = getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.eq("defect", defect));

        Order order = getOrder();
        if (order != null) {
            criteria.addOrder(order);
        }

        return criteria.list();
    }

    @Override
    public List<Event> retrieveUngroupedByUser(User user) {
        List<String> userEventActions = list();
        for (EventAction eventAction : EventAction.userEventActions) {
            userEventActions.add(eventAction.name());
        }

        return retrieveUngrouped(userEventActions, user, null, null);
    }

    @Override
    public List<Event> retrieveGroupedByUser(User user) {
        List<String> userGroupedEventAction = list();
        for (EventAction eventAction : EventAction.userGroupedEventAction) {
            userGroupedEventAction.add(eventAction.name());
        }

        return retrieveGrouped(userGroupedEventAction, user, null, null);
    }

    @Override
    public List<Event> retrieveGlobalUngrouped(Set<Integer> appIds, Set<Integer> teamIds) {
        List<String> globalEventActions = list();
        for (EventAction eventAction : EventAction.globalEventActions) {
            globalEventActions.add(eventAction.name());
        }

        return retrieveUngrouped(globalEventActions, null, appIds, teamIds);
    }

    @Override
    public List<Event> retrieveGlobalGrouped(Set<Integer> appIds, Set<Integer> teamIds) {
        List<String> globalGroupedEventAction = list();
        for (EventAction eventAction : EventAction.globalGroupedEventAction) {
            globalGroupedEventAction.add(eventAction.name());
        }

        return retrieveGrouped(globalGroupedEventAction, null, appIds, teamIds);
    }

    private List<Event> retrieveUngrouped(List<String> eventActions, User user, Set<Integer> appIds, Set<Integer> teamIds) {
        Criteria criteria = getEventCriteria(eventActions, user, appIds, teamIds);

        List<Event> events = criteria.list();

        return events;
    }

    private List<Event> retrieveGrouped(List<String> eventActions, User user, Set<Integer> appIds, Set<Integer> teamIds) {
        Criteria criteria = getEventCriteria(eventActions, user, appIds, teamIds);

        criteria.setProjection(Projections.projectionList()
                        .add(Projections.count("id").as("groupCount"))
                        .add(Projections.groupProperty("eventAction").as("eventAction"))
                        .add(Projections.groupProperty("scan").as("scan"))
                        .add(Projections.groupProperty("deletedScanId").as("deletedScanId"))
                        .add(Projections.min("date"), "date")
                        .add(Projections.groupProperty("application"), "application")
                        .add(Projections.groupProperty("user"), "user")
        );

        criteria.setResultTransformer(Transformers.aliasToBean(Event.class));

        List<Event> events = criteria.list();

        for (Event event : events) {
            EventAction eventAction = event.getEventActionEnum();
            EventAction groupedEventAction = eventAction.getGroupedEventAction();
            String groupedEventActionString = groupedEventAction.toString();
            event.setEventAction(groupedEventActionString);
        }

        return events;
    }

    private Criteria getEventCriteria(List<String> eventActions, User user, Set<Integer> appIds, Set<Integer> teamIds) {
        Criteria criteria = getSession()
                .createCriteria(getClassReference())
                .add(Restrictions.eq("active", true))
                .add(Restrictions.in("eventAction", eventActions));

        criteria.createAlias("scan", "scan", Criteria.LEFT_JOIN);
        criteria.createAlias("application", "application", Criteria.LEFT_JOIN);
        criteria.createAlias("application.organization", "application.organization", Criteria.LEFT_JOIN);

        if (user != null) {
            criteria.add(Restrictions.eq("user", user));
        }

        if ((appIds != null) && (!appIds.isEmpty()) && (teamIds != null) && (!teamIds.isEmpty())) {
            criteria.add(Restrictions.or(
                    Restrictions.in("application.id", appIds),
                    Restrictions.in("application.organization.id", teamIds)
            ));
        } else if ((appIds != null) && (!appIds.isEmpty())) {
            criteria.add(Restrictions.in("application.id", appIds));
        } else if ((teamIds != null) && (!teamIds.isEmpty())) {
            criteria.add(Restrictions.in("application.organization.id", teamIds));
        }

        Order order = getOrder();
        if (order != null) {
            criteria.addOrder(order);
        }

        return criteria;
    }
}
