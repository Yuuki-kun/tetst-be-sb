package com.amigos.authenautho.demo.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue
    private Long c_id;

    private String commentText;
    private Date commentDate;

    @ManyToOne
    @JoinColumn(name = "r_id")
    private Review review;

    @ManyToOne
    @JoinColumn(name = "KH_ID")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> replies;

}

