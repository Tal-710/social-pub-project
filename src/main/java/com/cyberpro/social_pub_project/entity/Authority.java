package com.cyberpro.social_pub_project.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "authorities")
public class Authority {

    @Embeddable
    public static class AuthorityId implements Serializable {

        @Column(name = "username", nullable = false )
        private String username;

        @Column(name = "authority", nullable = false)
        private String authority;

        public AuthorityId() {}

        public AuthorityId(String username, String authority) {
            this.username = username;
            this.authority = authority;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AuthorityId that = (AuthorityId) o;
            return Objects.equals(username, that.username) && Objects.equals(authority, that.authority);
        }

        @Override
        public int hashCode() {
            return Objects.hash(username, authority);
        }
    }

    @EmbeddedId
    private AuthorityId id;


    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username" ,insertable=false, updatable=false)
    @JsonBackReference
    private User user;

    @Column(name = "username" , insertable=false, updatable=false)
    private String username;


    public Authority() {
    }

    public Authority(String username, String authority, User user) {
        this.id = new AuthorityId(username, authority);
        this.user = user;
    }

    public AuthorityId getId() {
        return id;
    }

    public void setId(AuthorityId id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}