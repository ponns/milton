/*
 * Copyright (C) 2012 McEvoy Software Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/*
 * Copyright 2012 McEvoy Software Ltd.
 *
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
 */
package com.ettrema.httpclient.calsync.store;

import com.ettrema.httpclient.annotation.Etag;
import com.ettrema.httpclient.annotation.ModifiedDate;
import com.ettrema.httpclient.annotation.Name;
import com.ettrema.httpclient.calsync.CalSyncEvent;
import com.ettrema.httpclient.calsync.CalendarStore;
import com.ettrema.httpclient.calsync.parse.BeanPropertyMapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author brad
 */
public class AnnotationCalendarStore implements CalendarStore {

    private final BeanPropertyMapper beanMapper;
    private final CalendarEventFactory calendarEventFactory;
    private String id = "annotationStore";
    private boolean readonly;

    public AnnotationCalendarStore(BeanPropertyMapper beanMapper, CalendarEventFactory calendarEventFactory) {
        this.beanMapper = beanMapper;
        this.calendarEventFactory = calendarEventFactory;
    }

    public AnnotationCalendarStore(String id, BeanPropertyMapper beanMapper, CalendarEventFactory calendarEventFactory) {
        this.id = id;
        this.beanMapper = beanMapper;
        this.calendarEventFactory = calendarEventFactory;
    }
    
    
    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getCtag() {
        return calendarEventFactory.getCtag(id);
    }

    @Override
    public List<CalSyncEvent> getChildren() {
        List beans = calendarEventFactory.getChildren(id);

        List<CalSyncEvent> children = new ArrayList<CalSyncEvent>();
        if (beans != null) {
            for (Object b : beans) {
                ICalBeanWrapper wrapper = new ICalBeanWrapper(b);
                children.add(wrapper);
            }
        }
        return children;
    }

    @Override
    public void deleteEvent(CalSyncEvent event) {
        calendarEventFactory.deleteEvent(id, event.getName());
    }

    @Override
    public String getICalData(CalSyncEvent event) {
        ICalBeanWrapper wrapper = (ICalBeanWrapper) event;
        Object bean = wrapper.getBean();
        return beanMapper.toVCard(bean);
    }

    @Override
    public String setICalData(CalSyncEvent event, String icalData) {
        ICalBeanWrapper wrapper = (ICalBeanWrapper) event;
        Object bean = wrapper.getBean();
        beanMapper.toBean(bean, icalData);
        calendarEventFactory.saveAndUpdateTags(id, bean);

        String etag = beanMapper.getProperty(bean, Etag.class, String.class);
        return etag;
    }

    @Override
    public Date getModifiedDate(CalSyncEvent event) {
        Date modDate = beanMapper.getProperty(event, ModifiedDate.class, Date.class);
        return modDate;
    }

    @Override
    public String createICalEvent(String name, String icalText) {
        Object newEvent = calendarEventFactory.create(id, name);
        beanMapper.toBean(newEvent, icalText);
        calendarEventFactory.saveAndUpdateTags(id, newEvent);

        String etag = beanMapper.getProperty(newEvent, Etag.class, String.class);
        return etag;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReadonly(boolean b) {
        readonly = b;
    }

    public class ICalBeanWrapper implements CalSyncEvent {

        private final Object bean;

        public ICalBeanWrapper(Object bean) {
            this.bean = bean;
        }

        @Override
        public String getEtag() {
            String etag = beanMapper.getProperty(bean, Etag.class, String.class);
            return etag;
        }

        @Override
        public String getName() {
            String name = beanMapper.getProperty(bean, Name.class, String.class);
            return name;
        }

        public Object getBean() {
            return bean;
        }
    }
}
