package controller;

import model.Slot;
import model.Database;
import model.Payment;

import java.sql.*;
import java.util.*;

public class SlotController {
    private static Map<Integer, Slot> slots = new HashMap<>();

    public static boolean addSlot(int id, String type) {
        if (slots.containsKey(id)) return false;
        
        Slot slot = new Slot(id, type);
        slots.put(id, slot);
        
        try (Connection conn = Database.connect()) {
            String sql = "INSERT INTO slots (id, type, status, bookedBy) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setString(2, type);
            ps.setString(3, "Empty");
            ps.setString(4, "");
            return ps.executeUpdate() > 0;
        } catch(Exception ex) {
            return false;
        }
    }

    public static boolean deleteSlot(int id) {
        if (!slots.containsKey(id)) return false;
        slots.remove(id);
        
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM slots WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch(Exception ex) {
            return false;
        }
    }

    public static boolean updateSlotStatus(int id, String status, String bookedBy) {
        if (!slots.containsKey(id)) return false;
        
        Slot slot = slots.get(id);
        slot.setStatus(status);
        slot.setBookedBy(bookedBy);
        
        try (Connection conn = Database.connect()) {
            String sql = "UPDATE slots SET status=?, bookedBy=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, bookedBy);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        } catch(Exception ex) {
            return false;
        }
    }

    public static void loadSlotsFromDB() {
        slots.clear();
        try (Connection conn = Database.connect()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM slots ORDER BY id");
            while (rs.next()) {
                int id = rs.getInt("id");
                String type = rs.getString("type");
                String status = rs.getString("status");
                String bookedBy = rs.getString("bookedBy");

                Slot s = new Slot(id, type);
                s.setStatus(status);
                s.setBookedBy(bookedBy);

                // Load payment information
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM payments WHERE slot_id = ?")) {
                    ps.setInt(1, id);
                    ResultSet paymentRs = ps.executeQuery();
                    if (paymentRs.next()) {
                        s.setPayment(new Payment(paymentRs));
                    }
                }

                slots.put(id, s);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static Map<Integer, Slot> getSlots() {
        return slots;
    }

    public static Slot getSlotById(int id) {
        return slots.get(id);
    }

    public static List<Slot> getAvailableSlotsByType(String type) {
        List<Slot> availableSlots = new ArrayList<>();
        for (Slot slot : slots.values()) {
            if (slot.getType().equals(type) && slot.getStatus().equals("Empty")) {
                availableSlots.add(slot);
            }
        }
        return availableSlots;
    }

    public static List<Slot> getSlotsByType(String type) {
        List<Slot> allSlotsOfType = new ArrayList<>();
        for (Slot slot : slots.values()) {
            if (slot.getType().equals(type)) {
                allSlotsOfType.add(slot);
            }
        }
        return allSlotsOfType;
    }

    public static String getSlotsSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== PARKING SLOTS STATUS ===\n\n");
        
        int total = slots.size();
        int empty = 0;
        int booked = 0;
        int parked = 0;

        for (Slot s : slots.values()) {
            sb.append(s).append("\n");
            switch (s.getStatus()) {
                case "Empty": empty++; break;
                case "Booked": booked++; break;
                case "Parked": parked++; break;
            }
        }

        sb.append("\n=== SUMMARY ===\n");
        sb.append("Total Slots: ").append(total).append("\n");
        sb.append("Empty: ").append(empty).append("\n");
        sb.append("Booked: ").append(booked).append("\n");
        sb.append("Parked: ").append(parked).append("\n");
        sb.append("Occupied: ").append(booked + parked).append("/").append(total);

        return sb.toString();
    }

    public static boolean updateSlot(int id, String type) {
        if (!slots.containsKey(id)) {
            return false;
        }

        try (Connection conn = Database.connect()) {
            String sql = "UPDATE slots SET type=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, type);
            ps.setInt(2, id);
            
            if (ps.executeUpdate() > 0) {
                slots.get(id).setType(type);
                return true;
            }
            return false;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}