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

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

/**
 * Provides JavaFX media player services
 * @author uniCenta
 */


public class VideoPlayer extends Application {    
    public static void main(String[] args) { launch(args); }
        private static final String MEDIA_URL =
// Testing URL
                "http://unicenta.org/downloads/unicentaopos_v3Beta1_preview.mp4";
       
@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("uniCenta Media Player");
        Group root = new Group();
        Scene scene = new Scene(root, 540, 210);
// create media player
        Media media = new Media(MEDIA_URL);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setAutoPlay(true);
// create mediaView and add media player to the viewer
        MediaView mediaView = new MediaView(mediaPlayer);
        ((Group)scene.getRoot()).getChildren().add(mediaView);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
   
}
