package controller;

import model.Slot;

public class ManagerController {
    
    public static boolean parkVehicle(int slotId) {
        Slot slot = SlotController.getSlotById(slotId);
        if (slot != null && slot.getStatus().equals("Booked")) {
            return SlotController.updateSlotStatus(slotId, "Parked", slot.getBookedBy());
        }
        return false;
    }
    
    public static boolean releaseVehicle(int slotId) {
        Slot slot = SlotController.getSlotById(slotId);
        if (slot != null && !slot.getStatus().equals("Empty")) {
            return SlotController.updateSlotStatus(slotId, "Empty", "");
        }
        return false;
    }

    public static boolean bookSlot(int slotId, String username) {
        Slot slot = SlotController.getSlotById(slotId);
        if (slot != null && slot.getStatus().equals("Empty")) {
            return SlotController.updateSlotStatus(slotId, "Booked", username);
        }
        return false;
    }


    public static boolean releaseUserSlot(int slotId, String username) {
        Slot slot = SlotController.getSlotById(slotId);
        if (slot != null && slot.getStatus().equals("Booked") && slot.getBookedBy().equals(username)) {
            return SlotController.updateSlotStatus(slotId, "Empty", "");
        }
        return false;
    }
}
