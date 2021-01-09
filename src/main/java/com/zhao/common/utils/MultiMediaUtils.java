package com.zhao.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import ws.schild.jave.EncoderException;
//import ws.schild.jave.MultimediaObject;
//import ws.schild.jave.ScreenExtractor;
//import ws.schild.jave.VideoProcessor;
//import ws.schild.jave.info.MultimediaInfo;
//import ws.schild.jave.info.VideoInfo;
//import ws.schild.jave.info.VideoSize;
//import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
//import ws.schild.jave.progress.VideoProgressListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 音视频工具类
 * @Author: zhaolianqi
 * @Date: 2020/11/24 11:45
 * @Version: v1.0
 */
public class MultiMediaUtils {

    /**
     * 提取视频帧，可指定视频位置来提取
     * @param video 源视频文件
     * @param target 要保存的目标图片名称
     * @Author zhaolianqi
     * @Date 2020/11/24 16:33
     */
    public static void extractFrame(File video, File target){
        extractFrame(video, 0, 0, 1, target, 8);
    }

    /**
     * 提取视频帧，可指定视频位置来提取
     * @param video 源视频文件
     * @param target 要保存的目标图片名称
     * @param quality 截图质量，范围：[1, 31]，值越小质量越高
     * @Author zhaolianqi
     * @Date 2020/11/24 16:33
     */
    public static void extractFrame(File video, File target, int quality){
        extractFrame(video, 0, 0, 1, target, quality);
    }

    /**
     * 提取视频帧，可指定视频位置来提取
     * @param video 源视频文件
     * @param seconds 要截取的位置，多少秒
     * @param target 要保存的目标图片名称
     * @Author zhaolianqi
     * @Date 2020/11/24 16:33
     */
    public static void extractFrame(File video, int seconds, File target){
        extractFrame(video, 0, 0, seconds, target, 8);
    }

