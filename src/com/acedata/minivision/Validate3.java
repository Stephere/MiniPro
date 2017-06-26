package com.acedata.minivision;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 *  小视-银行卡三要素
 */
public class Validate3 {
    public static void getVerifyResult(){

        String url = "https://apiserver1.acedata.com.cn:2443/gate2minivision/verbose/personal/nameIdcardBankcard/validation?source=gate&";
        List<String> params = new ArrayList<>();
        params.add("name");
        params.add("idcard");
        params.add("bankcard");
        List<String> urls = XlsxReaderUtil.getUrl(params,url);


        for (String tempurl:urls){

            String cols = ApiSendUtil.getCols(params,tempurl);
            String result = verifyData(tempurl);

            // 结果输出到 result.csv 文件
            String text = cols+","+result;
            XlsxReaderUtil.writeToCSVfile(text);
            System.out.println(cols+" : 查询完毕--->"+result);

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
        String veryfyResult="";
        String result = ApiSendUtil.apiHttpsSend(url);

        //JSON响应
        JSONObject jsonObject = null;
        try{
            jsonObject = JSONObject.fromObject(result);
        } catch (Exception e){
            System.out.println("Json转换出错!");
        }
        if(jsonObject != null ){
            String retCode = (String)jsonObject.get("resCode");
            String statusCode = jsonObject.getJSONObject("data").getString("statusCode");
            if(retCode.equals("0000")){
                if(statusCode!=null) {
                    veryfyResult = statusCode.equals("2005") ? "一致" : "不一致";
                }else{
                    veryfyResult = "提交成功，返回结果为空";
                }
            }else{
                veryfyResult = "查询失败";
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
