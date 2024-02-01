package com.rumisystem.rumisskey_server;

import com.rumisystem.rumisskey_server.HTTP.HTTP_SERVER;

public class Main {
	public static void main(String[] args) {
		System.out.println("Ru,Misskey Server");

		// HTTP鯖起動
		HTTP_SERVER.main();
	}

	public static void LOG_PRINT(String CLASS_NAME, int MODE, String TEXT) {
		switch (MODE) {
			case 0:
				System.out.println("[  OK  ]" + "[ " + CLASS_NAME + " ]" + TEXT);
				break;
			case 1:
				System.out.println("[ INFO ]" + "[ " + CLASS_NAME + " ]" + TEXT);
				break;
			case 2:
				System.out.println("[ ERR ]" + "[ " + CLASS_NAME + " ]" + TEXT);
				break;
			case 3:
				System.out.println("[ *** ]" + "[ " + CLASS_NAME + " ]" + TEXT);
				break;
			default:
				System.out.println("ERR");
				break;
		}
	}
}
