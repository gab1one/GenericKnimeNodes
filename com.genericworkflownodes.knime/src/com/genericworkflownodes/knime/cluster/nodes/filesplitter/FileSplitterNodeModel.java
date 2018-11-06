/**
 * Copyright (c) 2012, Stephan Aiche.
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
package com.genericworkflownodes.knime.cluster.nodes.filesplitter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;

import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataTableSpecCreator;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.DataContainer;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.filestore.FileStore;
import org.knime.core.data.uri.IURIPortObject;
import org.knime.core.data.uri.URIPortObjectSpec;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObject;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.port.PortType;
import org.knime.core.util.FileUtil;

import com.genericworkflownodes.knime.base.data.port.FileStoreURIPortObject;
import com.genericworkflownodes.knime.base.data.port.SerializableFileStoreCell;
import com.genericworkflownodes.knime.base.data.port.SimpleFileStoreCell;
import com.genericworkflownodes.knime.cluster.filesplitter.Splitter;
import com.genericworkflownodes.knime.cluster.filesplitter.SplitterFactory;
import com.genericworkflownodes.knime.cluster.filesplitter.SplitterFactoryManager;
import com.genericworkflownodes.util.MIMETypeHelper;

/**
 * This is the model implementation of FileMerger. This nodes takes two files
 * (file lists) as input and outputs a merged list of both inputs.
 *
 * @author aiche
 */
public class FileSplitterNodeModel extends NodeModel {

    /*
     * The logger instance. (currently unused)
     */
    // private static final NodeLogger logger = NodeLogger
    // .getLogger(FileMergerNodeModel.class);

    private static final String FACTORY_ID_KEY = "factoryID";

    private static final String NUM_PARTS_KEY = "numParts";

    public static SettingsModelString createFactoryIDSettingsModel() {
        return new SettingsModelString(FACTORY_ID_KEY, null);
    }

    public static SettingsModelInteger createNumPartsSettingsModel() {
        return new SettingsModelInteger(NUM_PARTS_KEY, 2);
    }

    private Splitter m_splitter;
    private SettingsModelString m_factoryID = createFactoryIDSettingsModel();
    private SettingsModelInteger m_numParts = createNumPartsSettingsModel();

    /**
     * Static method that provides the incoming {@link PortType}s.
     *
     * @return The incoming {@link PortType}s of this node.
     */
    private static PortType[] getIncomingPorts() {
        return new PortType[] { IURIPortObject.TYPE };
    }

    /**
     * Static method that provides the outgoing {@link PortType}s.
     *
     * @return The outgoing {@link PortType}s of this node.
     */
    private static PortType[] getOutgoing() {
        return new PortType[] { BufferedDataTable.TYPE };
    }

    /**
     * Constructor for the node model.
     */
    protected FileSplitterNodeModel() {
        super(getIncomingPorts(), getOutgoing());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObject[] execute(final PortObject[] inData,
            final ExecutionContext exec) throws Exception {
        IURIPortObject input = (IURIPortObject) inData[0];

        if (input.getURIContents().size() != 1) {
            throw new InvalidSettingsException("This node can only split a single file");
        }

        // The factory for creating the splitter
        String factoryID = m_factoryID.getStringValue();
        SplitterFactory factory = SplitterFactoryManager.getInstance().getFactory(factoryID);

        if (factory == null) {
            throw new InvalidSettingsException("No splitter configured for the input files.");
        }
        m_splitter = factory.createSplitter();

        File f = FileUtil.getFileFromURL(input.getURIContents().get(0).getURI().toURL());

        // File Store in which we store the files
        FileStore fs = exec.createFileStore("FileSplitter");

        File[] outputs = new File[m_numParts.getIntValue()];
        for (int i = 0; i < m_numParts.getIntValue(); i++) {
            int idx = f.getPath().lastIndexOf('.');
            String ext;
            String name;
            if (idx == -1) {
                ext = "";
                name = f.getName();
            } else {
                ext = f.getPath().substring(idx);
                name = f.getName().substring(0, f.getName().lastIndexOf('.'));
            }
            outputs[i] = Paths.get(fs.getFile().toString()).resolve(name + i + ext).toFile();
            outputs[i].getParentFile().mkdirs();
        }

        m_splitter.split(f, outputs);
        DataContainer dc = exec.createDataContainer(createSpec());

        for (int i = 0; i < m_numParts.getIntValue(); i++) {
            FileStoreURIPortObject po = new FileStoreURIPortObject(fs);
            String relPath = Paths.get(fs.getFile().toString())
                                .relativize(Paths.get(outputs[i].getAbsolutePath()))
                                .toString();
            po.registerFile(relPath);
            SimpleFileStoreCell cell = new SimpleFileStoreCell(fs, Collections.singletonList(relPath));

            dc.addRowToTable(new DefaultRow(new RowKey("Row" + i), cell));
        }

        dc.close();

        return new PortObject[] {(BufferedDataTable)dc.getTable()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
    }

    private DataTableSpec createSpec() {
        DataTableSpecCreator specCreator = new DataTableSpecCreator();
        specCreator.addColumns(new DataColumnSpecCreator("files", SerializableFileStoreCell.TYPE).createSpec());
        return specCreator.createSpec();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PortObjectSpec[] configure(final PortObjectSpec[] inSpecs)
            throws InvalidSettingsException {
        String factoryID = m_factoryID.getStringValue();
        URIPortObjectSpec spec = (URIPortObjectSpec)inSpecs[0];

        // If no factory has been selected in the dialog, we take the first one that matches
        if (factoryID == null) {
            String ext = spec.getFileExtensions().get(0);
            String mime = MIMETypeHelper.getMIMEtypeByExtension(ext).orElse(null);

            Iterator<SplitterFactory> factories = SplitterFactoryManager
                                                    .getInstance()
                                                    .getFactories(mime)
                                                    .iterator();
            if (!factories.hasNext()) {
                throw new InvalidSettingsException("No suitable splitter found for mimetype " + mime + ".");
            }
            SplitterFactory fac = factories.next();
            factoryID = fac.getID();
            m_factoryID.setStringValue(factoryID);
            setWarningMessage("No splitter selected. Choosing " + factoryID + ".");
        } else if (SplitterFactoryManager.getInstance().getFactory(factoryID) == null) {
            throw new InvalidSettingsException("No splitter available for the input file types.");
        }
        return new PortObjectSpec[] {createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
        m_factoryID.saveSettingsTo(settings);
        if (m_splitter != null) {
            m_splitter.saveSettingsTo(settings);
        }
        m_numParts.saveSettingsTo(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        if (m_factoryID != null) {
            m_factoryID.loadSettingsFrom(settings);
            SplitterFactory factory = SplitterFactoryManager.getInstance()
            .getFactory(m_factoryID.getStringValue());
            if (factory != null) {
                m_splitter = factory.createSplitter();
                m_splitter.loadSettingsFrom(settings);
            }
        }
        m_numParts.loadSettingsFrom(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        m_factoryID.validateSettings(settings);
        m_numParts.validateSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
    }

}
