package com.pack;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;

@Controller
public class RestaurantController extends HttpServlet {
    @RequestMapping(value = "/partner")
    public String getPartner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String currentURL = request.getRequestURI();
        session.setAttribute("previousPage", currentURL);

        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");
        if (loggedIn == null || !loggedIn) {
            response.sendRedirect("login");
            return "Login";
        }
        else {
            return "AddRestaurant";
        }
    }


    @RequestMapping(value = "/restaurantform", method = RequestMethod.POST)
    public String submitresForm(HttpServletRequest request, HttpServletResponse response,@RequestParam("l") Long r , @RequestParam("a") String s,
                                @RequestParam("b") String t, @RequestParam("c") Long u, @RequestParam("d") String v
            , @RequestParam("e") String w, @RequestParam("f") Long x, @RequestParam("g") String[] y, @RequestParam("h") String[] z
            , @RequestParam("i") String m, @RequestParam("j") String n, @RequestParam("k") CommonsMultipartFile file) {
        try {
            HttpSession session = request.getSession();

            String type = String.join(",", y);
            String cuisine = String.join(",", z);

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/fooddelivery?characterEncoding=utf8", "root", "root");
            PreparedStatement stmt = con.prepareStatement("insert into restaurants (res_name, address, res_phone, manager, email, phone, type, cuisine, timing_open, timing_close, username, res_id) values(?,?,?,?,?,?,?,?,?,?,?,?)");

            stmt.setString(1, s);
            stmt.setString(2, t);
            stmt.setLong(3, u);
            stmt.setString(4, v);
            stmt.setString(5, w);
            stmt.setLong(6, x);
            stmt.setString(7, type);
            stmt.setString(8, cuisine);
            stmt.setString(9, m);
            stmt.setString(10, n);
            stmt.setString(11, (String) session.getAttribute("userName"));
            stmt.setLong(12, r);

            InputStream inputStream = null;

            if (file != null) {
                inputStream = file.getInputStream();
            }

            String message = null;

            try {
                PreparedStatement stmt1 = con.prepareStatement("SELECT * FROM restaurants WHERE username=? and res_name=?");
                stmt1.setString(1, (String) session.getAttribute("userName"));
                stmt1.setString(2, s);

                ResultSet rs1 = stmt1.executeQuery();

                String sql = "INSERT INTO res_images (name, res_id, data) values (?, ?, ?)";
                PreparedStatement statement = con.prepareStatement(sql);
                statement.setString(1, file.getName());
                statement.setLong(2, r);

                if (inputStream != null) {
                    statement.setBlob(3, inputStream);
                }

                stmt.executeUpdate();
                int row = statement.executeUpdate();
                if (row > 0) {
                    message = "File uploaded and saved into database";
                }
            } catch (SQLException ex) {
                System.out.println(ex);
            }

            return "ResFormSubmit";
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
