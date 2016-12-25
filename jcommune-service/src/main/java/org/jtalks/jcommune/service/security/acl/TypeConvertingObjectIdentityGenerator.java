/**
 * Copyright (C) 2011  JTalks.org Team
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.jtalks.jcommune.service.security.acl;

import org.apache.commons.lang.Validate;
import org.jtalks.common.model.entity.*;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import javax.validation.constraints.Min;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * If we don't want to have Object Identity (OID) in the database having the same type as the entity class, we can have
 * this generator being initialized with conversion rules so that some other value goes to database. <br/><br/>
 * <b>Example</b>: You're saving an instance of {@link Branch} as an Object Identity to the database into ACL tables. If
 * you use usual {@link org.springframework.security.acls.model.ObjectIdentityRetrievalStrategy}, then you'll have in
 * your {@code acl_class} table a record with {@link Branch} value. But if you want to
 * have a custom type being saved there, let's say {@code BRANCH_CLASS}, you can use this generator and set a conversion
 * rule <u>{@code Branch.class -> BRANCH_CLASS}</u> and the latter will be saved into database. <br/><br/><b>When we
 * need this</b>: Since we have a number of projects working with the same database (like Poulpe and JCommune), they all
 * have their own custom classes for instance for {@link Branch} and thus if we use a default OID generator, we'll end
 * up having Poulpe saving its own Branch with its own class to the database, and JCommune can't read it since it has
 * its own Branch and it expects a different value stored in {@code acl_class} table. But if we use different classes
 * and we covert their class name to the same {@code acl_class}, then we can use the same ACL records in Poulpe and
 * JCommune. This is required because Poulpe gives the permissions to Branches and JCommune needs to read those
 * permissions. <br/><br/><b>Other notes:</b> <ul><li>This class will be empty without any rules if created with default
 * constructor, thus if you want to ensure you use the same string constants for all the projects, you should use {@link
 * #createDefaultGenerator()} which contains mapping for entities in common modules and fill it with additional rules
 * specific to your project.</li> <li>While specifying the conversion rules for the class, not only the very specified
 * class is going to be converted to the specific string, but also if the object is of assignable class ({@code object
 * instanceof Class or any of its ancestors} ), then this conversion rule still is going to be applied. Thus if the rule
 * states Branch->BRANCH, then PoulpeBranch is also going to be converted to BRANCH. This is also important because of
 * Hibernate that creates proxies </li></ul>
 *
 * @author stanislav bashkirtsev
 */
@ThreadSafe
public class TypeConvertingObjectIdentityGenerator {
    /**
     * Creates an instance of {@link ObjectIdentity} from the specified id of the entity and the type of it (it's not
     * necessary a class name).
     *
     * @param id   a database id of the entity we want to create an object identity for, must be greater than 0
     * @param type a type of the entity, it can be either a class or a result of conversion rule (e.g. either
     *             {@link Branch} or <u>BRANCH_CLASS</u> or anything else, see class
     *             JavaDocs for more details. Note, that if a conversion rules are applied for this entity and you're
     *             specifying the wrong type here, you'll end-up with an invalid Object Identity that can't be found in
     *             database because its type is absent in {@code acl_class}.
     * @return an object identity constructed from the specified id of the entity and the entity type (either a simple
     *         class name or a converted value)
     * @throws IllegalArgumentException if the specified id is zero or less (aka not persisted yet)
     * @see #addConversionRule(Class, String)
     * @see #setAdditionalConversionRules
     */
    public ObjectIdentity createObjectIdentity(@Min(1) long id, String type) {
        Validate.isTrue(id > 0, "Entity must be persisted before creating Secured OID for it!");
        return new ObjectIdentityImpl(type, id);
    }

    /**
     * Creates an Object Identity (OID) for the specified entity using the conversion rules set into generator.
     *
     * @param domainObject an entity that is stored into a database and which is going to be an Object Identity
     *                     (permissions will be assigned to SID to do something with this object). Must be persisted
     *                     already and have its id.
     * @return an object identity with the ID of specified entity and a type according to the conversion rules. If no
     *         conversion rule was found for the class of specified object, then it's {@link
     *         Object#getClass()#getSimpleName} will be used as the OID type).
     * @throws IllegalArgumentException if the specified domain object is not persistent (it's ID is not a positive
     *                                  number)
     * @see #addConversionRule(Class, String)
     * @see #setAdditionalConversionRules
     */
    public ObjectIdentity getObjectIdentity(@Nonnull Entity domainObject) {
        String type = getType(domainObject);
        return createObjectIdentity(domainObject.getId(), type);
    }

    /**
     * You don't usually need an empty OID generator since there are common entities with common names that are shared
     * between projects within JTalks, that's why you can use this factory method that creates a generator with already
     * filled values, see the method body to understand for what classes the conversion rules are set. Afterwards you
     * can fill the class with your custom classes specific only to your project. Note, that if an entity was placed in
     * the common modules of JTalks, it might make sense to add it to this method.
     *
     * @return an OID generator with already filled type conversion rules
     */
    public static TypeConvertingObjectIdentityGenerator createDefaultGenerator() {
        return new TypeConvertingObjectIdentityGenerator()
                .addConversionRule(Group.class, "GROUP")
                .addConversionRule(Branch.class, "BRANCH")
                .addConversionRule(Section.class, "SECTION")
                .addConversionRule(Component.class, "COMPONENT");
    }

    /**
     * Adds additional conversion rule to the generator.
     *
     * @param entityClass a class to covert its name to the string
     * @param convertTo   a string the specified class will be converted while creating an Object Identity
     * @return this
     * @see #setAdditionalConversionRules
     */
    public TypeConvertingObjectIdentityGenerator addConversionRule(Class entityClass, String convertTo) {
        oidClassToTypeMap.put(entityClass, convertTo);
        return this;
    }

    /**
     * Iterates through all the conversion rules and searches for the one applicable to the specified entity. If a
     * conversion rule was set via {@link #addConversionRule(Class, String)} or {@link #setAdditionalConversionRules},
     * then the string will be returned representing the type of specified object, otherwise a {@link
     * Class#getSimpleName()} of the specified object will be returned.
     *
     * @param domainObject an object to find its conversion rule or to generate it from its class's simple name
     * @return a type of the object identity according to the conversion rules or the class's simple name if no rule was
     *         found for this entity
     */
    private String getType(Object domainObject) {
        for (Map.Entry<Class, String> nextConversionPair : oidClassToTypeMap.entrySet()) {
            if (nextConversionPair.getKey().isInstance(domainObject)) {
                return nextConversionPair.getValue();
            }
        }
        return domainObject.getClass().getSimpleName();
    }

    /**
     * Adds the specified conversion rules to the generator. <b>Note</b>, that it doesn't remove old rules, but it can
     * override them if the key value of the map will be the same.
     *
     * @param oidClassToTypeMap see {@link #addConversionRule(Class, String)} for what each entry of the map means
     */
    public void setAdditionalConversionRules(@Nonnull Map<Class, String> oidClassToTypeMap) {
        this.oidClassToTypeMap.putAll(oidClassToTypeMap);
    }

    private final Map<Class, String> oidClassToTypeMap = new ConcurrentHashMap<Class, String>();
}
