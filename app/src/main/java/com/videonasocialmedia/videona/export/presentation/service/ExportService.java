/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.export.presentation.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.activity.ShareActivity;
import com.videonasocialmedia.videona.utils.Constants;

/**
 * Created by alvaro on 11/07/16.
 */
public class ExportService extends Service implements ExportView {

    private ExportPresenter exportPresenter;
    private NotificationManager notificationManager;
    private Notification.Builder builder;

    private OnExportProjectListener exportProjectListener;

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();
    private String mediaPathVideoExported;

    public class LocalBinder extends Binder implements OnExportServiceListener{
        public ExportService getService() {
            return ExportService.this;
        }

        @Override
        public void registerListener(OnExportProjectListener callback) {
            exportProjectListener = callback;
            exportProjectListener.messageService("Init");
        }

        @Override
        public void unregisterListener() {
            exportProjectListener = null;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        exportPresenter = new ExportPresenter();
        exportPresenter.onCreate(this);
        notificationManager = (NotificationManager) VideonaApplication.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       // return null;
        // TODO: Return the communication channel to the service.
       // throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        builder = prepareNotificationBuilder();

        exportPresenter.startExportingProject();

        return START_NOT_STICKY;
    }

    private Notification.Builder prepareNotificationBuilder() {
        Intent intent= new Intent(this, ShareActivity.class);
        intent.putExtra("VideoExportedFinished", mediaPathVideoExported);
        intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new Notification.Builder(this)
                .setAutoCancel(true) // Close after click
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker("Exporting project")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle("Exporting project")  // the label of the entry
                .setContentText("Magic")  // the contents of the entry
                .setProgress(100, 0, false)
                .setContentIntent(contentIntent);  // The intent to send when the entry is clicked
    }

    @Override
    public void showNotification(boolean foreground) {
        // Send notification
        if (foreground)
            startForeground(Constants.NOTIFICATION_EXPORT_ID, builder.build());
        else {
            stopForeground(true);
            notificationManager.notify(Constants.NOTIFICATION_EXPORT_ID, builder.build());
        }
    }

    @Override
    public void setNotificationProgress(int progress) {
        if (progress > 100) {
            builder.setContentText("Exporting finished");
            builder.setContentTitle("Exporting finished");
            builder.setProgress(0, 0, false);
            showNotification(false);
        } else {
            builder.setProgress(100, progress, false);
            updateNotification();
        }
    }

    private void updateNotification() {
        notificationManager.notify(Constants.NOTIFICATION_EXPORT_ID, builder.build());
    }


    @Override
    public void hideNotification() {
        if(exportProjectListener != null)
            notificationManager.cancel(Constants.NOTIFICATION_EXPORT_ID);
    }

    @Override
    public void showMessage(String message) {
        if(exportProjectListener != null)
         exportProjectListener.messageService(message);
    }

    @Override
    public void onSuccessVideoExported(String mediaPath) {
        if(mediaPath!=null) {
            if(exportProjectListener != null)
                exportProjectListener.onSuccessVideoExported(mediaPath);
            mediaPathVideoExported = mediaPath;

        }
    }

}
