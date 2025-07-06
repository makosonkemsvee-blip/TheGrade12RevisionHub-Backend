package com.investhoodit.RevisionHub.dto;

import java.util.List;

public class GroupUsersUpdateDTO {
    private List<UserDTO> users;

    public GroupUsersUpdateDTO(){}

    public GroupUsersUpdateDTO(List<UserDTO> users){
        this.users = users;
    }

    //getters and setters
    public List<UserDTO> getUsers(){return users;}
    public void setUsers(List<UserDTO> users){ this.users = users;}

}
