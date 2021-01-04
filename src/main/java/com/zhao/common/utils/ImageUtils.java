package com.zhao.common.utils;

import com.zhao.common.exception.BusinessException;
import sun.misc.BASE64Decoder;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;

public class ImageUtils {

	static private char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
    static private byte[] codes = new byte[256];
	public static final String IMAGE_TYPE_GIF = "gif";// 动图
	public static final String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
	public static final String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
	public static final String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
	public static final String IMAGE_TYPE_PNG = "png";// 可移植网络图形
	public static final String IMAGE_TYPE_PSD = "psd";// Photoshop的专用格式Photoshop

	static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }
	
	/**
	 * 读取图像的二进制流
	 * 
	 * @param infile
	 * @return
	 */
	public static FileInputStream getByteImage(String infile) {
		FileInputStream inputImage = null;
		File file = new File(infile);
		try {
			inputImage = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件不存在");
		}
		return inputImage;
	}

	/**
	 * 将图片输入流保存为目标图片
	 * @param inputStream 图片输入流
	 * @param path 要保存的完整路径（含文件名）
	 * @Author zhaolianqi
	 * @Date 2020/11/17 14:40
	 */
	public static void readImg(InputStream inputStream, String path) {
		try {
			File file = new File(path.substring(0, path.lastIndexOf("/")));
			if (!file.exists()){
				file.mkdirs();
			}
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			byte[] buf = new byte[1024];
			int len = 0;
			while ((len = inputStream.read(buf)) != -1) {
				fileOutputStream.write(buf, 0, len);// 写
			}
			inputStream.close();
			fileOutputStream.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("文件不存在");
		} catch (IOException e) {
			throw new RuntimeException("服务器异常");
		}
	}

	/**
	 * 下载图片
	 * @param imgPath 图片网络地址
	 * @param savePath 要保存的完整路径（含文件名）
	 * @Author zhaolianqi
	 * @Date 2020/11/17 14:41
	 */
	public static void downLoadImg(String imgPath, String savePath) {
		InputStream is = null;
		try {
			URL url = new URL(imgPath);
			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			ImageUtils.readImg(is, savePath);
		} catch (Exception e) {
			throw new RuntimeException("文件不存在");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
	}

	/**
	 * 本地图片文件转base64编码
	 * @param file 图片
	 * @return
	 */
	public static String imageFile2Base64(File file) {
		FileInputStream is;
		try {
			ByteArrayOutputStream data = new ByteArrayOutputStream();
			is = new FileInputStream(file);
			byte[] by = new byte[1024];
			int len;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
			return encode(data.toByteArray());
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
	}

	/**
	 * 网络图片转base64字符串
	 * @Author zhaolianqi
	 * @Date 2020/11/17 14:39
	 */
	public static String imageToBase64ByOnline(String imageUrl) {
		ByteArrayOutputStream data = new ByteArrayOutputStream();
		InputStream is = null;
		try {
			URL url = new URL(imageUrl);
			byte[] by = new byte[1024];
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			is = conn.getInputStream();
			int len;
			while ((len = is.read(by)) != -1) {
				data.write(by, 0, len);
			}
		} catch (IOException e) {
			throw new BusinessException(e.getMessage());
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				throw new BusinessException(e.getMessage());
			}
		}
//		BASE64Encoder encoder = new BASE64Encoder();
		return encode(data.toByteArray());
	}

	/**
	 * 数据转base64字符串
	 * @Author zhaolianqi
	 * @Date 2020/11/17 14:39
	 */
	private static String encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return String.valueOf(out);
    }
	
	/**
	 * 裁剪图片
	 * @param img 图片文件
	 * @param x 起始位置x
	 * @param y 起始位置y
	 * @param w 裁剪的宽度
	 * @param h 裁剪的高度
	 * @return
	 */
	public static BufferedImage cutImage(File img, int x, int y, int w, int h) {
		try {
			BufferedImage image = ImageIO.read(img);
			if (w > image.getWidth() - x)
				w = image.getWidth() - x;
			if (h > image.getHeight() - y)
				h = image.getHeight() - y;
			return image.getSubimage(x, y, w, h);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 裁剪图片并将裁剪的图片保存为新的图片
	 * @param img 图片文件
	 * @param x 起始位置x
	 * @param y 起始位置y
	 * @param w 裁剪的宽度
	 * @param h 裁剪的高度
	 * @param savePath 要保存的完整路径（含文件名）
	 * @Author zhaolianqi
	 * @Date 2020/11/17 14:50
	 */
	public static void cutImage(File img, int x, int y, int w, int h, String savePath) {
		int i = savePath.lastIndexOf(".");
		if (i < 0)
			throw new RuntimeException("savePath必须包含文件后缀");
		BufferedImage image = cutImage(img, x, y, w, h);
		File outputfile = new File(savePath);
		try {
			ImageIO.write(image, savePath.substring(i + 1), outputfile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * base64字符串转byte数组
	 * @param base64Str 要转换的base64字符串
	 * @return
	 */
	public static byte[] base64ToByte(String base64Str) {
		if (base64Str == null) // 图像数据为空
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		// Base64解码
		byte[] b;
		try {
			b = decoder.decodeBuffer(base64Str);
		} catch (IOException e) {
			return null;
		}
		for (int i = 0; i < b.length; ++i) {
			if (b[i] < 0) {// 调整异常数据
				b[i] += 256;
			}
		}
		return b;
	}

	/**
	 * 压缩图片
	 * @param file 图片
	 * @param savePath 要保存的完整路径（含文件名）
	 * @param quality 压缩质量，0 - 1之间的数字
	 * @Author zhaolianqi
	 * @Date 2020/11/17 15:51
	 */
	public static void compress(File file, String savePath, float quality) {
		ImageOutputStream out = null;
		String fileFormat = getFileFormat(savePath);
		try {
			BufferedImage bfImage =  ImageIO.read(file);
			File targetFile = new File(savePath);
			if (targetFile.exists())
				targetFile.delete();
			out = ImageIO.createImageOutputStream(targetFile);
			ImageWriteParam writeParam = null;
			ImageWriter writer = getWriter(bfImage, fileFormat);
			writer.setOutput(out);
			RenderedImage renderedImage = toRenderedImage(bfImage);
			if (quality < 1 || quality > 0){
				writeParam = writer.getDefaultWriteParam();
				if (writeParam.canWriteCompressed()) {
					writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
					writeParam.setCompressionQuality(quality);
					final ColorModel colorModel = renderedImage.getColorModel(); // ColorModel.getRGBdefault();
					writeParam.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
				}
			}
			try {
				if (writeParam != null) {
					writer.write(null, new IIOImage(renderedImage, null, null), writeParam);
				} else {
					writer.write(renderedImage);
				}
				out.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				writer.dispose();
				if (out != null)
					out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static RenderedImage toRenderedImage(Image img) {
		if (img instanceof RenderedImage) {
			return (RenderedImage) img;
		}
		return copyImage(img, BufferedImage.TYPE_INT_RGB, null);
	}

	/**
	 * 获取imageWriter
	 * @param img img
	 * @param formatName 图片类型，jpg、png等
	 * @Author zhaolianqi
	 * @Date 2020/11/17 15:48
	 */
	private static ImageWriter getWriter(Image img, String formatName) {
		final ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
		final Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
		return iter.hasNext() ? iter.next() : null;
	}

	private static BufferedImage toBufferedImage(Image image, String imageType) {
		final int type = imageType.equalsIgnoreCase(IMAGE_TYPE_PNG)
				? BufferedImage.TYPE_INT_ARGB
				: BufferedImage.TYPE_INT_RGB;
		return toBufferedImage(image, type);
	}

	public static BufferedImage toBufferedImage(Image image, int imageType) {
		BufferedImage bufferedImage;
		if (image instanceof BufferedImage) {
			bufferedImage = (BufferedImage) image;
			if (imageType != bufferedImage.getType()) {
				bufferedImage = copyImage(image, imageType, null);
			}
		} else {
			bufferedImage = copyImage(image, imageType, null);
		}
		return bufferedImage;
	}

	private static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
		final BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), imageType);
		final Graphics2D bGr = createGraphics(bimage, backgroundColor);
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	private static Graphics2D createGraphics(BufferedImage image, Color color) {
		final Graphics2D g = image.createGraphics();
		if (null != color) {
			// 填充背景
			g.setColor(color);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
		}
		return g;
	}

	/**
	 * 图片左下角加水印
	 * @param file 图片
	 * @param savePath 新图片要保存的路径（含文件名）
	 * @param pressText 水印文字
	 * @Author zhaolianqi
	 * @Date 2020/11/17 16:38
	 */
	public static void watermark(File file, String savePath, String pressText) {
		watermark(file, savePath, pressText, Color.WHITE, null, 10, -10, 1f);
	}

	/**
	 * 图片加水印
	 * @param file 图片
	 * @param savePath 新图片要保存的路径（含文件名）
	 * @param pressText 水印文字
	 * @param color 水印颜色，默认黑色
	 * @param font 水印字体，默认{@link Font#SANS_SERIF}
	 * @param x 相对于左上角x坐标，如果是负值则从反方向计算位置
	 * @param y 相对于左上角y坐标，如果是负值则从反方向计算位置
	 * @param alpha 水印透明度，取值范围：[0.0, 1.0]，含边界值
	 * @Author zhaolianqi
	 * @Date 2020/11/17 16:32
	 */
	public static void watermark(File file, String savePath, String pressText, Color color, Font font, int x, int y, float alpha) {
		int i = savePath.lastIndexOf(".");
		String format = getFileFormat(savePath);
		OutputStream outputStream = null;
		try {
			BufferedImage targetImage = ImageIO.read(file);
			final Graphics2D g = targetImage.createGraphics();
			if (font == null) {
				// 默认字体
				font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
			}
			// 透明度
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			// 基于左上角绘制
			// 抗锯齿
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(font);
			// 如果是负值，则从反方向计算位置
			if (x < 0)
				x = targetImage.getWidth() + x;
			if (y < 0)
				y = targetImage.getHeight() + y;

			if (color == null)
				color = Color.BLACK;
			g.setColor(color);
			g.drawString(pressText, x, y);
			g.dispose();
			outputStream = new FileOutputStream(new File(savePath));
			ImageIO.write(targetImage, format, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 缩放图片
	 * @param file 图片文件
	 * @param savePath 新图片要保存的路径（含文件名）
	 * @param w 目标宽度
	 * @param h 目标高度
	 * @Author zhaolianqi
	 * @Date 2020/11/17 17:13
	 */
	public static void scale(File file, String savePath, int w, int h) {
		scale(file, savePath, w, h, null);
	}

	/**
	 * 缩放图片
	 * @param file 图片文件
	 * @param savePath 新图片要保存的路径（含文件名）
	 * @param times 缩放比例
	 * @Author zhaolianqi
	 * @Date 2020/11/17 17:13
	 */
	public static void scale(File file, String savePath, float times) {
		if (times < 0)
			times = 1F;
		scale(file, savePath, 0, 0, times);
	}

	/**
	 * 缩放图片
	 * @param file 图片文件
	 * @param savePath 新图片要保存的路径（含文件名）
	 * @param w 目标宽度
	 * @param h 目标高度
	 * @param times 缩放比例, 如果不为null，则优先使用此参数
	 * @Author zhaolianqi
	 * @Date 2020/11/17 17:13
	 */
	private static void scale(File file, String savePath, int w, int h, Float times){
		String format = getFileFormat(savePath);
		File destFile = new File(savePath);
		BufferedImage bufImg = null; //读取图片
		try {
			bufImg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		double wr, hr; // 缩放比例
		if (times == null){
			wr = w * 1.0 / bufImg.getWidth();
			hr = h * 1.0 / bufImg.getHeight();
		} else {
			wr = times;
			hr = times;
		}
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Image Itemp = ato.filter(bufImg, null);
		try {
			ImageIO.write((BufferedImage) Itemp, format, destFile);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
	}

	/**
	 * 获取图片文件后缀名，默认：jpg
	 * @Author zhaolianqi
	 * @Date 2020/11/17 17:08
	 */
	private static String getFileFormat(String filePath) {
		int i = filePath.lastIndexOf(".");
		if (i < 0)
			return "jpg";
		return filePath.substring(i + 1);
	}

	/**
	 * 计算图片的主色调，返回RGB颜色值
	 * @param file 图片文件
	 * @Author zhaolianqi
	 * @Date 2020/11/18 16:25
	 */
	public static String getImageColorSolution(File file){
		java.util.List<String> colors = getImageColorSolution(file, 1);
		if (colors.isEmpty())
			return null;
		return colors.get(0);
	}

	/**
	 * 计算图片的主色调，返回RGB颜色值
	 * @param file 图片文件
	 * @param count 要获取的颜色数量，返回的结果是按相似度从大到小排序
	 * @Author zhaolianqi
	 * @Date 2020/11/18 16:25
	 */
	public static java.util.List<String> getImageColorSolution(File file, int count){
		return getImageColorSolution(file, count, 4);
	}

	/**
	 * 计算图片的主色调，返回RGB颜色值
	 * @param file 图片文件
	 * @param count 要获取的颜色数量，返回的结果是按相似度从大到小排序
	 * @param rootPointCount 初始种子点数量，数字越大计算月精细，效率越慢，根据实际需求来，推荐值[2, 16]之间
	 * @Author zhaolianqi
	 * @Date 2020/11/18 16:25
	 */
	public static java.util.List<String> getImageColorSolution(File file, int count, int rootPointCount){
		try {
			BufferedImage image = ImageIO.read(file);
			return new ImageColorSolution().getColorSolution(image, rootPointCount, count);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * base64保存为图片
	 * @param base64str 图片的base64字符串
	 * @param savePath 保存文件名（可以包含目录）
	 * @Author zhaolianqi
	 * @Date 2020/12/31 14:26
	 */
	public static boolean base642file(String base64str, String savePath) {
		//对字节数组字符串进行Base64解码并生成图片
		if (base64str == null) {
			return false;
		}
		int i = savePath.lastIndexOf("/");
		if (i >= 0){
			File dirFile = new File(savePath.substring(0, i));
			if (!dirFile.exists()){
				dirFile.mkdirs();
			}
		}
		OutputStream out = null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			//Base64解码
			byte[] b = decoder.decodeBuffer(base64str);
			for (i = 0; i < b.length; ++i) {
				//调整异常数据
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			//生成jpeg图片
			out = new FileOutputStream(savePath);
			out.write(b);
			out.flush();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out = null;
			}
		}
	}

}
