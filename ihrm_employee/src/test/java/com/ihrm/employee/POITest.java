package com.ihrm.employee;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class POITest {
    //创建excel文件
    @Test
    public void testCreateExcel() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("test");
        FileOutputStream output = new FileOutputStream("H:\\poi\\test.xlsx");
        wb.write(output);
        output.close();
    }

    //创建单元格
    @Test
    public void testCreateUnit() throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("test");
        Row row = sheet.createRow(3);
        Cell cell = row.createCell(0);
        cell.setCellValue("你大爷");

        //创建单元格样式对象
        CellStyle cellStyle = wb.createCellStyle();
        //设置边框
        cellStyle.setBorderBottom(BorderStyle.DASH_DOT);//下边框
        cellStyle.setBorderTop(BorderStyle.HAIR);//上边框
        //设置字体
        Font font = wb.createFont();//创建字体对象
        font.setFontName("华文行楷");//设置字体
        font.setFontHeightInPoints((short)28);//设置字号
        cellStyle.setFont(font);
        //设置宽高
        sheet.setColumnWidth(0, 31 * 256);//设置第一列的宽度是31个字符宽度
        row.setHeightInPoints(50);//设置行的高度是50个点
        //设置居中显示
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//水平居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        //设置单元格样式
        cell.setCellStyle(cellStyle);
        //合并单元格
        CellRangeAddress region =new CellRangeAddress(0, 3, 0, 2);
        sheet.addMergedRegion(region);

        FileOutputStream output = new FileOutputStream("H:\\poi\\test.xlsx");
        wb.write(output);
        output.close();
    }

    @Test
    public void testCreateIMG() throws IOException {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook();
        //2.创建表单Sheet
        Sheet sheet = wb.createSheet("test");
        //读取图片流
        FileInputStream stream=new FileInputStream("H:\\poi\\logo.jpg");
        byte[] bytes= IOUtils.toByteArray(stream);
        //读取图片到二进制数组
        stream.read(bytes);
        //向Excel添加一张图片,并返回该图片在Excel中的图片集合中的下标
        int pictureIdx = wb.addPicture(bytes,Workbook.PICTURE_TYPE_JPEG);
        //绘图工具类
        CreationHelper helper = wb.getCreationHelper();
        //创建一个绘图对象
        Drawing<?> patriarch = sheet.createDrawingPatriarch();
        //创建锚点,设置图片坐标
        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);//从0开始
        anchor.setRow1(0);//从0开始
        //创建图片
        Picture picture = patriarch.createPicture(anchor, pictureIdx);
        picture.resize();
        //6.文件流
        FileOutputStream fos = new FileOutputStream("H:\\poi\\test01.xlsx");
        //7.写入文件
        wb.write(fos);
        fos.close();
    }

    @Test
    public void testLoadExcel() throws IOException {
        //1.创建workbook工作簿
        Workbook wb = new XSSFWorkbook("H:\\poi\\demo.xlsx");
        //2.获取sheet 从0开始
        Sheet sheet = wb.getSheetAt(0);
        int totalRowNum = sheet.getLastRowNum();
        Row row = null;
        Cell cell = null;
        //循环所有行
        for (int rowNum = 3; rowNum <sheet.getLastRowNum(); rowNum++) {
            row = sheet.getRow(rowNum);
            StringBuilder sb = new StringBuilder();
            //循环每行中的所有单元格
            for(int cellNum = 2; cellNum < row.getLastCellNum();cellNum++) {
                cell = row.getCell(cellNum);
                sb.append(getValue(cell)).append("-");
            }
            System.out.println(sb.toString());
        }
    }

    //获取数据
    private static Object getValue(Cell cell) {
        Object value = null;
        switch (cell.getCellType()) {
            case STRING: //字符串类型
                value = cell.getStringCellValue();
                break;
            case BOOLEAN: //boolean类型
                value = cell.getBooleanCellValue();
                break;
            case NUMERIC: //数字类型（包含日期和普通数字）
                if(DateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                }else{
                    value = cell.getNumericCellValue();
                }
                break;
            case FORMULA: //公式类型
                value = cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }
}
