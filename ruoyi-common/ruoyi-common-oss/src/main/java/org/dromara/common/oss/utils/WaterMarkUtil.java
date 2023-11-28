package org.dromara.common.oss.utils;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * @description: 水印工具类
 * @author: zhou shuai
 * @date: 2023/11/28 14:37
 * @version: v1
 */
@Slf4j
public class WaterMarkUtil {

    private final static String TAR_PDF_DIR = System.getProperty("java.io.tmpdir") + File.separator + "watermark" + File.separator + "pdf";

    private final static String TAR_IMAGE_DIR = System.getProperty("java.io.tmpdir") + File.separator + "watermark" + File.separator + "image";

    static {
        log.info("=====正在生成水印路径=====");
        FileUtil.mkdir(TAR_PDF_DIR);
        FileUtil.mkdir(TAR_IMAGE_DIR);
    }

    /**
     * 给图片添加文字水印
     *
     * @param srcImageUrl      需要添加水印的图片的路径
     * @param imageFileName    添加水印后图片名称
     * @param color            水印文字的颜色
     * @param waterMarkContent 水印文案
     * @param formaName        图片后缀
     * @return: java.lang.String 添加水印后图片输出路径
     */
    public static String imageAddTextWaterMark(String srcImageUrl, String imageFileName, Color color, String waterMarkContent, String formaName) throws Exception {
        // 1、生成目标图片路径
        String tarImagePath = TAR_IMAGE_DIR + File.separator + imageFileName;
        // 2、读取原图片信息
        Image srcImg = ImageIO.read(new URL(srcImageUrl));
        // 原始图片宽度
        int srcImgWidth = srcImg.getWidth(null);
        // 原始图片高度
        int srcImgHeight = srcImg.getHeight(null);
        BufferedImage buffImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
        // 3、得到画笔对象
        Graphics2D g = buffImg.createGraphics();
        g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
        // 4、设置水印文字颜色
        g.setColor(color);
        // 5、设置水印文字Font
        Font font = new Font("宋体", Font.BOLD, buffImg.getHeight() / 4);
        g.setFont(font);
        // 6、设置水印文字透明度
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.15f));
        // 获取水印文字总长度
        int watermarkLength = g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());
        int x = (srcImgWidth - watermarkLength) / 2;
        int y = srcImgHeight / 2;
        // 7、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
        g.drawString(waterMarkContent, x, y);
        // 8、释放资源
        g.dispose();
        // 9、生成图片
        FileOutputStream outImgStream = new FileOutputStream(tarImagePath);
        ImageIO.write(buffImg, formaName, outImgStream);
        outImgStream.flush();
        outImgStream.close();
        return tarImagePath;
    }

    /**
     * 给PDF添加文字水印
     *
     * @param srcPdfUrl        插入前的文件路径
     * @param pdfFileName      pdf文件名称
     * @param waterMarkContent 水印文案
     * @param numberOfPage     每页需要插入的条数
     * @return: java.lang.String 生成的水印的pdf文件路径
     */
    public static String addPdfWaterMark(String srcPdfUrl, String pdfFileName, String waterMarkContent, int numberOfPage) throws Exception {
        PdfReader reader = null;
        PdfStamper stamper = null;
        try {
            reader = new PdfReader(new URL(srcPdfUrl));
            String tarPdfPath = TAR_PDF_DIR + File.separator + pdfFileName;
            stamper = new PdfStamper(reader, new FileOutputStream(tarPdfPath));
            PdfGState gs = new PdfGState();
            //设置字体
            BaseFont font = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            //设置透明度
            gs.setFillOpacity(0.4f);

            int total = reader.getNumberOfPages() + 1;
            PdfContentByte content;
            for (int i = 1; i < total; i++) {
                //在内容上方加水印
                content = stamper.getOverContent(i);
                content.beginText();
                content.setGState(gs);
                //水印颜色
                content.setColorFill(BaseColor.DARK_GRAY);
                //水印字体样式和大小
                content.setFontAndSize(font, 35);
                //插入水印  循环每页插入的条数
                for (int j = 0; j < numberOfPage; j++) {
                    content.showTextAligned(Element.ALIGN_CENTER, waterMarkContent, 300, 200 * (j + 1), 30);
                }
                content.endText();
            }
            log.info("PDF水印添加完成!");
            return tarPdfPath;
        } finally {
            //一定不要忘记关闭流
            if (stamper != null) {
                stamper.close();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        /*String srcPdfUrl = "http://139.196.208.53:9001/ruoyi/2023/11/24/75bbe2b1e3de442a83a5023b9c83837e.pdf";
        String pdfFilePath = addPdfWaterMark(srcPdfUrl, "test2.pdf", "zhoushuai1119", 1);
        System.out.println("pdfFilePath is " + pdfFilePath);*/

        String srcImageUrl = "http://139.196.208.53:9001/ruoyi/2023/09/16/e17c34425eaf4d1eb9885e4d2ca9068d.png";
        String imageFilePath = imageAddTextWaterMark(srcImageUrl, "test3.png", Color.RED, "zhoushuai1119", "png");
    }

}
