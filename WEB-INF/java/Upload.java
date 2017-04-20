import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import Entities.*;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import org.apache.commons.io.output.*;
import org.apache.commons.io.FilenameUtils;

/**
 * Servlet utilizado para subir una nueva
 * foto de perfil de usuario.
 */
public class Upload extends HttpServlet
{
		
	//Crea un nuevo mensaje y le da el id de la conversación a la que pertenece.
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	throws IOException, ServletException
	{
        String realpath = this.getServletConfig().getServletContext().getRealPath("/WEB-INF");
        
		// establece ContentType y sistema de codificacion de caracteres
		response.setContentType("text/html; charset=UTF-8");
		
		UsersEntity user = (UsersEntity)request.getSession().getAttribute("UserData");
		String userName;
		RequestDispatcher rd ;
		String path = request.getRealPath("/") + getServletContext().getInitParameter("image_path");
		System.out.println(path);
		
		if (user == null)
		{
			System.out.println("ERROR INVALID USER");
			response.sendRedirect("home");
		}

		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );
		if( !isMultipart )
		{
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");  
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>"); 
			out.println("</body>");
			out.println("</html>");
			return;
		}
		
		/*FileItemFactory es una interfaz para crear FileItem*/
		FileItemFactory file_factory = new DiskFileItemFactory();
	
		/*ServletFileUpload esta clase convierte los input file a FileItem*/
		ServletFileUpload servlet_up = new ServletFileUpload(file_factory);
		
		/*sacando los FileItem del ServletFileUpload en una lista */
		try
		{ 
			List items = servlet_up.parseRequest(request);
		
			for(int i=0;i<items.size();i++)
			{
				/*FileItem representa un archivo en memoria que puede ser pasado al disco duro*/
				FileItem item = (FileItem) items.get(i);
				
				/*item.isFormField() false=input file; true=text field*/
				if (! item.isFormField())
				{
				
					/*cual sera la ruta al archivo en el servidor*/
					String filename = user.getName() + "."+(String)(FilenameUtils.getExtension(item.getName()));
					
					File archivo_server = new File(path+filename);
					/*y lo escribimos en el servido*/
					item.write(archivo_server);
					System.out.print("Nombre --> " + filename );
					System.out.print("<br> Tipo --> " + item.getContentType());
					System.out.print("<br> tamaño --> " + (item.getSize()/1240)+ "KB");
					System.out.print("<br>");
					
					user.setPhoto(filename);
				}
			}
			
		/************UNA VEZ SUBIDA LA FOTO MODIFICAR LA DB CON EL NOMBRE DE LA FOTO PARA EL USUARIO**************/
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

        HibernateUtils hib = new HibernateUtils(realpath);
        try
        {
            
            hib.attach(user);
            System.out.println("SUCCESS: User: " + user.getName() + " has been successfully updated his profile picture " + user.getPhoto());
        }
        catch(Exception e)
        {
        
        }
        
        response.sendRedirect("user?name="+user.getName());
    }
}

