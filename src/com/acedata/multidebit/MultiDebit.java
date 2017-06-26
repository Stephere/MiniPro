package com.acedata.multidebit;

import com.acedata.util.ApiSendUtil;
import com.acedata.util.XlsxReaderUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2017/5/5.
 * 多头借贷查询   24个月
 */
public class MultiDebit {
    public static void getVerifyResult(){

        String url = "https://api.acedata.com.cn:2443/oreo/personal/creditInfoAll?account=fymn&cycle=24&";
        List<String> params = new ArrayList<>();
        params.add("cellphone");
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

        List<String> SList = new ArrayList<>();
        SList.add("S002");
        SList.add("S004");
        SList.add("S007");
        SList.add("S009");
        SList.add("S012");

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
                    String province = resultJson.getString("province");
                    String city = resultJson.getString("city");
                    String S002detail = "";
                    String S004detail = "";
                    String S007detail = "";
                    String S009detail = "";
                    String S012detail = "";
                    String S013detail = "";

                    JSONObject resultData =resultJson.getJSONObject("data");
                    for(int i=0;i<SList.size();i++){
                        String Sname = SList.get(i);
                        JSONObject Sobj = resultData.getJSONObject(Sname);
                        JSONArray SArr = Sobj.getJSONArray("data");
                        for(int j=0;j<SArr.size();j++){
                            switch (Sname) {
                                case "S002":
                                    JSONObject S002 = JSONObject.fromObject(SArr.get(j));
                                    System.out.println("S002:"+S002);
                                    if( S002.containsKey("platformType") && S002.containsKey("registerTime")) {
                                        String platformType002 = S002.getString("platformType");
                                        String registerTime002 = S002.getString("registerTime");
                                        platformType002 = mapPlatform(platformType002);
                                        S002detail += platformType002 + " | " + registerTime002+"\n";
                                    }
                                    break;
                                case "S004":
                                    JSONObject S004 = JSONObject.fromObject(SArr.get(j));
                                    System.out.println("S004:"+S004);
                                    if(S004.containsKey("platformType") && S004.containsKey("applicationTime") && S004.containsKey("applicationAmount") && S004.containsKey("applicationResult")) {
                                        String platformType004 = S004.getString("platformType");
                                        String registerTime004 = S004.getString("applicationTime");
                                        String applicationAmount004 = S004.getString("applicationAmount");
                                        String applicationResult004 = S004.getString("applicationResult");
                                        platformType004 = mapPlatform(platformType004);
                                        S004detail += platformType004 + " | " + registerTime004 + " | " + applicationAmount004 + " | " + applicationResult004+"\n";
                                    }
                                    break;
                                case "S007":
                                    JSONObject S007 = JSONObject.fromObject(SArr.get(j));
                                    System.out.println("S007:"+S007);
                                    if( S007.containsKey("platformType") && S007.containsKey("loanLendersTime") && S007.containsKey("loanLendersAmount")) {
                                        String platformType007 = S007.getString("platformType");
                                        String loanLendersTime007 = S007.getString("loanLendersTime");
                                        String loanLendersAmount007 = S007.getString("loanLendersAmount");
                                        platformType007 = mapPlatform(platformType007);
                                        S007detail += platformType007 + " | " + loanLendersTime007 + " | " + loanLendersAmount007+"\n";
                                    }
                                    break;
                                case "S009":
                                    JSONObject S009 = JSONObject.fromObject(SArr.get(j));
                                    System.out.println("S009:"+S009);
                                    if( S009.containsKey("platformType") && S009.containsKey("rejectionTime") ) {
                                        String platformType009 = S009.getString("platformType");
                                        String rejectionTime009 = S009.getString("rejectionTime");
                                        platformType009 = mapPlatform(platformType009);
                                        S009detail += platformType009 + " | " + rejectionTime009+"\n";
                                    }
                                    break;
                                case "S012":
                                    JSONObject S012 = JSONObject.fromObject(SArr.get(j));
                                    System.out.println("S012:"+S012);
                                    if( S012.containsKey("counts") && S012.containsKey("money")) {
                                        String counts012 = S012.getString("counts");
                                        String money012 = S012.getString("money");
                                        S012detail += counts012 + " | " + money012+"\n";
                                    }
                                    break;

                                case "S013":
                                    break;

                            }
                        }

                    }
                    veryfyResult = province+"-"+city+",\""+isEmpty(S002detail)+"\",\""+isEmpty(S004detail)+"\",\""+isEmpty(S007detail)+"\",\""+isEmpty(S009detail)+"\",\""+isEmpty(S012detail)+"\",\""+isEmpty(S013detail)+"\"";
                }else{
                    veryfyResult = jsonObject.getJSONObject("data").getString("statusMsg");
                }
            }else{
                System.out.println(jsonObject.getString("resMsg"));
                veryfyResult = "查询失败";
            }
        }else{
            veryfyResult = "无结果";
        }

        return veryfyResult;
    }

    public static String mapPlatform(String platform){
        switch (platform){
            case "0":
                return "全部";
            case "1":
                return "银行";
            case "2":
                return "非银行";
        }
        return "未知";
    }

    public static String isEmpty(String str ){ return  str!=null && !str.equals("")?str:"[0]";}

    public static List<String> concurrenceCall(final List<String> urls ){

        List<String> results = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Future<List<String>>> futures = new ArrayList<>();

        for( int i=0;i<5;i++){
            final List<String> subList = urls.subList(10 * i, 10 * (i + 1));
            Callable<List<String>> task = new Callable<List<String>>() {
                @Override
                public List<String> call() throws Exception {
                    List<String> subResult = new ArrayList();
                    for (int j=0;j<subList.size();j++){
                        subResult.add(ApiSendUtil.apiHttpsSend(subList.get(j)));
                    }
                    return subResult;
                }
            };
            futures.add(executorService.submit(task));
        }
        for (Future<List<String>> future : futures) {
            try {
                results.addAll(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return results;
    }

    public static void main(String[] args) {

        getVerifyResult();

    }
}
