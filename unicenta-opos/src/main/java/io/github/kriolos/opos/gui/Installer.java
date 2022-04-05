/*
 * Copyright (C) 2022 KriolOS
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
package io.github.kriolos.opos.gui;

import java.util.Date;
import java.util.Timer;
import java.util.logging.Logger;
import org.openide.modules.ModuleInstall;
import org.openide.windows.OnShowing;

/**
 *
 * @author poolborges
 */
@OnShowing
public class Installer extends ModuleInstall implements Runnable {

    public static final Logger LOGGER = Logger.getLogger(Installer.class.getName());
    private static Timer timer;
    private static final long serialVersionUID = 1L;

    public Installer() {
        super();
        
        timer = new Timer();
        
        Schedule timetask = new Schedule();
        
        timer.scheduleAtFixedRate(timetask, new Date(), 60_000l * 30); // (60s x 30) 30min timeout
        
        System.out.println("Kriolos.POS Schedule has loaded.");
    }

    @Override
    public void run() {
        System.out.println("Kriolos.POS Schedule is running.");
    }

    @Override
    public void close() {
        if(timer != null){
            timer.cancel();
        }
        System.out.println("Kriolos.POS Schedule is closed.");
    }
}