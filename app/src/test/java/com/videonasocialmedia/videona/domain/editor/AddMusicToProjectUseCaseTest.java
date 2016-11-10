/*
 * Copyright (c) 2016. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Javier Liarte <jliarte@gmail.com>
 */
package com.videonasocialmedia.videona.domain.editor;

import com.videonasocialmedia.videona.model.entities.editor.Profile;
import com.videonasocialmedia.videona.model.entities.editor.Project;
import com.videonasocialmedia.videona.model.entities.editor.media.Music;
import com.videonasocialmedia.videona.model.entities.editor.track.AudioTrack;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;


@RunWith(PowerMockRunner.class)
//@PowerMockRunnerDelegate(RobolectricTestRunner.class) // But when we finally successfully mock it it seems we can get rid of Robolectric here
@PrepareForTest({AddMusicToProjectUseCase.class})
public class AddMusicToProjectUseCaseTest {
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        // FIXME: tests are not independent as Project keeps state between tests
        Project singletonProject = Project.getInstance(null, null, null);
        singletonProject.clear();
    }

    @Test
    public void testAddMusicToTrackAddsMusicToProjectDefaultAudioTrack() throws Exception {
        Project videonaProject = getAProject(); // TODO: inject as a dependence in Use Case constructor
        Music musicToAdd = new Music(42, "musicNameId", 3, 2, "author");

        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 0);
        AudioTrack projectAudioTrack = videonaProject.getAudioTracks().get(0);

        assert ( projectAudioTrack.getItems().size() == 1 );
        assert ( projectAudioTrack.getItems().get(0).equals(musicToAdd) );
    }

//    @Test public void testAddMusicToTrackSendsMusicAddedToProjectEventToBusOnSuccess() throws Exception {
//        Project videonaProject = getAProject(); // TODO: inject as a dependence in Use Case constructor
//        Music musicToAdd = new Music(42, "musicNameId", 3, 2);
//
//        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 0);
//
//        ArgumentCaptor<MusicAddedToProjectEvent> event_post_argument =
//                ArgumentCaptor.forClass(MusicAddedToProjectEvent.class);
//        Mockito.verify(mockedEventBus).post(event_post_argument.capture());
//        assertThat(event_post_argument.getValue().music, IsEqual.equalTo(musicToAdd));
//    }

//    FIXME: cannot reach catch as method signature only allows Music type
//    @Ignore @Test public void testAddMusicToTrackDoesntAddToProjectDefaultTrackIfNotMusic() throws Exception {
//        class NoAudio extends Media {
//            public NoAudio(String identifier, String iconPath, String medokiaPath, int startTime, int duration, ArrayList<User> authors, License license) {
//                super(identifier, iconPath, mediaPath, startTime, duration, authors, license);
//            }
//        }
//        Project videonaProject = getAProject();
//        Music musicToAdd = new Music(42, "musicNameId", 3, 2);
//        NoAudio noAudioToAdd = new NoAudio("identifier", "iconPath", "mediaPath", 0, 10, null, null);
//
//        new AddMusicToProjectUseCase().addMusicToTrack(noAudioToAdd, 0);
//        AudioTrack project_audio_track = videonaProject.getAudioTracks().get(0);
//
//        assert(project_audio_track.getItems().size() == 0);
//    }

    private Project getAProject() {
        Profile profile = Profile.getInstance(Profile.ProfileType.free);
        String rootPath = "projectRootPath";
        String title = "project title";
        return Project.getInstance(title, rootPath, profile);
    }

//    @Test public void testAddMusicToTrackSendsErrorEventToBusOnSuccess() throws Exception {
//        Project videonaProject = getAProject(); // TODO: inject as a dependence in Use Case constructor
//        Music musicToAdd = new Music(42, "musicNameId", 3, 2);
//
//        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 1);
//
//        ArgumentCaptor<ErrorAddingMusicToProjectEvent> event_post_argument =
//                ArgumentCaptor.forClass(ErrorAddingMusicToProjectEvent.class);
//        Mockito.verify(mockedEventBus).post(event_post_argument.capture());
//        assertThat(event_post_argument.getValue().music, IsEqual.equalTo(musicToAdd));
//    }

    @Test
    public void testAddMusicToTrackDoesntAddToNonExistentTrackIndex() throws Exception {
        Project videonaProject = getAProject(); // TODO: inject as a dependence in Use Case constructor
        Music musicToAdd = new Music(42, "musicNameId", 3, 2, "");

        new AddMusicToProjectUseCase().addMusicToTrack(musicToAdd, 1);
        AudioTrack projectAudioTrack = videonaProject.getAudioTracks().get(0);

        assertEquals(projectAudioTrack.getItems().size(), 0);
    }
}