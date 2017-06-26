package com.acedata.portraitcheck;

import com.acedata.portraitcheck.sslverify.MyVerifier;
import com.acedata.portraitcheck.sslverify.MyX509TrustManager;
import net.sf.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/2.
 *  肖像核查
 */
public class PortraitCheck {

    public static final String filePath = "F:\\Liu\\项目\\Git\\MiniPro\\data\\test.xlsx";
    public static final String restPath = "F:\\Liu\\项目\\Git\\MiniPro\\data\\result.csv";

    public static void getVerifyResult(){
        InputStream is = null;
        XSSFWorkbook xssfWorkbook = null;
        try {
            is = new FileInputStream(filePath);
            xssfWorkbook = new XSSFWorkbook(is);

            // 获取每一个工作薄
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            // 获取当前工作薄的每一行
            String result = "";
            String url = "https://api.acedata.com.cn:2443/oreo/personal/portraitCheck?account=fymn&";
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {

                     //读取第一列数据
                    XSSFCell one = xssfRow.getCell(0);
                    String idCard = one!=null ? getValue(one) : null;

                    //读取第二列数据
                    XSSFCell two = xssfRow.getCell(1);
                    String name = two!=null ? getValue(two) : null;

                    if(isNotEmpty(name) && isNotEmpty(idCard)){
                        String tempurl = url+"name="+name+"&idcard="+idCard;
                        result = verifyData(tempurl);

                        // 结果输出到 result.csv 文件
                        String text = name+","+idCard+","+result;
                        writeToCSVfile(text);
                        System.out.println("查询完毕："+name+"--->"+result);
                    }

                }
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(is != null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //转换数据格式
    public static String getValue(XSSFCell xssfRow) {

        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            DecimalFormat df = new DecimalFormat("0");
            return  df.format(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
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
        String result = "";
        String veryfyResult = "";
        BufferedReader in = null;
        TrustManager[] tm = {new MyX509TrustManager()};

        try {
            //创建SSLContext对象，并使用我们指定的信任管理器初始化
            SSLContext sslContext = SSLContext.getInstance("SSL","SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());

            //从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();

            // 创建URL连接,并设置其SSLSocketFactory对象
            URL realUrl = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection)realUrl.openConnection();
            connection.setSSLSocketFactory(ssf);
            connection.setHostnameVerifier(new MyVerifier());
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常：" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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
                if( statusCode.equals("2005")){
                    String photo = jsonObject.getJSONObject("data").getString("photo");
                    veryfyResult = "一致,"+photo;
                }else {
                    veryfyResult = "不一致,未知";
                }
            }else if( retCode.equals("2007")){
                veryfyResult = "未查得,未查得";
            }else {
                veryfyResult = "未知,未知";
            }
        }else{
            veryfyResult += "无结果,无结果";
        }

        return veryfyResult;
    }

    public static boolean isNotEmpty(String str){ return  str!=null && !str.equals("") ? true : false ;}


    /**
     *
     * @return
     */
    public static void writeToCSVfile(String text) {
        File csvFile = new File(restPath);
        BufferedWriter csvFileOutputStream = null;
        try {
            // GBK读取内容，防止中文乱码
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile, true), "GBK"), 1024);

            //写入
            csvFileOutputStream.write(text);
            csvFileOutputStream.newLine();
            csvFileOutputStream.flush();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(csvFileOutputStream != null) {
                try {
                    csvFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }


    public static void main(String[] args) {
        getVerifyResult();
    }

}

