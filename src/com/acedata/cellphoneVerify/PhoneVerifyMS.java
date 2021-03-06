package com.acedata.cellphoneVerify;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 * 马上手机号三要素认证
 */
public class PhoneVerifyMS {

    public static void getVerifyResult(){
        List<String> params = new ArrayList<>();
        params.add("name");
        params.add("idcard");
        params.add("cellphone");

        String url = "https://api.acedata.com.cn:2443/oreo/personal/validation3?feature=name+idcard+cellphone&account=fymn&";
        List<String> urls = XlsxReaderUtil.getUrl(params,url);

        for (String tempurl:urls){

            String cols =ApiSendUtil.getCols(params,tempurl);
            String result = verifyData(tempurl);

            // 结果输出到 result.csv 文件
            String text = cols+","+result;
            XlsxReaderUtil.writeToCSVfile(text);
            System.out.println("查询完毕--->"+result);

        }
    }



    /**
     *
     *  API查询结果
     *
     * @param url
     * @return
     */
    public static String verifyData( String url ){
        String result = ApiSendUtil.apiHttpsSend(url);;
        String veryfyResult = "";

        //JSON响应
        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.fromObject(result);
        } catch (Exception e){
            System.out.println("Json转换出错!");
        }
        if(jsonObject != null ){
            String retCode = (String)jsonObject.get("resCode");
            if(retCode.equals("0000")){
                String statusCode = jsonObject.getJSONObject("data").getString("statusCode");
                if(statusCode!=null) {
                    veryfyResult = statusCode.equals("2005") ? "一致" : "不一致";
                }else{
                    veryfyResult = "提交成功，返回结果为空";
                }
            }else{
                veryfyResult = jsonObject.getString("resMsg");
            }
        }else{
            veryfyResult = "无结果";
        }

        return veryfyResult;
    }

    public static void main(String[] args) {
        getVerifyResult();
    }

}

