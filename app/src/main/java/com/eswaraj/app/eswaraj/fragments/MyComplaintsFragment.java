package com.eswaraj.app.eswaraj.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.eswaraj.app.eswaraj.R;
import com.eswaraj.app.eswaraj.activities.SingleComplaintActivity;
import com.eswaraj.app.eswaraj.adapters.ComplaintListAdapter;
import com.eswaraj.app.eswaraj.base.BaseFragment;
import com.eswaraj.app.eswaraj.events.GetCategoriesDataEvent;
import com.eswaraj.app.eswaraj.events.GetUserComplaintsEvent;
import com.eswaraj.app.eswaraj.events.GetUserEvent;
import com.eswaraj.app.eswaraj.middleware.MiddlewareServiceImpl;
import com.eswaraj.web.dto.CategoryWithChildCategoryDto;
import com.eswaraj.web.dto.UserDto;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;


public class MyComplaintsFragment extends BaseFragment {

    @Inject
    EventBus eventBus;
    @Inject
    MiddlewareServiceImpl middlewareService;

    private ListView mcList;

    private List<CategoryWithChildCategoryDto> categoryDtoList;
    private UserDto userDto;
    private Boolean categoriesDataReady = false;
    private Boolean userDataReady = false;

    public MyComplaintsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_complaints, container, false);
        mcList = (ListView) rootView.findViewById(R.id.mcList);

        mcList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), SingleComplaintActivity.class);
                i.putExtra("COMPLAINT", (Serializable) mcList.getAdapter().getItem(position));
                startActivity(i);
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        eventBus.registerSticky(this);
    }

    @Override
    public void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }

    public void onEventMainThread(GetCategoriesDataEvent event) {
        if(event.getSuccess()) {
            categoryDtoList = event.getCategoryList();
            categoriesDataReady = true;
            if(userDataReady) {
                middlewareService.loadUserComplaints(getActivity(), userDto, true);
            }
        }
        else {
            //This will never happen
        }
    }

    public void onEventMainThread(GetUserEvent event) {
        if(event.getSuccess()) {
            userDto = event.getUserDto();
            userDataReady = true;
            if(categoriesDataReady) {
                middlewareService.loadUserComplaints(getActivity(), userDto, true);
            }
        }
        else {
           //This will never happen
        }
    }

    public void onEventMainThread(GetUserComplaintsEvent event) {
        if(event.getSuccess()) {
            mcList.setAdapter(new ComplaintListAdapter(getActivity(), R.layout.item_complaint_list, event.getComplaintDtoList(), categoryDtoList));
        }
        else {
            Toast.makeText(getActivity(), "Could not fetch user complaints. Error = " + event.getError(), Toast.LENGTH_LONG).show();
        }
    }
}
