package com.ccec.dexterservice.managers;

import android.location.Location;

/**
 * Created by manish on 11/11/16.
 */

public class AppData {
    //updated from profile
    public static String serviceType = "Car";

    public static String selectedLoc = "";
    public static Location selectedCordLoc = null;

    public static int selectedItem = 0;

    public static boolean setOne = true;

    public static Object currentVeh = null;
    public static Object currentVehCust = null;

    public static String currentPath = "";
    public static String currentSelectedUser = "";

    public static int getSelectedItem() {
        return selectedItem;
    }

    public static void setSelectedItem(int selectedItem) {
        AppData.selectedItem = selectedItem;
    }
}
