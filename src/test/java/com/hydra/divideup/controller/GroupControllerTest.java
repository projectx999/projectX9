package com.hydra.divideup.controller;

import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.service.GroupService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;
import static org.hamcrest.Matchers.*;
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

   @Test
    void testGetGroupsByUser() throws Exception {
        final String id1 = "123";
        Group group1 = new Group("Test Group1", Set.of("user1", "user2"), "user1");
        group1.setId(id1);

        final String id2 = "234";
        Group group2 = new Group("Test Group2", Set.of("user1", "user3"), "user3");
        group2.setId(id2);

        List<Group> user1_groups = List.of(group1, group2);

        /*find groups for user1*/
        //when
        when(groupService.getGroupsByUser("user1")).thenReturn(user1_groups);

        //then
        mockMvc.perform(get(groupsUrl + "/user/{userId}", "user1"))
                .andExpect(status().isOk())
                //groups>group1
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(id1)))
                .andExpect(jsonPath("$[0].groupName", is(group1.getGroupName())))
                .andExpect(jsonPath("$[0].members", containsInAnyOrder("user1", "user2")))
                .andExpect(jsonPath("$[0].members", not(hasItem("user3"))))
                //groups>group2
                .andExpect(jsonPath("$[1].id", is(id2)))
                .andExpect(jsonPath("$[1].groupName", is(group2.getGroupName())))
                .andExpect(jsonPath("$[1].members", containsInAnyOrder("user1", "user3")))
                .andExpect(jsonPath("$[1].members", not(hasItem("user2"))));
        //and
        verify(groupService, times(1)).getGroupsByUser("user1");

        /*find groups for user3*/
        Mockito.reset(groupService);

        List<Group> user2_groups = List.of(group2);

        when(groupService.getGroupsByUser("user3")).thenReturn(user2_groups);

        //then
        mockMvc.perform(get(groupsUrl + "/user/{userId}", "user3"))
                .andExpect(status().isOk())
                //groups>group1
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(id2)))
                .andExpect(jsonPath("$[0].groupName", is(group2.getGroupName())))
                .andExpect(jsonPath("$[0].members", containsInAnyOrder("user1", "user3")))
                .andExpect(jsonPath("$[0].members", not(hasItem("user2"))));
                //groups>group2

        //and
        verify(groupService, times(1)).getGroupsByUser("user3");

    }

    @Test
    void testGetGroupsByUser_EmptyGroups() throws Exception {
        //when
        when(groupService.getGroupsByUser("user4")).thenReturn(Collections.emptyList());

        //then
        mockMvc.perform(get(groupsUrl + "/user/{userId}","user4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(0)));
        //and
        verify(groupService,times(1)).getGroupsByUser("user4");

    }

}



