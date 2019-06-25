/**
 * Copyright (c) 2013, aiche.
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
package com.genericworkflownodes.knime.nodes.io.outputfiles;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.program.Program;
import org.knime.core.node.NodeView;
import org.knime.core.util.FileUtil;

/**
 * @author aiche
 * 
 */
public class OpenFolderNodeView extends NodeView<OutputFilesNodeModel> {

    protected OpenFolderNodeView(OutputFilesNodeModel nodeModel) {
        super(nodeModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onClose() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onOpen() {
        setShowNODATALabel(true);
    }

    public void openFolder() throws IOException {
        String f_name = getNodeModel().m_filename.getStringValue();
        if (!"".equals(f_name)) {
            File f = FileUtil.getFileFromURL(FileUtil.toURL(f_name));
            if (f.getParentFile() != null)
                Program.launch(f.getParentFile().getCanonicalPath());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void modelChanged() {
        try {
            setShowNODATALabel(true);
            openFolder();
        } catch (IOException e) {
            getLogger().error(
                    "Could not open the folder for the selected output files.");
            getLogger().error(e.getMessage());
            e.printStackTrace();
        }
    }

}
