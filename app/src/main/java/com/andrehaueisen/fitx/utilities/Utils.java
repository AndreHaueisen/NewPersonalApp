package com.andrehaueisen.fitx.utilities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import com.andrehaueisen.fitx.R;
import com.andrehaueisen.fitx.client.firebase.ClientDatabase;
import com.andrehaueisen.fitx.models.ClientFitClass;
import com.andrehaueisen.fitx.models.PersonalFitClass;
import com.andrehaueisen.fitx.personal.firebase.PersonalDatabase;
import com.andrehaueisen.fitx.widget.ClassWidgetProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.CharMatcher;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

import static com.bumptech.glide.Glide.with;

/**
 * Created by andre on 9/9/2016.
 */

public class Utils {

    private static final int DAY_INDEX = 0;
    private static final int MONTH_INDEX = 1;
    private static final int YEAR_INDEX = 2;

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static String getWeekDayFromDateCode(String dateCode) {
        //String stringDateCode = String.valueOf(dateCode);
        try {

            Date date = new SimpleDateFormat("MMddyyyy", Locale.US).parse(dateCode);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
        } catch (ParseException pe) {
            return null;
        }

    }

    public static void refreshPersonalDataOnLogin(final Activity activity, final FirebaseUser user, final Intent intent) {

        String name = user.getDisplayName();
        String email = user.getEmail();
        String key = encodeEmail(email);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY, key);
        editor.putString(Constants.SHARED_PREF_PERSONAL_NAME, name);
        editor.commit();

        saveServerKeyToDatabase(sharedPreferences, key);

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("personalTrainer/" + encodeEmail(email) + "/" + Constants.PERSONAL_PROFILE_PICTURE_NAME).getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                Uri photoUrl = user.getPhotoUrl();

