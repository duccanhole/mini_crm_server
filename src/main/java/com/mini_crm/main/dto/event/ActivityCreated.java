package com.mini_crm.main.dto.event;

import com.mini_crm.main.model.Activity;
import org.springframework.context.ApplicationEvent;

public class ActivityCreated extends ApplicationEvent {
    private Activity activity;

    public ActivityCreated(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
