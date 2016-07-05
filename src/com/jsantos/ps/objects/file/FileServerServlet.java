package com.jsantos.ps.objects.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.util.media.Media;

import com.jsantos.ps.clipboard.Clipboard;
import com.jsantos.ps.store.DataFolder;
import com.jsantos.ps.store.database.DS;
import com.jsantos.ps.webapp.CTX;

@SuppressWarnings("serial")
public class FileServerServlet extends HttpServlet {
	
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.


    public void init() throws ServletException {
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
    	
    	if(request.getParameter("objectkey").equals("clipboard")){
    		fileFromClipboard(request,response);
    		
    	}else{
    		fileFromOid(request,response);
    	};
    	
    	
    }
    
    
    private void fileFromOid(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
    	String objectKey = request.getParameter("objectkey");
	   	
		String md5 = null;
		String fileName = null;
		String mimeType = null;
    	try {
    		Connection conn = DS.getDataSource().getConnection();
    		boolean found = false;
    		try{
    			String sql = "select r.filename, d.mimetype, d.md5 from fileref r, filedata d where r.filedatakey=d.filedatakey and r.objectkey=?";
    			PreparedStatement pst = conn.prepareStatement(sql);
    			pst.setString(1, objectKey);
    			ResultSet rs = pst.executeQuery();
    			if (rs.next()){
    				found = true;
    				fileName = rs.getString("filename");
    				mimeType = rs.getString("mimetype");
    				md5 = rs.getString("md5").trim();
    			}
    		}
    		finally{
    			conn.close();
    		}
	        if (!found) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
	            return;
	        }
		} catch (SQLException sqle) {
			throw new ServletException(sqle);
		}
    	
        File file = new File(DataFolder.getFilesFolderPath() + md5);
       // System.out.println(file.getAbsolutePath());

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }

        String contentType = mimeType;
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } finally {
            close(output);
            close(input);
        }
    }
    private void fileFromClipboard(HttpServletRequest request, HttpServletResponse response) throws IOException{
    	
    	if (!CTX.getCfg().clipboard.isPublic()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
            return;
        }
    	BufferedInputStream input = null;
        BufferedOutputStream output = null;
    	
    	
    	if(CTX.getCfg().clipboard.getDataType().equals(Clipboard.TYPE_FILE)){
    	    	
    	Media media= CTX.getCfg().clipboard.getFile();
    	    	
    	 if (media == null) {
             response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404.
             return;
         }
    	
    	response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", media.getContentType());
        response.setHeader("Content-Length", String.valueOf( media.getByteData().length));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + media.getName() + "\"");

        

        try {
            // Open streams.
            input = new BufferedInputStream(media.getStreamData(), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } finally {
            close(output);
            close(input);
        }
    	}else if(CTX.getCfg().clipboard.getDataType().equals(Clipboard.TYPE_TEXT)){
    		 output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);
    		 output.write(CTX.getCfg().clipboard.getText().getBytes("UTF-8"));
    		 close(output);
    	}
    	
    }
    

    private static void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}