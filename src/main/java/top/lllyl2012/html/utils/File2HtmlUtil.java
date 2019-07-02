package top.lllyl2012.html.utils;

import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.converter.core.BasicURIResolver;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

@Component
public class File2HtmlUtil {

    /**
     * 根据系统返回路径
     */
    public static String getRoot(){
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")){
            return "C:/Users/Public/Pictures" ;
        }else {
            return "/home/static";
        }
    }

    public static void word2007ToHtml(byte[] body, HttpServletResponse response) throws Exception {
        String imagePathStr = getRoot() + "/image/";
        ByteArrayInputStream is = new ByteArrayInputStream(body);
        OutputStreamWriter outputStreamWriter = null;
        try {
            XWPFDocument document = new XWPFDocument(is);
            XHTMLOptions options = XHTMLOptions.create();
            // 存放图片的文件夹
            options.setExtractor(new FileImageExtractor(new File(imagePathStr)));
            // html中图片的路径
            options.URIResolver(new BasicURIResolver("image"));
//            outputStreamWriter = new OutputStreamWriter(new FileOutputStream(targetFileName), "utf-8");
//            outputStreamWriter = response.getWriter();
            XHTMLConverter xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance();
            xhtmlConverter.convert(document, response.getWriter(), options);
        } finally {
            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }
        }
    }

    public static void word2003ToHtml(byte[] body, HttpServletResponse response) throws Throwable {
        final String path =  getRoot() + "/image/word/media";
        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        ByteArrayInputStream input = new ByteArrayInputStream(body);
        HWPFDocument wordDocument = new HWPFDocument(input);
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            @Override
            public String savePicture(byte[] content, PictureType pictureType,
                                      String suggestedName, float widthInches, float heightInches) {
                return suggestedName;
            }
        });
        wordToHtmlConverter.processDocument(wordDocument);
        List pics = wordDocument.getPicturesTable().getAllPictures();
        if (pics != null) {
            for (int i = 0; i < pics.size(); i++) {
                Picture pic = (Picture) pics.get(i);
                try {
                    pic.writeImageContent(new FileOutputStream(path + "/"
                            + pic.suggestFullFileName()));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");
        serializer.setOutputProperty(OutputKeys.METHOD, "html");
        serializer.transform(domSource, streamResult);
        outStream.close();

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(outStream.toByteArray());
        outputStream.flush();
        outputStream.close();
    }

    public static void pdf2Html(byte[] body, HttpServletResponse response) throws Throwable {
        response.setContentType("application/pdf");
        OutputStream out = response.getOutputStream();
        out.write(body);
        out.flush();
        out.close();
    }

    public static void image2Html(byte[] body, HttpServletResponse response)throws Throwable{
        response.setContentType("image/png");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(body);//这里填表单访问接口
        outputStream.flush();
        outputStream.close();
    }

    /**
     * excel03转html
     * filename:要读取的文件所在文件夹
     * filepath:文件名
     * htmlname:生成html名称
     * path:html存放路径
     * */
    public static void PoiExcelToHtml (InputStream input, HttpServletResponse response) throws Exception {
//        String htmlname="exportExcel"+sourceid+".html";
//        String path=request.getSession().getServletContext().getRealPath("/view/excel");
//        String filename=request.getSession().getServletContext().getRealPath("/vod/mp4");
//        InputStream input=new FileInputStream(filename+"/"+filepath);
        HSSFWorkbook excelBook=new HSSFWorkbook(input);
        ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter (DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() );
        excelToHtmlConverter.processWorkbook(excelBook);//excel转html
        Document htmlDocument =excelToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();//字节数组输出流
        DOMSource domSource = new DOMSource (htmlDocument);
        StreamResult streamResult = new StreamResult (outStream);
        /** 将document中的内容写入文件中，创建html页面 */
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty (OutputKeys.ENCODING, "utf-8");
        serializer.setOutputProperty (OutputKeys.INDENT, "yes");
        serializer.setOutputProperty (OutputKeys.METHOD, "html");
        serializer.transform (domSource, streamResult);
        outStream.close();
//        String content = new String (outStream.toString("UTF-8"));

        OutputStream outputStream = response.getOutputStream();
        outputStream.write(outStream.toByteArray());//这里填表单访问接口
        outputStream.flush();
        outputStream.close();

    }

    public static void ExcelToHtml (InputStream fis, HttpServletResponse res) throws Exception{
        Workbook workbook = null;
        try {
            String html="";
            workbook =  new XSSFWorkbook(fis);
            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                Sheet sheet = workbook.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }
                html+="<html>\n" +
                        "<head>\n" +
                        " <style type=\"text/css\">.b1{white-space-collapsing:preserve;}\n" +
                        ".t1{border-collapse:collapse;border-spacing:0;}\n" +
                        ".r1{height:13.8pt;}\n" +
                        ".c1{white-space:pre-wrap;color: black; font-size:11pt;}\n" +
                        "</style>\n" +
                        "</head>\n" +
                        "<body class='b1'><br><br>";

                int firstRowIndex = sheet.getFirstRowNum();
                int lastRowIndex = sheet.getLastRowNum();
                html+="<table border='1px' class='t1'>";
                Row firstRow = sheet.getRow(firstRowIndex);

                String groupCol = "<colgroup>";
                String thCol = "";
                for (int i = firstRow.getFirstCellNum(); i <= firstRow.getLastCellNum(); i++) {
                    Cell cell = firstRow.getCell(i);
                    String cellValue = getCellValue(cell, true);
                    thCol +="<th>" + cellValue + "</th>";
                    groupCol += "<col width='56'>";
                }
                groupCol += "</colgroup>";
                html += groupCol;
                html += thCol;

                //行
                for (int rowIndex = firstRowIndex + 1; rowIndex <= lastRowIndex; rowIndex++) {
                    Row currentRow = sheet.getRow(rowIndex);
                    html+="<tr>";
                    if(currentRow!=null){

                        int firstColumnIndex = currentRow.getFirstCellNum();
                        int lastColumnIndex = currentRow.getLastCellNum();
                        //列
                        for (int columnIndex = 0; columnIndex <= firstRow.getLastCellNum(); columnIndex++) {
                            Cell currentCell = currentRow.getCell(columnIndex);
                            String currentCellValue = getCellValue(currentCell, true);
                            html+="<td class='c1'>"+currentCellValue + "</td>";
                        }
                    }else{
                        html+=" ";
                    }
                    html+="</tr>";
                }
                html+="</table></body></html>";


                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                DOMSource domSource = new DOMSource ();
                StreamResult streamResult = new StreamResult (outStream);

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer serializer = tf.newTransformer();
                serializer.setOutputProperty (OutputKeys.ENCODING, "utf-8");
                serializer.setOutputProperty (OutputKeys.INDENT, "yes");
                serializer.setOutputProperty (OutputKeys.METHOD, "html");
                serializer.transform (domSource, streamResult);
                outStream.close();

                //这两行一定要放前面
                res.setCharacterEncoding("utf-8");
                res.setContentType("text/html; charset=utf-8");
                PrintWriter printWriter = res.getWriter();

                printWriter.write(html);
                printWriter.flush();
                printWriter.close();
//                FileUtils.writeStringToFile(new File (path, htmlname), html, "gbk");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取单元格
     *
     */
    private static String getCellValue(Cell cell, boolean treatAsStr) {
        if (cell == null) {
            return "";
        }

        if (treatAsStr) {
            cell.setCellType(Cell.CELL_TYPE_STRING);
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else {
            return String.valueOf(cell.getStringCellValue());
        }
    }

    public static void mp42Html(HttpServletResponse res, String url) {
        res.setContentType("text/html");
        PrintWriter out = null;
        try {
            out = res.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        out.println("<!DOCTYPE HTML>");
        out.println("<HTML lang='en'>");
        out.println(" <HEAD><meta charset=\"UTF-8\"><TITLE>sender</TITLE></HEAD>");
        out.println(" <BODY>");
        out.println("<video controls='controls'  src='"+url+"'>");
        out.println("</video>");
        out.println(" </BODY>");
        out.println("</HTML>");
        out.flush();
        out.close();
    }
}
