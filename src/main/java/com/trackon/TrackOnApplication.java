package com.trackon;

import com.formdev.flatlaf.FlatLightLaf;
import com.trackon.view.LoginView;

import javax.swing.*;
//import java.awt.*;

public class TrackOnApplication {
    public static void main(String[] args) {
        try {
            // Set up the look and feel
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf");
        }
        
        // Create and show the login window
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("TrackOn - Certificate Tracking System");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            
            // Add the login view
            frame.add(new LoginView(frame));
            
            frame.setVisible(true);
        });
    }
} 