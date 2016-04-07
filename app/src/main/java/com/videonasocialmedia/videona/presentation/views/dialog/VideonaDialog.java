package com.videonasocialmedia.videona.presentation.views.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.views.listener.OnVideonaDialogListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Veronica Lago Fominaya on 02/02/2016.
 */
public class VideonaDialog extends DialogFragment {

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.image)
    ImageView image;
    @Bind(R.id.message)
    TextView message;
    @Bind(R.id.positiveButton)
    Button positiveButton;
    @Bind(R.id.negativeButton)
    Button negativeButton;
    private OnVideonaDialogListener listener;
    private int idDialog;

    public VideonaDialog() {}

    private static VideonaDialog newInstance(String title, int image, String message,
                                               String positive, String negative, int idDialog) {
        VideonaDialog frag = new VideonaDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("image", image);
        args.putString("message", message);
        args.putString("positiveButton", positive);
        args.putString("negativeButton", negative);
        args.putInt("idDialog", idDialog);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.VideonaDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_videona, null);
        builder.setView(v);
        ButterKnife.bind(this, v);

        String titleText = getArguments().getString("title");
        if(titleText == null)
            hideTitle();
        else
            setTitle(titleText);
        int imageId = getArguments().getInt("image");
        if(imageId == 0)
            hideImage();
        else
            setImage(imageId);
        String messageText = getArguments().getString("message");
        if(messageText == null)
            hideMessage();
        else
            setMessage(messageText);
        String positiveButtonText = getArguments().getString("positiveButton");
        if(positiveButtonText == null)
            hidePositiveButton();
        else
            setPositiveButton(positiveButtonText);
        String negativeButtonText = getArguments().getString("negativeButton");
        if(negativeButtonText == null)
            hideNegativeButton();
        else
            setNegativeButton(negativeButtonText);
        idDialog = getArguments().getInt("idDialog");

        return builder.create();
    }

    private void hideTitle() {
        title.setVisibility(View.GONE);
    }

    private void setTitle(String restTitle) {
        title.setText(restTitle);
    }

    private void hideImage() {
        image.setVisibility(View.GONE);
    }

    private void setImage(int resImage) {
        image.setImageResource(resImage);
    }

    private void hideMessage() {
        message.setVisibility(View.GONE);
    }

    private void setMessage(String resMessage) {
        message.setText(resMessage);
    }

    private void hidePositiveButton() {
        positiveButton.setVisibility(View.GONE);
    }

    private void setPositiveButton(String resPositive) {
        positiveButton.setText(resPositive);
    }

    private void hideNegativeButton() {
        negativeButton.setVisibility(View.GONE);
    }

    private void setNegativeButton(String resNegative) {
        negativeButton.setText(resNegative);
    }

    public void setListener(OnVideonaDialogListener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.positiveButton)
    public void onClickPositiveButton() {
        if(listener != null)
            listener.onClickPositiveButton(idDialog);
    }

    @OnClick(R.id.negativeButton)
    public void onClickNegativeButton() {
        if(listener != null)
            listener.onClickNegativeButton(idDialog);
    }

    public static class Builder {
        private String title;
        private int image;
        private String message;
        private String positiveButton;
        private String negativeButton;
        private int idDialog;
        private OnVideonaDialogListener listener;

        public Builder() {
            this.title = null;
            this.image = 0;
            this.message = null;
            this.positiveButton = null;
            this.negativeButton = null;
            this.idDialog = 0;
            this.listener = null;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withImage(int image) {
            this.image = image;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder withPositiveButton(String positiveButton) {
            this.positiveButton = positiveButton;
            return this;
        }

        public Builder withNegativeButton(String negativeButton) {
            this.negativeButton = negativeButton;
            return this;
        }

        public Builder withCode(int idDialog) {
            this.idDialog = idDialog;
            return this;
        }

        public Builder withListener(OnVideonaDialogListener listener) {
            this.listener = listener;
            return this;
        }

        public VideonaDialog create() {
            VideonaDialog dialog = VideonaDialog.newInstance(
                    title,
                    image,
                    message,
                    positiveButton,
                    negativeButton,
                    idDialog
            );
            if(listener != null)
                dialog.setListener(listener);
            return dialog;
        }
    }
}
