<%@page contentType="text/html" pageEncoding="UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<>
    <head>
        <title>${post.title}</title>
    </head>
<>

    <jsp:include page="header.jsp"/>

    <span style="font-size: 32px; color: #00008b">${post.title}</span>
    <p>${post.summary}</p>
    <hr/>
    <p>${post.body}</p>
    <hr/>

    <form action="/blog" method="post">
        <table>
            <tr>
                <input type="hidden" name="postId" value="${post.id}"/>
                <td>Author</td>
                <td><input type="text" name="commentAuthor"></td>
            </tr>
            <tr>
                <td>Comment</td>
                <td><textarea name="commentText" rows="10" cols="60"></textarea></td>
            </tr>
            <tr>
                <td></td>
                <td><input type="submit" value="Submit"></td>
            </tr>
        </table>

    </form>
    <p>Comments:</p>

            <c:forEach items="${comments}" var="comment">
                        <p>${comment.author} (${comment.created_dt}):${comment.comment}</p>
            </c:forEach>



</body>
</html>
