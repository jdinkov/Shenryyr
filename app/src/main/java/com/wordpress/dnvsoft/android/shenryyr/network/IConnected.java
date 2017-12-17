package com.wordpress.dnvsoft.android.shenryyr.network;

public interface IConnected {
    void onPreExecute();
    void onPostExecute();
    void onDisconnected();
    void onCanceled();
}
