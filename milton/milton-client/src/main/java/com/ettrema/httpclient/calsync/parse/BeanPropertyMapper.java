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
package com.ettrema.httpclient.calsync.parse;

import com.ettrema.httpclient.calsync.parse.annotation.GivenName;
import com.ettrema.httpclient.calsync.parse.annotation.LastName;
import com.ettrema.httpclient.calsync.parse.annotation.Uid;
import info.ineighborhood.cardme.engine.VCardEngine;
import info.ineighborhood.cardme.io.VCardWriter;
import info.ineighborhood.cardme.vcard.VCard;
import info.ineighborhood.cardme.vcard.VCardImpl;
import info.ineighborhood.cardme.vcard.features.FormattedNameFeature;
import info.ineighborhood.cardme.vcard.features.NameFeature;
import info.ineighborhood.cardme.vcard.types.*;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author brad
 */
public class BeanPropertyMapper {

    private final Map<Class, Mapper> mapOfMappers;
    private final PropertyAccessor propertyAccessor;

    public BeanPropertyMapper(PropertyAccessor propertyAccessor) {
        this.propertyAccessor = propertyAccessor;
        mapOfMappers = new HashMap<Class, Mapper>();
        addMapper(Uid.class, new UidMapper());
        addMapper(GivenName.class, new GivenNameMapper());
        addMapper(LastName.class, new LastNameMapper());
    }

    private void addMapper(Class c, Mapper m) {
        mapOfMappers.put(c, m);
    }

    public void toBean(Object bean, String icalText) {
        VCardEngine cardEngine = new VCardEngine();
        VCard vcard;
        try {
            vcard = cardEngine.parse(icalText);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                Method read = pd.getReadMethod();
                Annotation[] annotations = read.getAnnotations();
                for (Annotation anno : annotations) {
                    Mapper mapper = mapOfMappers.get(anno.annotationType());
                    if (mapper != null) {
                        mapper.mapToBean(vcard, bean, pd);
                    }
                }
            }
        }
    }

    /**
     * Find a property with the given annotation and return its value
     *
     * @param bean
     * @param annotationClass
     * @return
     */
    public <T> T getProperty(Object bean, Class annotationClass, Class<T> valueClass) {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                Method read = pd.getReadMethod();
                Annotation[] annotations = read.getAnnotations();
                for (Annotation anno : annotations) {
                    if (anno.annotationType() == annotationClass) {
                        return propertyAccessor.get(bean, read, valueClass);
                    }
                }
            }
        }
        return null;
    }

    public String toVCard(Object bean) {
        VCard card = new VCardImpl();
        card.setBegin(new BeginType());
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
        for (PropertyDescriptor pd : pds) {
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
                Method read = pd.getReadMethod();
                Annotation[] annotations = read.getAnnotations();
                for (Annotation anno : annotations) {
                    Mapper mapper = mapOfMappers.get(anno.annotationType());
                    if (mapper != null) {
                        mapper.mapToCard(card, bean, pd);
                    }
                }
            }
        }
        FormattedNameFeature fname = card.getFormattedName();
        if (fname == null) {
            NameFeature nameFeature = card.getName();
            if (nameFeature != null) {
                String formattedName = nameFeature.getGivenName() + " " + nameFeature.getFamilyName();
                fname = new FormattedNameType(formattedName);
                card.setFormattedName(fname);
            }
        }

        card.setEnd(new EndType());

        VCardWriter writer = new VCardWriter();
        writer.setVCard(card);
        String text = writer.buildVCardString();
        return text;
    }

    public abstract class Mapper {

        abstract void mapToBean(VCard card, Object bean, PropertyDescriptor pd);

        abstract void mapToCard(VCard card, Object bean, PropertyDescriptor pd);
    }

    public class UidMapper extends Mapper {

        @Override
        void mapToBean(VCard card, Object bean, PropertyDescriptor pd) {
            Method m = pd.getWriteMethod();
            String uid;
            if (card.getUID() != null && card.getUID().hasUID()) {
                uid = card.getUID().getUID();
            } else {
                uid = UUID.randomUUID().toString();
            }
            propertyAccessor.set(bean, m, uid);
        }

        @Override
        void mapToCard(VCard card, Object bean, PropertyDescriptor pd) {
            String uid = propertyAccessor.get(bean, pd.getReadMethod(), String.class);
            card.setUID(new UIDType(uid));
        }
    }

    public class GivenNameMapper extends Mapper {

        @Override
        void mapToBean(VCard card, Object bean, PropertyDescriptor pd) {
            if (card.getName() != null) {
                String s = card.getName().getGivenName();
                propertyAccessor.set(bean, pd.getWriteMethod(), s);
            }

        }

        @Override
        void mapToCard(VCard card, Object bean, PropertyDescriptor pd) {
            if (card.getName() == null) {
                NameFeature nf = new NameType();
                card.setName(nf);
            }
            String s = propertyAccessor.get(bean, pd.getReadMethod(), String.class);
            card.getName().setGivenName(s);
        }
    }

    public class LastNameMapper extends Mapper {

        @Override
        void mapToBean(VCard card, Object bean, PropertyDescriptor pd) {
            if (card.getName() != null) {
                String s = card.getName().getFamilyName();
                propertyAccessor.set(bean, pd.getWriteMethod(), s);
            }

        }

        @Override
        void mapToCard(VCard card, Object bean, PropertyDescriptor pd) {
            if (card.getName() == null) {
                NameFeature nf = new NameType();
                card.setName(nf);
            }
            String s = propertyAccessor.get(bean, pd.getReadMethod(), String.class);
            card.getName().setFamilyName(s);
        }
    }
}
