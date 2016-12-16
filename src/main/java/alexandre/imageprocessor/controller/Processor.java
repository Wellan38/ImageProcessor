/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package alexandre.imageprocessor.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Alexandre
 */
public class Processor
{
    public static final double CONTAINS_TEXT_RATIO = 0.005;
    public static final double MIN_EMPTY_LINES_RATIO = 0.01;
    
    /**
     * 
     * @param path
     * @return
     * @throws IOException 
     */
    public static BufferedImage readImage(String path) throws IOException
    {
        File image_file;
        try
        {
            image_file = new File(path);
        }
        catch(Exception e)
        {
            System.out.println("Couldn't load file " + path);
            throw e;
        }
        
        BufferedImage image;
        
        try
        {
            image = ImageIO.read(image_file);
        }
        catch (IOException e)
        {
            System.out.println("Couldn't convert file " + path + " as an image.");
            throw e;
        }
        
        return image;
    }
    
    public static int[] getExtrema (BufferedImage image)
    {
        int[] extrema = {Integer.MAX_VALUE, Integer.MIN_VALUE}; // first is min, second is max
        
        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                int value = Math.abs(image.getRGB(j, i));
                
                if (value < extrema[0])
                {
                    extrema[0] = value;
                }
                
                if (value > extrema[1])
                {
                    extrema[1] = value;
                }
            }
        }
        
        return extrema;
    }
    
    public static Double[][] imageToArray (BufferedImage image)
    {
        Double[][] res = new Double[image.getHeight()][image.getWidth()];
        
        int[] extrema = getExtrema(image);
        
        for (int i = 0; i < image.getHeight(); i++)
        {
            for (int j = 0; j < image.getWidth(); j++)
            {
                int value = Math.abs(image.getRGB(j, i));
                
                if (value > extrema[0] + (extrema[1] - extrema[0]) / 2)
                {
                    res[i][j] = 1.;
                }
                else
                {
                    res[i][j] = 0.;
                }
            }
        }
        
        return res;
    }
    
    public static int getNumberOfLines(BufferedImage image)
    {
        int nbLines = 0;
        
        Double[][] input = imageToArray(image);
        
        boolean betweenLines = true;
        
        for (int i = 0; i < image.getHeight(); i++)
        {
            boolean containsText = false;
            
            int nbDots = 0;
            
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (input[i][j] == 1.)
                {
                    nbDots++;
                }
                
                if (nbDots >= CONTAINS_TEXT_RATIO * image.getWidth())
                {
                    containsText = true;
                    break;
                }
            }
            
            if (betweenLines && containsText)
            {
                nbLines++;
                betweenLines = false;
            }
            
            if (!betweenLines && !containsText)
            {
                betweenLines = true;
            }
        }
        
        return nbLines;
    }
    
    public static List<Double[][]> getLines (BufferedImage image)
    {
        List<Double[][]> lines = new ArrayList<>();
        
        Double[][] input = imageToArray(image);
        
        boolean newLine = true;
        
        int nbEmptyLines = 0;
        
        for (int i = 0; i < image.getHeight(); i++)
        {
            boolean containsText = false;
            
            int nbDots = 0;
            
            for (int j = 0; j < image.getWidth(); j++)
            {
                if (input[i][j] == 1.)
                {
                    nbDots++;
                }
                
                if (nbDots >= CONTAINS_TEXT_RATIO * image.getWidth())
                {
                    containsText = true;
                    
                    break;
                }
            }
            
            if (!containsText)
            {
                nbEmptyLines++;
            }
            
            if (containsText)
            {
                if (newLine && nbEmptyLines >= MIN_EMPTY_LINES_RATIO * image.getHeight())
                {
                    Double[][] line = new Double[1][image.getWidth()];
                    line[0] = input[i];
                    lines.add(line);
                    
                    newLine = false;
                    
                    
                }
                else
                {
                    lines.set(lines.size() - 1, addLine(lines.get(lines.size() - 1), input[i]));
                }
                
                nbEmptyLines = 0;
            }
            else
            {
                newLine = true;
            }
        }
        
        return lines;
    }
    
    public static List<Double[][]> getCharactersInLine(Double[][] line)
    {
        List<Double[][]> characters = new ArrayList<>();
        
        boolean newChar = true;
        
        for (int j = 0; j < line[0].length; j++)
        {
            boolean emptyColumn = false;
            int nbDots = 0;
            
            for (int i = 0; i < line.length; i++)
            {
                if (line[i][j] == 1.)
                {
                    nbDots++;
                }
            }
            
            if (nbDots < 2)
            {
                emptyColumn = true;
                nbDots = 0;
            }
            
            if (emptyColumn)
            {
                newChar = true;
            }
            else
            {
                if (newChar)
                {
                    newChar = false;
                    
                    Double[][] character = new Double[line.length][1];
                    
                    for (int k = 0; k < line.length; k++)
                    {
                        character[k][0] = line[k][j];
                    }
                    
                    characters.add(character);
                }
                else
                {
                    characters.set(characters.size() - 1, addColumnToChar(characters.get(characters.size() - 1), line, j));
                }
            }
        }
        
        return characters;
    }
    
    public static List<List<Double[][]>> getCharacters(BufferedImage image)
    {
        List<List<Double[][]>> characters = new ArrayList<>();
        
        List<Double[][]> lines = getLines(image);
        
        for (Double[][] line : lines)
        {
            characters.add(getCharactersInLine(line));
        }
        
        return characters;
    }
    
    private static Double[][] addLine(Double[][] array, Double[] newLine)
    {
        array = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = newLine;
        
        return array;
    }
    
    private static Double[][] addColumnToChar(Double[][] character, Double[][] line, int index)
    {
        Double[][] newCharacter = new Double[character.length][character[0].length + 1];
        
        for (int i = 0; i < line.length; i++)
        {
            for (int j = 0; j < character[0].length; j++)
            {
                newCharacter[i][j] = character[i][j];
            }
            
            newCharacter[i][character[0].length] = line[i][index];
        }
        
        return newCharacter;
    }
}
