package com.jsantos.ps.objects.mail.client;

import java.util.Vector;

import javax.mail.Part;

public class MailParts{
	public Part textPart = null;
	public Part htmlPart = null;
	public Vector<Part> fileAttachments = new Vector<Part>();
	public Vector<Part> mailAttachments = new Vector<Part>();
}
