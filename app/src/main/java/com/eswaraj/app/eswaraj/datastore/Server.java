package com.eswaraj.app.eswaraj.datastore;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.eswaraj.app.eswaraj.base.BaseClass;
import com.eswaraj.app.eswaraj.config.Constants;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetCategoriesImagesEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.helpers.NetworkAccessHelper;
import com.eswaraj.app.eswaraj.volley.CommentPostRequest;
import com.eswaraj.app.eswaraj.volley.CommentsRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintImageRequest;
import com.eswaraj.app.eswaraj.volley.ComplaintPostRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesDataRequest;
import com.eswaraj.app.eswaraj.volley.LoadCategoriesImagesRequest;
import com.eswaraj.app.eswaraj.volley.RegisterFacebookUserRequest;
import com.eswaraj.app.eswaraj.volley.RegisterUserAndDeviceRequest;
import com.eswaraj.app.eswaraj.volley.UserComplaintsRequest;
import com.eswaraj.web.dto.AddressDto;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.ComplaintDto;
import com.eswaraj.web.dto.PersonDto;
import com.eswaraj.web.dto.RegisterFacebookAccountRequest;
import com.eswaraj.web.dto.UserDto;
import com.facebook.Session;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class Server extends BaseClass implements ServerInterface {

    @Inject
    EventBus eventBus;
    @Inject
    LoadCategoriesDataRequest loadCategoriesDataRequest;
    @Inject
    LoadCategoriesImagesRequest loadCategoriesImagesRequest;
    @Inject
    RegisterFacebookUserRequest registerFacebookUserRequest;
    @Inject
    RegisterUserAndDeviceRequest registerUserAndDeviceRequest;
    @Inject
    ComplaintPostRequest complaintPostRequest;
    @Inject
    UserComplaintsRequest userComplaintsRequest;
    @Inject
    ComplaintImageRequest complaintImageRequest;
    @Inject
    CommentsRequest commentsRequest;
    @Inject
    CommentPostRequest commentPostRequest;


    public void loadCategoriesData(Context context) {
        loadCategoriesDataRequest.processRequest(context);
    }


    @Override
    public void loadCategoriesImages(Context context, List<CategoryWithChildCategoryDto> categoriesList) {
        loadCategoriesImagesRequest.processRequest(context, categoriesList);
    }


    @Override
    public void loadUserData(Context context, Session session) {

        registerFacebookUserRequest.processRequest(context, session);

        //TODO: Uncomment above line and delete the mock code once the server side fixes are done

        /*
        UserDto userDto = new UserDto();
        PersonDto personDto = new PersonDto();
        AddressDto addressDto = new AddressDto();
        addressDto.setLattitude(28.64);
        addressDto.setLongitude(77.22);
        personDto.setPersonAddress(addressDto);
        userDto.setPerson(personDto);
        userDto.setId((long) 84076);
        userDto.setExternalId("47cf0992-42f4-49ec-9ae0-d12ee91d4bae");

        GetUserEvent event = new GetUserEvent();
        event.setSuccess(true);
        event.setUserDto(userDto);
        eventBus.postSticky(event);
        */

    }

    @Override
    public void loadUserComplaints(Context context, UserDto userDto) {
        userComplaintsRequest.processRequest(context, userDto);
    }

    @Override
    public void loadComplaintImage(Context context, String url, Long id) {
        complaintImageRequest.processRequest(context, url, id);
    }

    @Override
    public void loadComments(Context context, ComplaintDto complaintDto, int count) {
        commentsRequest.processRequest(context, complaintDto, count);
    }

    @Override
    public void registerDevice(Context context) {
        registerUserAndDeviceRequest.processRequest(context);
    }

    @Override
    public void saveUserLocation(Context context, UserDto userDto, double lat, double lng) {
        //TODO: Implement post request here
    }

    @Override
    public void postComplaint(UserDto userDto, CategoryWithChildCategoryDto categoryDto, Location location, String description, File image) {
        complaintPostRequest.processRequest(userDto, categoryDto, location, description, image);
    }

    @Override
    public void postComment(UserDto userDto, ComplaintDto complaintDto, String comment) {
        commentPostRequest.processRequest(userDto, complaintDto, comment);
    }
}
