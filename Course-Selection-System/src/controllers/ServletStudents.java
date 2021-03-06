package controllers;

import com.alibaba.fastjson.JSON;
import models.Student;
import utils.ServletJSON;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServletStudents", urlPatterns = "/student")
public class ServletStudents extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Student student = ServletJSON.parse(request, Student.class);
        if (new Student().insertOne(student)) {
            doGet(request, response);
        } else {
            response.setStatus(400);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println(JSON.toJSON(new Student().findAll()));
    }
}
