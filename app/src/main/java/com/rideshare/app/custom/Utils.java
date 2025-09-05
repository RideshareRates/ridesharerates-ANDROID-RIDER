package com.rideshare.app.custom;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    // get current date in specific format
    public String getCurrentDateInSpecificFormat(String date) {

        Calendar currentCalDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        try {
            currentCalDate.setTime(sdf.parse(date));// all done
        } catch (ParseException e) {

        }

        String dayNumberSuffix = getDayNumberSuffix(currentCalDate.get(Calendar.DAY_OF_MONTH));
        DateFormat dateFormat = new SimpleDateFormat("d'" + dayNumberSuffix + "' MMM yyyy");
        return dateFormat.format(currentCalDate.getTime());
    }

    // get day number
    private String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    // gte minimal address
    public static String getMinimalAddress(Context context, double latitude, double longitude) throws IOException {
        String address = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();
        address = city + ", " + state + " " + postalCode + ", " + country + ".";

        return address;
    }

    // to validate mobile
    public static boolean isValidMobile(String mobile) {
        String regex = "\\(\\d{3}\\)\\s\\d{3}-\\d{4}";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (mobile == null) {
            return false;
        }
        // Pattern class contains matcher()
        //  method to find matching between
        //  given string and regular expression.
        Matcher m = p.matcher(mobile);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    // to check string is null or empty
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.equalsIgnoreCase("null") || value.trim().length() == 0;
    }

    // getting location from address
    public static Address getLatLongFromAddress(String address,Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Address location = null;
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                location = addresses.get(0);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // Use the latitude and longitude as needed
                System.out.println("Latitude: " + latitude + ", Longitude: " + longitude);
            } else {
                // Handle the case where no address was found
                System.out.println("No location found for the given address.");
            }
        } catch (IOException e) {
            // Handle the exception
            e.printStackTrace();
            System.out.println("No location found " + e.getMessage());
        }
        return location;
    }

}
