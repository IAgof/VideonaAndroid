package com.videonasocialmedia.videona.domain.editor;

import android.support.annotation.NonNull;

import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;
import com.videonasocialmedia.videona.presentation.mvp.presenters.GetMusicFromProjectCallback;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

/**
 * Created by jliarte on 31/05/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class GetMusicFromProjectUseCaseTest {
    @Mock private GetMusicFromProjectCallback mockedListener;
    @Captor private ArgumentCaptor<Music> retrievedMusicCaptor;

    @Before
    public void injectDoubles() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void constructorSetsProjectInstance() {
        Project videonaProject = getAProject();

        GetMusicFromProjectUseCase getMusicFromProjectUseCase = new GetMusicFromProjectUseCase();

        assertThat("Project field set after construction", getMusicFromProjectUseCase.project, is(videonaProject));
    }

    @Test
    public void getMusicFromProjectReturnsProjectMusic() {
        Project videonaProject = getAProject();
        Music project_music = new Music(1, "resourceName", 2, 3, "");
        ArrayList<AudioTrack> audioTracks = getAudioTracks(project_music);
        videonaProject.setAudioTracks(audioTracks);

        new GetMusicFromProjectUseCase().getMusicFromProject(mockedListener);

        Mockito.verify(mockedListener).onMusicRetrieved(retrievedMusicCaptor.capture());
        Music retrievedMusic = retrievedMusicCaptor.getValue();
        assertThat(retrievedMusic, is(project_music));
    }

    @Test
    public void getMusicFromProjectNotifiesWithNullIfNoMusic() {
        Project project = getAProject();

        new GetMusicFromProjectUseCase().getMusicFromProject(mockedListener);

        Mockito.verify(mockedListener).onMusicRetrieved(retrievedMusicCaptor.capture());
        assertThat("Music retrieved when no audio tracks", retrievedMusicCaptor.getValue(), CoreMatchers.<Music>nullValue());
    }

    @NonNull
    public ArrayList<AudioTrack> getAudioTracks(Music music) {
        ArrayList<AudioTrack> audioTracks = new ArrayList<AudioTrack>();
        AudioTrack audioTrack = new AudioTrack();
        try {
            audioTrack.insertItem(music);
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
//            illegalItemOnTrack.printStackTrace();
        }
        audioTracks.add(audioTrack);
        return audioTracks;
    }

    private Project getAProject() {
        String title = "project title";
        String rootPath = "project/root/path";
        Profile profile = Profile.getInstance(Profile.ProfileType.free);
        return Project.getInstance(title, rootPath, profile);
    }
}
