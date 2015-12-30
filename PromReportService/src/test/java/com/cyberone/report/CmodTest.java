package com.cyberone.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CmodTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println("ReportBatchCronJob executed : Start [" + dateTime.format(new Date()) + "]");
		
		Process p = null;
		String line = null;
		BufferedReader br = null;
		StringBuilder builder = new StringBuilder();

		String[] commands = {"./startReportBatch.sh"};
		
		ProcessBuilder processBuilder = new ProcessBuilder(commands);

		String promHome = "/usr/local/src"; //System.getProperty("prom.home");
		System.out.println("########################################################################################");
		System.out.println("########## Start Report_AutoBatch_Process");
		System.out.println("########## currentTime : " + new Date());
		System.out.println("########## promHome : " + promHome);
		File file = new File(promHome + "/jasper/batch");
		System.out.println("########################################################################################");
		
		try {
			processBuilder.directory(file);
			p = processBuilder.start();
			p.waitFor();

			br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = br.readLine()) != null) {
				builder.append(line + "\n");
			}
			br.close();

			System.out.println(builder.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().indexOf("error=13") >= 0) {
				try {
			        Class<?> fspClass = Class.forName("java.util.prefs.FileSystemPreferences");
			        Method chmodMethod = fspClass.getDeclaredMethod("chmod", String.class, Integer.TYPE);
			        chmodMethod.setAccessible(true);
			        chmodMethod.invoke(null, "/usr/local/src/jasper/batch/startReportBatch.sh", 0755);
			        
					p = processBuilder.start();
					p.waitFor();

					br = new BufferedReader(new InputStreamReader(p.getInputStream()));
					while ((line = br.readLine()) != null) {
						builder.append(line + "\n");
					}
					br.close();
					
					System.out.println(builder.toString());
					
			    } catch (Throwable ex) {
			        ex.printStackTrace();
			    }
			}
		} finally {
			if (p != null) p.destroy();
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			System.out.println("ReportBatchCronJob executed : End [" + dateTime.format(new Date()) + "]");
		}		
		
	}

}

