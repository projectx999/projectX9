package com.hydra.divideup.controller;

import static com.hydra.divideup.exception.DivideUpError.GROUP_DELETE_UNSETTLE;
import static com.hydra.divideup.exception.DivideUpError.GROUP_NOT_FOUND;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hydra.divideup.entity.Group;
import com.hydra.divideup.exception.IllegalOperationException;
import com.hydra.divideup.exception.RecordNotFoundException;
import com.hydra.divideup.service.GroupService;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    value = GroupController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class GroupControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private GroupService groupService;

  @Autowired ObjectMapper objectMapper;

  private final String groupsUrl = "/api/v1/groups";

  @Test
  void testGetGroup() throws Exception {
    // given
    final String id = "123";
    Group group = new Group("Test Group", Set.of("user1", "user2"), "user1");
    group.setId(id);

    // when
    when(groupService.getGroup(id)).thenReturn(group);

    // then
    mockMvc
        .perform(get(groupsUrl + "/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(id)))
        .andExpect(jsonPath("$.groupName", is(group.getGroupName())))
        .andExpect(jsonPath("$.members", containsInAnyOrder("user1", "user2")));
    // and
    verify(groupService, times(1)).getGroup(id);
  }

  @Test
  void testGetGroup_NotFound() throws Exception {
    final String id = "234";

    // when
    when(groupService.getGroup(id)).thenThrow(new RecordNotFoundException(GROUP_NOT_FOUND));

    // then
    mockMvc
        .perform(get(groupsUrl + "/{id}", id))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(GROUP_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(GROUP_NOT_FOUND.getMessage())));
    // and
    verify(groupService, times(1)).getGroup(id);
  }

  @Test
  void testGetGroupsByUser() throws Exception {
    // given
    final String id1 = "123";
    Group group1 = new Group("Test Group1", Set.of("user1", "user2"), "user1");
    group1.setId(id1);

    final String id2 = "234";
    Group group2 = new Group("Test Group2", Set.of("user1", "user3"), "user3");
    group2.setId(id2);

    List<Group> user1_groups = List.of(group1, group2);

    /*find groups for user1*/
    // when
    when(groupService.getGroupsByUser("user1")).thenReturn(user1_groups);

    // then
    mockMvc
        .perform(get(groupsUrl + "/user/{userId}", "user1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // groups>group1
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(id1)))
        .andExpect(jsonPath("$[0].groupName", is(group1.getGroupName())))
        .andExpect(jsonPath("$[0].members", containsInAnyOrder("user1", "user2")))
        .andExpect(jsonPath("$[0].members", not(hasItem("user3"))))
        // groups>group2
        .andExpect(jsonPath("$[1].id", is(id2)))
        .andExpect(jsonPath("$[1].groupName", is(group2.getGroupName())))
        .andExpect(jsonPath("$[1].members", containsInAnyOrder("user1", "user3")))
        .andExpect(jsonPath("$[1].members", not(hasItem("user2"))));
    // and
    verify(groupService, times(1)).getGroupsByUser("user1");

    /*find groups for user3*/
    Mockito.reset(groupService);

    List<Group> user2_groups = List.of(group2);

    when(groupService.getGroupsByUser("user3")).thenReturn(user2_groups);

    // then
    mockMvc
        .perform(get(groupsUrl + "/user/{userId}", "user3"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        // groups>group1
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(id2)))
        .andExpect(jsonPath("$[0].groupName", is(group2.getGroupName())))
        .andExpect(jsonPath("$[0].members", containsInAnyOrder("user1", "user3")))
        .andExpect(jsonPath("$[0].members", not(hasItem("user2"))));
    // groups>group2

    // and
    verify(groupService, times(1)).getGroupsByUser("user3");
  }

  @Test
  void testGetGroupsByUser_EmptyGroups() throws Exception {
    // when
    when(groupService.getGroupsByUser("user4")).thenReturn(Collections.emptyList());

    // then
    mockMvc
        .perform(get(groupsUrl + "/user/{userId}", "user4"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(0)));
    // and
    verify(groupService, times(1)).getGroupsByUser("user4");
  }

  @Test
  void testCreateGroup() throws Exception {
    // given
    final String groupId = "234";
    final String createdBy = "created_user";
    Group group = new Group("New Group", new HashSet<>(1), createdBy);
    group.setId(groupId);
    // Adding created user as member
    group.getMembers().add(createdBy);

    // when
    when(groupService.createGroup(any(Group.class))).thenReturn(group);

    // then
    mockMvc
        .perform(
            post(groupsUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(group)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(groupId)))
        .andExpect(jsonPath("$.groupName", is(group.getGroupName())))
        .andExpect(jsonPath("$.createdBy", is(group.getCreatedBy())))
        .andExpect(jsonPath("$.members", hasSize(1)))
        .andExpect(jsonPath("$.members", containsInAnyOrder("created_user")));
    // verify
    verify(groupService, times(1)).createGroup(any(Group.class));
  }

  @Test
  public void testUpdateGroup() throws Exception {
    // given
    final String groupId = "234";
    Group updatedGroup = new Group("New Group", Set.of("user1", "user2"), "created_user");
    updatedGroup.setId(groupId);

    // when
    when(groupService.updateGroup(eq(groupId), any(Group.class))).thenReturn(updatedGroup);

    // then
    mockMvc
        .perform(
            put(groupsUrl + "/{id}", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedGroup)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(groupId)))
        .andExpect(jsonPath("$.groupName", is(updatedGroup.getGroupName())))
        .andExpect(jsonPath("$.createdBy", is(updatedGroup.getCreatedBy())))
        .andExpect(jsonPath("$.members", hasSize(2)))
        .andExpect(jsonPath("$.members", containsInAnyOrder("user1", "user2")));
    // verify
    verify(groupService, times(1)).updateGroup(eq(groupId), any(Group.class));
  }

  @Test
  public void testUpdateGroup_groupNotFound() throws Exception {
    // given
    final String groupId = "234";
    Group updatedGroup = new Group("New Group", Set.of("user1", "user2"), "created_user");
    updatedGroup.setId(groupId);

    // when
    when(groupService.updateGroup(eq(groupId), any(Group.class)))
        .thenThrow(new RecordNotFoundException(GROUP_NOT_FOUND));

    // then
    mockMvc
        .perform(
            put(groupsUrl + "/{id}", groupId)
                .content(objectMapper.writeValueAsString(updatedGroup))
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(GROUP_NOT_FOUND.getCode())))
        .andExpect(jsonPath("$.message", is(GROUP_NOT_FOUND.getMessage())));

    // verify
    verify(groupService, times(1)).updateGroup(eq(groupId), any(Group.class));
  }

  @Test
  public void testDeleteGroup() throws Exception {
    // given
    final String groupId = "1";
    Group deletedGroup = new Group("Deleted Group", Set.of("user1", "user2"), "created_user");
    deletedGroup.setId(groupId);

    // when
    Mockito.when(groupService.deleteGroup(groupId)).thenReturn(deletedGroup);

    // then
    mockMvc
        .perform(delete(groupsUrl + "/{id}", groupId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(objectMapper.writeValueAsString(deletedGroup)));

    // verify
    verify(groupService, Mockito.times(1)).deleteGroup(groupId);
  }

  @Test
  public void testDeleteGroup_NotSettledThrowException() throws Exception {
    // given
    final String groupId = "1";
    Group deletedGroup = new Group("Deleted Group", Set.of("user1", "user2"), "created_user");
    deletedGroup.setId(groupId);

    // when
    Mockito.when(groupService.deleteGroup(groupId))
        .thenThrow(new IllegalOperationException(GROUP_DELETE_UNSETTLE));

    // then
    mockMvc
        .perform(delete(groupsUrl + "/{id}", groupId).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code", is(GROUP_DELETE_UNSETTLE.getCode())))
        .andExpect(jsonPath("$.message", is(GROUP_DELETE_UNSETTLE.getMessage())));

    // verify
    verify(groupService, Mockito.times(1)).deleteGroup(groupId);
  }
}
