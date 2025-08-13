package me.pafias.putils;

public class InventoryUtils {

    public static int parseSizeToInvSize(int size) {
        if (size <= 9) return 9;
        else if (size <= 18) return 18;
        else if (size <= 27) return 27;
        else if (size <= 36) return 36;
        else if (size <= 45) return 45;
        else return 54;
    }

}
