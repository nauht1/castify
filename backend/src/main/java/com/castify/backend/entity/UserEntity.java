package com.castify.backend.entity;

import com.castify.backend.enums.Role;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id; // Sửa dòng này
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "user")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity implements UserDetails {

    @Id
    private String id; // Change Integer to String for MongoDB
    private String firstName;
    private String middleName;
    private String lastName;
    @NotNull
    private String username;
    private String avatarUrl;
    private String coverUrl;
    private LocalDateTime birthday;
    private String addressElements;
    private String ward;
    private String district;
    private String provinces;
//    private String address;
    private String password;
    private String phone;
    @NotNull
    private String email;
    private boolean isActive;
    private boolean isNonLocked;
    private boolean isNonBanned;
    private LocalDateTime createdDay;
    private LocalDateTime lastLogin;
    private LocalDateTime lastUpdateUsername;
    private List<String> badgesId;
//    @Enumerated(EnumType.STRING)
    private Role role;
    private List<String> following = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if(isNonBanned && isNonLocked && isActive)
            return true;
        return false;
//        return isActive;
    }
    public String getFullname(){
        return lastName+" "+middleName+" "+firstName;
    }
    public int getAge(){
        LocalDateTime today = LocalDateTime.now();
        int ages = today.getYear() - birthday.getYear();
//        if(birthday.getMonthValue() > today.getMonthValue()){
//            if(birthday.getDayOfMonth()< today.getDayOfMonth())
//                age=age-1;
//        }
        return ages;
    }

    public int getTotalFollowing(){
        return following.size();
    }

    public boolean isFollow(String targetId){
        return following.contains(targetId);
    }

    public String getAddress() {
        String address = "";

        if (addressElements != null && !addressElements.isEmpty() && !addressElements.isBlank()) {
            address += addressElements;
        }

        if (ward != null && !ward.isEmpty() && !ward.isBlank()) {
            address += (address.isEmpty() ? "" : ", ") + ward;
        }

        if (district != null && !district.isEmpty() && !district.isBlank()) {
            address += (address.isEmpty() ? "" : ", ") + district;
        }

        if (provinces != null && !provinces.isEmpty() && !provinces.isBlank()) {
            address += (address.isEmpty() ? "" : ", ") + provinces;
        }else{
            address += ".";
        }

        return address;
    }
    public void setNonBanned(boolean isNotBanned){
        this.isNonBanned = isNotBanned;
    }

}
