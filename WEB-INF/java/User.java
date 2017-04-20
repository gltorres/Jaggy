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


public class User extends HttpServlet
{
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
    	RequestDispatcher rd = request.getRequestDispatcher("profile.jsp");
        
        // Establece ContentType y sistema de codificacion de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
        // Comprobamos si el usuario está logeado y recuperamos sus datos de la sesión
        UsersEntity userLogin = (UsersEntity)request.getSession().getAttribute("UserData");
		
		if (userLogin != null)
		{
            String userLoginName = (String)userLogin.getName();
            
            request.setAttribute("userLoginName", userLoginName);
            request.setAttribute("userLoginPhoto", (String)userLogin.getPhoto());
            request.setAttribute("userLoginAlias", userLogin.getAlias());
		}

		HibernateUtils hib = new HibernateUtils(path);
		try
		{
			UsersEntity user = hib.getUser(request.getParameter("name"));
			if (user == null)
			{
				rd = request.getRequestDispatcher("error404.jsp");
			}
			else
			{
				// Si el parámetro GET "lastMsgId" está seteado es que el usuario solo quiere los mensajes recientes:
		    	String lastMsgId = request.getParameter("lastMsgId");

				if(lastMsgId != null)
				{
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
					ArrayList<MessagesEntity> newMsgs = hib.getProfileMsgs(user.getId(), Integer.parseInt(lastMsgId));
					
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
					request.setAttribute("userName",(String)user.getName());
					request.setAttribute("userPhoto",(String)user.getPhoto());
					request.setAttribute("userAlias",user.getAlias());
					

					int userId = user.getId();
					int numMessages = hib.numMessages(userId);
					int numFollowing = hib.numFollowing(userId);
					int numFollowers = hib.numFollowers(userId);
					
					request.setAttribute("numMessages", numMessages);
					request.setAttribute("numFollowing", numFollowing);
					request.setAttribute("numFollowers", numFollowers);

					ArrayList<MessagesEntity> profileMsgs = hib.getProfileMsgs(userId);
					
					request.setAttribute("profileMsgs", profileMsgs);
					/*
					for (MessagesEntity msg : profileMsgs) 
					{ 
						System.out.println("[User]: " + msg.getUser().getName());
					}
					*/
					rd.forward(request,response);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
	
			rd = request.getRequestDispatcher("error.jsp");
		}
    }
}