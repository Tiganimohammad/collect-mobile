package org.openforis.collect.android.gui.input;

import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;

import org.openforis.collect.R;
import org.openforis.collect.android.SurveyService;
import org.openforis.collect.android.gui.AudioPlayer;
import org.openforis.collect.android.gui.SurveyNodeActivity;
import org.openforis.collect.android.gui.util.Dialogs;
import org.openforis.collect.android.gui.util.Views;
import org.openforis.collect.android.util.CollectPermissions;
import org.openforis.collect.android.viewmodel.UiFileAttribute;

import java.io.IOException;

public class AudioFileAttributeComponent extends FileAttributeComponent {

    private final View inputView;
    private Button recordBtn, stopBtn, deleteBtn, selectFileBtn;
    private Chronometer recordingChronometer;
    private MediaRecorder mediaRecorder;
    private AudioPlayer audioPlayer;

    private boolean recording = false;

    AudioFileAttributeComponent(UiFileAttribute attribute, SurveyService surveyService, FragmentActivity context) {
        super(attribute, surveyService, context);

        inputView = context.getLayoutInflater().inflate(R.layout.file_attribute_audio, null);

        setupRecordButton();
        setupStopRecordingButton();
        setupRecordingChronometer();
        setupSelectFileButton();
        setupDeleteButton();

        audioPlayer = inputView.findViewById(R.id.audio_player);

        if (file.exists()) {
            audioPlayer.setSource(file);
            audioPlayer.prepare();
        }

        updateViewState();
    }

    @Override
    protected View toInputView() {
        return inputView;
    }

    @Override
    public void onDeselect() {
        if (audioPlayer != null) {
            audioPlayer.stop();
        }
        stopRecording();
        super.onDeselect();
    }

    private void setupRecorder() {
        try {
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setOutputFile(file.getPath());
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mediaRecorder.prepare();
        } catch (IOException e) {
            Log.e("CollectAudioRecorder", "Error setting up media recorder", e);
        }
    }

    private void setupRecordButton() {
        recordBtn = inputView.findViewById(R.id.record_btn);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (CollectPermissions.checkRecordAudioPermissionOrRequestIt(context)) {
                    if (file != null && file.exists()) {
                        Dialogs.confirm(context, R.string.confirm_label,
                                R.string.file_attribute_audio_overwrite_confirm_message,
                                new Runnable() {
                                    public void run() {
                                        startRecording();
                                    }
                                }, null, R.string.overwrite_label, R.string.cancel_label);
                    } else {
                        startRecording();
                    }
                }
            }
        });
    }

    private void setupStopRecordingButton() {
        stopBtn = inputView.findViewById(R.id.stop_btn);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stopRecording();
            }
        });
    }

    private void setupRecordingChronometer() {
        recordingChronometer = inputView.findViewById(R.id.recording_chronometer);
    }

    private void setupSelectFileButton() {
        selectFileBtn = inputView.findViewById(R.id.select_file_btn);
        selectFileBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                selectFile();
            }
        });
    }

    private void setupDeleteButton() {
        deleteBtn = inputView.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Dialogs.confirm(context, R.string.confirm_label,
                        R.string.file_attribute_audio_delete_confirm_message,
                        new Runnable() {
                            public void run() {
                                removeFile();
                                updateViewState();
                            }
                        }, null, R.string.delete, R.string.cancel_label);
            }
        });
    }

    private void startRecording() {
        audioPlayer.stop();

        if (mediaRecorder != null) {
            stopRecording();
        }
        setupRecorder();
        if (mediaRecorder != null) {
            mediaRecorder.start();
            recordingChronometer.setBase(SystemClock.elapsedRealtime());
            recordingChronometer.start();
            recording = true;
            updateViewState();
        }
    }

    private void stopRecording() {
        if (recording) {
            recording = false;
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            recordingChronometer.stop();
            updateViewState();

            audioPlayer.setSource(file);
            audioPlayer.prepare();

            fileChanged();
        }
    }

    private void selectFile() {
        if (CollectPermissions.checkReadExternalStoragePermissionOrRequestIt(context)) {
            ((SurveyNodeActivity) context).setAudioChangedListener(this);
            Intent intent = showFileSelectorIntent();
            context.startActivityForResult(Intent.createChooser(intent, "Select Audio file"),
                    SurveyNodeActivity.AUDIO_SELECTED_REQUEST_CODE);
        }
    }

    private Intent showFileSelectorIntent() {
        Intent intent = new Intent();
        intent.setType("file/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    public void audioSelected(Uri audioUri) {
        System.out.print("ciao");
    }

    private void updateViewState() {
        Views.toggleVisibility(recordBtn, !recording);
        Views.toggleVisibility(stopBtn, recording);
        Views.toggleVisibility(audioPlayer, file.exists() && !recording);
        Views.toggleVisibility(deleteBtn, file.exists() && !recording);
        Views.toggleVisibility(selectFileBtn, !recording);
    }
}
