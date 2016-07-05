package com.jsantos.ps.store.backup.create;

import java.io.IOException;
import java.io.Writer;


public class IndentedWriter{
	int indentLevel = 0;
	Writer out;
	
	public IndentedWriter(Writer out) throws IOException {
		this.out = out;
	}

	public void println(String str) throws IOException{
		if (null != str){
			print(str);
			out.write("\r\n");
		}
	}
	
	public void print(String str) throws IOException{
		if (null != str){
			for (int i=0; i<indentLevel; i++) out.write("\t");
			out.write(str);
		}
	}
	
	public void flush() throws IOException{
		out.flush();
	}
	
	public void close() throws IOException{
		out.flush();
		out.close();
	}
	
}
