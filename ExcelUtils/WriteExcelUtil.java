package com.jd.market.interact.vender.utils;

import org.apache.poi.hssf.usermodel.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by maoyi on 2016/8/1.
 */
public class WriteExcelUtil {
    /***
     *
     * @param map key为sheet名，list<object>为该sheet内容
     * @param title key为sheet名，map<> key为Object字段名 value为表头名
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public static InputStream writeExcel(Map<String,List> map , Map<String,Map<String,String>> title) throws NoSuchFieldException, IllegalAccessException, IOException {
        if(null == map || map.isEmpty()){return null;}
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);

        for (Map.Entry<String, List> entry : map.entrySet()) {
            HSSFSheet sheet = workbook.createSheet(entry.getKey());
            int offset = 0;
            if(null != title && !title.isEmpty()){
                Map<String, String> titleMap = title.get(entry.getKey());
                if(null != titleMap && !titleMap.isEmpty()){
                    List<Map.Entry<String,String>> titleList = new ArrayList<>(titleMap.entrySet());
                    writeHead(sheet,titleList,cellStyle);
                    offset++;
                    List<List<String>> values = parseField(entry.getValue(), titleList);
                    writeBody(values,offset,sheet,cellStyle);
                }
            }
        }
        workbook.write(out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    private static List<List<String>> parseField(List<Object> objectList,List<Map.Entry<String,String>> titleList) throws NoSuchFieldException, IllegalAccessException {
        List<List<String>> result = new ArrayList<>();
        for (Object obj : objectList) {
            List<String> temp = new ArrayList<>();
            for (Map.Entry<String, String> entry : titleList) {
                Field field = obj.getClass().getDeclaredField(entry.getKey());
                field.setAccessible(true);
                temp.add(field.get(obj).toString());
                field.setAccessible(false);
            }
            result.add(temp);
        }
        return result;
    }

    private static void writeHead(HSSFSheet sheet , List<Map.Entry<String,String>> titleList,HSSFCellStyle style){
        HSSFRow row = sheet.createRow(0);
        for(int i =0 ; i<titleList.size();i++){
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(titleList.get(i).getValue());
        }
    }

    private static void writeBody(List<List<String>> values,int offset,HSSFSheet sheet,HSSFCellStyle style){
        for (int i =0 ; i < values.size() ; i++) {
            HSSFRow row = sheet.createRow(i + offset);
            for(int j =0 ; j< values.get(i).size();j++){
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(style);
                cell.setCellValue(values.get(i).get(j));
            }
        }
    }
}
