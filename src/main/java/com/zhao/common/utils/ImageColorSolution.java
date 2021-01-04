package com.zhao.common.utils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 获取图片的主色调，可应用在自动生成配色方案
 * @Author zhaolianqi
 * @Date 2020/11/18 11:52
 */
public class ImageColorSolution {

    BufferedImage image = null;

    //定义RGB像素点
    public static class Point {
        int R;
        int G;
        int B;

        Point(){}

        public Point(int r, int g, int b) {
            super();
            R = r;
            G = g;
            B = b;
        }

        public int getR() {
            return R;
        }
        public void setR(int r) {
            R = r;
        }
        public int getG() {
            return G;
        }
        public void setG(int g) {
            G = g;
        }
        public int getB() {
            return B;
        }
        public void setB(int b) {
            B = b;
        }

        // 计算像素的欧氏距离
        public int colorDistance(Point point){
            int absR = this.R - point.R;
            int absG = this.G - point.G;
            int absB = this.B - point.B;
            return (int) Math.sqrt(absR * absR + absG * absG + absB * absB);
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        };

        @Override
        public boolean equals(Object obj) {
            Point point = (Point)obj;
            if (this.R == point.R && this.G == point.G && this.B == point.B){
                return true;
            }
            return false;
        }
        public void add(Point point){
            this.R += point.R;
            this.G += point.G;
            this.B += point.B;
        }

        @Override
        public String toString() {
            return "R="+this.R+" G="+this.G+" B="+this.B;
        }
    }

    //点群内部类
    public static class PointGroup {
        int summaryR = 0;
        int summaryG = 0;
        int summaryB = 0;
        int pointCount = 0;
        Point point;

        public void addPoint(Point point){
            this.summaryR += point.R;
            this.summaryG += point.G;
            this.summaryB += point.B;
            pointCount++;
        }

        public Point getNewRGB(){
            Point newpoint = new Point();
            if (pointCount != 0 ){
                newpoint.R = summaryR / pointCount;
                newpoint.G = summaryG / pointCount;
                newpoint.B = summaryB / pointCount;
            } else {
                newpoint= this.point;
            }
            return newpoint;
        }
    }

    // 得到某位置的像素RGB
    public Point getRGB(int x, int y){
        Point point = new Point();
        int pixel = image.getRGB(x, y);
        point.R = (pixel & 0xff0000) >> 16;
        point.G = (pixel & 0xff00) >> 8;
        point.B = (pixel & 0xff);
        return  point;
    }

    public boolean isEnd(List<PointGroup> rootPoint){
        Point oldPoint = new Point();
        Point newPoint = new Point();
        for (PointGroup pointGroup : rootPoint) {
            oldPoint.add(pointGroup.point);
            newPoint.add(pointGroup.getNewRGB());
            pointGroup.point = pointGroup.getNewRGB();
        }
        return oldPoint.equals(newPoint);
    }

    public List<Point> getSimilarity(List<PointGroup> rootPoint, int count){
        //去除与白色相近的颜色
        Iterator<PointGroup> it = rootPoint.iterator();
        while (it.hasNext()){
            PointGroup i = it.next();
            if (i.point.colorDistance(new Point(255, 255, 255)) < 30){
                it.remove();
            }
        }
        //根据RGB的总和对颜色排序
        rootPoint.sort(Comparator.comparingInt(p -> (p.point.R + p.point.G + p.point.B)));
        //如果颜色小于等于3个，直接输出
        if (rootPoint.size() <= 3){
            return rootPoint.stream().map(pg -> pg.point).collect(Collectors.toList());
        }
        int index = 1;
        int min = 100000;
        //得到3个最相近的颜色，即代表了主色调
        for (int i = 1 ; i < rootPoint.size() - 1; i++){
            int temp = rootPoint.get(i + 1).point.colorDistance(rootPoint.get(i).point) + rootPoint.get(i).point.colorDistance(rootPoint.get(i - 1).point);
            if (temp < min){
                min = temp;
                index = i;
            }
        }
        //根据R-G-B中最大的排序
        int max = 0;
        //得到RGB中最大的分量
        if (rootPoint.get(index).point.R >= rootPoint.get(index).point.G && rootPoint.get(index).point.R >= rootPoint.get(index).point.B){
            max = 1;
        } else if (rootPoint.get(index).point.G >= rootPoint.get(index).point.R && rootPoint.get(index).point.G >= rootPoint.get(index).point.B){
            max = 2;
        } else {
            max = 3;
        }
        // [210,178,103, 200,205,197, 159,138,96]
        switch (max) {
            case 1: // 根据R值排序
                rootPoint.sort(Comparator.comparingInt(a -> - a.point.R));
                break;
            case 2: // 根据G值排序
                rootPoint.sort(Comparator.comparingInt(a -> - a.point.G));
                break;
            case 3: // 根据B值排序
                rootPoint.sort(Comparator.comparingInt(a -> - a.point.B));
                break;
            default:
                break;
        }
        if (count > rootPoint.size())
            count = rootPoint.size();
        return rootPoint.subList(0, count).stream().map(pg -> pg.point).collect(Collectors.toList());
    }

    /**
     * 根据图片得到配色方案
     * @param image bufferImage
     * @param rootPointCount 种子点的个数
     * @param colorCount 得到的色调个数
     * @return
     */
    public List<String> getColorSolution(BufferedImage image, int rootPointCount, int colorCount){
        this.image = image;
        int width = image.getWidth();
        int height = image.getHeight();
        //最大迭代次数
        int maxIterator = 5000000;
        int offsetWidth = width / 32;
        int offsetHeight = height / 32;

        //随机得到32*32个点，在其中取rootPointNum个点，取RGB值做为种子点
        List<PointGroup> rootPoint = new ArrayList<>(32 * 32);
        for (int i = 0; i < width; i += offsetWidth)
            for (int j = 0; j < height; j += offsetHeight){
                PointGroup pg = new PointGroup();
                pg.point = getRGB(i, j);
                rootPoint.add(pg);
            }
        //设置种子点集群初始阈值
        int threshold = 6;
        //当当前的种子点个数大于用户设置的上限时 提高阈值
        while (rootPoint.size() > rootPointCount){
            for (int i = 0; i < rootPoint.size(); i++){
                for (int j = i + 1; j < rootPoint.size(); j++){
                    if (rootPoint.get(i).point.colorDistance(rootPoint.get(j).point) < threshold){
                        rootPoint.remove(j);
                    }
                }
            }
            threshold += 1;
        }
        int count = 0;
        do {
            //遍历图片所有像素点，将每个点都加入其中一个种子点
            for (int i = 0; i < width ; i++){
                for(int j = 0; j < height; j++){
                    Point point = getRGB(i, j);
                    int index = 0;
                    int dis = 10000;
                    for (int m = 0; m < rootPoint.size(); m++) {
                        int curDis = point.colorDistance(rootPoint.get(m).point);
                        if (curDis < dis){
                            dis = curDis;
                            index = m;
                        }
                    }
                    rootPoint.get(index).addPoint(point);
                    count++;
                }
            }
            //查看新的种子点平均RGB是否等于原来的种子点，如果是，则说明收敛完毕
            if (isEnd(rootPoint)){
                break;
            }
        } while (count < maxIterator);
        List<Point> pointList = getSimilarity(rootPoint, colorCount);
        return pointList.stream().map(point -> point.getR() + "," + point.getG() + "," + point.getB()).collect(Collectors.toList());
    }
}
