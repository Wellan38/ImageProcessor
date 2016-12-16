/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import alexandre.imageprocessor.controller.Processor;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class Main
{
    public static void main(String[] args) throws IOException
    {
        BufferedImage im = Processor.readImage("C:\\Users\\Alexandre\\Desktop\\TestImages\\TestImage3.png");
        
        List<List<Double[][]>> characters = Processor.getCharacters(im);
        
        for (List<Double[][]> line : characters)
        {
            for (Double[][] charac : line)
            {
                for (int i = 0; i < charac.length; i++)
                {
                    for (int j = 0; j < charac[0].length; j++)
                    {
                        if (charac[i][j] == 1.)
                        {
                            System.out.print("*");
                        }
                        else
                        {
                            System.out.print(" ");
                        }
                    }
                    
                    System.out.println();
                }
            }
            
            System.out.println("-----");
        }
        
//        for (int l = 0; l < lines.size(); l++)
//        {
//            Double[][] line = lines.get(l);
//            
//            System.out.println("\nLine " + (l+1) + ":\n");
//
//            for (int i = 0; i < line.length; i++)
//            {
//                for (int j = 0; j < im.getWidth(); j++)
//                {
//                    if (line[i][j] == 1.)
//                    {
//                        System.out.print("*");
//                    }
//                    else
//                    {
//                        System.out.print(" ");
//                    }
//                }
//                System.out.println();
//            }
//        }
        
        
        
        //System.out.println(Processor.getNumberOfLines(im));
    }
}
