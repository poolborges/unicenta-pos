//  KrOS POS  - Open Source Point Of Sale
//  Copyright (c) 2017 uniCenta
//  https://unicenta.com
//
//  This file is part of uniCenta Remote Display
//
//  uniCenta Remote Display is free software: you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  uniCenta Remote Display is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with KrOS POS.  If not, see <http://www.gnu.org/licenses/>.

/* uniCenta's OrderPop is a simple utilty to list tickets sent to remote printer
 * It connects to and list KrOS POS orders table rows and requires a 
 * manual refresh using the button.
 * It runs independently of KrOS POS and uses the provided orderpop.bat

*/

package com.unicenta.orderpop;

import javafx.application.Application;
import javafx.collections.*;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public class OrderPop extends Application {

    private static final Logger logger = Logger.getLogger(OrderPop.class.getName());

    public static void main(String[] args) { 
        launch(args); 
    }
   
  
    // Executor's DB init's concurrent JavaFX operations
    // Future init's DB setup has been complete
    private ExecutorService databaseExecutor;
    private Future databaseSetupFuture;

    // init's the app'
    // set the DB Executor thread pool size to 1 ensure 
    // only one DB command is executed at any one time
    @Override public void init() throws Exception {
        databaseExecutor = Executors.newFixedThreadPool(
            1, 
            new DatabaseThreadFactory()
            );  

        // run the DB parallel to the JavaFX application setup
        DBSetupTask setup = new DBSetupTask();
        databaseSetupFuture = databaseExecutor.submit(setup);
    }

    // shutdown the app
    @Override public void stop() throws Exception {
        databaseExecutor.shutdown();
    
        if (!databaseExecutor.awaitTermination(3, TimeUnit.SECONDS)) {
            logger.info("DB thread time-out + 3 sec's not shut down clean");
        }
        
        Platform.exit();        
    }
    
    // Pop the UI will use fxml rather than in code css to set styling later for release  
    @Override public void start(Stage stage) throws InterruptedException, ExecutionException {
        databaseSetupFuture.get();

        final ListView<String> orderView = new ListView<>();
        final ProgressIndicator databaseActivityIndicator = new ProgressIndicator();

        Image fetchO = new Image(getClass().getResourceAsStream("/com/openbravo/images/resources.png"));
        final Button fetchOrders = new Button("", new ImageView(fetchO));
//        fetchOrders.setPrefSize(80, 45);

        // fetch Orders table data and fill the list
        databaseActivityIndicator.setVisible(false);
        fetchOrders.setOnAction(event ->
            fetchDBOrdersListView(
                fetchOrders,
                databaseActivityIndicator,
                orderView
            ));

    /* we don't need the clear button but useful when testing    
        Image clearO = new Image(getClass().getResourceAsStream("/com/openbravo/images/reload.png"));
        final Button clearOrderList = new Button("", new ImageView(clearO));
        fetchOrders.setPrefSize(80, 45);
        clearOrderList.setOnAction(event -> orderView.getItems().clear());
    */    
    /*  we want to keep this open and user has to use the window frame's x
        Image closeO = new Image(getClass().getResourceAsStream("/com/openbravo/images/fileclose.png"));        
        closeO.setPrefSize(80, 45);        
        final Button closeOrderList = new Button("", new ImageView(closeO));
        closeOrderList.setOnAction(event ->
                Platform.exit());
    */
        
        VBox layout = new VBox(10);
        layout.setStyle("-fx-background-color: aliceblue; -fx-padding: 5;");
        layout.getChildren().setAll(
            new HBox(10,
                fetchOrders,
//                clearOrderList,
//                closeOrderList,        
                databaseActivityIndicator
                ),
            orderView
        );

        layout.setPrefHeight(300);
        layout.setPrefWidth(500);
        
        stage.getIcons().add(new Image("/com/openbravo/images/app_logo_48x48"));
        stage.setTitle("Orders Waiting...");
        stage.setScene(new Scene(layout));
        stage.setAlwaysOnTop(true);        
        stage.show();

    }

    private void fetchDBOrdersListView(
        final Button triggerButton, 
        final ProgressIndicator databaseActivityIndicator, 
        final ListView<String> listView) {
        final FetchOrdersTask fetchOrdersTask = new FetchOrdersTask();
    
        triggerButton.disableProperty().bind(
            fetchOrdersTask.runningProperty()
        );

        databaseActivityIndicator.visibleProperty().bind(
            fetchOrdersTask.runningProperty()
        );

        databaseActivityIndicator.progressProperty().bind(
            fetchOrdersTask.progressProperty()
        );

        fetchOrdersTask.setOnSucceeded(t ->
            listView.setItems(fetchOrdersTask.getValue())
        );

        listView.setStyle("-fx-font-size: 14px;");

// prep for css
//        listView.getStylesheets().add(
//                this.getClass().getResource("listStyle.css").toExternalForm());

        databaseExecutor.submit(fetchOrdersTask);
    }

    abstract class DBTask<T> extends Task<T> {
        DBTask() {
            setOnFailed(t -> logger.log(Level.SEVERE, null, getException()));
        }
    }

    class FetchOrdersTask extends DBTask<ObservableList<String>> {
        @Override protected ObservableList<String> call() throws Exception {
        Thread.sleep(1000); 
            try (Connection con = getConnection()) {
                return fetchOrders(con);
            }
        }

        // Using an ObservableList here but could use a table as well to give more
        // flexibility to highlight specific row/column/cells    
        private ObservableList<String> fetchOrders(Connection con) throws SQLException {
            logger.info("Fetch Orders from DB");
            ObservableList<String> orders = FXCollections.observableArrayList();
            Statement st = con.createStatement();      
            ResultSet rs = st.executeQuery("SELECT orderid, "
//                + "DATE_FORMAT(ordertime,'%b %d %Y %h:%i %p') AS ordertime, "
                + "qty, "
                + "ordertime, "                
                + "details "
                + "FROM orders "
                + "ORDER BY ordertime, orderid");

            while (rs.next()) {
                orders.add(rs.getString("orderid") + " - " 
                    + rs.getString("ordertime") 
                    + " - " + rs.getString("qty")
                    + " * " + rs.getString("details"));
            }
    
            logger.info("Found " + orders.size() + " orders");
            return orders;
        }
    }

/*
 * Check to see if we have a connection to database and 
 * and pop some data in Orders table to prime if needed
*/  
    class DBSetupTask extends DBTask {
    @Override protected Void call() throws Exception {
        try (Connection con = getConnection()) {
            if (!schemaExists(con)) {
            //  un-comment if we need to create a new Orders table
            // createSchema(con);
            // populateDatabase(con);
            }
        }
      
        return null;
    }

    private boolean schemaExists(Connection con) {
        logger.info("Check for Orders table");      
        try {
            Statement st = con.createStatement();      
            st.executeQuery("select count(*) from orders");
            logger.info("Orders table exists");      
        } catch (SQLException ex) {
            logger.info("Create Orders table");
            return false;
        }
        return true;
    }

/* 
 * We don't need to create Orders table as should already exist
 * as created by KrOS POS or unicenta_remote_display apps
    private void createSchema(Connection con) throws SQLException {
      logger.info("Add Orders table to schema if not exist");
      Statement st = con.createStatement();
      String table = "create table orders(id integer, orderid varchar(50))";
      st.executeUpdate(table);
      logger.info("Created schema");
    }
*/
    
/*
 *  Useful if we want to fill the Orders table with some sample data
 *  MySQL-createSampleData.sql - INSERT x12 rows
    private void populateDatabase(Connection con) throws SQLException {
      logger.info("Populating database");      
      Statement st = con.createStatement();      
      for (String order: SAMPLE_ORDER_DATA) {
        st.executeUpdate("insert into orders values(1,'" + order + "')");
      }
      logger.info("Populated database");
    }
*/  
}
  
    private Connection getConnection() throws ClassNotFoundException, SQLException {
    // use explicit connection rather than KrOS POS instance session
    // better for this to be independent but use current instance's credentials

        logger.info("Get DB connection");
         /* TODO
        String url = com.openbravo.pos.forms.AppConfig.getInstance().getProperty("db.URL") +
                com.openbravo.pos.forms.AppConfig.getInstance().getProperty("db.schema") +
                com.openbravo.pos.forms.AppConfig.getInstance().getProperty("db.options");
        String sDBUser = com.openbravo.pos.forms.AppConfig.getInstance().getProperty("db.user");
        String sDBPassword = com.openbravo.pos.forms.AppConfig.getInstance().getProperty("db.password");

        if (sDBUser != null && sDBPassword != null && sDBPassword.startsWith("crypt:")) {

            com.openbravo.pos.util.AltEncrypter cypher = new com.openbravo.pos.util.AltEncrypter("cypherkey" + sDBUser);

            sDBPassword = cypher.decrypt(sDBPassword.substring(6));

        }
        return DriverManager.getConnection(url, sDBUser, sDBPassword);
          */
        
        return null;
    } 

    static class DatabaseThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
        @Override public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "Database-Connection-" 
                    + poolNumber.getAndIncrement() + "-thread");
            thread.setDaemon(true);
            return thread;
        }
    }  
}