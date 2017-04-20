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


public class Conversation extends HttpServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
        // establece ContentType y sistema de codificación de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
        UsersEntity userLogIn = (UsersEntity)request.getSession().getAttribute("UserData");
        String userLogInName;
        RequestDispatcher rd ;

        int originalMsgId = -1;

        try
        {
            originalMsgId = Integer.parseInt(request.getParameter("msgId"));
        }
        catch(NumberFormatException e)
        {
            e.printStackTrace();
        }
		
		if (userLogIn != null)
		{			
			userLogInName = (String)userLogIn.getName();
			
			request.setAttribute("userLoginName", userLogInName);
			request.setAttribute("userLoginPhoto", (String)userLogIn.getPhoto());
			request.setAttribute("userLoginAlias", userLogIn.getAlias());
		}
			
		rd = request.getRequestDispatcher("conversation.jsp");

		HibernateUtils hib = new HibernateUtils(path);
		try
		{
			
			
			ArrayList<MessagesEntity> conversationMsgs = hib.getConversation(originalMsgId);
			if (conversationMsgs.size() == 0)
			{
				rd = request.getRequestDispatcher("error404.jsp");
			}
			else
			{
				request.setAttribute("conversationMsgs", conversationMsgs);
            }
		}
		catch (Exception e)
		{
			e.printStackTrace();
			rd = request.getRequestDispatcher("error.jsp");
		}
		
		rd.forward(request,response);
		
        // obtiene un PrintWriter para escribir la respuesta
        PrintWriter out = response.getWriter();
    }

}