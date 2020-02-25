import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import javax.imageio.stream.FileImageInputStream;

public class Image2 extends Image
{
    private BufferedImage img;
    private String fileName;			// Input file name
    private int pixelDepth=3;			// pixel depth in byte
    public ArrayList<RGB> LUT = new ArrayList<>();

    //Constructors
    public Image2(int w, int h){ super(w, h);}
    public Image2(String fn){ super(fn);}

    //GetName
    // public String getName(){
    //     return this.fileName;
    // }
    //GrayScale
    public void grayScalePixel(int x, int y, int[] irgb){
        byte[] rgb = new byte[3];
        int gray = (int) Math.round(0.299*irgb[0] + 0.587*irgb[1] + 0.114*irgb[2]);
        if(gray < 0)gray = 0;
        if(gray  > 255)gray = 255;
        for(int i=0;i<3;i++)rgb[i] = (byte) gray;
        setPixel(x,y,rgb);
    }
    public void grayScale(){
        int[] irgb = new int[3];
        for (int i = 0; i < getW(); i++){
            for(int j = 0; j < getH(); j++){
              getPixel(i, j, irgb );
              grayScalePixel(i,j,irgb);
            }
          }
    }
    //Threshold
    public int thresholdPixel(int x, int y, int n){
        // byte[] rgb = new byte[3];
        int[] irgb = new int[3];
        int[] boundaries = new int[n];
        int color = 0;
        //Create Array for boundries
        for(int i = 0; i < n; i++){
           boundaries[i] = i * (255/(n-1));
        }
        //Get RGB value for pixel at (x,y)
        getPixel(x, y, irgb);
        //Determine the color of the pixel
        for(int k = 0; k <n-1; k++){
            // if(irgb[0] == 0){color = 0;}
            if(irgb[0] == 0){
                color = 0;
                break;
            }
            else if(irgb[0] > 255){
                color = 255;
                break;
            }
            ////////////
            else{
                if(irgb[0] < (boundaries[k] + boundaries[k+1])/2){
                    color = boundaries[k];
                    break;
                }
                else color = boundaries[k+1];
            }
        }
        return color;
    }
     //N-level Threshold
    public void threshold(int n){
        int[] rgb = new int[3];
        int color;
        //Iterate to (x,y) pixel
        for (int i = 0; i < getH(); i++){
            for(int j = 0; j < getW(); j++){
                color = thresholdPixel(j, i, n);
                for(int k=0;k<3;k++)rgb[k] = color;
                setPixel(j,i,rgb);
            }
        }
    }
    //N-level error diffision
    public void errorDiffusionPixel(int x, int y, int n){
        int[] A = new int[3];
        getPixel(x, y, A);
        int B = thresholdPixel(x, y, n);
        int error = A[0] - B;

        for(int k=0;k<3;k++)A[k] = B;
        setPixel(x,y,A);

        int[] neighbor = new int[3];
        int[] newNeighbor = new int[3];
        int color = 0;

        // //Right
        if( (x+1) < getW()){
            getPixel(x+1, y, neighbor);
            color = neighbor[0] + error*7/16;
            if(color < 0) color = 0;
            if(color > 255) color = 255;
            for(int k=0;k<3;k++)newNeighbor[k] = color;
            setPixel(x+1, y, newNeighbor);
                //BottomRight
                if((y+1) < getH()){
                    getPixel(x+1, y+1, neighbor);
                    color = neighbor[0] + error*1/16;
                    if(color < 0) color = 0;
                    if(color > 255) color = 255;
                    for(int k=0;k<3;k++)newNeighbor[k] = color;  
                    setPixel(x+1, y+1, newNeighbor);
                }
        }
        //Bottom
        if((y+1) < getH()){
            getPixel(x, y+1, neighbor);
            color = neighbor[0] + error*5/16;
            if(color < 0) color = 0;
            if(color > 255) color = 255;
            for(int k=0;k<3;k++)newNeighbor[k] = color; 
            setPixel(x, y+1, newNeighbor);
        }
        //BottomLeft
        if((x-1)>= 0 &&(y+1)<getH()){
            getPixel(x-1, y+1, neighbor);
            color = neighbor[0] + error*3/16;
            if(color < 0) color = 0;
            if(color > 255) color = 255;
            for(int k=0;k<3;k++)newNeighbor[k] = color;
            setPixel(x-1, y+1, newNeighbor);
        }
    }
    public void errorDiffusion(int n){
        //Iterate to (x,y) pixel
        for (int i = 0; i < getH(); i++){
            for(int j = 0; j < getW(); j++){
                errorDiffusionPixel(j, i, n);
            }
          }
    }
    //N-level Uniform Color Quantization
    public void createColorQuantizationLUT(){
        LUT = new ArrayList<>();
        int R = 16, G = 16, B = 32;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                for(int k = 0; k < 4; k++){
                    this.LUT.add(new RGB(R, G, B));
                    B += 64;
                }
                B = 32;
                G += 32;
            }
            B = 32;
            G = 16;
            R += 32;
        }
        System.out.println("---------------LUT TABLE-------------");
        System.out.println("Index\tR\tG\tB");
        for(int i = 0; i < LUT.size(); i++){
            RGB list = this.LUT.get(i);
            System.out.println(i +"\t"+ list.getR() +"\t"+ list.getG()+"\t"+list.getB());
        }
        System.out.println("--------------------------------------------");
    }
    public void createPopularityLUT(){
        LUT = new ArrayList<>();
        int R = 2, G = 2, B = 2;
        for(int i = 0; i < 64; i++){
            for(int j = 0; j < 64; j++){
                for(int k = 0; k < 64; k++){
                    this.LUT.add(new RGB(R, G, B));
                    B += 4;
                }
                B = 2;
                G += 4;
            }
            B = 2;
            G = 2;
            R += 4;
        }
        //Implement the rest at a later time
    }
    public void getLUTIndex(int[] irgb){
        String representation = "";
        Integer index;
        String R = Integer.toBinaryString(irgb[0]);
        String G = Integer.toBinaryString(irgb[1]);
        String B = Integer.toBinaryString(irgb[2]);

        for(int i = 0; i < 8 - R.length(); i++) R = "0" + R;
        for(int i = 0; i < 8 - G.length(); i++) G = "0" + G;
        for(int i = 0; i < 8 - B.length(); i++) B = "0" + B;

        R = R.substring(0,3);
        G = G.substring(0, 3);
        B = B.substring(0, 2);

        representation = R + G + B;
        index = Integer.parseInt(representation,2);

        for(int k = 0; k < 3; k++) irgb[k] = index;
    }
    public void createColorQuantizationIndexImage(){
        createColorQuantizationLUT();
        int[] irgb = new int[3];
        for(int i = 0; i < getW(); i++){
            for(int j = 0; j < getH(); j++){
                getPixel(i, j, irgb);
                getLUTIndex(irgb);
                setPixel(i, j, irgb);
            }
        }
    }
    public void createPopularityIndexImage(){
        createPopularityLUT();
        int[] irgb = new int[3];
        for(int i = 0; i < getW(); i++){
            for(int j = 0; j < getH(); j++){
                getPixel(i, j, irgb);
                getLUTIndex(irgb);
                setPixel(i, j, irgb);
            }
        }
    }
    public void generateBasedOnLUT(){
        RGB newValue;
        int[] irgb = new int[3];
        for(int i = 0; i < getW(); i++){
            for(int j = 0; j < getH(); j++){
                getPixel(i, j, irgb);
                newValue = LUT.get(irgb[0]);
                irgb[0] = newValue.getR();
                irgb[1] = newValue.getG();
                irgb[2] = newValue.getB();
                setPixel(i, j, irgb);
            }
        }
    }
}