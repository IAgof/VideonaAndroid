package com.videonasocialmedia.videona.presentation.views.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import com.videonasocialmedia.videona.domain.editor.export.ExportProjectUseCase;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnExportFinishedListener;
import com.videonasocialmedia.videona.utils.Constants;
import com.videonasocialmedia.videona.utils.Utils;

/**
 * Created by  on 26/05/16.
 */
public class ExportProjectService extends IntentService implements OnExportFinishedListener {

    private static final String TAG = "ExportProjectService" ;
    public static final String NOTIFICATION = Constants.NOTIFICATION_EXPORT_SERVICES_RECEIVER;
    public static final String FILEPATH = "filepath";
    public static final String RESULT = "result";

    ExportProjectUseCase exportUseCase;

    //TODO Add persistence. Needed to navigate for ShareActivity if service has finished.
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     */
    public ExportProjectService() {
        super("ExportProjectService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        exportUseCase = new ExportProjectUseCase(this);
        exportUseCase.export();
    }

    private void publishResults(String outputPath, int result) {
        Intent intent = new Intent(NOTIFICATION);
        if(result == Activity.RESULT_OK) {
            intent.putExtra(FILEPATH, outputPath);
        }
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }

    @Override
    public void onExportError(String error) {
        publishResults(error, Activity.RESULT_CANCELED);
    }

    @Override
    public void onExportSuccess(Video video) {
        Utils.addFileToVideoGallery(video.getMediaPath().toString());
        publishResults(video.getMediaPath(), Activity.RESULT_OK);
    }

}
