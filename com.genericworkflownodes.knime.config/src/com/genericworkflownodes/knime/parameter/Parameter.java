/**
 * Copyright (c) 2012, Marc Röttig.
 *
 * This file is part of GenericKnimeNodes.
 * 
 * GenericKnimeNodes is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.genericworkflownodes.knime.parameter;

import java.io.Serializable;

/**
 * The generic Parameter base class is used to store all possible CTD parameters
 * (double, int, string, int list, ...).
 * 
 * @author roettig
 * 
 * @param <T>
 *            The type of the stored parameter.
 */
public abstract class Parameter<T> implements Serializable {
    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 2145565007955019813L;

    /**
     * The unique key identifying the parameter.
     */
    private String m_key;

    /**
     * The actual value of the parameter.
     */
    private T m_value;

    /**
     * A description of the parameter.
     */
    private String m_description;

    /**
     * The section used to categorize the parameter.
     */
    private String m_section;

    /**
     * Flag indicating if the parameter is optional or not.
     */
    private boolean m_isOptional;

    /**
     * Flag indicating if the parameter should be hidden from the "average"
     * user.
     */
    private boolean m_isAdvanced;
    
    /**
     * Flag indicating if the parameters default value was used (e.g. because of version upgrade)
     */
    private boolean m_defaulted = true;

    /**
     * Constructor with unique m_key of parameter and generic m_value to store.
     * 
     * @param key
     *            the key of the parameter
     * @param value
     *            the generic value of the parameter
     */
    public Parameter(final String key, final T value) {
        m_key = key;
        m_value = value;

        setDescription("");
        setSection("default");
        setIsOptional(true);
        setAdvanced(false);
    }

    /**
     * Returns the associated unique key (name) of the parameter.
     * 
     * @return key
     */
    public String getKey() {
        return m_key;
    }

    /**
     * Sets the unique key (name) of the parameter.
     * 
     * @param key
     *            the key of the parameter
     */
    public void setKey(final String key) {
        m_key = key;
    }

    /**
     * Returns the generic value stored by this object.
     * 
     * @return the value of the parameter.
     */
    public T getValue() {
        return m_value;
    }

    /**
     * Sets the value stored by this object.
     * 
     * @param value
     *            the m_value to store
     */
    public void setValue(final T value) {
        m_value = value;
        m_defaulted = false;
    }

    /**
     * Returns the description for this parameter object.
     * 
     * @return the description text of the parameter.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * Sets the description text of this parameter object.
     * 
     * @param description
     *            the description text of the parameter.
     */
    public final void setDescription(final String description) {
        m_description = description;
    }

    /**
     * Returns the m_section (category) for the parameter.
     * 
     * @return m_section the m_section of the parameter
     */
    public String getSection() {
        return m_section;
    }

    /**
     * Sets the section (category) for the parameter.
     * 
     * @param section
     *            the section of the parameter.
     */
    public final void setSection(final String section) {
        m_section = section;
    }

    /**
     * Returns if this parameter has <code>null</code> as m_value.
     * 
     * @return <code>true</code> if the parameter is <code>null</code>,
     *         <code>false</code> otherwise.
     */
    public boolean isNull() {
        if (m_value == null) {
            return true;
        }
        return false;
    }

    /**
     * Returns whether the parameter is deemed optional.
     * 
     * @return True if the parameter is optional, false otherwise.
     */
    public boolean isOptional() {
        return m_isOptional;
    }

    /**
     * Sets whether the parameter is deemed optional.
     * 
     * @param isOptional
     *            flag whether parameter is optional.
     */
    public void setIsOptional(final boolean isOptional) {
        m_isOptional = isOptional;
    }

    /**
     * Returns a textual information about the data type stored in this object.
     * 
     * @return mnemonic of parameter
     */
    public abstract String getMnemonic();

    /**
     * Extracts data stored in the supplied string (previously generated by
     * {@link Parameter#getStringRep()}) representation and set the m_value
     * accordingly.
     * 
     * @param s
     *            special string representation of parameter.
     * @throws InvalidParameterValueException
     *             If the given string does not contain a valid m_value for the
     *             parameter.
     */
    public abstract void fillFromString(String s)
            throws InvalidParameterValueException;

    /**
     * Returns a special string representation which can be transferred through
     * string channels and reconstructed later on using
     * {@link Parameter#fillFromString()}.
     * 
     * @return special string representation of parameter
     */
    public String getStringRep() {
        return toString();
    }

    /**
     * Return whether the parameter is advanced (only for expert users) or not.
     * 
     * @return True if the parameter should only be shown to expert users, false
     *         otherwise.
     */
    public boolean isAdvanced() {
        return m_isAdvanced;
    }

    /**
     * Set whether the parameter is advanced (only for expert users) or not.
     * 
     * @param newAdvanced
     *            New isAdvanced flag for the parameter.
     */
    public final void setAdvanced(final boolean newAdvanced) {
        m_isAdvanced = newAdvanced;
    }
    
    /**
     * Was the parameters default from the tool description used (true) or was the value
     * at least once set (false)?
     * 
     */
    public boolean isDefaulted() {
        return m_defaulted;
    }
    
    /**
     * Set whether the parameters default was used.
     * 
     * @param newAdvanced
     *            New isAdvanced flag for the parameter.
     */
    public final void setDefaulted(final boolean newDefaulted) {
        m_defaulted = newDefaulted;
    }

    /**
     * Checks whether the supplied generic value is compatible with the data
     * type of the parameter.
     * 
     * @param val
     *            value to validate.
     * 
     * @return True if the supplied value is valid, false otherwise.
     */
    public abstract boolean validate(T val);

    /**
     * Separator token.
     */
    public static final String SEPARATOR_TOKEN = "@@@__@@@";

}
