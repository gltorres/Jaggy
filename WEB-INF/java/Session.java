import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.json.simple.JSONObject;
import Entities.*;

/**
 * Servlet que recibe las credenciales de login de un usuario mediante ajax.
 * - Si todo va bien inicializa los datos de sesión.
 * - En caso de error devolverá una respuesta ajax para mostrar al usuario.
 */
public class Session extends HttpServlet {
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
        // establece ContentType y sistema de codificación de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
		//jaggy/session?action=logout
		String action = request.getParameter("action");
		if (action != null)
		{
			if(action.equals("logout"))
			{
				request.getSession().invalidate();
			}
		}
		// Este servlet solo soporta peticiones POST.
		response.sendRedirect("home");
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
        String path = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");

        // establece ContentType y sistema de codificación de caracteres
        response.setContentType("text/html; charset=UTF-8");
        
		String username = request.getParameter("username");
		String password = request.getParameter("password");
			
		JSONObject jsonResponse = new JSONObject();
		
		
		UsersEntity user;
			
		HibernateUtils hib = new HibernateUtils(path);
		try
		{
			
			user = hib.getUser(username);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			user = null;
		}
		
		
		if (user == null)
			jsonResponse.put("error", "Usuario incorrecto");
		else
		{
            if (user.getPassword().equals(password))
            {
                // Si la contraseña es correcta establecemos los datos de sesión
                request.getSession().setAttribute("UserData", user);
                // Y redirigimos al usuario a la página principal.
                //response.sendRedirect("home");
                jsonResponse.put("redirect", "home");
            }
            else
            {
                jsonResponse.put("error", "Contraseña incorrecta.");
            }
		}
			
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "nocache");
		response.setCharacterEncoding("utf-8");
			
		response.getWriter().println(jsonResponse);
	}
}