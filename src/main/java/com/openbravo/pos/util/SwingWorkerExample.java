/*
 * Copyright (C) 2016 uniCenta <info at unicenta.com>
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
package com.openbravo.pos.util;

/**
 *
 * @author uniCenta <info at unicenta.com>
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SwingWorkerExample extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final JButton startButton, stopButton;
    private final JScrollPane scrollPane = new JScrollPane();
    private JList listBox = null;
    private final DefaultListModel listModel = new DefaultListModel();
    private final JProgressBar progressBar;
    private mySwingWorker swingWorker;

    public SwingWorkerExample() {
        super("SwingWorkerExample");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(2, 2));
        startButton = makeButton("Start");
        stopButton = makeButton("Stop");
        stopButton.setEnabled(false);
        progressBar = makeProgressBar(0, 99);
        listBox = new JList(listModel);
        scrollPane.setViewportView(listBox);
        getContentPane().add(scrollPane);
        //Display the window.
        pack();
        setVisible(true);
    }
//Class SwingWorker<T,V> T - the result type returned by this SwingWorker's doInBackground
//and get methods V - the type used for carrying out intermediate results by this SwingWorker's 
//publish and process methods

    private class mySwingWorker extends javax.swing.SwingWorker<ArrayList<Integer>, Integer> {
//The first template argument, in this case, ArrayList<Integer>, is what s returned by doInBackground(), 
//and by get(). The second template argument, in this case, Integer, is what is published with the 
//publish method. It is also the data type which is stored by the java.util.List that is the parameter
//for the process method, which recieves the information published by the publish method.

        @Override
        protected ArrayList<Integer> doInBackground() {
//Returns items of the type given as the first template argument to the SwingWorker class.
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                System.out.println("javax.swing.SwingUtilities.isEventDispatchThread() returned true.");
            }
            Integer tmpValue = 1;
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) { //find every 100th prime, just to make it slower
                    tmpValue = FindNextPrime(tmpValue);
//isCancelled() returns true if the cancel() method is invoked on this class. That is the proper way
//to stop this thread. See the actionPerformed method.
                    if (isCancelled()) {
                        System.out.println("SwingWorker - isCancelled");
                        return list;
                    }
                }
//Successive calls to publish are coalesced into a java.util.List, which is what is received by process, 
//which in this case, isused to update the JProgressBar. Thus, the values passed to publish range from 
//1 to 100.
                publish(i);
                list.add(tmpValue);
            }
            return list;
        }//Note, always use java.util.List here, or it will use the wrong list.

        @Override
        protected void process(java.util.List<Integer> progressList) {
//This method is processing a java.util.List of items given as successive arguments to the publish method.
//Note that these calls are coalesced into a java.util.List. This list holds items of the type given as the
//second template parameter type to SwingWorker. Note that the get method below has nothing to do with the 
//SwingWorker get method; it is the List's get method. This would be a good place to update a progress bar.
            if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
                System.out.println("javax.swing.SwingUtilities.isEventDispatchThread() + returned false.");
            }
            Integer percentComplete = progressList.get(progressList.size() - 1);
            progressBar.setValue(percentComplete);
        }

        @Override
        protected void done() {
            System.out.println("doInBackground is complete");
            if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
                System.out.println("javax.swing.SwingUtilities.isEventDispatchThread() + returned false.");
            }
            try {
//Here, the SwingWorker's get method returns an item of the same type as specified as the first type parameter
//given to the SwingWorker class.
                ArrayList<Integer> results = get();
                results.stream().forEach((i) -> {
                    listModel.addElement(i.toString());
                });
            } catch (InterruptedException | ExecutionException e) {
                System.out.println("Caught an exception: " + e);
            }
            startButton();
        }

        boolean IsPrime(int num) { //Checks whether a number is prime
            int i;
            for (i = 2; i <= num / 2; i++) {
                if (num % i == 0) {
                    return false;
                }
            }
            return true;
        }

        protected Integer FindNextPrime(int num) { //Returns next prime number from passed arg.       
            do {
                if (num % 2 == 0) {
                    num++;
                } else {
                    num += 2;
                }
            } while (!IsPrime(num));
            return num;
        }
    }

    private JButton makeButton(String caption) {
        JButton b = new JButton(caption);
        b.setActionCommand(caption);
        b.addActionListener(this);
        getContentPane().add(b);
        return b;
    }

    private JProgressBar makeProgressBar(int min, int max) {
        JProgressBar progressBar1 = new JProgressBar();
        progressBar1.setMinimum(min);
        progressBar1.setMaximum(max);
        progressBar1.setStringPainted(true);
        progressBar1.setBorderPainted(true);
        getContentPane().add(progressBar1);
        return progressBar1;
    }

    private void startButton() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        System.out.println("SwingWorker - Done");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Start" == null ? e.getActionCommand() == null : "Start".equals(e.getActionCommand())) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
// Note that it creates a new instance of the SwingWorker-derived class. Never reuse an old one.
            (swingWorker = new mySwingWorker()).execute(); // new instance
        } else if ("Stop" == null ? e.getActionCommand() == null : "Stop".equals(e.getActionCommand())) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            swingWorker.cancel(true); // causes isCancelled to return true in doInBackground
            swingWorker = null;
        }
    }

    public static void main(String[] args) {
// Notice that it kicks it off on the event-dispatching thread, not the main thread.
        SwingUtilities.invokeLater(() -> {
            SwingWorkerExample swingWorkerExample = new SwingWorkerExample();
        });
    }
}
