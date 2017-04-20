import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Date;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import Entities.*;
import java.nio.charset.StandardCharsets;

/**
 * Servlet que publica un nuevo mensaje
 * en la base de datos.
 */
public class Publish extends HttpServlet
{
		
	//Crea un nuevo mensaje y le da el id de la conversación a la que pertenece.
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
		// establece ContentType y sistema de codificación de caracteres
		response.setContentType("text/html; charset=UTF-8");
		
		UsersEntity user = (UsersEntity)request.getSession().getAttribute("UserData");
		String userName;
		RequestDispatcher rd ;
		
		if (user == null)
		{
			System.out.println("ERROR INVALID USER");
			response.sendRedirect("home");
			return;
		}

		String text  = request.getParameter("message");
		byte ptext[] = text.getBytes(StandardCharsets.ISO_8859_1);
		text = new String(ptext, StandardCharsets.UTF_8);
		text = text.trim();
		String msgType  = request.getParameter("type");
		
		if ((text != null) && (text.length() > 0) && (text.length() <= 141))
		{
			MessagesEntity msg = new MessagesEntity();
			msg.setText(text);
			msg.setUser(user);
			msg.setPublishDate(new Date());
			msg.extractHashtags();
			msg.setOriginalId(0);
            msg.setPreviousId(0);
			
			HibernateUtils hib = new HibernateUtils(path);
			try
			{
				hib.postMessage(msg);
				System.out.println("SUCCESS: " + msg.toString());
			}
			catch(Exception e)
			{
			
			}
			
		}
		response.sendRedirect("home");
	}
	
}