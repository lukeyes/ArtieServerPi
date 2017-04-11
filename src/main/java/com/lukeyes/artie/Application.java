package com.lukeyes.artie;

import javax.swing.*;

/**
 * Created by luke on 4/11/2017.
 */
public class Application {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                new Runnable() {
                    @Override
                    public void run() {
                       ChatServer.getInstance();
                    }
                }
        );
    }
}
