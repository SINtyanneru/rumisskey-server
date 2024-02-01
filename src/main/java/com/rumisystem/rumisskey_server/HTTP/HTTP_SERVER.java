package com.rumisystem.rumisskey_server.HTTP;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rumisystem.rumisskey_server.Main.LOG_PRINT;

public class HTTP_SERVER {
	public static void main() {
		try {
			int PORT = 8085; // サーバーポート

			HttpServer SERVER = HttpServer.create(new InetSocketAddress(PORT), 0);

			SERVER.createContext("/", new HTTP_HANDLER());
			SERVER.setExecutor(null); // デフォルトのExecutorを使用

			SERVER.start();
			LOG_PRINT("HTTP", 0, "Started HTTP Server localhost:" + PORT);
		} catch (Exception EX) {
			LOG_PRINT("HTTP", 2, EX.getMessage());
		}
	}

	static class HTTP_HANDLER implements HttpHandler {
		@Override
		public void handle(HttpExchange EXCHANGE) {
			try {
				LOG_PRINT("HTTP", 1, "HTTP Request:" + EXCHANGE.getRequestMethod() + " " + EXCHANGE.getRequestURI());

				//URIパラメーターを解析するやつ
				HashMap<String, String> URI_PARAM_HM = new HashMap<String, String>();//URIパラメーターを解析した結果のハッシュマップ
				int MARK_INDEX = EXCHANGE.getRequestURI().toString().indexOf("?");
				if (MARK_INDEX != -1) {//?が見つかった場合
					//?以降の文字列を取得
					String[] URI_PARAM_ARRAY = EXCHANGE.getRequestURI().toString().substring(MARK_INDEX + 1).split("&");
					for (String URI_PARAM : URI_PARAM_ARRAY) {
						URI_PARAM_HM.put(URI_PARAM.split("=")[0], URI_PARAM.split("=")[1]);
					}
				}

				//POSTされたデータ
				String POST_DATA = null;
				if ("POST".equals(EXCHANGE.getRequestMethod())) {
					//リクエストのInputStreamからデータを読み取る
					InputStreamReader ISR = new InputStreamReader(EXCHANGE.getRequestBody(), "UTF-8");
					BufferedReader BR = new BufferedReader(ISR);
					StringBuilder POST_DATA_SB = new StringBuilder();
					String LINE;
					while ((LINE = BR.readLine()) != null) {
						POST_DATA_SB.append(LINE);
					}
					POST_DATA = POST_DATA_SB.toString();
				}

				//リクエストヘッダーを取得
				Map<String, List<String>> HEADER_TEMP = EXCHANGE.getRequestHeaders();
				HashMap<String, String> HEADER_DATA = new HashMap<String, String>();

				//ヘッダーをコンソールに出力
				for (Map.Entry<String, List<String>> entry : HEADER_TEMP.entrySet()) {
					String HEADER_KEY = entry.getKey();
					List<String> HEADER_VAL = entry.getValue();
					//データをhashMapに入れるのだちんちんまーんこ
					HEADER_DATA.put(HEADER_KEY, HEADER_VAL.get(0).toString());
				}

				ROUTER ROUTER_OBJ = new ROUTER(EXCHANGE, URI_PARAM_HM, POST_DATA, HEADER_DATA);
			} catch (Exception EX) {
				LOG_PRINT("HTTP", 2, EX.getMessage());
			}
		}
	}
}
