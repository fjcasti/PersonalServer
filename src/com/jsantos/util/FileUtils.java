package com.jsantos.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FileUtils
{
	public static String loadFile(String fileName, Class requester){
		try{
			StringBuffer fileContent = new StringBuffer();
			System.out.println(requester.getResourceAsStream(fileName));
			BufferedReader in = new BufferedReader(new InputStreamReader(requester.getResourceAsStream(fileName)));
			for (String line=in.readLine(); null != line; line=in.readLine()) {
				fileContent.append(line);
				fileContent.append("\r\n");
			}
			return fileContent.toString();
		}
		catch(IOException ioe){
			System.out.println(ioe);
			ioe.printStackTrace();
			throw new RuntimeException(ioe);
		}
	}

	
	public static void saveStream(String fileName, InputStream iStream)	throws IOException
	{
		saveStream(fileName, iStream, false);
	}


	public static void saveStream(String fileName, InputStream iStream,boolean createDir) throws IOException{
		String me = "FileUtils.WriteToFile";
		if (fileName == null){
			throw new IOException(me + ": filename is null");
		}
		if (iStream == null){
			throw new IOException(me + ": InputStream is null");
		}

		File theFile = new File(fileName);

		// Check if a file exists.
		if (theFile.exists()){
			String msg =
				theFile.isDirectory() ? "directory" :
					(! theFile.canWrite() ? "not writable" : null);
			if (msg != null){
				throw new IOException(me + ": file '" + fileName + "' is " + msg);
			}
		}

		// Create directory for the file, if requested.
		if (createDir && theFile.getParentFile() != null){
			theFile.getParentFile().mkdirs();
		}

		// Save InputStream to the file.
		BufferedOutputStream fOut = null;
		try{
			fOut = new BufferedOutputStream(new FileOutputStream(theFile));
			byte[] buffer = new byte[32 * 1024];
			int bytesRead = 0;
			while ((bytesRead = iStream.read(buffer)) != -1){
				fOut.write(buffer, 0, bytesRead);
			}
		}
		catch (Exception e){
			throw new IOException(me + " failed, got: " + e.toString());
		}
		finally{
			try{
				if (iStream != null){
					iStream.close();
				}
			}
			finally		{
				if (fOut != null){
					fOut.close();
				}
			}
		}
	}
	
	public static String calculateMD5Checksum(File file) throws IOException{
		try{
			InputStream fis =  new FileInputStream(file);
	
			byte[] buffer = new byte[1024];
			MessageDigest complete = MessageDigest.getInstance("MD5");
			int numRead;
			do {
				numRead = fis.read(buffer);
				if (numRead > 0) {
					complete.update(buffer, 0, numRead);
				}
			} while (numRead != -1);
			fis.close();
			return new BigInteger(1,complete.digest()).toString(16).trim();
		}
		catch (NoSuchAlgorithmException nsae){
			throw new IOException(nsae);
		}
		
	}
	
}
