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
import org.json.simple.JSONObject;


/**
 * Servlet utilizado para reenviar un mensaje
 * publicado por un usuario a todos los usuarios
 * que te siguen.
 */
public class Forward extends HttpServlet
{
	
	//Incluye el mensaje reenviado en tu perfil, mostrando los datos originales del mensaje.
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
		// establece ContentType y sistema de codificación de caracteres
		response.setContentType("text/html; charset=UTF-8");
		
		RequestDispatcher rd;
		JSONObject jsonResponse = new JSONObject();

		UsersEntity user = (UsersEntity)request.getSession().getAttribute("UserData");

		if (user == null)
		{
			System.out.println("ERROR INVALID USER");
			jsonResponse.put("error", "La sesión ha expirado.");
			jsonResponse.put("redirect", response.encodeRedirectURL("/home"));
			
			/*
			String path2 = getServletContext().getRealPath("");
   			
   			String[]modified;
   			modified = path2.split("/");
   
		    for(int i = 0; i<modified.length;i++)
   			{
    			if(modified[i].equals("webapps"))
    			{
     				path2 = "/" + modified[i+1] + "/home";
     				jsonResponse.put("redirect", path2);
     				System.out.println(path2);
    			}
   			}
			*/
			jsonResponse.put("redirect", "home");
			response.getWriter().println(jsonResponse);
			//response.sendRedirect("home");
			return;
		}

		int forwarderId;
		int msgId = -1;
		
		forwarderId = (int)user.getId();
		
		try
		{
            msgId = Integer.parseInt(request.getParameter("fwdMsgId"));
		}
        catch (NumberFormatException e)
        {
            e.printStackTrace();
        }
		
		
		HibernateUtils hib = new HibernateUtils(path);
		try
		{
			
			
			hib.forwardMessage(forwarderId, msgId);
			jsonResponse.put("success", "Mensaje reenviado con éxito.");
			System.out.println("SUCCESS: Forwarded Message: " + msgId + " by user: " + user.getName());
		}
		catch (Exception e)
		{
			jsonResponse.put("error", "Error en el servidor.");
			e.printStackTrace();
		}
        
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "nocache");
		response.setCharacterEncoding("utf-8");
			
		response.getWriter().println(jsonResponse);
		//response.sendRedirect("home");
	}
	
}