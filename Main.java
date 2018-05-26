import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.UUID;

public class Main {

    private static ServerSocket server;
    private static int port=9856;

    public static void main(String[] args) throws IOException, ClassNotFoundException,InterruptedException {

        server=new ServerSocket(port);
        while(true) {
            System.out.println("wait..");
            Socket socket = server.accept();
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            FileOutputStream fos = new FileOutputStream("/home/maxim/content.jpg");
            int styleNumber=(int) ois.readObject();
            System.out.println("message "+styleNumber);
            int i;
            while(true){
                //System.out.println("hi");
                i=(int)ois.readObject();
                if(i==-1) break;
                fos.write(i);

            }
            fos.close();


            UUID id=UUID.randomUUID();
            String input="#!/bin/bash\n" +
		    "exec 2>/home/maxim/log1.log\n" +
                    "convert content.jpg -resize 40% content.jpg\n"+
                    "echo $USER >> /home/maxim/logs.txt\n"+
                    ". activate magenta\n" +
                    "image_stylization_transform " +
                    "--num_styles=32 " +
                    "--checkpoint=/home/maxim/style/multistyle-pastiche-generator-varied.ckpt " +
                    "--input_image=/home/maxim/content.jpg " +
                    "--which_styles=\"["+String.valueOf(styleNumber)+"]\" " +
                    "--output_dir=/home/maxim/style " +
                    "--output_basename=\""+id+"\"";
            BufferedWriter bw=new BufferedWriter(new FileWriter("/home/maxim/start.sh"));
            bw.write(input);
            bw.close();

            ProcessBuilder pb = new ProcessBuilder("bash","/home/maxim/start.sh");
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lne = null;
            while ((lne = reader.readLine()) != null)
            {
                System.out.println(lne);
            }

            reader.close();


            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());

           // oos.writeObject(styleNumber);
            FileInputStream fis=new FileInputStream(new File("/home/maxim/style/"+id+"_"+styleNumber+".png"));
            while(true){
                i=fis.read();
                if(i==-1) break;
                oos.writeObject(i);
            }
            //oos.writeObject(-1);
            fis.close();
            ois.close();
            oos.close();

            //ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            /*FileOutputStream fos = new FileOutputStream("/home/maxim/content.jpg");
            int i;
            String styleNumber=String.valueOf(ois.readObject());

            while ((i = ois.read()) > -1) {
                fos.write(i);

                System.out.println(i);
            }
            System.out.println(11);
            //oos.writeObject("Success");

            //oos.close();
            ois.close();

            fos.close();

            UUID id=UUID.randomUUID();
            String input="#!/bin/bash\n" +
                    "image_stylization_transform " +
                    "--num_styles=32 " +
                    "--checkpoint=/home/maxim/style/multistyle-pastiche-generator-varied.ckpt " +
                    "--input_image=/home/maxim/content.jpg " +
                    "--which_styles=\"["+String.valueOf(styleNumber)+"]\" " +
                    "--output_dir=/home/maxim/style " +
                    "--output_basename=\""+id+"\"";
           // input.replace("[20]","["+ styleNumber+"]");
            //System.out.println(input);
            BufferedWriter bw=new BufferedWriter(new FileWriter("/home/maxim/start.sh"));
            bw.write(input);
            bw.close();



            ProcessBuilder pb = new ProcessBuilder("sh","/home/maxim/start.sh");
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String lne = null;
            while ((lne = reader.readLine()) != null)
            {
                System.out.println(lne);
            }
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            FileInputStream fis=new FileInputStream(new File("/home/maxim/style/"+id+".png"));
            while((i=fis.read())!=-1){
                oos.write(i);
            }
            fis.close();

            oos.close();
            */
            socket.close();
            if(input=="exit") break;
        }
        server.close();
    }
}
