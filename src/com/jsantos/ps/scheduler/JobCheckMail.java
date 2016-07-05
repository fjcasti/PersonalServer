package com.jsantos.ps.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobCheckMail implements org.quartz.Job{

	@Override
	public void execute(JobExecutionContext arg) throws JobExecutionException {
		System.out.println("Mensaje de prueba de checkmail");
			
	}

}