    /**
     * 提取视频帧，可指定视频位置来提取
     * @param video 源视频文件
     * @param width 提取的截图宽度
     * @param height 提取的截图高度
     * @param seconds 要截取的位置，多少秒
     * @param target 要保存的目标图片名称
     * @param quality 截图质量，范围：[1, 31]，值越小质量越高
     * @Author zhaolianqi
     * @Date 2020/11/24 16:33
     */
    public static void extractFrame(File video, int width, int height, int seconds, File target, int quality){
//        MultimediaObject multimediaObject = new MultimediaObject(video, new DefaultFFMPEGLocator());
//        ScreenExtractor screenExtractor = new ScreenExtractor();
        if (target.exists()){
            if (!target.delete()){
                throw new RuntimeException("输出目标已存在，且无法删除");
            }
        }
        if (quality < 1 || quality > 31)
            quality = 1;
//        try {
//            MultimediaInfo multimediaInfo = multimediaObject.getInfo();
//            VideoInfo videoInfo = multimediaInfo.getVideo();
//            if (width < 1 || width > videoInfo.getSize().getWidth())
//                width = videoInfo.getSize().getWidth();
//            if (height < 1 || height > videoInfo.getSize().getHeight())
//                height = videoInfo.getSize().getHeight();
//            screenExtractor.render(multimediaObject, width, height, seconds, target, quality);
//        } catch (EncoderException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 将多个视频合并成一个视频
     * @param videos 要合并的视频列表
     * @param destination 新的视频
     * @Author zhaolianqi
     * @Date 2020/11/24 17:02
     */
    public static void mergeVideos(List<File> videos, File destination){
        mergeVideos(videos, destination, null, null);
    }

    /**
     * 将多个视频合并成一个视频
     * @param videos 要合并的视频列表
     * @param destination 新的视频
     * @param onComplete 完成时的回调
     * @Author zhaolianqi
     * @Date 2020/11/24 17:02
     */
    public static void mergeVideos(List<File> videos, File destination, Runnable onComplete){
        mergeVideos(videos, destination, onComplete, null);
    }

    /**
     * 将多个视频合并成一个视频，多个视频必须是同一类型
     * @param videos 要合并的视频列表
     * @param destination 新的视频
     * @param onComplete 完成时的回调
     * @param onProgress 合并进度回调，参数为当前合并进度 [0.0 - 10.0]
     * @Author zhaolianqi
     * @Date 2020/11/24 17:02
     */
    public static void mergeVideos(List<File> videos, File destination, Runnable onComplete, Consumer<Double> onProgress){
//        VideoProcessor videoProcessor = new VideoProcessor();
        if (destination.exists()){
            if (!destination.delete())
                throw new RuntimeException("输出目标已存在，且无法删除");
        }
//        try {
//            videoProcessor.catClipsTogether(videos, destination, new VideoProgressListener() {
//                @Override
//                public void onBegin() { }
//                @Override
//                public void onMessage(String s) { }
//                @Override
//                public void onProgress(Double aDouble) {
//                    if (onProgress != null)
//                        onProgress.accept(aDouble);
//                }
//                @Override
//                public void onError(String s) { }
//                @Override
//                public void onComplete() {
//                    if (onComplete != null)
//                        onComplete.run();
//                }
//            });
//        } catch (Exception e){
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
    }

    /**
     * 将秒转换成时分秒格式，hh:mm:ss
     * @param seconds 要转换的秒数
     * @Author zhaolianqi
     * @Date 2020/11/25 13:58
     */
    private static String parseSeconds2FormatTime(int seconds){
        if (seconds <= 0)
            return "00:00:00";
        StringBuilder sb = new StringBuilder(4);
        int h = 0;
        int m = 0;
        if (seconds >= 3600)
            h = seconds / 3600;
        if (seconds >= 60)
            m = (seconds - h*3600) / 60;
        int s = seconds - (h*3600 + m*60);
        if (h < 10){
            sb.append("0").append(h);
        } else {
            sb.append(h);
        }
        sb.append(":");
        if (m < 10){
            sb.append("0").append(m);
        } else {
            sb.append(m);
        }
        sb.append(":");
        if (s < 10){
            sb.append("0").append(s);
        } else {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * 截取视频片段，精确模式
     * @param source 源视频
     * @param target 要保存的文件名，含路径
     * @param startSeconds 从多少秒开始截取
     * @param seconds 要截取的时长，单位：秒
     * @Author zhaolianqi
     * @Date 2020/11/25 14:01
     */
    public static void cutVideo(File source, File target, int startSeconds, int seconds){
        cutVideo(source, target, startSeconds, seconds, true, null);
    }

    /**
     * 截取视频片段
     * @param source 源视频
     * @param target 要保存的文件名，含路径
     * @param startSeconds 从多少秒开始截取
     * @param seconds 要截取的时长，单位：秒
     * @param exactly true：精确模式，速度较慢，不会丢失关键帧，截取的时长也很精确 <br>
     *                false：非精确模式，速度较快，但可能会丢失关键帧，截取的时长不是很精确
     * @param onMessage 程序执行过程中的回显消息回调
     * @Author zhaolianqi
     * @Date 2020/11/25 14:01
     */
    public static void cutVideo(File source, File target, int startSeconds, int seconds, boolean exactly, Consumer<String> onMessage){
        if (target.exists()){
            if (!target.delete())
                throw new RuntimeException("输出目标已存在，且无法删除");
        }
        List<String> arguments = new ArrayList<>(8);
        arguments.add("-ss");
        arguments.add(parseSeconds2FormatTime(startSeconds));
        arguments.add("-t");
        arguments.add(parseSeconds2FormatTime(seconds));
        arguments.add("-i");
        arguments.add(source.getAbsolutePath());
        if (exactly){ // 精确模式，速度慢一点，会重新编码，时间精确
            arguments.add("-c:v libx264 -c:a aac -strict experimental -b:a 98k");
        } else { // 非精确模式，速度很快，不会重新编码，但可能会出现关键帧丢失、截取出的视频长度不够不精确（可能会有几秒的误差）
            arguments.add("-vcodec copy -acodec copy");
        }
        arguments.add(target.getAbsolutePath());
        execute(arguments, onMessage);
    }

    /**
     * 视频水印位置
     * @Author zhaolianqi
     * @Date 2020/11/25 15:16
     */
    public static class WaterMarkPosition {
        /** 1：左上角 */
        public static final int TOP_LEFT = 1;
        /** 2：右上角 */
        public static final int TOP_RIGHT = 2;
        /** 3：左下角 */
        public static final int BOTTOM_LEFT = 3;
        /** 4：右下角 */
        public static final int BOTTOM_RIGHT = 4;
    }

    /**
     * 视频左上角添加水印
     * @param source 源视频
     * @param logo 水印图片
     * @Author zhaolianqi
     * @Date 2020/11/25 15:15
     */
    public static void addWaterMark(File source, File logo, File target){
        addWaterMark(source, logo, target, WaterMarkPosition.TOP_LEFT, null);
    }

    /**
     * 视频添加水印
     * @param source 源视频
     * @param logo 水印图片
     * @param position 水印位置，参考：{@link MultiMediaUtils.WaterMarkPosition}
     * @Author zhaolianqi
     * @Date 2020/11/25 15:15
     */
    public static void addWaterMark(File source, File logo, File target, int position){
        addWaterMark(source, logo, target, position, null);
    }

    /**
     * 视频添加水印
     * @param source 源视频
     * @param logo 水印图片
     * @param onMessage 程序执行过程中的回显消息回调
     * @param position 水印位置，参考：{@link MultiMediaUtils.WaterMarkPosition}
     * @Author zhaolianqi
     * @Date 2020/11/25 15:15
     */
    public static void addWaterMark(File source, File logo, File target, int position, Consumer<String> onMessage){
        checkTargetFile(target);
        List<String> arguments = new ArrayList<>(8);
        arguments.add("-i");
        arguments.add(source.getAbsolutePath());
        arguments.add("-i");
        arguments.add(logo.getAbsolutePath());
        arguments.add("-filter_complex");
        switch (position){
            case WaterMarkPosition.TOP_RIGHT: // 右上角
                arguments.add("[0:v][1:v]overlay=main_w-overlay_w-10:10[out]");
                break;
            case WaterMarkPosition.BOTTOM_LEFT: // 左下角
                arguments.add("[0:v][1:v]overlay=10:main_h-overlay_h-10[out]");
                break;
            case WaterMarkPosition.BOTTOM_RIGHT: // 右下角
                arguments.add("[0:v][1:v]overlay=main_w-overlay_w-10:main_h-overlay_h-10[out]");
                break;
            default: // 默认在左上角
                arguments.add("[0:v][1:v]overlay=10:10[out]");
        }
        arguments.add("-map [out] -map 0:a -codec:a copy");
        arguments.add(target.getAbsolutePath());
        execute(arguments, onMessage);
    }

    /**
     * 调用ffmpeg执行命令
     * @param arguments ffmpeg命令
     * @param onMessage 执行过程中的消息回调
     * @Author zhaolianqi
     * @Date 2020/11/25 14:46
     */
    private static void execute(List<String> arguments, Consumer<String> onMessage){
        MyFFMPEGProcess process = new MyFFMPEGProcess();
        process.execute(String.join(" ", arguments), onMessage);
    }

    /**
     * 视频分片，生成m3u8格式的资源清单
     * @param source 源视频
     * @param savePath 分片保存目录
     * @param segmentTime 每片时长，单位：秒
     * @Author zhaolianqi
     * @Date 2020/11/25 15:47
     */
    public static void spliceVideo(File source, File savePath, int segmentTime){
        spliceVideo(source, savePath, segmentTime, null);
    }

    /**
     * 视频分片，生成m3u8格式的资源清单
     * @param source 源视频
     * @param savePath 分片保存目录
     * @param segmentTime 每片时长，单位：秒
     * @param onMessage 执行过程中的消息回调
     * @Author zhaolianqi
     * @Date 2020/11/25 15:47
     */
    public static void spliceVideo(File source, File savePath, int segmentTime, Consumer<String> onMessage){
        if (!savePath.exists()){
            if (!savePath.mkdirs())
                throw new RuntimeException("保存目录创建失败");
        }
        if (savePath.isFile())
            throw new RuntimeException("savePath只能是目录");
        int  index = source.getName().lastIndexOf(".");
        if (index < 0)
            throw new RuntimeException("不识别的文件");
        String filename = source.getName().substring(0, index);
        List<String> arguments = new ArrayList<>(8);
        arguments.add("-i");
        arguments.add(source.getAbsolutePath());
        arguments.add("-c copy -map 0 -f segment -segment_list");
        arguments.add(savePath.getAbsolutePath() + "/" + filename + ".m3u8");
        arguments.add("-segment_time");
        arguments.add(String.valueOf(segmentTime));
        arguments.add(savePath.getAbsolutePath() + "/" + filename + "_%03d.ts");
        execute(arguments, onMessage);
    }

    /**
     * 视频转gif动图
     * @param source 源视频
     * @param target 保存的文件名，只能是.gif格式文件
     * @param startSeconds 视频位置，单位：秒
     * @param seconds 要转换的时长，单位：秒
     * @param width GIF图宽度
     * @Author zhaolianqi
     * @Date 2020/11/25 17:28
     */
    public static void video2gif(File source, File target, int startSeconds, int seconds, int width){
        video2gif(source, target, startSeconds, seconds, width, -1, null);
    }

    /**
     * 视频转gif动图
     * @param source 源视频
     * @param target 保存的文件名，只能是.gif格式文件
     * @param startSeconds 视频位置，单位：秒
     * @param seconds 要转换的时长，单位：秒
     * @param width GIF图宽度
     * @param height GIF图高度，如果传入-1，则根据宽度自适应
     * @param onMessage 执行过程中的消息回调
     * @Author zhaolianqi
     * @Date 2020/11/25 17:28
     */
    public static void video2gif(File source, File target, int startSeconds, int seconds, int width, int height, Consumer<String> onMessage){
//        MultimediaObject multimediaObject = new MultimediaObject(source);
//        try {
//            VideoInfo videoInfo = multimediaObject.getInfo().getVideo();
//            VideoSize size = videoInfo.getSize();
//            if (width <= 0)
//                width = size.getWidth();
//            if (height == -1){ // 高度自适应
//                height = size.getHeight() / (size.getWidth()/width);
//            }
//            if (height <= 0)
//                height = size.getHeight();
//            checkTargetFile(target);
//            List<String> arguments = new ArrayList<>(8);
//            arguments.add("-i");
//            arguments.add(source.getAbsolutePath());
//            arguments.add("-ss");
//            arguments.add(parseSeconds2FormatTime(startSeconds));
//            arguments.add("-t");
//            arguments.add(String.valueOf(seconds));
//            arguments.add("-s");
//            arguments.add(width + "x" + height);
//            arguments.add("-pix_fmt rgb24");
//            arguments.add(target.getAbsolutePath());
//            execute(arguments, onMessage);
//        } catch (EncoderException e) {
//            throw new RuntimeException(e);
//        }
    }

    private static void checkTargetFile(File target){
        if (target.exists()){
            if (!target.delete())
                throw new RuntimeException("输出目标已存在，且无法删除");
        }
    }

    public static void main(String[] args) throws Exception {

        System.out.println("============> task begin <==============");

//        mergeVideos(
//                Arrays.asList(
//                        new File("F:/videos/girl.mp4"),
//                        new File("F:/videos/天安门.mp4"),
////                        new File("F:/videos/中秋国庆大放价.mp4"),
//                        new File("F:/videos/贵州多民族文化 蓝光(1080P)_15968863926679.mp4"),
//                        new File("F:/videos/灵渠，位于广西兴安境内，是中国伟大的古建筑之一.mp4"),
//                        new File("F:/videos/灵渠的铧嘴,像一艘战舰.mp4")
//                ),
//                new File("F:/videos/mergedVideo.mp4")
//        );


//        DefaultFFMPEGLocator locator= new  DefaultFFMPEGLocator();
//        String exePath= locator.getExecutablePath();
//        System.out.println("ffmpeg executable found in <" + exePath + ">");

//        File source = new File("F:/videos/天安门.mp4");
//        File target = new File("F:/videos/天安门_target.mp4");
//
//        cutVideo(source, target, 0, 45, false, System.out::println);

//        addWaterMark(
//                new File("F:/videos/test.mp4"),
//                new File("F:/images/logo.png"),
//                new File("F:/videos/test_watermark.mp4"),
//                WaterMarkPosition.BOTTOM_RIGHT
//        );

//        spliceVideo(
//                new File("F:/videos/灵渠.mp4"),
//                new File("F:/videos/灵渠"),
//                30,
//                System.out::println
//        );

        video2gif(
                new File("F:/videos/灵渠.mp4"),
                new File("F:/videos/灵渠.gif"),
                10,
                5,
                200,
                -1,
                System.out::println
        );

        System.out.println("done");

//        getVideoCover(
//                "F:/tools/ffmpeg/bin/ffmpeg",
//                "F:/videos/test.mp4",
//                "F:/videos/test-mp4.jpg");
    }

    static Logger log = LoggerFactory.getLogger(MultiMediaUtils.class);

//    /**
//     * 获取第一秒第一帧的缩略图 -- （cmd(windows): ffmpeg.exe -ss 00:00:01 -y -i test1.mp4 -vframes 1 new.jpg）
//     *
//     * @param ffmpegPath      ffmpeg.exe文件路径，可在rest或者admin中进行配置，使用配置文件进行读取
//     * @param videoInputPath  视频文件路径（输入）
//     * @param coverOutputPath 缩略图输出路径
//     * @throws IOException
//     */
//    public static void getVideoCover(String ffmpegPath, String videoInputPath, String coverOutputPath) throws IOException {
//        // 构建命令
//        List<String> command = Lists.newArrayList();
//        command.add(ffmpegPath);
//        command.add("-ss");
//        command.add("00:00:01");
//        command.add("-y");
//        command.add("-i");
//        command.add(videoInputPath);
//        command.add("-vframes");
//        command.add("1");
//        command.add(coverOutputPath);
//        // 执行操作
////        ProcessBuilder builder = new ProcessBuilder(command);
////        Process process = builder.start();
//
//        Runtime run = Runtime.getRuntime();
//        Process process = run.exec(String.join(" ", command));
//
//        InputStream errorStream = process.getErrorStream();
//        InputStreamReader isr = new InputStreamReader(errorStream);
//        BufferedReader br = new BufferedReader(isr);
//        String line = "";
//        while ((line = br.readLine()) != null) {
//            System.out.println(line);
//        }
//        if (br != null) {
//            br.close();
//        }
//        if (isr != null) {
//            isr.close();
//        }
//        if (errorStream != null) {
//            errorStream.close();
//        }
//
//        System.out.println("done");
//    }

}
