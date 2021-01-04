package com.zhao.common.utils;

import ws.schild.jave.process.ProcessLocator;
import ws.schild.jave.process.ffmpeg.DefaultFFMPEGLocator;
import ws.schild.jave.utils.RBufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * 执行ffmpeg的process封装
 * @Author: zhaolianqi
 * @Date: 2020/11/25 14:17
 * @Version: v1.0
 */
public class MyFFMPEGProcess implements AutoCloseable {

    private Process process = null;
    private InputStream errorStream = null;

    private String ffmpegPath = null;

    public MyFFMPEGProcess(){
        ProcessLocator locator = new DefaultFFMPEGLocator();
        this.ffmpegPath = locator.getExecutablePath();
    }

    public MyFFMPEGProcess(String ffmpegPath){
        this.ffmpegPath = ffmpegPath;
    }

    public void execute(String commend, Consumer<String> onMessage){
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(this.ffmpegPath + " " + commend);
            errorStream = process.getErrorStream();
            RBufferedReader reader = new RBufferedReader(new InputStreamReader(errorStream));
            String line;
            while((line = reader.readLine()) != null) {
                if (onMessage != null){
                    onMessage.accept(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                this.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public InputStream getErrorStream() {
        if (process == null)
            return null;
        return process.getErrorStream();
    }

    @Override
    public void close() throws Exception {
        try {
            if (errorStream != null) {
                errorStream.close();
            }
            if (process != null){
                if (process.getInputStream() != null)
                    process.getInputStream().close();
                if (process.getOutputStream() != null)
                    process.getOutputStream().close();
                process.destroy();
                process = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
