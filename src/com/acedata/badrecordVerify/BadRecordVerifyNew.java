package com.acedata.badrecordVerify;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/2.
 * 刑事案底 优选
 */
public class BadRecordVerifyNew {

    public static void getVerifyResult(){

        String url = "https://api.acedata.com.cn:2443/oreo/personal/criminalCase/query?account=fymn&";
        List<String> params = new ArrayList<>();
        params.add("idcard");
        params.add("name");
        List<String> urls = XlsxReaderUtil.getUrl(params,url);


        for (String tempurl:urls){

            String cols =ApiSendUtil.getCols(params,tempurl);
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
            String statusCode = jsonObject.getJSONObject("data").getString("statusCode");
            if(statusCode!=null && statusCode.equals("2012")) {
                String caseMsg =jsonObject.getJSONObject("data").getJSONObject("result").getString("caseMsg");
                if(caseMsg!=null && !caseMsg.equals("")) veryfyResult += caseMsg+",";

                JSONArray resultArr = jsonObject.getJSONObject("data").getJSONObject("result").getJSONArray("caseDetails");
                for(int i=0 ; i<resultArr.size();i++) {
                    JSONObject resultJson = JSONObject.fromObject(resultArr.get(i));
                    String caseType = resultJson.containsKey("caseType") && isEmpty(resultJson.getString("caseType"))? resultJson.getString("caseType") : "无";
                    String caseTime = resultJson.containsKey("caseTime") && isEmpty(resultJson.getString("caseTime")) ? resultJson.getString("caseTime") : "无";
                    String caseSource = resultJson.containsKey("caseSource") && isEmpty(resultJson.getString("caseSource")) ? resultJson.getString("caseSource") : "无";
                    veryfyResult +=  caseType + "," + caseTime + "," + caseSource+",";
                }
            }else{
                veryfyResult = jsonObject.getJSONObject("data").getString("statusMsg");
            }
        }else{
            veryfyResult += ",无结果";
        }

        return veryfyResult;
    }

    public static boolean isEmpty(String str){ return str!=null && !str.equals("") ? true : false;}

    public static void main(String[] args) {
        getVerifyResult();
    }

}

