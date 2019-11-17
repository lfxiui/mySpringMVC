package com.lfxiui.mvc.controller;

import com.lfxiui.mvc.annotation.Controller;
import com.lfxiui.mvc.annotation.RequestMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Fuxi
 */
@Controller
@RequestMapping(path = "/user")
public class UserController {

    @RequestMapping(path = "/login")
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/main.jsp").forward(request, response);
    }
}
