package com.andrehaueisen.fitx;

/**
 * Created by Carla on 14/09/2016.
 */

public class Constants {

    //Firebase constants
    public static final String FILE_PROVIDER_AUTHORITY = "com.andrehaueisen.fitx.fileprovider";

    public static final String FIREBASE_STORAGE_ROOT_URL = "gs://personalapp-ad97d.appspot.com";
    public static final String FIREBASE_DATABASE_ROOT_URL = "https://personalapp-ad97d.firebaseio.com/";

    public static final String FIREBASE_LOCATION_PERSONAL_TRAINER = "personalTrainer";
    public static final String FIREBASE_LOCATION_CLIENT = "client";
    public static final String FIREBASE_LOCATION_AGENDA = "personal_week_agenda_codes";
    public static final String FIREBASE_LOCATION_GYMS = "gyms";
    public static final String FIREBASE_LOCATION_SPECIALTIES = "personal_specialties";
    public static final String FIREBASE_LOCATION_PERSONAL_AGENDA_RESTRICTIONS = "agendaRestrictions";
    public static final String FIREBASE_LOCATION_PERSONAL_CLASSES = "personalFitClasses";
    public static final String FIREBASE_LOCATION_CLIENT_CLASSES = "clientFitClasses";
    public static final String FIREBASE_LOCATION_CLASSES_RECEIPT = "fitClassesReceipt";
    public static final String FIREBASE_LOCATION_PERSONAL_REVIEWS = "personalReviews";
    public static final String FIREBASE_LOCATION_UID_MAPPINGS = "uid_mappings";
    public static final String FIREBASE_LOCATION_SERVER_DEVICE_ID_MAPPINGS = "server_device_id_mappings";

    public static final String AGENDA_CODES_START_LIST = "agendaTimeCodesStart";
    public static final String AGENDA_CODES_END_LIST = "agendaTimeCodesEnd";

    public static final String WEEK_DAY_BUNDLE_KEY = "week_day";
    public static final String WEEK_DAY_MONDAY_KEY = "Monday";
    public static final String WEEK_DAY_TUESDAY_KEY = "Tuesday";
    public static final String WEEK_DAY_WEDNESDAY_KEY = "Wednesday";
    public static final String WEEK_DAY_THURSDAY_KEY = "Thursday";
    public static final String WEEK_DAY_FRIDAY_KEY = "Friday";
    public static final String WEEK_DAY_SATURDAY_KEY = "Saturday";
    public static final String WEEK_DAY_SUNDAY_KEY = "Sunday";

    //SharedPreferences constants
    public static final String SHARED_PREF_FILE_NAME = "com.andrehaueisen.fitx.PREFERENCE_FILE_KEY";
    public static final String SHARED_PREF_CLIENT_EMAIL_UNIQUE_KEY = "client_unique_key";
    public static final String SHARED_PREF_CLIENT_NAME = "client_name";
    public static final String SHARED_PREF_PERSONAL_EMAIL_UNIQUE_KEY = "personal_unique_key";
    public static final String SHARED_PREF_PERSONAL_NAME = "personal_name";
    public static final String SHARED_PREF_PERSONAL_PHOTO_URI_PATH = "personalPhotoUri";
    public static final String SHARED_PREF_CLIENT_PHOTO_URI_PATH = "clientPhotoUri";
    public static final String SHARED_PREF_SERVER_TOKEN = "server_token";

    //Pre Login User Extras
    public static final String UNDEFINED_USER_EXTRA_KEY = "undefined_user";

    //Camera constants
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    public static final int REQUEST_IMAGE_LOAD = 1;
    public static final String IMAGE_CODE_BUNDLE_KEY = "image_code";
    public static final int PERSONAL_PROFILE_PICTURE = 0;
    public static final int CLIENT_PROFILE_PICTURE = 1;


    public static final String PERSONAL_PROFILE_PICTURE_NAME ="PersonalProfilePicOkGo.png";
    public static final String CLIENT_PROFILE_PICTURE_NAME ="ClientProfilePicOkGo.png";

    //PlacePicker constants
    public static final int PLACE_PICKER_REQUEST = 3;

    //ExtraKeys constants
    public static final String PERSONAL_LIST_EXTRA_KEY = "personalsExtraKey";
    public static final String CHOSEN_PERSONAL_KEYS_EXTRA_KEY = "personalKeysExtraKey";
    public static final String FIT_CLASS_EXTRA_KEY = "fitClassExtraKey";
    public static final String PERSONAL_EXTRA_KEY = "personalExtraKey";
    public static final String DRAWER_ITEM_CLICK_POSITION_EXTRA_KEY = "drawerItemPosition";

    //Bundle Keys
    public static final String PERSONAL_BUNDLE_KEY = "personalBundleKey";
    public static final String PERSONAl_WORKING_LOCATIONS_BUNDLE_KEY = "personal_working_loc_bundle_key";
    public static final String PERSONAL_PROFILE_PIC_BUNDLE_KEY = "personal_prof_pic_bundle_key";
    public static final String PERSONAL_SPECIALTIES_BUNDLE_KEY = "personal_specialties_bundle_key";
    public static final String PERSONAL_UNIQUE_KEY_BUNDLE_KEY = "personal_unique_key_bundle_key";
    public static final String FIT_CLASS_BUNDLE_KEY = "fitClassBundleKey";
    public static final String CLASS_START_TIMES_BUNDLE_KEY = "class_start_times_bundle_key";
    public static final String CLASS_END_TIMES_BUNDLE_KEY = "class_end_times_bundle_key";
    public static final String CLASS_DURATION_BUNDLE_KEY = "class_duration_bundle_key";
    public static final String CLASS_DATE_BUNDLE_KEY = "class_date_bundle_key";
    public static final String CLASS_PERSONAL_BUNDLE_KEY = "personal_bundle_key";

    //Saved State keys
    public static final String CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY = "confirmed_personal_classes_state";
    public static final String NOT_CONFIRMED_PERSONALS_CLASSES_SAVED_STATE_KEY = "not_confirmed_personal_classes_state";
    public static final String GYMS_SAVED_STATE_KEY = "work_places_state";
    public static final String SPECIALTIES_SAVED_STATE_KEY = "specialties_state";
    public static final String REVIEWS_SAVED_STATE_KEY = "reviews_state";
    public static final String HAS_SCREEN_ROTATED_SAVED_STATE_KEY = "has_screen_rotated_state";
    public static final String CLIENT_SAVED_STATE_KEY = "client_state";
    public static final String FIT_CLASS_SAVED_STATE_KEY = "fit_class_state";
    public static final String CLASS_DATE_CALENDAR_SAVED_STATE_KEY = "date_calendar_state";
    public static final String CLASS_TIME_CALENDAR_SAVED_STATE_KEY = "time_calendar_state";
    public static final String CLASS_DURATION_SAVED_STATE_KEY = "duration_state";
    public static final String MAIN_OBJECTIVE_SAVED_STATE_KEY = "main_objective_state";
    public static final String GYM_PHOTO_SAVED_STATE_KEY = "gym_photo_state";
    public static final String RECYCLER_VIEW_SAVED_STATE_KEY = "recycler_saved_state";
    public static final String CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY = "confirmed_client_classes_state";
    public static final String NOT_CONFIRMED_CLIENT_CLASSES_SAVED_STATE_KEY = "not_confirmed_client_classes_state";

    //PersonalClassesAdapter constants
    public static final int LONG_PRESS_TIME = 500; // Time in miliseconds

    //
    public static final String CLASS_RECEIPTS_EXTRA_KEY = "class_receipts_extra_key";

}
