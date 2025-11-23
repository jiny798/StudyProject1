package com.sparta.camp.java.FinalProject.domain.user.mapper;

import com.sparta.camp.java.FinalProject.domain.user.dto.UserResponse;
import com.sparta.camp.java.FinalProject.domain.user.dto.UserSearchResponse;
import com.sparta.camp.java.FinalProject.domain.user.entity.User;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-23T00:19:00+0900",
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

        Long id = null;
        String name = null;
        String email = null;
        LocalDateTime createdAt = null;

        id = user.getId();
        name = user.getName();
        email = user.getEmail();
        createdAt = user.getCreatedAt();

        UserResponse userResponse = new UserResponse( id, name, email, createdAt );

        return userResponse;
    }
}
