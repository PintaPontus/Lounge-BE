package com.pinta.lounge.repository;

import com.pinta.lounge.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    @Query("""
        select user
        from UserEntity user
        where user.email = :name
            or user.username = :name
    """)
    Optional<UserEntity> findUser(@Param("name") String name);

    @Query("""
            select user.id
            from UserEntity user
            where user.username = :username
        """)
    Optional<Long> findUserId(@Param("username") String username);
}