                if (!task.isSuccessful()) {
                    new PhotoTask(photoUrl, true).execute(activity);
                }else{
                    photoUrl = task.getResult().getDownloadUrl();
                    Utils.getSharedPreferences(activity).edit().putString(Constants.SHARED_PREF_PERSONAL_PROFILE_PHOTO_URI_PATH, photoUrl.toString()).apply();
                }
                activity.startActivity(intent);
            }
        });

    }

    public static float getSmallestScreenWidth(Context context){

        DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
        float screenWidth = dm.widthPixels / dm.density;
        float screenHeight = dm.heightPixels / dm.density;

        if (screenWidth < screenHeight){
            return screenWidth;
        }else{
            return screenHeight;
        }
    }

    private static void saveServerKeyToDatabase(SharedPreferences sharedPreferences, String key){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        HashMap<String, String> emailKeyServerKeyMap = new HashMap<>(1);
        String refreshedToken = sharedPreferences.getString(Constants.SHARED_PREF_SERVER_TOKEN, null);
        if(refreshedToken != null) {
            emailKeyServerKeyMap.put(key, refreshedToken);
            database.child(Constants.FIREBASE_LOCATION_SERVER_DEVICE_ID_MAPPINGS).setValue(emailKeyServerKeyMap);
        }
    }

    public static void refreshClientDataOnLogin(final Activity activity, final FirebaseUser user, final Intent intent) {

        String name = user.getDisplayName();
        String email = user.getEmail();
        String key = encodeEmail(email);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, 0);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY, key);
        editor.putString(Constants.SHARED_PREF_CLIENT_NAME, name);
        editor.commit();

        saveServerKeyToDatabase(sharedPreferences, key);

        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("client/" + encodeEmail(email) + "/" + Constants.CLIENT_PROFILE_PICTURE_NAME).getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
            @Override
            public void onComplete(@NonNull Task<StorageMetadata> task) {
                Uri photoUrl = user.getPhotoUrl();
                if (!task.isSuccessful()) {

                    new PhotoTask(photoUrl, false).execute(activity);
                }else{
                    photoUrl = task.getResult().getDownloadUrl();
                    Utils.getSharedPreferences(activity).edit().putString(Constants.SHARED_PREF_CLIENT_PHOTO_URI_PATH, photoUrl.toString()).apply();
                }
                activity.startActivity(intent);
            }
        });
    }

    private static class PhotoTask extends AsyncTask<Activity, Void, Void>{

        Uri mPhotoUrl;
        boolean mIsPersonal;
        private PhotoTask(Uri photoUrl, boolean isPersonal) {
            mPhotoUrl = photoUrl;
            mIsPersonal = isPersonal;
        }

        @Override
        protected Void doInBackground(Activity... activities) {
            Activity activity = activities[0];
            try {
                Bitmap bitmap = with(activity).load(mPhotoUrl).asBitmap().into(100, 100).get();
                if(mIsPersonal) {
                    PersonalDatabase.saveProfilePicsToFirebase(activity, bitmap, Constants.PERSONAL_PROFILE_PICTURE_NAME);
                }else{
                    ClientDatabase.saveProfilePicsToFirebase(activity, bitmap);
                }

            }catch (InterruptedException | ExecutionException exception){
                Log.e(Utils.class.getSimpleName(), "exception");
            }

            return null;
        }
    }

    public static String getWrittenDateFromDateCode(Context context, String dateCode) {

        try {
            Locale locale = Utils.getCurrentLocale(context);

            SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
            Date date = formatter.parse(dateCode);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
            int dayOfMoth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);

            return dayOfWeek + ", " + String.valueOf(month) + "/" + String.valueOf(dayOfMoth) + "/" + String.valueOf(year);

        } catch (ParseException pe) {
            return null;
        }
    }

    public static ArrayList<Integer> getDateFromTimeCode(String dateCode) {

        try {

            SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
            Date date = formatter.parse(dateCode);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int dayOfMoth = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            ArrayList<Integer> dateArray = new ArrayList<>();
            dateArray.add(DAY_INDEX, dayOfMoth);
            dateArray.add(MONTH_INDEX, month);
            dateArray.add(YEAR_INDEX, year);

            return dateArray;

        } catch (ParseException pe) {
            return null;
        }
    }

    public static boolean isClassOnThePast(PersonalFitClass personalFitClass) {

        int durationCode = personalFitClass.getDurationCode();
        int startTime = personalFitClass.getStartTimeCode();
        String dateCode = personalFitClass.getDateCode();

        int endClassTimeCode = startTime + durationCode;
        int minutes = Utils.getMinutesFromTimeCode(endClassTimeCode);
        int hours = Utils.getHourFromTimeCode(endClassTimeCode);

        ArrayList<Integer> dateArray = Utils.getDateFromTimeCode(dateCode);
        int day = dateArray.get(Utils.DAY_INDEX);
        int month = dateArray.get(Utils.MONTH_INDEX);
        int year = dateArray.get(Utils.YEAR_INDEX);

        Calendar classDate = new GregorianCalendar(year, month, day, hours, minutes);
        classDate.clear(Calendar.SECOND);

        return Calendar.getInstance().after(classDate);

    }

    public static boolean isClassOnThePast(ClientFitClass clientFitClass) {

        int durationCode = clientFitClass.getDurationCode();
        int startTime = clientFitClass.getStartTimeCode();
        String dateCode = clientFitClass.getDateCode();

        int endClassTimeCode = startTime + durationCode;
        int minutes = Utils.getMinutesFromTimeCode(endClassTimeCode);
        int hours = Utils.getHourFromTimeCode(endClassTimeCode);

        ArrayList<Integer> dateArray = Utils.getDateFromTimeCode(dateCode);
        int day = dateArray.get(Utils.DAY_INDEX);
        int month = dateArray.get(Utils.MONTH_INDEX);
        int year = dateArray.get(Utils.YEAR_INDEX);

        Calendar classDate = new GregorianCalendar(year, month, day, hours, minutes);
        classDate.clear(Calendar.SECOND);

        return Calendar.getInstance().after(classDate);

    }

    public static ClientFitClass extractClientFitClass(PersonalFitClass personalFitClass, String personalKey, String personalName) {

        boolean isConfirmed = personalFitClass.isConfirmed();
        int startTimeCode = personalFitClass.getStartTimeCode();
        int durationCode = personalFitClass.getDurationCode();
        String dateCode = personalFitClass.getDateCode();
        String placeLongitude = personalFitClass.getPlaceLongitude();
        String placeLatitude = personalFitClass.getPlaceLatitude();
        String mainObjective = personalFitClass.getMainObjective();
        String classKey = personalFitClass.getClassKey();
        String clientName = personalFitClass.getClientName();
        String placeAddress = personalFitClass.getPlaceAddress();
        String placeName = personalFitClass.getPlaceName();

        return new ClientFitClass(dateCode, classKey, placeLongitude, placeLatitude, placeName, startTimeCode, durationCode, personalKey, clientName, personalName, mainObjective, placeAddress, isConfirmed);

    }

    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {

        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public static boolean isServiceRunning(Activity activity, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);

        // Loop through the running services
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isServiceRunning(Application application, Class<?> serviceClass) {
        ActivityManager activityManager = (ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE);

        // Loop through the running services
        for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static SharedPreferences getSharedPreferences(Activity activity) {
        return activity.getSharedPreferences(Constants.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static boolean isEmailValid(String userEmail) {
        return Patterns.EMAIL_ADDRESS.matcher(userEmail).matches();
    }

    public static boolean isBirthdayValid(String birthday) {

        String DATE_PATTERN = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
        Pattern pattern = Pattern.compile(DATE_PATTERN);

        Matcher matcher = pattern.matcher(birthday);

        if (matcher.matches()) {

            matcher.reset();

            if (matcher.find()) {

                String day = matcher.group(1);
                String month = matcher.group(2);
                int year = Integer.parseInt(matcher.group(3));

                if (day.equals("31") &&
                        (month.equals("4") || month.equals("6") || month.equals("9") ||
                                month.equals("11") || month.equals("04") || month.equals("06") ||
                                month.equals("09"))) {
                    return false; // only 1,3,5,7,8,10,12 has 31 days
                } else if (month.equals("2") || month.equals("02")) {
                    //leap year
                    if (year % 4 == 0) {
                        if (day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        if (day.equals("29") || day.equals("30") || day.equals("31")) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static String formatGrade(float grade) {
        String gradeString = String.valueOf(grade);

        if (gradeString.length() > 4) {
            return String.valueOf(grade).substring(0, 4);
        } else {
            return gradeString;
        }

    }

    public static boolean isAgeGraterThan18(int year) {

        Calendar calendar = Calendar.getInstance();

        return (calendar.get(Calendar.YEAR) - year <= 17);

    }

    public static boolean isPhoneValid(String phoneNumber) {
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static String filterCrefDigits(String inpureCref) {
        return CharMatcher.DIGIT.retainFrom(inpureCref);
    }

    public static Toast generateToast(Context context, String message) {
        return Toast.makeText(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast generateErrorToast(Context context, String message)
    {
        return Toasty.error(context, message, Toast.LENGTH_SHORT, true);
    }

    public static Toast generateSuccessToast(Context context, String message){
        return Toasty.success(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast generateInfoToast(Context context, String message){
        return Toasty.info(context, message, Toast.LENGTH_SHORT);
    }

    public static Toast generateWarningToast(Context context, String message){
        return Toasty.warning(context, message, Toast.LENGTH_SHORT);
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getCurrentLocale(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static String getClockFromTimeCode(Context context, int crudeTime) {

        int hour = crudeTime / 4;
        int minutes = (crudeTime % 4) * 15;

        return String.format(getCurrentLocale(context), "%02d:%02d", hour, minutes);
    }

    public static void updateWidget(Context context) {
        Intent intent = new Intent(context.getApplicationContext(), ClassWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(context, ClassWidgetProvider.class));

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }



    public static String formatPersonalName(String personalName){

        String normalizedPersonalName = Normalizer.normalize(personalName, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalizedPersonalName).replaceAll("").toUpperCase();

    }

    public static boolean isDeviceConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return !(networkInfo == null || (networkInfo.getState() != NetworkInfo.State.CONNECTED));
    }

    public static String formatCrefNumber(String crefNumber){
        return StringUtils.leftPad(crefNumber, 6, '0');
    }

    public static int getTimeCodeFromClock(int hour, int minute) {

        return (hour * 4) + (minute / 15);
    }

    public static String makeFragmentPagerAdapterTag(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public static int getMinutesFromTimeCode(int timeCode) {
        return (timeCode % 4) * 15;
    }

    public static int getHourFromTimeCode(int timeCode) {
        return timeCode / 4;
    }

    public static String getClassDurationText(Context context, int durationCode) {

        switch (durationCode) {
            case 3:
                return context.getString(R.string.duration_45);
            case 4:
                return context.getString(R.string.duration_60);
            case 5:
                return context.getString(R.string.duration_75);
            case 6:
                return context.getString(R.string.duration_90);
            case 7:
                return context.getString(R.string.duration_105);
            case 8:
                return context.getString(R.string.duration_120);

            default:
                return null;
        }
    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static String decodeEmail(String userEmail) {
        return userEmail.replace(",", ".");
    }

}
