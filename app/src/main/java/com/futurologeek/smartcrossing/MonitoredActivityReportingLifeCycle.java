package com.futurologeek.smartcrossing;

public abstract class MonitoredActivityReportingLifeCycle extends com.futurologeek.smartcrossing.crop.MonitoredActivity {

    @Override
    protected void onResume() {
        super.onResume();
        AppController.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.onPause();
    }
}
