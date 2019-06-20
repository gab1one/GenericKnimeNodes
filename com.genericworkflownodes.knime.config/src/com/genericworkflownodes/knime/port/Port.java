/**
 * Copyright (c) 2011, Marc Röttig.
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

package com.genericworkflownodes.knime.port;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The Port class represents a incoming or outgoing port of a KNIME node.
 * 
 * Getters can be used to find out the: <br/>
 * <ul>
 * <li>name of the port</li>
 * <li>description for the port</li>
 * <li>list of file extensions supported by this port</li>
 * </ul>
 * 
 * @author roettig, aiche
 * 
 */
public class Port implements Serializable {

    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -3038975820102785198L;

    /**
     * Flag to show if the port is optional for the tool.
     */
    private boolean m_isOptional;
    
    /**
     * Flag to show if the port is active for the tool (can only be false if optional and output port).
     */
    private boolean m_isActive;

    /**
     * The name of the port.
     */
    private String m_name;

    /**
     * The description of the port.
     */
    private String m_description;

    /**
     * Flag to indicate if this port can handle lists of files.
     */
    private boolean m_isMultiFile;

    /**
     * The list of supported file extensions.
     */
    private List<String> m_types = new ArrayList<String>();

    /**
     * Flag to indicate that this port represents an output prefix.
     */
    private boolean m_isPrefix;
    
    /**
     * Index of a linked Port from the opposite type:
     * For outputs it's an input port, for inputs it's an output.
     * -1 if unset.
     * Not encoded in the CTD yet, but only set in KNIME
     * OutputType tab in config dialog.
     */
    private int m_linkedPort = -1;
    
    /**
     * user-defined basename for this port
     */
    private String m_baseName;

    /**
     * Adds a supported {@link MIMEType} to the port.
     * 
     * @param MIMEtype
     *            A new {@link MIMEType} supported by this port.
     */
    public void addMimeType(final String type) {
        m_types.add(type);
    }

    /**
     * Returns the list of supported MIMEtypes of this port.
     * 
     * @return List of all {@link MIMEType}s supported by this port.
     */
    public List<String> getMimeTypes() {
        return m_types;
    }

    /**
     * Returns whether this port is optional or needs a mandatory incoming
     * connection.
     * 
     * @return True if the port is optional, false otherwise.
     */
    public boolean isOptional() {
        return m_isOptional;
    }

    /**
     * Sets whether this port is optional or needs a mandatory incoming
     * connection.
     * 
     * @param m_isOptional
     *            New indicator if the given port is optional or not.
     * 
     */
    public void setOptional(boolean isOptional) {
        m_isOptional = isOptional;
    }
    
    /**
     * Returns whether this port is active or not.
     * 
     * @return True if the port is active, false otherwise.
     */
    public boolean isActive() {
        return m_isActive;
    }
    
    /**
     * Sets whether this port is active or not.
     * 
     * @param m_isActive
     *            New indicator if the given port is active or not.
     * 
     */
    public void setActive(boolean isActive) {
        m_isActive = isActive;
    }

    /**
     * Returns the name of the port
     * 
     * @return port The name of the port.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the name of the port.
     * 
     * @param name
     *            The new port name.
     * 
     */
    public void setName(final String name) {
        m_name = name;
    }

    /**
     * Returns the description for this port.
     * 
     * @return The port description.
     */
    public String getDescription() {
        return m_description;
    }

    /**
     * Sets the description for this port.
     * 
     * @param description
     *            The new description of the port.
     */
    public void setDescription(final String description) {
        m_description = description;
    }

    /**
     * Returns whether this port allows multiple files of a given MIMEtype.
     * 
     * @return True if the port allows multiple files, false otherwise.
     */
    public boolean isMultiFile() {
        return m_isMultiFile;
    }

    /**
     * Set whether this port allows multiple files of a given MIMEtype.
     * 
     * @param isMultiFile
     *            New indicator if more then one value for the given port are
     *            allowed.
     */
    public void setMultiFile(boolean isMultiFile) {
        m_isMultiFile = isMultiFile;
    }

    /**
     * Returns whether this port is an output prefix.
     * 
     * @return True if the port is an output prefix, false otherwise.
     */
    public boolean isPrefix() {
        return m_isPrefix;
    }

    /**
     * Sets whether this port is an output prefix.
     * 
     * @param isPrefix
     *            New value for the output prefix flag.
     */
    public void setIsPrefix(boolean isPrefix) {
        m_isPrefix = isPrefix;
    }
    
    /**
     * Returns the index of a linked Port of the opposite type.
     * I.e. an output port maps to an input and vice versa
     * Especially useful for output name generation.
     * Usually for multiPorts
     * 
     * @return Index of the linked Port
     */
    public int getLinkedPortIndex() {
        return m_linkedPort;
    }

    /**
     * Sets the index of a linked Port of the opposite type.
     * 
     * @param index
     *            index of a linked Port of the opposite type
     */
    public void setLinkedPortIndex(int index) {
        m_linkedPort = index;
    }
    
    /**
     * Returns a defined basename for this Port.
     * To be used e.g. for output file generation.
     * 
     * @return Index of the linked Port
     */
    public String getUserBasename() {
        return m_baseName;
    }

    /**
     * Sets a user defined basename to be used
     * for output file generation.
     * 
     * @param name the new basename
     */
    public void setUserBasename(String name) {
        m_baseName = name;
    }
}
