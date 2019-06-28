package com.genericworkflownodes.knime.nodes.util.dontsave.file;

/*
 * ------------------------------------------------------------------------
 *
 *  Copyright (C) 2003 - 2014
 *  University of Konstanz, Germany and
 *  KNIME GmbH, Konstanz, Germany
 *  Website: http://www.knime.org; Email: contact@knime.org
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License, Version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 *  Additional permission under GNU GPL version 3 section 7:
 *
 *  KNIME interoperates with ECLIPSE solely via ECLIPSE's plug-in APIs.
 *  Hence, KNIME and ECLIPSE are both independent programs and are not
 *  derived from each other. Should, however, the interpretation of the
 *  GNU GPL Version 3 ("License") under any applicable laws result in
 *  KNIME and ECLIPSE being a combined program, KNIME GMBH herewith grants
 *  you the additional permission to use and propagate KNIME together with
 *  ECLIPSE with only the license terms in place for ECLIPSE applying to
 *  ECLIPSE and the GNU GPL Version 3 applying for KNIME, provided the
 *  license terms of ECLIPSE themselves allow for the respective use and
 *  propagation of ECLIPSE together with KNIME.
 *
 *  Additional permission relating to nodes for KNIME that extend the Node
 *  Extension (and in particular that are based on subclasses of NodeModel,
 *  NodeDialog, and NodeView) and that only interoperate with KNIME through
 *  standard APIs ("Nodes"):
 *  Nodes are deemed to be separate and independent programs and to not be
 *  covered works.  Notwithstanding anything to the contrary in the
 *  License, the License does not apply to Nodes, you are not required to
 *  license Nodes under the License, and you are granted a license to
 *  prepare and propagate Nodes, in each case even if such Nodes are
 *  propagated with or for interoperation with KNIME.  The owner of a Node
 *  may freely choose the license terms applicable to such Node, including
 *  when such Node is propagated with or for interoperation with KNIME.
 * ---------------------------------------------------------------------
 *
 * Created on Jul 4, 2014 by dietzc
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIContent;
import org.knime.core.data.uri.URIPortObject;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.node.port.PortTypeRegistry;
import org.knime.core.node.port.inactive.InactiveBranchConsumer;
import org.knime.core.node.port.inactive.InactiveBranchPortObject;
import org.knime.core.node.port.inactive.InactiveBranchPortObjectSpec;
import org.knime.core.node.workflow.LoopEndNode;

import com.genericworkflownodes.knime.base.data.port.FileStoreReferenceURIPortObject;

/**
 * @author Christian Dietz, Alexander Fillbrunn, University of Konstanz
 */
public class DontSaveEndNodeModel extends NodeModel implements InactiveBranchConsumer, LoopEndNode {

    /**
     * Default Constructor
     */
    protected DontSaveEndNodeModel() {
        super(new PortType[] { IURIPortObject.TYPE,
                PortTypeRegistry.getInstance().getPortType(IURIPortObject.class, true),
                PortTypeRegistry.getInstance().getPortType(IURIPortObject.class, true) },
                new PortType[] { IURIPortObject.TYPE,
                        PortTypeRegistry.getInstance().getPortType(IURIPortObject.class, true),
                        PortTypeRegistry.getInstance().getPortType(IURIPortObject.class, true) });
    }

    // specs from first iteration
    private PortObjectSpec[] m_resultPortObjectSpec;

    // copy of portObject of first iteration
    private PortObject[] m_resultPortObject;

    /** {@inheritDoc} */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs) throws InvalidSettingsException {

        if (!(inSpecs[0] instanceof InactiveBranchPortObjectSpec)) {
            // remember inSpecs if branch is active (first iteration)
            m_resultPortObjectSpec = inSpecs;
        }

        return m_resultPortObjectSpec;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inObjects, final ExecutionContext exec) throws Exception {

        if (!(this.getLoopStartNode() instanceof DontSaveStartNodeModel)) {
            throw new IllegalStateException("Don't Save End node is not connected"
                    + " to matching/corresponding Don't Save Start node.");
        }

        if ((inObjects[0] instanceof InactiveBranchPortObject)) {
            // second iteration returns the data && help the garbage collector a bit
            PortObject[] result = new PortObject[inObjects.length];
            fillArray(m_resultPortObject, result);
            m_resultPortObject = null;
            return result;
        } else {
            // first iteration
            // memorize data for later usage
            m_resultPortObject = inObjects;
            continueLoop();
            return m_resultPortObject;
        }
    }
    
    private void fillArray(PortObject[] src, PortObject[] dest) {
        for (int i = 0; i < src.length; i++) {
            if (src[i] != null) {
                dest[i] = src[i];
            } else {
                dest[i] = new URIPortObject(new ArrayList<URIContent>());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        m_resultPortObject = null;
        m_resultPortObjectSpec = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File nodeInternDir, final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // Nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        // Nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
        // Nothing to do here
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
        // Nothing to do here
    }

}
