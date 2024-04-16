package org.example.main;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class OpenCVExample {
    public static void main(String[]args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(Core.VERSION);
        recognizeFace();
    }

    public static boolean recognizeFace() {
        // Завантаження класифікатора Haar для виявлення облич
        CascadeClassifier faceDetector = new CascadeClassifier();
        faceDetector.load("src/haarcascade_frontalface_alt.xml");

        // Завантаження зображення з веб-камери або файлу
//        Mat image = Imgcodecs.imread("src/user_image.jpg");
        Mat image = Imgcodecs.imread("src/user_image1.png");

        // Виявлення облич на зображенні
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        // Перевірка, чи розпізнано хоча б одне обличчя
        if (faceDetections.toArray().length > 0) {
            // Виділення обличчя на зображенні
            for (Rect rect : faceDetections.toArray()) {
                Imgproc.rectangle(image, new org.opencv.core.Point(rect.x, rect.y),
                        new org.opencv.core.Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
            }

            // Відображення зображення з виділеними обличчями
            BufferedImage img = new BufferedImage(image.width(), image.height(), BufferedImage.TYPE_3BYTE_BGR);
            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGB);
            byte[] data = new byte[image.rows() * image.cols() * (int) (image.elemSize())];
            image.get(0, 0, data);
            img.getRaster().setDataElements(0, 0, image.cols(), image.rows(), data);
            JLabel picLabel = new JLabel(new ImageIcon(img));
            JOptionPane.showMessageDialog(null, picLabel);
            System.out.println("Обличчя розпізнано");
            return true;
        } else {
            System.out.println("Обличчя не розпізнано");
            return false;
        }
    }
}
