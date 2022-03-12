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
import java.util.TimerTask;
import org.openide.util.Exceptions;

/**
 *
 * @author poolborges
 */
public class Schedule extends TimerTask {

    public Schedule() {
        try {
            createSchedule();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    
    
    @Override
    public void run() {
        try {
            updateSchedule();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void createSchedule() {
        System.out.println("call createSchedule "+new Date());
    }
    
    private void updateSchedule(){
        System.out.println("call updateSchedule: "+new Date());
    }
    
}
