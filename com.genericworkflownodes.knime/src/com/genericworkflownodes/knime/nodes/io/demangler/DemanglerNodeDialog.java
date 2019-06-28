/**
 * Copyright (c) 2011-2012, Marc Röttig, Stephan Aiche.
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
package com.genericworkflownodes.knime.nodes.io.demangler;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.port.PortObjectSpec;

import com.genericworkflownodes.knime.mime.demangler.DemanglerRegistry;
import com.genericworkflownodes.knime.mime.demangler.IDemangler;
import com.genericworkflownodes.util.MIMETypeHelper;
import com.genericworkflownodes.util.ui.ChoiceDialog;
import com.genericworkflownodes.util.ui.ChoiceDialogListener;

/**
 * <code>NodeDialog</code> for the "IDemangler" Node.
 *
 *
 * @author aiche, roettig
 */
public class DemanglerNodeDialog extends NodeDialogPane implements
        ChoiceDialogListener {

    /**
     * The ChoiceElement to select the correct {@link IDemangler}.
     */
    private ChoiceDialog choice;

    /**
     * The DataModel for the ChoiceDialog.
     */
    private DefaultComboBoxModel model;

    /**
     * The actual selected demangler.
     */
    private String demanglerClassName;

    /**
     * The currently configured {@link MIMEType}.
     */
    private String configuredFileExtension;

    /**
     * Default c'tor.
     */
    protected DemanglerNodeDialog() {
        super();

        model = new DefaultComboBoxModel();

        choice = new ChoiceDialog(model);
        choice.registerChoiceListener(this);

        addTab("Demanglers", choice);

        // we assume there is no demangler selected
        demanglerClassName = "";
    }

    @Override
    protected void saveSettingsTo(NodeSettingsWO settings)
            throws InvalidSettingsException {
        settings.addString(DemanglerNodeModel.SELECTED_DEMANGLER_SETTINGNAME,
                demanglerClassName);
        settings.addString(
                DemanglerNodeModel.CONFIGURED_FILE_EXTENSION_SETTINGNAME,
                configuredFileExtension);
    }

    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {

        List<IDemangler> availableDemangler = new ArrayList<IDemangler>();
        String demanglerClassName = "";
        try {
            demanglerClassName = settings.getString(
                    DemanglerNodeModel.SELECTED_DEMANGLER_SETTINGNAME, "");
            configuredFileExtension = settings
                    .getString(DemanglerNodeModel.CONFIGURED_FILE_EXTENSION_SETTINGNAME);

            String mimeType = MIMETypeHelper.getMIMEtypeByExtension(configuredFileExtension).orElse(null);

            availableDemangler = DemanglerRegistry.getDemanglerRegistry()
                    .getDemangler(mimeType);
        } catch (InvalidSettingsException e) {
            e.printStackTrace();
        }

        model.removeAllElements();
        for (IDemangler d : availableDemangler) {
            model.addElement(d.getClass().getName());
        }

        // select already configured demangler -> find by class name
        if (!"".equals(demanglerClassName)) {
            int indexToSelect = model.getIndexOf(demanglerClassName);
            if (indexToSelect != -1) {
                model.setSelectedItem(demanglerClassName);
            }
        } else {
            // there is no pre-selected demangler
            model.setSelectedItem(model.getElementAt(0));
        }
    }

    @Override
    public void onChoice(final int selectedIndex) {
        demanglerClassName = (String) model.getElementAt(selectedIndex);
    }
}