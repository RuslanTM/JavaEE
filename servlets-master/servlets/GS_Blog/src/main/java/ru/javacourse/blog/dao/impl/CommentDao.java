package ru.javacourse.blog.dao.impl;

import ru.javacourse.blog.model.Category;
import ru.javacourse.blog.model.Comment;
import ru.javacourse.blog.model.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static ru.javacourse.blog.util.DateUtil.*;

import static ru.javacourse.blog.util.DateUtil.utilToSQLDate;

public class CommentDao extends AbstractDaoImpl<Comment> {
    @Override
    public void fillCreateStatement(PreparedStatement pstmt, Comment entity) {
        try {
            pstmt.setString(1, entity.getComment());
            pstmt.setDate(2, utilToSQLDate(entity.getCreated_dt()));
            pstmt.setString(3, entity.getAuthor());
            pstmt.setInt(4, entity.getPost().getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PostDao postDao = new PostDao();
    @Override
    public void fillEditStatement(PreparedStatement pstmt, Comment entity) {
        try {
            pstmt.setString(1, entity.getComment());
            pstmt.setDate(2, utilToSQLDate(entity.getCreated_dt()));
            pstmt.setString(3, entity.getAuthor());
            pstmt.setInt(4, entity.getPost().getId());
            pstmt.setInt(5, entity.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Comment getEntity(ResultSet resultSet) {
        try {
            Integer post_id = resultSet.getInt("postId");
            Post post = postDao.getById(post_id);
            return new Comment(resultSet, post);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Comment> getCommentsByPostId(Integer postId) {
        List<Comment> comments = new ArrayList<Comment>();
        Post post = postDao.getById(postId);

        try (Connection connection = dbUtil.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM comments WHERE postId=?");) {

            pstmt.setInt(1, postId);
            try (ResultSet resultSet = pstmt.executeQuery();){
                while (resultSet.next()){
                    comments.add(new Comment(resultSet, post));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;


    }

    @Override
    public String getCreateQuery() {
        return dbUtil.getQuery("create.comment");
    }

    @Override
    public String getDeleteQuery() {
        return dbUtil.getQuery("delete.comment.by.id");
    }

    @Override
    public String getEditQuery() {
        return dbUtil.getQuery("update.comment");
    }

    @Override
    public String getGetByIdQuery() {
        return dbUtil.getQuery("get.comment.by.id");
    }

    @Override
    public String getGetAllQuery() {
        return dbUtil.getQuery("get.all.comment");
    }
}
