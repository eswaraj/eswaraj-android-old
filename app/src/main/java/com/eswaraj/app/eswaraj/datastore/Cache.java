package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.util.Log;

import com.eswaraj.app.eswaraj.application.EswarajApplication;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.config.PreferenceConstants;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetComplaintImageEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.SharedPreferencesHelper;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class Cache extends BaseClass implements CacheInterface {

    @Inject
    SharedPreferencesHelper sharedPreferencesHelper;
    @Inject
    EventBus eventBus;

    public void loadUserData(Context context, Session session) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, null);
        try {
            UserDto userDto = new Gson().fromJson(json, UserDto.class);
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(true);
            event.setUserDto(userDto);
            eventBus.postSticky(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetUserEvent event = new GetUserEvent();
            event.setSuccess(false);
            event.setError("Invalid json");
            eventBus.postSticky(event);
        }
    }

    @Override
    public void updateUserData(Context context, String json) {
        if(json != null) {
            sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA, json);
            sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, true);
        }
        else {
            sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, false);
        }
    }

    public Boolean isUserDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_DATA_AVAILABLE, false)) {
            return true;
        }
        return false;
    }

    public Boolean isCategoriesDataAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }


    public void loadCategoriesData(Context context) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, null);
        try {
            List<CategoryWithChildCategoryDto> categoryDtoList;
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            categoryDtoList = gson.fromJson(json, new TypeToken<ArrayList<CategoryWithChildCategoryDto>>(){}.getType());
            getCategoriesDataEvent.setSuccess(true);
            getCategoriesDataEvent.setCategoryList(categoryDtoList);
            eventBus.post(getCategoriesDataEvent);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetCategoriesDataEvent getCategoriesDataEvent = new GetCategoriesDataEvent();
            getCategoriesDataEvent.setError("Invalid json");
            eventBus.post(getCategoriesDataEvent);
        }
    }

    @Override
    public void updateCategoriesData(Context context, String json) {
        sharedPreferencesHelper.putString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA, json);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_DOWNLOAD_TIME_IN_MS, new Date().getTime());
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_DATA_AVAILABLE, true);
    }

    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        //No data needs to be put here. The image file names will be inferred from the categoryId. Just put success=True in event object
        GetCategoriesImagesEvent getCategoriesImagesEvent = new GetCategoriesImagesEvent();
        getCategoriesImagesEvent.setSuccess(true);
        eventBus.post(getCategoriesImagesEvent);
    }

    @Override
    public Boolean isCategoriesImagesAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateCategoriesImages(Context context) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_AVAILABLE, true);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.CATEGORY_IMAGES_DOWNLOAD_TIME_IN_MS, new Date().getTime());
    }

    @Override
    public Boolean isUserComplaintsAvailable(Context context) {
        if(sharedPreferencesHelper.getBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_AVAILABLE, false)) {
            if((new Date().getTime() - sharedPreferencesHelper.getLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_DOWNLOAD_TIME_IN_MS, 0L)) < Constants.SERVER_DATA_UPDATE_INTERVAL_IN_MS) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateUserComplaints(Context context, String json) {
        sharedPreferencesHelper.putBoolean(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_AVAILABLE, true);
        sharedPreferencesHelper.putLong(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS_DOWNLOAD_TIME_IN_MS, new Date().getTime());
    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto) {
        Gson gson = new Gson();
        String json = sharedPreferencesHelper.getString(context, PreferenceConstants.FILE_SERVER_DATA, PreferenceConstants.USER_COMPLAINTS, null);
        try {
            List<ComplaintDto> complaintDtoList;
            GetUserComplaintsEvent event = new GetUserComplaintsEvent();
            complaintDtoList = gson.fromJson(json, new TypeToken<List<ComplaintDto>>(){}.getType());
            event.setSuccess(true);
            event.setComplaintDtoList(complaintDtoList);
            eventBus.post(event);
        } catch (JsonParseException e) {
            //This should never happen since json would only be stored in server if de-serialization was successful in Server class
            GetUserComplaintsEvent event = new GetUserComplaintsEvent();
            event.setError("Invalid json");
            eventBus.post(event);
        }
    }

    @Override
    public Boolean isComplaintImageAvailable(Context context, String url, Long id) {
        return new File(context.getFilesDir() + "/eSwaraj_complaint_" + id + ".png").exists();
    }

    @Override
    public void updateComplaintImage(Context context) {
        //Nothing to do here. Keeping the method for consistency
    }

    @Override
    public void loadComplaintImage(Context context, String url, Long id) {
        GetComplaintImageEvent event = new GetComplaintImageEvent();
        event.setSuccess(true);
        eventBus.post(event);
    }

    @Override
    public Boolean isCommentsAvailable(Context context, ComplaintDto complaintDto, int count) {
        return false;
    }

    @Override
    public void updateComments(Context context, String json, ComplaintDto complaintDto, int count) {
        //Nothing to do here right now. Might update later
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int count) {
        //Will not get called right now
    }
}
