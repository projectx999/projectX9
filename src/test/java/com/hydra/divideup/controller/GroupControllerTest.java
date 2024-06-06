package com.hydra.divideup.controller;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.service.GroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = GroupController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GroupService groupService;

    private final String groupsUrl = "/api/v1/groups";

    @Test
    void testGetGroup() throws Exception {
        final String id = "123";
        Group group = new Group("Test Group", Set.of("user1", "user2"), "user1");
        group.setId(id);

        //when
        when(groupService.getGroup(id)).thenReturn(group);

        //then
        mockMvc.perform(get(groupsUrl + "/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.groupName", is(group.getGroupName())))
                .andExpect(jsonPath("$.members", containsInAnyOrder("user1", "user2")));
        //and
        verify(groupService, times(1)).getGroup(id);
    }

    @Test
    void testGetGroup_NotFound() throws Exception {
        final String id = "234";

        //when
        when(groupService.getGroup(id)).thenThrow(new RecordNotFoundException(GROUP_NOT_FOUND));

        //then
        mockMvc.perform(get(groupsUrl + "/{id}", id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code", is(GROUP_NOT_FOUND.getCode())))
                .andExpect(jsonPath("$.message", is(GROUP_NOT_FOUND.getMessage())));
    }

}



