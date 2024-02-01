package com.rumisystem.rumisskey_server.HTTP;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumisskey_server.HTTP_REQUEST;
import com.rumisystem.rumisskey_server.Main;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;

import static com.rumisystem.rumisskey_server.Main.LOG_PRINT;

public class ROUTER {
	public ROUTER(HttpExchange EXCHANGE, HashMap URI_PARAM, String POST_DATA, HashMap HEADER_DATA){
		try{
			//リクエストURI
			String REQ_PATH = EXCHANGE.getRequestURI().toString();

			//リクエストURIからURIパラメーターを除去する
			int index = REQ_PATH.indexOf('?');
			if (index != -1) {
				REQ_PATH = REQ_PATH.substring(0, index);
			}

			//りざるとを登録するための変数
			HashMap<String, Object> RESULT = null;

			//ヘッダー
			Headers responseHeaders = EXCHANGE.getResponseHeaders();

			//CSS
			if(REQ_PATH.endsWith(".css")){
				responseHeaders.set("Content-Type", "text/css; charset=UTF-8");

				String CONTENTS = GET_FILE("/CONTENTS" + REQ_PATH);

				RESULT = new HashMap();
				RESULT.put("RESPONSE", CONTENTS);
				RESULT.put("STATUS_CODE", 200);
			} else if(REQ_PATH.endsWith(".js")){
				responseHeaders.set("Content-Type", "text/javascript; charset=UTF-8");

				String CONTENTS = GET_FILE("/CONTENTS" + REQ_PATH);

				RESULT = new HashMap();
				RESULT.put("RESPONSE", CONTENTS);
				RESULT.put("STATUS_CODE", 200);
			} else if(REQ_PATH.startsWith("/API")){
				//API
				switch (REQ_PATH){
					case "/API/CREATE_NOTE":{
						//ヘッダーを設定
						responseHeaders.set("Content-Type", "application/json; charset=UTF-8");

						ObjectMapper OBJ_MAPPER = new ObjectMapper();
						JsonNode POST_JSON = OBJ_MAPPER.readTree(POST_DATA);

						if(EXCHANGE.getRequestHeaders().get("TOKEN").toString() != null){
							HashMap<String, String> POST_BODY = new HashMap();
							POST_BODY.put("i", EXCHANGE.getRequestHeaders().get("TOKEN").toString().replace("[", "").replace("]", ""));
							POST_BODY.put("text", POST_JSON.get("TEXT").textValue());

							String POST_BODY_JSON = new ObjectMapper().writeValueAsString(POST_BODY);

							new HTTP_REQUEST("https://ussr.rumiserver.com/api/notes/create").SEND(POST_BODY_JSON);

							RESULT = new HashMap();
							RESULT.put("RESPONSE", "{\"STATUS\":true}");
							RESULT.put("STATUS_CODE", 200);
						} else {
							RESULT = new HashMap();
							RESULT.put("RESPONSE", "{\"STATUS\":false}");
							RESULT.put("STATUS_CODE", 200);
						}
						break;
					}
				}
			} else {//その他
				switch (REQ_PATH){
					case "/":{
						//ヘッダーを設定
						responseHeaders.set("Content-Type", "text/html; charset=UTF-8");

						String CONTENTS = GET_FILE("/CONTENTS/index.html");

						RESULT = new HashMap();
						RESULT.put("RESPONSE", CONTENTS);
						RESULT.put("STATUS_CODE", 200);
						break;
					}

					//どれでもないので404
					default:{
						//ヘッダーを設定
						responseHeaders.set("Content-Type", "text/html; charset=UTF-8");

						String CONTENTS = GET_FILE("/ERR/404.html");

						RESULT = new HashMap();
						RESULT.put("RESPONSE", CONTENTS);
						RESULT.put("STATUS_CODE", 404);
					}
				}
			}

			//結果がNUllじゃないなら
			if(RESULT != null){
				//レスポンスを返す
				byte[] BS = RESULT.get("RESPONSE").toString().getBytes("UTF-8");
				//ステータスコードと文字数
				EXCHANGE.sendResponseHeaders(Integer.parseInt(RESULT.get("STATUS_CODE").toString()), BS.length);
				//書き込むやつ
				OutputStream OS = EXCHANGE.getResponseBody();
				//文字列を書き込む
				OS.write(BS);
				//フラッシュする
				OS.flush();
				//終了
				OS.close();
			}else {
				//エラーを返す

				//ステータスコード(500)と文字数(0)
				EXCHANGE.sendResponseHeaders(500, 0);
				//書き込むやつ
				OutputStream OS = EXCHANGE.getResponseBody();
				//文字列を書き込む
				OS.write("".getBytes());
				//終了
				OS.close();
			}
		}catch (Exception EX){
			LOG_PRINT("HTTP-ROUTER", 2, EX.getMessage());
		}
	}

	private String GET_FILE(String PATH) throws IOException {
		InputStream IS = getClass().getResourceAsStream("/HTML" + PATH);
		BufferedReader BR = new BufferedReader(new InputStreamReader(IS));

		StringBuilder CONTENTS = new StringBuilder();
		String STR = "";
		while((STR = BR.readLine())!= null){
			CONTENTS.append(STR + "\n");
		}

		return CONTENTS.toString();
	}
}
