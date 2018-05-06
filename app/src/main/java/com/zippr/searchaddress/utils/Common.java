package com.zippr.searchaddress.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;


import com.google.android.gms.maps.model.LatLng;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;



public class Common {


    static ProgressDialog pDialog;
    static Dialog alertDialog = null;
    public static final int ACC_REGISTER_NOTIFICATION_ID = 12334;



    public static boolean getBooleanPerf(Context context, String key,
                                         boolean defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "Pref-Values", Context.MODE_PRIVATE);
        return sharedPref.getBoolean(key, defaultValue);
    }

    public static void deletePref(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "Pref-Values", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean isNetworkAvailable(Context context) {
       /* ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isFailover())
            return false;
        else if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected())
            return true;
        else
            return false;*/


        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }


    public static void displayProgress(Context context) {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.cancel();
            pDialog = null;
        }
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(true);
        pDialog.show();
    }

    public static void stopProgressDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.cancel();
        }

    }



    public static void hidesimpleSingleButtonAlertDialog(Dialog pDialog , Context context) {
        try {
            if (context != null && ((Activity) context).isFinishing()) {
                return;
            }
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void closeKeyBoard(Context context, View view) {
        InputMethodManager in = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    /*public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.online_notification : R.mipmap.ic_launcher;
    }*/


    /*public static void showAccRegNotification(Context context) {
        String title = getStringPref(context, Constants.driverName, "");
        if (context == null || title == null) {
            return;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(getNotificationIcon()).setContentTitle(
                title);
        mBuilder.setContentText(getStringPref(context, Constants.driverMobileNumber, ""));
        mBuilder.setOngoing(true);
        if (!(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(
                    context.getResources(), R.mipmap.ic_launcher));
        }
        mBuilder.setColor(context.getResources().getColor(
                R.color.colorPrimary));
        // Creates an explicit intent for an Activity in your app

        Intent intent = new Intent(context.getApplicationContext(), DashboardActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), (int) System.currentTimeMillis(), intent, 0);

        // The stack builder object will contain an artificial back stack for
        // the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        // TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        // stackBuilder.addParentStack(ResultActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      *//*  PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);*//*

        *//*PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0 *//**//* Request code *//**//*, pIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);*//*
        mBuilder.setContentIntent(pIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(ACC_REGISTER_NOTIFICATION_ID, mBuilder.build());
    }*/

    public static void cancelAccRegNotification(Context ctx) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx
                .getSystemService(ns);
        nMgr.cancel(ACC_REGISTER_NOTIFICATION_ID);
    }

    /*
     * old encrypted
     */

/*
    public static String encrypt(String inputString) {

        String password = "@*%$!eSaHai";
        //  String password =  "@*%$!RetailerApp";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();

            byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");

            //String str = new String(digest);
            KeySpec spec = new PBEKeySpec(Base64.encodeToString(digest, Base64.NO_WRAP).toCharArray(), salt, 1000, 256 + 128);
            SecretKey secretKey = factory.generateSecret(spec);
            //SecretKeySpec secretKeySpec = new SecretKeySpec(digest, "AES");
            byte[] data = secretKey.getEncoded();
            byte[] keyBytes = new byte[256 / 8];
            byte[] ivBytes = new byte[128 / 8];

            System.arraycopy(data, 0, keyBytes, 0, 256 / 8);
            System.arraycopy(data, 0, ivBytes, 0, 128 / 8);

            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(inputString.getBytes());

            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
*/
// new encrypt

    public static String encrypt(String stringToEncode) throws NullPointerException {
        String keyString = "@*%$!eSaHai";

        if (stringToEncode.length() == 0 || stringToEncode == null) {
            throw new NullPointerException("Please give text");
        }

        try {
            SecretKeySpec skeySpec = getKey(keyString);
            byte[] clearText = stringToEncode.getBytes("UTF8");

            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            // Cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParameterSpec);

            String encrypedValue = Base64.encodeToString(cipher.doFinal(clearText), Base64.DEFAULT);
            Log.d("jacek", "Encrypted: " + stringToEncode + " -> " + encrypedValue);
            return encrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }


        /*
     * old decrypt
     */
/*
    public static String decrypt( String decrypt) {
        String password = "@*%$!eSaHai";
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8")); // Change this to "UTF-16" if needed
            byte[] digest = md.digest();

            byte[] salt = new byte[]{1, 2, 3, 4, 5, 6, 7, 8};

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            Log.d("====", Base64.encodeToString(digest, Base64.NO_WRAP));
            KeySpec spec = new PBEKeySpec(Base64.encodeToString(digest, Base64.NO_WRAP).toCharArray(), salt, 1000, 256 + 128);
            SecretKey secretKey = factory.generateSecret(spec);
            //SecretKeySpec secretKeySpec = new SecretKeySpec(digest, "AES");
            byte[] data = secretKey.getEncoded();
            byte[] keyBytes = new byte[256 / 8];
            byte[] ivBytes = new byte[128 / 8];

            System.arraycopy(data, 0, keyBytes, 0, 256 / 8);
            System.arraycopy(data, 0, ivBytes, 0, 128 / 8);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decode(decrypt, Base64.NO_WRAP));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }


    }
*/
    // new decrypt

    public static String decrypt(String text) throws NullPointerException {

        String password = "@*%$!eSaHai";

        if (password.length() == 0 || password == null) {
            throw new NullPointerException("Please give Password");
        }

        if (text.length() == 0 || text == null) {
            throw new NullPointerException("Please give text");
        }

        try {
            SecretKey key = getKey(password);

            // IMPORTANT TO GET SAME RESULTS ON iOS and ANDROID
            final byte[] iv = new byte[16];
            Arrays.fill(iv, (byte) 0x00);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            byte[] encrypedPwdBytes = Base64.decode(text, Base64.DEFAULT);
            // cipher is not thread safe
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            byte[] decrypedValueBytes = (cipher.doFinal(encrypedPwdBytes));

            String decrypedValue = new String(decrypedValueBytes);
            Log.d("", "Decrypted: " + text + " -> " + decrypedValue);
            return decrypedValue;

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * Generates a SecretKeySpec for given password
     *
     * @param password
     * @return SecretKeySpec
     * @throws UnsupportedEncodingException
     */
    private static SecretKeySpec getKey(String password) throws UnsupportedEncodingException {

        // You can change it to 128 if you wish
        int keyLength = 256;
        byte[] keyBytes = new byte[keyLength / 8];
        // explicitly fill with zeros
        Arrays.fill(keyBytes, (byte) 0x0);

        // if password is shorter then key length, it will be zero-padded
        // to key length
        byte[] passwordBytes = password.getBytes("UTF-8");
        int length = passwordBytes.length < keyBytes.length ? passwordBytes.length : keyBytes.length;
        System.arraycopy(passwordBytes, 0, keyBytes, 0, length);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        return key;
    }





    public static boolean checkLocationPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean isLocationServiceEnabled(LocationManager lm) {

        boolean gps_enabled = false;
        boolean network_enabled = false;
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return gps_enabled || network_enabled;
    }



}

