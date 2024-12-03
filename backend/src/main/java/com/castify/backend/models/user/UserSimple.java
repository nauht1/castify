package com.castify.backend.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSimple {
    private String id;
//    private String firstName;
//    private String middleName;
//    private String lastName;
    private String fullname;
    private String username;
    private String avatarUrl;
    private boolean isFollow;
    private long totalFollower;
    private long totalFollowing;
    private long totalPost;
    public void setIsFollow(boolean follow) {
        this.isFollow=follow;
    }

}
