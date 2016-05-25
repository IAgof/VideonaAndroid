package com.videonasocialmedia.videona.model.entities.editor;

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
        Project videonaProject = Project.getInstance("project title", "root path", Profile.getInstance(Profile.ProfileType.free));

        videonaProject.clear();
        Project projectInstance = Project.getInstance(null, null, null);

        assertThat(videonaProject, not(projectInstance));
        assertThat(projectInstance.getTitle(), nullValue());
        assertThat(projectInstance.getProjectPath(), is("null/projects/null"));
        assertThat(projectInstance.getProfile(), nullValue());
    }
}
