package com.ensemblecp;

public class Validate {
    public static boolean validateInt(String value) {
        try { Integer.parseInt(value); return true; } catch (Exception e) { return false; }
    }

    public static boolean validateFloat(String value) {
        try { Float.parseFloat(value); return true; } catch (Exception e) { return false; }
    }

    public static boolean validateBoolean(String value) {
        try { Boolean.parseBoolean(value); return true; } catch (Exception e) { return false; }
    }

    public static boolean validateString(String value) {
        return value != null;
    }
}
