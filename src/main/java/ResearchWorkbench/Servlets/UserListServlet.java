package ResearchWorkbench.Servlets;

import ResearchWorkbench.Controllers.BusinessLayer;
import ResearchWorkbench.Models.ListItem;
import ResearchWorkbench.Models.UserList;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;


@WebServlet(name = "UserListServlet", value = "/UserListServlet")
public class UserListServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        int userId = Integer.parseInt(request.getParameter("userId"));
        BusinessLayer layer = new BusinessLayer();
        ArrayList<UserList> userLists = new ArrayList<UserList>();


        response.setContentType("text/html; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        if (method.equals("userListPage")){

            userLists = layer.getUserLists(userId);
            out.println("<a class=\"d-flex align-items-center flex-shrink-0 p-3 link-dark text-decoration-none border-bottom\">");
            out.println("<span class=\"fs-5 fw-semibold\">Lists</span>");
            out.println("<span class=\"pull-right\" >");
            out.println("<span class=\"btn btn-xs btn-default\" data-bs-toggle=\"modal\" data-bs-target=\"#createNewUserListModal\">");
            out.println("<span class=\"bi bi-plus-lg\" aria-hidden=\"true\"></span>");
            out.println("</span></span></a>");

            for (UserList userList : userLists) {
                out.println("<a onclick=\"getListItems(" + userList.getUserListId() + ")\" class=\"list-group-item list-group-item-action\" aria-current=\"true\">");
                out.println("<div class=\"d-flex w-100 justify-content-between\">");
                out.println("<h5 class=\"mb-1\">" + userList.getUserListName() + " </h5>");
                out.println("<span class=\"pull-right\">");

                out.println("<span class=\"btn btn-xs btn-default\" onclick=\"deleteUserList(" + userList.getUserListId() + "); event.stopPropagation();\">");
                out.println("<span class=\"bi bi-x-lg\" aria-hidden=\"true\"></span>");
                out.println("</span></span></div>");
                out.println("<select onchange=\"updateUserList(" + userList.getUserListId() + ",this.value); event.stopPropagation();\" class=\"form-select\" aria-label=\"Default select example\">");
                if (userList.getIsPrivate()){
                    out.println("<option value=\"0\">Public</option>");
                    out.println("<option value=\"1\" selected>Private</option>");
                } else {
                    out.println("<option value=\"0\" selected>Public</option>");
                    out.println("<option value=\"1\">Private</option>");
                }
                out.println("</select>");
                out.println(" </a>");
            }
        }

        if (method.equals("showPage")){
            String objectId = request.getParameter("objectId");
            userLists = layer.getUserListsContainingListItem(objectId, userId);

            out.println("<a class=\"d-flex align-items-center flex-shrink-0 p-3 link-dark text-decoration-none border-bottom\">");
            out.println("<span class=\"fs-5 fw-semibold\">User Lists</span></a>");

            if (userLists.size() == 0){
                out.println("<p style=\"text-align: center; margin-top: 8px; text-decoration: none\" >No other user lists contain this paper</p>");
            } else {

                out.println("<div class=\"list-group\">");
                for(UserList userList : userLists){
                    ArrayList<ListItem> listItems = layer.getListItems(userList.getUserListId());
//                    out.println("<ul class=\"list-group\">");
//                    out.println("<a style=\"text-decoration: none\" href=\"view_user_list.html?id=" + userList.getUserListId() + "\">");
//                    out.println("<li class=\"list-group-item list-group-item-action\" mb-1>");
//                    out.println("<div style=\"margin-bottom: 8px\">");
//                    out.println("<b>" + userList.getUserListName() + "</b>");
//                    out.println("<small>by " + layer.getUser(userList.getUserId()).getUserName() + "</small>");
//                    out.println("</div>");
//                    out.println("<ol class=\"list-group list-group-numbered\">");
//                    for (ListItem listItem : listItems ){
//                        out.println("<li onclick=\"showListItem('" + listItem.getListObjectId() + "'); event.preventDefault(); event.stopPropagation();\" class=\"list-group-item list-group-item-action d-flex justify-content-between align-items-start\">");
//                        out.println("<div class=\"ms-2 me-auto\">");
//                        out.println("<div>" + listItem.getObjectTitle() + "</div>");
//                        out.println("<small>"+ listItem.getObjectAuthor() + "</small>");
//                        out.println("</div>");
//                        out.println("</li>");
//                    }
//                    out.println("</li></a></ul>");
                    out.println("<a href=\"view_user_list.html?id=" + userList.getUserListId() + "\" class=\"list-group-item list-group-item-action\" aria-current=\"true\">");
                    out.println("<div class=\"d-flex w-100 justify-content-between\">");
                    out.println("<h5 class=\"mb-1\">" + userList.getUserListName() + " </h5>");
                    out.println("</div>");
                    out.println("<p class=\"mb-1\"> Created by: " + layer.getUser(userList.getUserId()).getUserName() + "</p>");
                    out.println("<small>" + listItems.size() + " resources</small>");
                    out.println(" </a>");
                }
            }
        }
        out.close();

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getParameter("method");
        int userId = Integer.parseInt(request.getParameter("userId"));
        //get the controller methods
        BusinessLayer layer = new BusinessLayer();

        if (method.equals("create")){
            String listName = request.getParameter("listName");
            boolean checked = Boolean.parseBoolean(request.getParameter("isPrivate"));


            //create the UserList object
            UserList userList = new UserList(listName,checked, userId);
            //create the user list
            userList.setUserListId(layer.createUserList(userList));
        }

        if (method.equals("delete")){
            int userListId = Integer.parseInt(request.getParameter("userListId"));

            layer.deleteUserList(userListId, userId);
        }
        if (method.equals("update")){
            int userListId = Integer.parseInt(request.getParameter("userListId"));
            int isPrivateInt = Integer.parseInt(request.getParameter("isPrivate"));
            Boolean isPrivate;
            if (isPrivateInt == 0){
                isPrivate = false;
            } else {
                isPrivate = true;
            }
            UserList newUserList = layer.getUserList(userListId);
            newUserList.setIsPrivate(isPrivate);
            layer.updateUserList(newUserList);
        }
    }

}
