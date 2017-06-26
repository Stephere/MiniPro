package com.acedata.riskcheck;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 *   360黑名单
 */
public class RiskCheck360 {
    public static void getVerifyResult(){

        String url = "https://api.acedata.com.cn:2443/oreo/personal/comprehensive/risk/check?account=fymn&";
        List<String> params = new ArrayList<>();
        params.add("name");
        params.add("cellphone");
        params.add("idcard");
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
     *  API查询结果，线上发送HTTPS
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
