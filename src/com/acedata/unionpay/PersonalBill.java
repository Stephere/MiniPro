package com.acedata.unionpay;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/12.
 *  银联-个人账单验证
 *
 */
public class PersonalBill {
    public static void getVerifyResult(){

        String url = "https://apiserver1.acedata.com.cn:2443/oreo/personal/bill/verification?account=fymn&";
        List<String> params = new ArrayList<>();
        params.add("name");
        params.add("bankcard");
        params.add("beginDate");
        params.add("endDate");
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
            String statusCode = jsonObject.getJSONObject("data").getString("statusCode");
            if(statusCode!=null && statusCode.equals("2012")) {
                JSONArray resultArr = jsonObject.getJSONObject("data").getJSONArray("result");
                for(int i=0 ; i<resultArr.size();i++) {
                    JSONObject resultJson = JSONObject.fromObject(resultArr.get(i));
                    String transTime = resultJson.containsKey("transTime") && resultJson.getString("transTime") != null ? resultJson.getString("transTime") : "无";
                    String transAmount = resultJson.containsKey("transAmount") && resultJson.getString("transAmount") != null ? resultJson.getString("transAmount") : "无";
                    veryfyResult += transTime + "," + transAmount+",";
                }
            }else{
                veryfyResult = jsonObject.getJSONObject("data").getString("statusMsg");
            }
        }else{
            veryfyResult += ",无结果";
        }

        return veryfyResult;
    }


    public static void main(String[] args) {
        getVerifyResult();
    }
}
