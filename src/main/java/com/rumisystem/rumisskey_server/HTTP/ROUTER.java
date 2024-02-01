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
import java.util.*;

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
				//ヘッダーを設定
				responseHeaders.set("Content-Type", "application/json; charset=UTF-8");

				switch (REQ_PATH){
					case "/API/CREATE_NOTE":{
						if(EXCHANGE.getRequestMethod().equals("POST")){
							ObjectMapper OBJ_MAPPER = new ObjectMapper();
							JsonNode REQUEST_POST_JSON = OBJ_MAPPER.readTree(POST_DATA);

							if(EXCHANGE.getRequestHeaders().get("TOKEN") != null && REQUEST_POST_JSON.get("TEXT") != null){
								//POST内容
								HashMap<String, String> POST_BODY = new HashMap();
								POST_BODY.put("i", EXCHANGE.getRequestHeaders().get("TOKEN").toString().replace("[", "").replace("]", ""));
								POST_BODY.put("text", REQUEST_POST_JSON.get("TEXT").textValue());

								//HTTPリクエストを送る
								String AJAX = new HTTP_REQUEST("https://ussr.rumiserver.com/api/notes/create").SEND(
										new ObjectMapper().writeValueAsString(POST_BODY)
								);

								ObjectMapper AJAX_OBJ_MAPPER = new ObjectMapper();
								JsonNode AJAX_JSON = AJAX_OBJ_MAPPER.readTree(AJAX);

								//返答の内容
								HashMap<String, Object> RESULT_BODY = new HashMap();
								RESULT_BODY.put("STATUS", true);
								RESULT_BODY.put("ID", AJAX_JSON.get("createdNote").get("id").textValue());

								RESULT = new HashMap();
								RESULT.put("RESPONSE", new ObjectMapper().writeValueAsString(RESULT_BODY));
								RESULT.put("STATUS_CODE", 200);
							} else {
								RESULT = new HashMap();
								RESULT.put("RESPONSE", "{\"STATUS\":false}");
								RESULT.put("STATUS_CODE", 200);
							}
						} else {//メソッドがPOSTじゃない
							RESULT = new HashMap();
							RESULT.put("RESPONSE", "{\"STATUS\":false}");
							RESULT.put("STATUS_CODE", 500);
						}
						break;
					}

					case "/API/GET_TIMELINE":{
						if(EXCHANGE.getRequestMethod().equals("GET")){
							if(EXCHANGE.getRequestHeaders().get("TOKEN") != null){
								//POST内容
								HashMap<String, Object> POST_BODY = new HashMap();
								POST_BODY.put("i", EXCHANGE.getRequestHeaders().get("TOKEN").toString().replace("[", "").replace("]", ""));
								POST_BODY.put("allowPartial", true);
								POST_BODY.put("limit", 20);
								POST_BODY.put("withRenotes", true);

								//HTTPリクエストを送る
								String AJAX = new HTTP_REQUEST("https://ussr.rumiserver.com/api/notes/timeline").SEND(
										new ObjectMapper().writeValueAsString(POST_BODY)
								);

								ObjectMapper AJAX_OBJ_MAPPER = new ObjectMapper();
								JsonNode AJAX_JSON = AJAX_OBJ_MAPPER.readTree(AJAX);

								//タイムラインの内容
								List<HashMap> TIMELINE_CONTENTS = new ArrayList<>();

								for(int I = 0; I < 20; I++){
									if(!AJAX_JSON.get(I).isNull()){
										JsonNode ROW = AJAX_JSON.get(I);

										HashMap<String, Object> TIMELINE_CONTENT = new HashMap<>();

										//ノートの情報
										TIMELINE_CONTENT.put("ID", ROW.get("id").textValue());
										TIMELINE_CONTENT.put("DATE", ROW.get("createdAt").textValue());
										TIMELINE_CONTENT.put("TEXT", ROW.get("text").textValue());

										//投稿者の情報
										HashMap<String, Object> AUTHOR_INFO = new HashMap<>();
										AUTHOR_INFO.put("ID", ROW.get("user").get("id").textValue());
										AUTHOR_INFO.put("NAME", ROW.get("user").get("name").textValue());
										AUTHOR_INFO.put("UID", ROW.get("user").get("username").textValue());
										AUTHOR_INFO.put("HOST", ROW.get("user").get("host").textValue());
										AUTHOR_INFO.put("ICON", ROW.get("user").get("avatarUrl").textValue());
										AUTHOR_INFO.put("BOT", ROW.get("user").get("isBot").asBoolean());
										AUTHOR_INFO.put("CAT", ROW.get("user").get("isCat").asBoolean());
										//追加
										TIMELINE_CONTENT.put("AUTHOR", AUTHOR_INFO);

										if(ROW.get("user").get("instance") != null){
											//インスタンスの情報
											HashMap<String, Object> INSTANCE_INFO = new HashMap<>();
											INSTANCE_INFO.put("NAME", ROW.get("user").get("instance").get("name").textValue());
											INSTANCE_INFO.put("SOFTWARE_NAME", ROW.get("user").get("instance").get("softwareName").textValue());
											INSTANCE_INFO.put("SOFTWARE_VERSION", ROW.get("user").get("instance").get("softwareVersion").textValue());
											INSTANCE_INFO.put("ICON", ROW.get("user").get("instance").get("iconUrl").textValue());
											INSTANCE_INFO.put("DOMAIN", ROW.get("user").get("host").textValue());
											INSTANCE_INFO.put("THEME_COLOR", ROW.get("user").get("instance").get("themeColor").textValue());
											//追加
											TIMELINE_CONTENT.put("INSTANCE", INSTANCE_INFO);
										}else {//Nullなので
											//追加
											TIMELINE_CONTENT.put("INSTANCE", null);
										}

										TIMELINE_CONTENTS.add(TIMELINE_CONTENT);
									} else {
										break;
									}
								}

								//返答の内容
								HashMap<String, Object> RESULT_BODY = new HashMap();
								RESULT_BODY.put("STATUS", true);
								RESULT_BODY.put("TIMELINE", TIMELINE_CONTENTS);

								RESULT = new HashMap();
								RESULT.put("RESPONSE", new ObjectMapper().writeValueAsString(RESULT_BODY));
								RESULT.put("STATUS_CODE", 200);
							}
						} else {//メソッドがGETじゃない
							RESULT = new HashMap();
							RESULT.put("RESPONSE", "{\"STATUS\":false}");
							RESULT.put("STATUS_CODE", 500);
						}
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
			LOG_PRINT("HTTP-ROUTER", 2, "Err");
			EX.printStackTrace();
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
