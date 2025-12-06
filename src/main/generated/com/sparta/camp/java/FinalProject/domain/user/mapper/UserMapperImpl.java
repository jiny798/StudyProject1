package com.sparta.camp.java.FinalProject.domain.user.mapper;

import com.sparta.camp.java.FinalProject.domain.user.dto.UserResponse;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserSearchResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-12-06T22:15:53+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.13 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserSearchResponse toSearch(User user) {
        if ( user == null ) {
            return null;
        }

        UserSearchResponse.UserSearchResponseBuilder userSearchResponse = UserSearchResponse.builder();

        userSearchResponse.id( user.getId() );
        userSearchResponse.name( user.getName() );
        userSearchResponse.email( user.getEmail() );
        userSearchResponse.createdAt( user.getCreatedAt() );

        return userSearchResponse.build();
    }

    @Override
    public UserResponse toResponse(User user) {
        if ( user == null ) {
            return null;
        }

        User user1 = null;

        user1 = user;

        UserResponse userResponse = new UserResponse( user1 );

        return userResponse;
    }
}
