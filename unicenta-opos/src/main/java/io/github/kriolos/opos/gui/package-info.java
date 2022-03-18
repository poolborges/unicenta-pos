/*
 * Copyright (C) 2022 Paulo Borges
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
 */
@OptionsPanelController.ContainerRegistration(id = "PosOptions", categoryName = "#OptionsCategory_Name_PosOptions", iconBase = "io/github/kriolos/opos/gui/icons/app_logo32.png", keywords = "#OptionsCategory_Keywords_PosOptions", keywordsCategory = "PosOptions")
@NbBundle.Messages(value = {"OptionsCategory_Name_PosOptions=KriolOS POS", "OptionsCategory_Keywords_PosOptions=krospos"})
package io.github.kriolos.opos.gui;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.NbBundle;

