package com.acedata.riskcheck;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 *  马上黑名单接口
 */
public class RiskCheckMS {
    public static void getVerifyResult(){

        String url = "http://122.114.116.31:10081/gate2msxf-wodun/verbose/personal/comprehensive/risk/check?";
        List<String> params = new ArrayList<>();
        params.add("cellphone");
        params.add("idcard");
        params.add("name");
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
     *  API查询结果，gate环境发送 http
     *
     * @param url
     * @return
     */
    public static String verifyData( String url ){
        String veryfyResult="";
        String result = ApiSendUtil.apiHttpSend(url);

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
                JSONObject resultJson = jsonObject.getJSONObject("data").getJSONObject("result");
                if(statusCode!=null && statusCode.equals("2012")) {
                    veryfyResult = resultJson.getString("msg");
                }else{
                    veryfyResult = jsonObject.getJSONObject("data").getString("statusMsg");
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
