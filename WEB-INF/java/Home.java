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
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;


/**
 * Servlet que comprueba si existe un usuario logueado.
 * - Si no existe lo redirige a un formulario para loguear
 * - Si existe redirige a la página principal del usuario.
 */
public class Home extends HttpServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
        // establece ContentType y sistema de codificación de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
		// Comprobamos si el usuario está logeado y recuperamos sus datos de la sesión
        UsersEntity user = (UsersEntity)request.getSession().getAttribute("UserData");
     
        RequestDispatcher rd ;
     
		if (user == null)
		{
			rd = request.getRequestDispatcher("login.jsp");
			rd.forward(request, response);
			return;
		}

		String userName = (String)user.getName();	
		request.setAttribute("userLoginName", userName);

        HibernateUtils hib = new HibernateUtils(path);
		try
		{
			// Si el parámetro GET "lastMsgId" está seteado es que el usuario solo quiere los mensajes recientes:
	    	String lastMsgId = request.getParameter("lastMsgId");
			if(lastMsgId != null)
			{
				//System.out.println("LAST ID " + Integer.parseInt(lastMsgId));
		        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response) {
		            private final StringWriter sw = new StringWriter();

		            @Override
		            public PrintWriter getWriter() throws IOException {
		                return new PrintWriter(sw);
		            }

		            @Override
		            public String toString() {
		                return sw.toString();
		            }
		        };

				//System.out.println("[lastMsgId]: " + lastMsgId);
				ArrayList<MessagesEntity> newMsgs = hib.getTimelineMsgs(user.getId(), Integer.parseInt(lastMsgId));
				
				JSONObject jsonResponse = new JSONObject();
				jsonResponse.put("msgCount", newMsgs.size());


				JSONArray jsonMsgs = new JSONArray();
				for (MessagesEntity msg : newMsgs) 
				{ 
					JSONObject jsonMsg = new JSONObject();

			        jsonMsg.put("id", msg.getId());

					request.setAttribute("msg", msg);
			        request.getRequestDispatcher("/WEB-INF/jspf/message.jsp").include(request, responseWrapper);
					jsonMsg.put("html", responseWrapper.toString());

					jsonMsgs.add(jsonMsg);
				}

				jsonResponse.put("msgs", jsonMsgs);

				response.setContentType("application/json");
				response.setHeader("Cache-Control", "nocache");
				response.setCharacterEncoding("utf-8");
					
				response.getWriter().println(jsonResponse); 
			}
			else
			{
				rd = request.getRequestDispatcher("home.jsp");

				request.setAttribute("userLoginPhoto", (String)user.getPhoto());
				request.setAttribute("userLoginAlias", user.getAlias());

				int numMessages = hib.numMessages(user.getId());
				int numFollowing = hib.numFollowing(user.getId());
				int numFollowers = hib.numFollowers(user.getId());
				
				request.setAttribute("numMessages", numMessages);
				request.setAttribute("numFollowing", numFollowing);
				request.setAttribute("numFollowers", numFollowers);

				ArrayList<MessagesEntity> timelineMsgs = hib.getTimelineMsgs(user.getId());
				
				request.setAttribute("timelineMsgs", timelineMsgs);


				rd.forward(request,response);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

			rd = request.getRequestDispatcher("error.jsp");
		}
   	}
}