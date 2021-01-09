package com.zhao.common.utils;

//import ws.schild.jave.utils.RBufferedReader;

import java.io.IOException;
import java.io.InputStream;
//import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * 执行shell、dos的process封装
 * @Author: zhaolianqi
 * @Version: v1.0
 */
public class MyProcess implements AutoCloseable {

    private Process process = null;
    private InputStream errorStream = null;

    public void execute(String command, Consumer<String> callback){
        execute(command, callback, null);
    }

    public void execute(String command, Consumer<String> callback, Consumer<String> errorCall){
        Runtime runtime = Runtime.getRuntime();
        try {
            process = runtime.exec(command);
            int status = process.waitFor();
            errorStream = process.getErrorStream();
            InputStream inputStream;
            if (errorStream.available() > 0){
                inputStream = errorStream;
            } else {
                inputStream = process.getInputStream();
            }
//            RBufferedReader reader = new RBufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
//            String line;
//            while((line = reader.readLine()) != null) {
//                sb.append(line).append("\n");
//            }
            if (status != 0 && errorCall != null){
                errorCall.accept(sb.toString());
            } else if (callback != null){
                callback.accept(sb.toString());
            }
        } catch (IOException | InterruptedException e) {
            if (errorCall != null)
                errorCall.accept(e.getLocalizedMessage());
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
