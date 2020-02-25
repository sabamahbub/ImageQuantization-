/*******************************************************
 CS4551 Multimedia Software Systems
 @ Author: Elaine Kang
 *******************************************************/

//
// Template Code - demonstrate how to use Image class
import java.util.Scanner;

public class CS4551_Mahbub{
  public static void main(String[] args){
	// if there is no commandline argument, exit the program
    if(args.length != 1){
      usage();
      System.exit(1);
    }
    System.out.println("--Welcome to Multimedia Software System--");
    Scanner input = new Scanner(System.in);
    int option = 0;
    boolean running = true;
    while(running){
      // img = new Image2(args[0]);
      System.out.println("Main Menu-----------------------------------");
      System.out.println("1. Conversion to Grasy- scale Image (24bit->8bit)");
      System.out.println("2. Conversion to N-level Image");
      System.out.println("3. Conversion to 8bit Indexed Color Image using Uniform Color Quantization (24bits->8bits)");
      System.out.println("4. Quit");
      System.out.println("Please enter the task number [1-4]:");
      option = input.nextInt();

      if(option == 1){
        Image2 img = new Image2(args[0]);
        img.grayScale();
        img.display();
        img.write2PPM(img.getName() + "-gray.ppm");
      }
      else if(option == 2){
      //Get N level
        System.out.println("N level:");
        int n = input.nextInt();
        Image2 img1 = new Image2(args[0]);
      //Convert to GrayScale then threshold
        img1.grayScale();
        img1.threshold(n);
        img1.display();
        img1.write2PPM(img1.getName() + "-threshold-" + n + "level.ppm");
      //Convert to Grayscale then Error Diffusion
        img1 = new Image2(args[0]);
        img1.grayScale();
        img1.errorDiffusion(n);
        img1.display();
        img1.write2PPM(img1.getName() + "-errordiffusion-"+ n +"level.ppm");
      }
      else if(option == 3){
        Image2 img2 = new Image2(args[0]);
        img2.createColorQuantizationIndexImage();
        img2.display();
        img2.write2PPM(img2.getName() + "-index.ppm");
        img2.generateBasedOnLUT();
        img2.display();
        img2.write2PPM(img2.getName() + "-QT8.ppm");
      }
      else if(option == 4){
        running = false;
      }
      else{
        running = false;
      }  
    }
    input.close();
    System.out.println("--Good Bye--");
    System.exit(0);
  }

  public static void usage(){
    System.out.println("\nUsage: java CS4551_Main [input_ppm_file]\n");
  }    
}
