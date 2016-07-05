package com.jsantos.ps.scheduler;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class JobLocalBackup implements org.quartz.Job{

	@Override
	public void execute(JobExecutionContext arg) throws JobExecutionException {
		System.out.println("Mensaje de prueba de copia local");
			
	}

}
