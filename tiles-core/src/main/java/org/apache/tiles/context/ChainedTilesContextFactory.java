/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.tiles.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.awareness.TilesRequestContextFactoryAware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implementation for TilesContextFactory, that creates a chain of
 * sub-factories, trying each one until it returns a not-null value.
 *
 * @version $Rev$ $Date$
 */
public class ChainedTilesContextFactory implements TilesContextFactory {

    /**
     * Factory class names initialization parameter to use.
     */
    public static final String FACTORY_CLASS_NAMES =
        "org.apache.tiles.context.ChainedTilesContextFactory.FACTORY_CLASS_NAMES";

    /**
     * The default class names to instantiate that compose the chain..
     */
    public static final String[] DEFAULT_FACTORY_CLASS_NAMES = {
            "org.apache.tiles.servlet.context.ServletTilesContextFactory",
            "org.apache.tiles.portlet.context.PortletTilesContextFactory",
            "org.apache.tiles.jsp.context.JspTilesContextFactory" };

    /**
     * The logging object.
     */
    private static final Log LOG = LogFactory
            .getLog(ChainedTilesContextFactory.class);

    /**
     * The Tiles context factories composing the chain.
     */
    private List<TilesContextFactory> factories;

    /**
     * Sets the factories to be used.
     *
     * @param factories The factories to be used.
     */
    public void setFactories(List<TilesContextFactory> factories) {
        this.factories = factories;
        injectParentTilesContextFactory();
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public void init(Map<String, String> configParameters) {
        String[] classNames = null;
        String classNamesString = configParameters.get(FACTORY_CLASS_NAMES);
        if (classNamesString != null) {
            classNames = classNamesString.split("\\s*,\\s*");
        }
        if (classNames == null || classNames.length <= 0) {
            classNames = DEFAULT_FACTORY_CLASS_NAMES;
        }

        factories = new ArrayList<TilesContextFactory>();
        for (int i = 0; i < classNames.length; i++) {
            try {
                Class<TilesContextFactory> clazz = (Class<TilesContextFactory>) Class
                        .forName(classNames[i]);
                if (TilesContextFactory.class.isAssignableFrom(clazz)) {
                    TilesContextFactory factory = clazz.newInstance();
                    factories.add(factory);
                } else {
                    throw new IllegalArgumentException("The class "
                            + classNames[i]
                            + " does not implement TilesContextFactory");
                }
            } catch (ClassNotFoundException e) {
                // We log it, because it could be a default configuration class that
                // is simply not present.
                LOG.warn("Cannot find TilesContextFactory class "
                        + classNames[i]);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cannot find TilesContextFactory class "
                            + classNames[i], e);
                }
            } catch (InstantiationException e) {
                throw new IllegalArgumentException(
                        "Cannot instantiate TilesFactoryClass " + classNames[i],
                        e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException(
                        "Cannot access TilesFactoryClass " + classNames[i]
                                + " default constructor", e);
            }
        }
        injectParentTilesContextFactory();
    }

    /** {@inheritDoc} */
    public TilesApplicationContext createApplicationContext(Object context) {
        TilesApplicationContext retValue = null;

        for (Iterator<TilesContextFactory> factoryIt = factories.iterator(); factoryIt
                .hasNext() && retValue == null;) {
            retValue = factoryIt.next().createApplicationContext(context);
        }

        if (retValue == null) {
            throw new IllegalArgumentException(
                    "Cannot find a factory to create the application context");
        }

        return retValue;
    }

    /** {@inheritDoc} */
    public TilesRequestContext createRequestContext(
            TilesApplicationContext context, Object... requestItems) {
        TilesRequestContext retValue = null;

        for (Iterator<TilesContextFactory> factoryIt = factories.iterator(); factoryIt
                .hasNext() && retValue == null;) {
            retValue = factoryIt.next().createRequestContext(context,
                    requestItems);
        }

        if (retValue == null) {
            throw new IllegalArgumentException(
                    "Cannot find a factory to create the request context");
        }

        return retValue;
    }

    /**
     * Injects this context factory to all chained context factories.
     */
    protected void injectParentTilesContextFactory() {
        for (TilesContextFactory factory : factories) {
            if (factory instanceof TilesRequestContextFactoryAware) {
                ((TilesRequestContextFactoryAware) factory)
                        .setRequestContextFactory(this);
            }
        }
    }
}
