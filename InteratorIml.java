package com.aodong.microfinance.remote;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aodong.microfinance.entity.event.OtherLoginEvent;
import com.aodong.microfinance.utils.Constant;
import com.aodong.microfinance.utils.SPUtils;
import com.aodong.microfinance.utils.SystemUtil;
import com.aodong.microfinance.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wangyc on 2017/9/8.
 */

public class InteratorIml implements Interator {

    private Handler handler;

    public InteratorIml(Handler handler) {
        this.handler = handler;
    }

    private void addToRequestQueue(String url, Map<String, String> paramsMap, String method, final int responseType) {
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.addHeader("token", (String) SPUtils.get(Constant.SAVE_TOKEN_KEY,""));
        requestBuilder.addHeader("os", "android");//系统类型
        requestBuilder.addHeader("os-ver", SystemUtil.getSystemVersion());//系统版本号
        requestBuilder.addHeader("app-ver", SystemUtil.getVersion());//app版本号
        requestBuilder.addHeader("brand", SystemUtil.getDeviceBrand() + "\\" +  SystemUtil.getSystemModel());//手机品牌
        FormBody.Builder builder = new FormBody.Builder();
        if (paramsMap != null && paramsMap.size() > 0) {
            for (String key : paramsMap.keySet()) {
                builder.add(key, paramsMap.get(key));
            }
        }
        FormBody formBody = builder.build();
        if (method.equals("POST")) {
            requestBuilder.method("POST",formBody);
        } else {
            url = url + "?" + getUrlParamsByMap(paramsMap);
            Log.e(">>url>>", url);
            requestBuilder.get();
        }

        requestBuilder.url(url);
        Request request = requestBuilder.build();
        Call call = OkHttpClientSL.getOkHttpClient().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(">>onFailure>>", call.toString());
                Message message = new Message();
                message.what = Constant.REQUEST_ERROR;
                message.obj = e.toString() + "";
                handler.sendMessage(message);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();

                try {
                    JSONObject jo = new JSONObject(result);
                    int code = jo.getInt("code");
                    if (code == 301) {
                        SPUtils.remove(Constant.SAVE_TOKEN_KEY);
                        EventBus.getDefault().post(new OtherLoginEvent("该用户已在其他地方登录~"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.e(">>onResponse>>", result);
                if (handler != null && Utils.isJson(result)) {
                    Message message = new Message();
                    message.obj = result;
                    message.what = responseType;
                    handler.sendMessage(message);
                }
            }
        });
    }


    private void post_user_image(String url, File file) {
        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);

            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("uploadify", file.getName(), body);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build();


        Call call = OkHttpClientSL.getOkHttpClient().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.what = Constant.POST_PICTURE_UPLOAD_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    try {
                        JSONObject jo = new JSONObject(result);
                        int code = jo.getInt("code");
                        if (code == 301) {
                            SPUtils.remove(Constant.SAVE_TOKEN_KEY);
                            EventBus.getDefault().post(new OtherLoginEvent("该用户已在其他地方登录~"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (handler != null && Utils.isJson(result)) {
                        Message message = new Message();
                        message.obj = result;
                        message.what = Constant.POST_PICTURE_UPLOAD;
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }

    //身份证多个上传
    private void post_user_images(String url, File file, final int flag) {

        // form 表单形式上传
        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (file != null) {
            // MediaType.parse() 里面是上传的文件类型。
            RequestBody body = RequestBody.create(MediaType.parse("image/*"), file);
            // 参数分别为， 请求key ，文件名称 ， RequestBody
            requestBody.addFormDataPart("uploadify", file.getName(), body);
        }

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody.build())
                .build();


        Call call = OkHttpClientSL.getOkHttpClient().newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String result = response.body().string();
                    try {
                        JSONObject jo = new JSONObject(result);
                        int code = jo.getInt("code");
                        if (code == 301) {
                            SPUtils.remove(Constant.SAVE_TOKEN_KEY);
                            EventBus.getDefault().post(new OtherLoginEvent("该用户已在其他地方登录~"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (handler != null && Utils.isJson(result)) {
                        Message message = Message.obtain();
                        message.obj = result;
                        message.what = Constant.POST_PICTURE_UPLOAD;
                        message.arg1 = flag;
                        handler.sendMessage(message);
                    }
                }
            }
        });
    }

    private void post_emergency_contact(String url, String json, final int responseType) {
        Request.Builder requestBuilder = new Request.Builder();
//        requestBuilder.addHeader("token", Constant.token);
        requestBuilder.addHeader("token", (String) SPUtils.get(Constant.SAVE_TOKEN_KEY,""));
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        requestBuilder.method("POST",body);
        requestBuilder.url(url);
        final Request request = requestBuilder.build();
        Call call = OkHttpClientSL.getOkHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(">>onFailure>>", call.toString());
                Message message = new Message();
                message.what = Constant.REQUEST_ERROR;
                message.obj = e.toString() + "";
                handler.sendMessage(message);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject jo = new JSONObject(result);
                    int code = jo.getInt("code");
                    if (code == 301) {
                        SPUtils.remove(Constant.SAVE_TOKEN_KEY);
                        EventBus.getDefault().post(new OtherLoginEvent("该用户已在其他地方登录~"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e(">>onResponse>>", result);
                if (handler != null && Utils.isJson(result)) {
                    Message message = new Message();
                    message.obj = result;
                    message.what = responseType;
                    handler.sendMessage(message);
                }
            }
        });
    }

    @Override
    public void user_login(Map<String, String> map) {
        addToRequestQueue(Constant.USER_LOGIN, map, Constant.POST, Constant.POST_USER_LOGIN);
    }

    @Override
    public void user_register(Map<String, String> map) {
        addToRequestQueue(Constant.USER_REGISTER, map, Constant.POST, Constant.POST_USER_REGISTER);
    }

    @Override
    public void send_verify_code(Map<String, String> map) {

        addToRequestQueue(Constant.SEND_VERIFY_CODE, map, Constant.GET, Constant.GET_SEND_VERIFY_CODE);
    }

    @Override
    public void user_forget_password(Map<String, String> map) {
        addToRequestQueue(Constant.FORGET_PASSWORD, map, Constant.POST, Constant.POST_FORGET_PASSWORD);
    }

    @Override
    public void user_password_update(Map<String, String> map) {
        addToRequestQueue(Constant.PASSWORD_UPDATE, map, Constant.POST, Constant.POST_PASSWORD_UPDATE);
    }

    @Override
    public void get_home() {
        addToRequestQueue(Constant.HOME, null, Constant.GET, Constant.GET_HOME);
    }

    @Override
    public void get_loan_list(Map<String, String> map) {

        addToRequestQueue(Constant.LOAN_LIST, map, Constant.GET, Constant.GET_LOAN_LIST);
    }

    @Override
    public void get_my_info() {
        addToRequestQueue(Constant.MY, null, Constant.GET, Constant.GET_MY);
    }


    @Override
    public void phone_confirm() {
        addToRequestQueue(Constant.PHONE_CONFIRM, null, Constant.GET, Constant.PHONE_CONFIRMS);
    }

    @Override
    public void query_borrow_detail(Map<String, String> map) {
        addToRequestQueue(Constant.BORROW_DETAIL, map, Constant.GET, Constant.GET_BORROW_DETAIL);
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    private static String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
//        if (s.endsWith("&")) {
//            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
//        }
        return s;
    }


    @Override
    public void identity_confirm() {
        addToRequestQueue(Constant.IDENTITY_CONFIRM, null, Constant.GET, Constant.IDENTITY_CONFIRMS);
    }

    @Override
    public void all_confirm_status() {
        addToRequestQueue(Constant.ALL_CONFIRM_STATUS, null, Constant.GET, Constant.ALL_CONFIRM_STATUSES);
    }

    @Override
    public void post_bank_information(Map<String, String> map) {
        addToRequestQueue(Constant.POST_BANK_INFORMATIOM, map, Constant.POST, Constant.POST_BANK_INFORMATIOMS);
    }

    @Override
    public void get_phone_user_information() {
        addToRequestQueue(Constant.PHONE_USER_INFORMATION, null, Constant.GET, Constant.PHONE_USER_INFORMATIONS);
    }

    @Override
    public void get_identity_information() {
        addToRequestQueue(Constant.IDENTITY_INFORMATION, null, Constant.GET, Constant.IDENTITY_INFORMATIONS);
    }

    @Override
    public void get_bank_infornation() {
        addToRequestQueue(Constant.BANK_INFORMATION, null, Constant.GET, Constant.BANK_INFORMATIONS);
    }

    @Override
    public void emergency_contanct_post(String json) {
        post_emergency_contact(Constant.EMERGENCY_CONTACT_POST, json, Constant.POST_EMERGENCY_CONTACT);
    }

    public void post_alipay_accout_number(Map<String, String> map) {
        addToRequestQueue(Constant.POST_ALIPAY_INFORMATION, map, Constant.POST, Constant.POST_ALIPAY_INFORMATIONS);
    }

    @Override
    public void post_contacts_information(String json) {
        post_emergency_contact(Constant.POST_CONTACTS_INFORMATION, json, Constant.POST_CONTACTS_INFORMATIONS);
    }

    @Override
    public void get_alipay_information() {
        addToRequestQueue(Constant.GET_ALIPAY_INFORMATION, null, Constant.GET, Constant.GET_ALIPAY_INFORMATIONS);
    }

    @Override
    public void post_alipay_information(Map<String, String> map) {
        addToRequestQueue(Constant.POST_ALIPAY_REPAY, map, Constant.GET, Constant.POST_ALIPAY_REPAYS);
    }

    @Override
    public void borrow_info_query() {
        addToRequestQueue(Constant.BORROW_RATE_QUERY, null, Constant.GET, Constant.GET_BORROW_RATE);
    }

    @Override
    public void get_repay_information(Map<String, String> map) {
        addToRequestQueue(Constant.GET_REPAY_INFORMATION, map, Constant.GET, Constant.GET_REPAY_INFORMATIONS);
    }

    @Override
    public void get_repay_details_information(Map<String, String> map) {
        addToRequestQueue(Constant.GET_REPAY_DETAILS_INFORMATION, map, Constant.GET, Constant.GET_REPAY_DETAILS_INFORMATIONS);
    }

    @Override
    public void post_identity_information(Map<String, String> map) {

    }

    @Override
    public void borrow_post(Map<String, String> map) {
        addToRequestQueue(Constant.BORROW_MONEY_POST, map, Constant.POST, Constant.POST_BORROW_MONEY);
    }

    @Override
    public void post_image(File file) {
        post_user_image(Constant.PICTURE_UPLOAD_URL, file);
    }

    @Override
    public void head_image_update(Map<String, String> map) {
        addToRequestQueue(Constant.HEADIMAGE_UPDATE_URL, map, Constant.POST, Constant.POST_HEAD_IMAGE_UPDATE);
    }

    @Override
    public void card_post(Map<String, String> map) {
        addToRequestQueue(Constant.CARD_POST, map,Constant.POST, Constant.POST_CARD_POST);
    }

    @Override
    public void post_images(File file, int flag) {
        post_user_images(Constant.PICTURE_UPLOAD_URL, file, flag);
    }

    public void get_lates_details(Map<String, String> map) {
        addToRequestQueue(Constant.LATES_DETAILS,map,Constant.GET,Constant.LATES_DETAILES);
    }
   //提交更换银行卡信息
    @Override
    public void post_bank_change_information(Map<String, String> map) {
        addToRequestQueue(Constant.CHANGE_BANK_INFORMATION,map,Constant.POST,Constant.CHANGE_BANK_INFORMATIONS);
    }
}