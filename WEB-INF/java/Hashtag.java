import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import Entities.*;


/**
 * Servlet que inserta un nuevo hastag en la
 * DB, y almacena la referencia de los mensajes 
 * que tienen ese hastag.
 */
public class Hashtag extends HttpServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
        UsersEntity userLogIn = (UsersEntity)request.getSession().getAttribute("UserData");
        String userLogInName;
        RequestDispatcher rd ;
	 		
		if (userLogIn != null)
		{			
			userLogInName = (String)userLogIn.getName();	

			request.setAttribute("userLoginName", userLogInName);
			request.setAttribute("userLoginPhoto", (String)userLogIn.getPhoto());
			request.setAttribute("userLoginAlias", userLogIn.getAlias());
		}
		
		rd = request.getRequestDispatcher("hashtag.jsp");
		// establece ContentType y sistema de codificacion de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
		String hashtag = "";
		hashtag = request.getParameter("name");

		System.out.println("[Hashtag] - " + hashtag);
		
		HibernateUtils hib = new HibernateUtils(path);
		try
		{
			ArrayList<MessagesEntity> hashMsgs = new ArrayList<MessagesEntity>();
			hashMsgs = hib.getMessagesByHashtag(hashtag);
			
			if (hashMsgs.size() == 0)
			{
				rd = request.getRequestDispatcher("error404.jsp");
			}
			else
			{
				for (MessagesEntity msg : hashMsgs)
				{
					System.out.println("[Message] - " + msg.getText()); 
				}

				request.setAttribute("hashtag", hashtag);
				request.setAttribute("hashMsgs", hashMsgs);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			rd = request.getRequestDispatcher("error.jsp");
		}
		
		rd.forward(request,response);
	
    }
}
