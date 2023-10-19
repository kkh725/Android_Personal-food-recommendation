package com.example.toss_test.etc;



import static com.example.toss_test.fragment.Recommend_Fragment.Store_Status;
import static com.example.toss_test.fragment.Recommend_Fragment.Store_arr;
import static com.example.toss_test.fragment.Recommend_Fragment.listViewAdapter;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;


public class HttpWebSocket {

    private OkHttpClient client;
    private WebSocket webSocket;

    public String WEB_SOCKET_URL = "ws://221.158.178.99:8082";
    public String get_text, Store_name, congestion;
    public Handler handler;


    public HttpWebSocket() {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(WEB_SOCKET_URL).build();
        webSocket = client.newWebSocket(request, listener);
        handler = new Handler(Looper.getMainLooper());
    }


    public WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosed(webSocket, code, reason);
            Log.d("TLOG", "소켓 onClosing");
        }

        @Override
        public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
            super.onClosing(webSocket, code, reason);
            Log.d("TLOG", "소켓 onClosing");
            webSocket.close(1000, null);
            webSocket.cancel();

        }


        @Override //연결실패시 작동
        public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable okhttp3.Response response) {
            super.onFailure(webSocket, t, response);
            Log.d("TLOG", "소켓 onFailure : " + t.toString());
        }

        @Override //메세지 문자열로 받아올때 작동
        public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
            super.onMessage(webSocket, text);
            Log.d("TLOGgg", "text 데이터 확인 : " + text.toString());
            get_text = text.toString();

            Log.d("TLOGgg", "getText : " + get_text);

            /** 비동기식 처리작업이기때문에 메인스레드에서 ui 변경을 할 시 늦게 작업됨.
             따라서 핸들러를 사용해서 서브 스레드에서 ui를 변경한다.
             사장님어플에서 서버로 매우혼잡,혼잡 등 신호를 보내면 서버로부터 "가게/owner/혼잡도" 의 정보를 받아온다.
             이 정보를 가게와 혼잡도로 split하여 가게명이 store_arr 0번 배열값과 같다면 1번리스트의 정보(혼잡도)를 수정해준다.
             */
            handler.post(new Runnable() {
                @Override
                public void run() { // 해쉬맵에 모든 가게명과 혼잡도를 0으로 초기화 해둔다.
                    // 그리고 실시간으로 들어올때만 혼잡도를 업데이트
                    Store_name = get_text.split("/")[0];
                    congestion = get_text.split("/")[2];
                    Log.d("가게명/혼잡도", Store_name + " " + congestion);

                    Store_Status.put(Store_name, congestion);
                    Log.d("hash data",Store_Status.values().toString());
                    Log.d("hash data",Store_Status.keySet().toString());

                    /**
                     * 서버로부터 받아온 가게명이 0번 배열값과 같다면 첫번째 리스트의 혼잡도 변경.
                     * 1번 배열값과 같다면 두번째 리스트의 혼잡도 변경 // 리스트도 0인덱스가 1번.
                     */

                    if(Store_name.equals(Store_arr[0])){
                        listViewAdapter.updateItem(0,"혼잡도 : " + congestion);
                    }

                    else if(Store_name.equals(Store_arr[1])){
                        listViewAdapter.updateItem(1,"혼잡도 : " + congestion);
                    }

                    else if(Store_name.equals(Store_arr[2])){
                        listViewAdapter.updateItem(2,"혼잡도 : " + congestion);
                    }

                    else if(Store_name.equals(Store_arr[3])){
                        listViewAdapter.updateItem(3,"혼잡도 : " + congestion);
                    }

                    else if(Store_name.equals(Store_arr[4])){
                        listViewAdapter.updateItem(4,"혼잡도 : " + congestion);
                    }

                    else if(Store_name.equals(Store_arr[5])){
                        listViewAdapter.updateItem(5,"혼잡도 : " + congestion);
                    }

                }
            });

        }

        @Override // 메세지 바이트로 받아올때 작동
        public void onMessage(@NotNull WebSocket webSocket, @NotNull ByteString bytes) {
            super.onMessage(webSocket, bytes);
            Log.d("TLOGgg", "ByteString 데이터 확인 : " + bytes.toString());
            get_text = bytes.toString();

            Log.d("TLOGgg", "getText : " + get_text);

            /** 비동기식 처리작업이기때문에 메인스레드에서 ui 변경을 할 시 늦게 작업됨.
             따라서 핸들러를 사용해서 서브 스레드에서 ui를 변경한다.

             */
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Store_name = get_text.split("/")[0];
                    congestion = get_text.split("/")[2];
                    Log.d("가게명/혼잡도", Store_name + " " + congestion);
                    if(Store_Status.containsKey(Store_name)){
                        Store_Status.put(Store_name, congestion);
                    }

                    Log.d("hash data",Store_Status.values().toString());
                    Log.d("hash data",Store_Status.keySet().toString());
                }
            });
        }

        @Override //소켓 열릴때 작동 웹소켓 정보와 응답 확인
        public void onOpen(@NotNull WebSocket webSocket, @NotNull okhttp3.Response response) {
            super.onOpen(webSocket, response);
            Log.d("TLOG", "전송 데이터 확인 : " + webSocket + " : " + response);
        }
    };

    public void closeWebSocket() {
        if (webSocket != null) {
            webSocket.close(1000, "Closing WebSocket");
        }
    }


    public void sendWebSocketMessage(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        }
    }

}