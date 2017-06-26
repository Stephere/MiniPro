package com.acedata.cellphoneVerify;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/8.
 *  英齐手机号三要素查询
 *  除了url不同，其他编码与马上的三要素接口一毛一样（ 即：PhoneVerifyMS ）
 */
public class PhoneVerifyYQ {
    public static void getVerifyResult(){
        List<String> params = new ArrayList<>();
        params.add("name");
        params.add("idcard");
        params.add("cellphone");

        String url = "https://apiserver1.acedata.com.cn:2443/gate2qydata/verbose/personal/mobile/validation3?source=gate&account=fymn&";
        List<String> urls = XlsxReaderUtil.getUrl(params,url);

        for (String tempurl:urls){

            String cols =ApiSendUtil.getCols(params,tempurl);
            String result = PhoneVerifyMS.verifyData(tempurl);

            // 结果输出到 result.csv 文件
            String text = cols+","+result;
            XlsxReaderUtil.writeToCSVfile(text);
            System.out.println(text+"查询完毕--->"+result);

        }
    }

    public static void main(String[] args) {
        getVerifyResult();
    }

}
