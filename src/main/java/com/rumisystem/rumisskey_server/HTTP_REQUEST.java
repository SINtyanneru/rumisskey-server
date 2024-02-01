package com.rumisystem.rumisskey_server;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class HTTP_REQUEST {
	private URL REQIEST_URI = null;

	public HTTP_REQUEST(String INPUT_REQ_URL){
		try{
			REQIEST_URI = new URL(INPUT_REQ_URL);
		}catch (Exception EX) {
			System.err.println(EX);
			System.exit(1);
		}
	}

	public String SEND(String POST_BODY){
		try{
			System.out.println("[  ***  ]GET:" + REQIEST_URI.toString());
			HttpURLConnection HUC = (HttpURLConnection) REQIEST_URI.openConnection();

			//POSTだと主張する
			HUC.setRequestMethod("POST");

			//POST可能に
			HUC.setDoInput(true);
			HUC.setDoOutput(true);

			HUC.setRequestProperty("Content-Type", "application/json; charset=utf-8");

			HUC.connect();

			//リクエストボディに送信したいデータを書き込む
			PrintStream PS = new PrintStream(HUC.getOutputStream());
			PS.print(POST_BODY);
			PS.close();

			//レスポンスコード
			int RES_CODE = HUC.getResponseCode();
			BufferedReader BR = new BufferedReader(new InputStreamReader(HUC.getInputStream(), StandardCharsets.UTF_8));
			StringBuilder RES_STRING = new StringBuilder();

			String INPUT_LINE;
			while ((INPUT_LINE = BR.readLine()) != null){
				RES_STRING.append(INPUT_LINE);
			}

			BR.close();
			System.out.println("[  OK   ]GET");
			return RES_STRING.toString();
		}catch (Exception EX){
			EX.printStackTrace();
			return null;
		}
	}
}
