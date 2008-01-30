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

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.tiles.Attribute;
import org.apache.tiles.AttributeContext;
import org.easymock.EasyMock;

import junit.framework.TestCase;

/**
 * Tests <code>BasicAttributeContext</code>.
 *
 * @version $Rev$ $Date$
 */
public class BasicAttributeContextTest extends TestCase {

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext()}.
     */
    public void testBasicAttributeContext() {
        AttributeContext context = new BasicAttributeContext();
        assertNull("There are some spurious attributes", context
                .getLocalAttributeNames());
        assertNull("There are some spurious attributes", context
                .getCascadedAttributeNames());
    }

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext(Map)}.
     */
    public void testBasicAttributeContextMapOfStringAttribute() {
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        AttributeContext context = new BasicAttributeContext(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
                attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
                attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(AttributeContext)}.
     */
    public void testBasicAttributeContextAttributeContext() {
        Set<String> localAttributes = new LinkedHashSet<String>();
        Set<String> cascadedAttributes = new LinkedHashSet<String>();
        localAttributes.add("local1");
        localAttributes.add("local2");
        cascadedAttributes.add("cascaded1");
        cascadedAttributes.add("cascaded2");
        AttributeContext toCopy = EasyMock.createMock(AttributeContext.class);
        EasyMock.expect(toCopy.getLocalAttributeNames()).andReturn(
                localAttributes);
        EasyMock.expect(toCopy.getLocalAttribute("local1")).andReturn(
                new Attribute("value1")).anyTimes();
        EasyMock.expect(toCopy.getLocalAttribute("local2")).andReturn(
                new Attribute("value2")).anyTimes();
        EasyMock.expect(toCopy.getCascadedAttributeNames()).andReturn(
                cascadedAttributes);
        EasyMock.expect(toCopy.getCascadedAttribute("cascaded1")).andReturn(
                new Attribute("value3")).anyTimes();
        EasyMock.expect(toCopy.getCascadedAttribute("cascaded2")).andReturn(
                new Attribute("value4")).anyTimes();
        EasyMock.replay(toCopy);
        BasicAttributeContext context = new BasicAttributeContext(toCopy);
        Attribute attribute = context.getLocalAttribute("local1");
        assertNotNull("Attribute local1 not found", attribute);
        assertEquals("Attribute local1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getLocalAttribute("local2");
        assertNotNull("Attribute local2 not found", attribute);
        assertEquals("Attribute local2 has not been set correctly", "value2",
                attribute.getValue());
        attribute = context.getCascadedAttribute("cascaded1");
        assertNotNull("Attribute cascaded1 not found", attribute);
        assertEquals("Attribute cascaded1 has not been set correctly",
                "value3", attribute.getValue());
        attribute = context.getCascadedAttribute("cascaded2");
        assertNotNull("Attribute cascaded2 not found", attribute);
        assertEquals("Attribute cascaded2 has not been set correctly",
                "value4", attribute.getValue());
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(BasicAttributeContext)}.
     */
    public void testBasicAttributeContextBasicAttributeContext() {
        AttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        AttributeContext context = new BasicAttributeContext(toCopy);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#inheritCascadedAttributes(AttributeContext)}.
     */
    public void testInheritCascadedAttributes() {
        AttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        AttributeContext context = new BasicAttributeContext();
        context.inheritCascadedAttributes(toCopy);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#addAll(Map)}.
     */
    public void testAddAll() {
        AttributeContext context = new BasicAttributeContext();
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        context.addAll(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
                attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#addMissing(Map)}.
     */
    public void testAddMissing() {
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        AttributeContext context = new BasicAttributeContext(name2attrib);
        name2attrib.remove("name2");
        name2attrib.put("name1", new Attribute("Value 1a"));
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addMissing(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "Value 1",
                attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "Value 2",
                attribute.getValue());
        attribute = context.getAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "Value 3",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getAttribute(String)}.
     */
    public void testGetAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
                attribute.getValue());
        attribute = context.getAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttribute(String)}.
     */
    public void testGetLocalAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getLocalAttribute("name2");
        assertNull("Attribute name2 found", attribute);
        attribute = context.getLocalAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttribute(String)}.
     */
    public void testGetCascadedAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
                attribute.getValue());
        attribute = context.getCascadedAttribute("name3");
        assertNotNull("Attribute name3 not found", attribute);
        assertEquals("Attribute name3 has not been set correctly", "value3a",
                attribute.getValue());
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttributeNames()}.
     */
    public void testGetLocalAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getLocalAttributeNames();
        assertTrue("Attribute name1 is not present", names.contains("name1"));
        assertFalse("Attribute name2 is present", names.contains("name2"));
        assertTrue("Attribute name3 is not present", names.contains("name3"));
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttributeNames()}.
     */
    public void testGetCascadedAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getCascadedAttributeNames();
        assertFalse("Attribute name1 is present", names.contains("name1"));
        assertTrue("Attribute name2 is not present", names.contains("name2"));
        assertTrue("Attribute name3 is not present", names.contains("name3"));
    }

    /**
     * Tests {@link BasicAttributeContext#putAttribute(String, Attribute)}.
     */
    public void testPutAttributeStringAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"));
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
    }

    /**
     * Tests
     * {@link BasicAttributeContext#putAttribute(String, Attribute, boolean)}.
     */
    public void testPutAttributeStringAttributeBoolean() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull("Attribute name1 not found", attribute);
        assertEquals("Attribute name1 has not been set correctly", "value1",
                attribute.getValue());
        attribute = context.getCascadedAttribute("name1");
        assertNull("Attribute name1 found", attribute);
        attribute = context.getCascadedAttribute("name2");
        assertNotNull("Attribute name2 not found", attribute);
        assertEquals("Attribute name2 has not been set correctly", "value2",
                attribute.getValue());
        attribute = context.getLocalAttribute("name2");
        assertNull("Attribute name2 found", attribute);
    }

    /**
     * Tests {@link BasicAttributeContext#clear()}.
     */
    public void testClear() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.clear();
        Set<String> names = context.getLocalAttributeNames();
        assertTrue("There are local attributes", names == null
                || names.isEmpty());
        names = context.getCascadedAttributeNames();
        assertTrue("There are cascaded attributes", names == null
                || names.isEmpty());
    }
}