package com.aodong.microfinance.remote;

import java.io.File;
import java.util.Map;

/**
 * Created by wangyc on 2017/9/20.
 */

public interface Interator {

    void user_login(Map<String, String> map);

    void user_register(Map<String, String> map);

    void send_verify_code(Map<String, String> map);

    void user_forget_password(Map<String, String> map);

    void user_password_update(Map<String, String> map);

    void get_home();

    void get_loan_list(Map<String, String> map);

    void get_my_info();

    void phone_confirm();

    void query_borrow_detail(Map<String, String> map);

    void identity_confirm();

    void all_confirm_status();

    void post_bank_information(Map<String, String> map);

    void get_phone_user_information();

    void get_identity_information();

    void get_bank_infornation();

    void emergency_contanct_post(String json);

    void post_alipay_information(Map<String, String> map);

    void post_contacts_information(String json);

    void borrow_info_query();

    void get_repay_information(Map<String, String> map);

    void get_repay_details_information(Map<String, String> map);

    void post_identity_information(Map<String, String> map);

    void get_alipay_information();

    void borrow_post(Map<String, String> map);

    void post_image(File file);

    void post_images(File file, int flag);


    void head_image_update(Map<String, String> map);

    void card_post(Map<String, String> map);

    void get_lates_details(Map<String, String> map);

    void post_bank_change_information(Map<String, String> map);
}
