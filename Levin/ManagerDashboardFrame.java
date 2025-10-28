package view;

import controller.SlotController;
import controller.ManagerController;
import javax.swing.*;
import java.awt.*;

public class ManagerDashboardFrame extends JFrame {
    private JTextArea output;
    private JLabel statusLabel;

    public ManagerDashboardFrame() {
        setTitle("Manager Dashboard - Parking System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);

        initializeUI();
        refreshData();
        setVisible(true);
    }

    private void initializeUI() {
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 102, 204));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Manager Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        // Button panel
        JPanel buttonPanel = createButtonPanel();

        // Output area
        output = new JTextArea();
        output.setFont(new Font("Consolas", Font.PLAIN, 14));
        output.setEditable(false);
        output.setBackground(new Color(248, 248, 248));
        output.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(output);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Parking Slots Overview"));

        // Layout
        setLayout(new BorderLayout(10, 10));
        add(headerPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.WEST);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);

        String[][] buttonData = {
            {"Add Slot", "GREEN"}, {"Delete Slot", "RED"}, 
            {"Update Slot", "ORANGE"}, {"Park Vehicle", "BLUE"},
            {"Release Vehicle", "MAGENTA"}, {"Refresh", "GRAY"}, 
            {"Logout", "DARK_GRAY"}
        };

        for (String[] data : buttonData) {
            JButton btn = new JButton(data[0]);
            btn.setBackground(getColor(data[1]));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setFont(new Font("Arial", Font.BOLD, 12));
            btn.setPreferredSize(new Dimension(150, 35));

            switch (data[0]) {
                case "Add Slot": btn.addActionListener(e -> addSlot()); break;
                case "Delete Slot": btn.addActionListener(e -> deleteSlot()); break;
                case "Update Slot": btn.addActionListener(e -> new UpdateSlotFrame(this)); break;
                case "Park Vehicle": btn.addActionListener(e -> parkVehicle()); break;
                case "Release Vehicle": btn.addActionListener(e -> releaseVehicle()); break;
                case "Refresh": btn.addActionListener(e -> refreshData()); break;
                case "Logout": btn.addActionListener(e -> logout()); break;
            }

            panel.add(btn);
        }

        return panel;
    }

    private Color getColor(String colorName) {
        switch (colorName) {
            case "GREEN": return Color.GREEN;
            case "RED": return Color.RED;
            case "ORANGE": return Color.ORANGE;
            case "BLUE": return Color.BLUE;
            case "MAGENTA": return Color.MAGENTA;
            case "GRAY": return Color.GRAY;
            case "DARK_GRAY": return Color.DARK_GRAY;
            default: return Color.BLACK;
        }
    }

    private void addSlot() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Slot ID:", "Add New Slot", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null) return;
            
            int id = Integer.parseInt(idStr);
            
            String[] types = {"2W", "4W"};
            String type = (String) JOptionPane.showInputDialog(this, 
                "Select Vehicle Type:", "Slot Type", 
                JOptionPane.QUESTION_MESSAGE, null, types, types[0]);

            if (type != null) {
                if (SlotController.addSlot(id, type)) {
                    refreshData();
                    showMessage("Slot " + id + " added successfully!", "Success");
                } else {
                    showMessage("Slot ID " + id + " already exists!", "Duplicate Slot");
                }
            }
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid slot ID number!", "Invalid Input");
        }
    }

    private void deleteSlot() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Slot ID to Delete:", "Delete Slot", JOptionPane.WARNING_MESSAGE);
            if (idStr == null) return;
            
            int id = Integer.parseInt(idStr);
            
            if (SlotController.getSlotById(id) == null) {
                showMessage("Slot ID " + id + " not found!", "Slot Not Found");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete Slot " + id + "?", "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (SlotController.deleteSlot(id)) {
                    refreshData();
                    showMessage("Slot " + id + " deleted successfully!", "Success");
                } else {
                    showMessage("Cannot delete occupied slot!", "Cannot Delete");
                }
            }
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid slot ID number!", "Invalid Input");
        }
    }

    private void parkVehicle() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Slot ID to Park Vehicle:", "Park Vehicle", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null) return;
            
            int id = Integer.parseInt(idStr);
            
            if (ManagerController.parkVehicle(id)) {
                refreshData();
                showMessage("Vehicle parked in Slot " + id + "!", "Success");
            } else {
                showMessage("Slot must be booked first!", "Cannot Park");
            }
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid slot ID number!", "Invalid Input");
        }
    }

    private void releaseVehicle() {
        try {
            String idStr = JOptionPane.showInputDialog(this, "Enter Slot ID to Release:", "Release Vehicle", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null) return;
            
            int id = Integer.parseInt(idStr);
            
            if (ManagerController.releaseVehicle(id)) {
                refreshData();
                showMessage("Slot " + id + " released successfully!", "Success");
            } else {
                showMessage("Slot " + id + " not found or not occupied!", "Cannot Release");
            }
        } catch (NumberFormatException ex) {
            showMessage("Please enter a valid slot ID number!", "Invalid Input");
        }
    }

    public void refreshData() {
        SlotController.loadSlotsFromDB();
        output.setText(SlotController.getSlotsSummary());
        statusLabel.setText("Last updated: " + new java.util.Date());
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame();
        }
    }

    private void showMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Example of running the Manager Dashboard
        SwingUtilities.invokeLater(() -> {
            new ManagerDashboardFrame();
        });
    }
}