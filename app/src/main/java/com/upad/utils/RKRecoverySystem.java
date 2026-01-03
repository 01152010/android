/*************************************************************************
	> File Name: RKRecoverySystem.java
	> Author: jkand.huang
	> Mail: jkand.huang@rock-chips.com
	> Created Time: Wed 02 Nov 2016 03:10:47 PM CST
 ************************************************************************/
package com.upad.utils;

import android.content.Context;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class RKRecoverySystem{
	private static final String TAG = "RKRecoverySystem";
	private static final File RECOVERY_DIR = new File("/cache/recovery");
	private static final File COMMAND_FILE = new File(RECOVERY_DIR, "command");
	private static final File UPDATE_FLAG_FILE = new File(RECOVERY_DIR, "last_flag");

	public static void installPackage(Context context, File packageFile)throws IOException{
        String filename = packageFile.getPath();
		String arg = "--update_package=" + filename;
		writeFlagCommand(filename);
		bootCommand(context, arg);
	}

	private static void bootCommand(Context context, String arg) throws IOException {
		RECOVERY_DIR.mkdirs();  // In case we need it
		COMMAND_FILE.delete();  // In case it's not writable

		FileWriter command = new FileWriter(COMMAND_FILE);
		try {
			command.write(arg);
			command.write("\n");
		} finally {
			command.close();
		}

		// Having written the command file, go ahead and reboot
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		pm.reboot("recovery");

		throw new IOException("Reboot failed (no permissions?)");
	}

	public static String readFlagCommand() {
		if(UPDATE_FLAG_FILE.exists()) {
			Log.d(TAG, "UPDATE_FLAG_FILE is exists");
			char[] buf = new char[128];
			int readCount = 0;
            try {
				FileReader reader = new FileReader(UPDATE_FLAG_FILE);
				readCount = reader.read(buf, 0, buf.length);
				Log.d(TAG, "readCount = " + readCount + " buf.length = " + buf.length);
			}catch (IOException e) {
				Log.e(TAG, "can not read /cache/recovery/last_flag!");
			}finally {
				UPDATE_FLAG_FILE.delete();
				
			}
			
			StringBuilder sBuilder = new StringBuilder();
			for(int i = 0; i < readCount; i++) {
				if(buf[i] == 0) {
					break;
				}
				sBuilder.append(buf[i]);	
			}
			return sBuilder.toString();
		}else {
			return null;
		}
	}
	
	public static void writeFlagCommand(String path) throws IOException{
		RECOVERY_DIR.mkdirs();
		UPDATE_FLAG_FILE.delete();
		FileWriter writer = new FileWriter(UPDATE_FLAG_FILE);
		try {
			writer.write("updating$path=" + path);
		}finally {
			writer.close();
		}
	}
}
