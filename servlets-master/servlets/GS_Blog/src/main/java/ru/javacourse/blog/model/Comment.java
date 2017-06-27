package ru.javacourse.blog.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Comment extends BaseEntity{
    private String comment;
    private Date created_dt;
    private String author;
    private Post post;

    public Comment(String comment, Date created_dt, String author, Post post) {
        this.comment = comment;
        this.created_dt = created_dt;
        this.author = author;
        this.post = post;
    }

    public Comment(ResultSet resultSet, Post post) throws SQLException{
        this.id=resultSet.getInt("id");
        this.comment=resultSet.getString("comment");
        this.created_dt=resultSet.getDate("created_date");
        this.author=resultSet.getString("author");
        this.post=post;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated_dt() {
        return created_dt;
    }

    public void setCreated_dt(Date created_dt) {
        this.created_dt = created_dt;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
