package com.acedata.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/4.
 */
public class XlsxReaderUtil {

    public static final String filePath = "F:\\Liu\\项目\\Git\\MiniPro\\data\\test.xlsx";
    public static final String restPath = "F:\\Liu\\项目\\Git\\MiniPro\\data\\result.csv";

    public static List getUrl(List<String> params, String oriurl) {
        InputStream is = null;
        XSSFWorkbook xssfWorkbook = null;
        List<String> urls = new ArrayList<>();
        String url = oriurl;
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
                    for (int i = 0; i < params.size(); i++) {
                        XSSFCell cell = xssfRow.getCell(i);
                        String str = cell != null ? XlsxReaderUtil.getValue(cell).trim() : null;
                        if (isNotEmpty(str)) {
                            if(i==0){
                                url += params.get(i) + "=" + str;
                            }else{
                                url += "&" + params.get(i) + "=" + str;
                            }
                        }
                    }
                    urls.add(url);
                    url = oriurl;
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

        return urls;
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

    public static boolean isNotEmpty(String str){ return  str!=null && !str.equals("") ? true : false ;}

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
}
