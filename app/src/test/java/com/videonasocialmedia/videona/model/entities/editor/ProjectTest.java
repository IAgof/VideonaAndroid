package com.videonasocialmedia.videona.model.entities.editor;

import com.videonasocialmedia.videona.model.entities.editor.exceptions.IllegalItemOnTrack;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.model.entities.editor.track.MediaTrack;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

/**
 * Created by jliarte on 11/05/16.
 */

@RunWith(MockitoJUnitRunner.class)
public class ProjectTest {
    @Test
    public void clearShoudCreateANewNullProject() throws Exception {
        Project videonaProject = getAProject();

        videonaProject.clear();
        Project projectInstance = Project.getInstance(null, null, null);

        assertThat(videonaProject, not(projectInstance));
        assertThat(projectInstance.getTitle(), nullValue());
        assertThat(projectInstance.getProjectPath(), is("null/projects/null"));
        assertThat(projectInstance.getProfile(), nullValue());
    }

    @Test
    public void projectClipsIs0OnAnEmtyProject() {
        Project videonaProject = getAProject();

        assertThat(videonaProject.numberOfClips(), is(0));
    }

    @Test
    public void projectNumberOfClipsIsMediaTrackItemsLength() {
        Project videonaProject = getAProject();
        MediaTrack mediaTrack = videonaProject.getMediaTrack();
        try {
            mediaTrack.insertItemAt(0, new Video("/path1"));
            mediaTrack.insertItemAt(1, new Video("/path2"));

            assertThat(videonaProject.numberOfClips(), is(2));
        } catch (IllegalItemOnTrack illegalItemOnTrack) {
            illegalItemOnTrack.printStackTrace();
        }
    }

    @Test
    public void getMusicReturnsMusicWhenMusicHasOneSet() throws IllegalItemOnTrack {
        Project videonaProject = getAProject();
        Music music = new Music(1, "Music title", 2, 3, "Music Author");

        videonaProject.getAudioTracks().get(0).insertItem(music);

        assertThat(videonaProject.getMusic(), is(music));
    }

    @Test
    public void getMusicReturnsNullIfNoMusicHasSet() {
        Project videonaProject = getAProject();

        assertThat(videonaProject.getMusic(), is(nullValue()));
    }

    public Project getAProject() {
        return Project.getInstance("project title", "root path", Profile.getInstance(Profile.ProfileType.free));
    }
}
