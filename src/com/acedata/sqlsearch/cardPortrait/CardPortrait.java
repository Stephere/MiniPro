package com.acedata.sqlsearch.cardPortrait;

import com.acedata.util.MailUtil;
import com.acedata.util.XlsxReaderUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2017/6/23.
 */
public class CardPortrait {

    public static final String filePath = "F:\\Liu\\项目\\Git\\MiniPro\\data\\sqlparams.xlsx";

    public static void main(String[] args) {
        List<String> params = getParams();
        for(int i=0; i<params.size(); i++) {
            String param = params.get(i).replaceAll("\\**","");
            if (XlsxReaderUtil.isNotEmpty(param) && param.length()==10) {
                String head = param.substring(0,6);
                String tail = param.substring(6);
                List<PortraitPO> resultList = getSqlResult(head, tail);
                for (PortraitPO result : resultList) {
                    // 结果输出到 result.csv 文件
                    String cusName = result.getAccountName().split("name=")[1].split("&")[0];
                    String text =  params.get(i)+"," + result.getName() + "," + cusName + "," + result.getRequestTime() + "\t," + result.getCostTime();
                    XlsxReaderUtil.writeToCSVfile(text);
                    System.out.println(cusName + " 查询完毕---> " + text);
                }
            }
        }
        //发邮件
        //sendMail();
        System.out.println("Sql执行完毕！");
    }


    public static List getParams() {
        InputStream is = null;
        XSSFWorkbook xssfWorkbook = null;
        List<String> params = new ArrayList<>();
        try {
            is = new FileInputStream(filePath);
            xssfWorkbook = new XSSFWorkbook(is);

            // 获取第一个工作薄
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            // 获取当前工作薄的每一行
            String result = "";
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                        XSSFCell cell = xssfRow.getCell(0);
                        String str = cell != null ? XlsxReaderUtil.getValue(cell).trim() : null;
                        if (XlsxReaderUtil.isNotEmpty(str)) {
                            params.add(str);
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } // 使用finally块来关闭输入流
        finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return params;
    }


    public static Connection getConn(){
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://116.255.252.138:3506/aceincome";
        String username = "acereader";
        String password = "acedata1706";
        Connection conn = null;
        try {
            Class.forName(driver); //classLoader,加载对应驱动
            conn =  DriverManager.getConnection(url, username, password);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public synchronized static List<PortraitPO>  getSqlResult(String cardHead , String cardTail){
        String sql = "SELECT a.nickName 'name',r.requestTime 'requestTime',r.requestUrl 'accountName' from request_response r LEFT JOIN `user` a on r.requestAccount=a.`name` \n" +
                "WHERE r.requestUrl like '%"+cardHead+"%' AND r.requestUrl like '%"+cardTail+"'  AND (r.requestName='/personal/portrait' or r.requestName='/personal/cardPortrait')\n" +
                "AND r.requestTime>'2017-01-01 00:00:00';";
        long start = System.currentTimeMillis();
        Connection conn = getConn();
        List<PortraitPO> resultList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement prstm = null;
        try {
            prstm = conn.prepareStatement(sql);
            rs = prstm.executeQuery();
            long end = System.currentTimeMillis();
            while(rs.next()){
                PortraitPO portraitPO = new PortraitPO();
                portraitPO.setName(rs.getString("name"));
                portraitPO.setAccountName(rs.getString("accountName"));
                portraitPO.setRequestTime(rs.getString("requestTime"));
                portraitPO.setCostTime((end-start)/1000+"秒");
                resultList.add(portraitPO);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if(conn!=null) {
                try {
                    conn.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        return resultList;

    }

    public static void sendMail(){
        java.util.Date date = new java.util.Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒sss毫秒");
        String time  = dateFormat.format(date);

        String mailHeader = "【 授权卡号列表查库完成 】+【"+time+"】";
        String content = "hi all：<br/><br/>" +
                "大兄弟们，授权卡号列表匹配姓名以及客户信息，这些鬼东西查完啦！";
        try {
            MailUtil.sendEmail(mailHeader, content);
        }catch (Exception e){
            System.out.println("妈的！邮件没发出去！");
        }
    }

}
