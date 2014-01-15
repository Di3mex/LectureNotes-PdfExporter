package de.diemex.pdfexport;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfDestination;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Command line class to export notebooks generated by lecture notes to pdf.
 */
public class PdfExporter
{
    public static void main(String arg0[]) throws DocumentException, IOException
    {
        long time = System.currentTimeMillis();

        List<String> args = Arrays.asList(arg0);

        if (args.contains("-f"))
        {
            File picFolder = new File(getArgValue(args, "-f")); //possible errors if null?
            if (!picFolder.exists())
            {
                System.out.println("folder " + picFolder.getPath() + " doesn't exist");
                return;
            }

            List<File> pages = Arrays.asList(picFolder.listFiles(new FileFilter()
            {
                @Override
                public boolean accept(File file)
                {
                    return file.getName().matches("page[0-9]*.png");
                }
            }));
            //Collections.sort(pages, new LN_FileComparator());
            String[] fileNames = new String[pages.size()];

            for (int i = 0; i < pages.size(); i++)
                fileNames[i] = pages.get(i).getName();

            int pageCount = getPageCount(fileNames);

            //Just make sure that there are actually pngs in the folder at all
            if (pageCount <= 0)
            {
                System.out.println("No pictures have been found in this folder");
                return;
            }

            Document pdfDoc = new Document();

            String outputFileName = (getPos(args, "-o") > -1) ? getArgValue(args, "-o") : picFolder.getPath() + File.separator + picFolder.getName() + ".pdf";
            File outFile = new File(outputFileName);
            if (!outFile.exists())
                outFile.createNewFile();

            PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(outFile));
            //TOC
            writer.setViewerPreferences(PdfWriter.PageModeUseOutlines);
            pdfDoc.open();
            PdfOutline toc = writer.getRootOutline();

            System.out.println("Exporting -- " + picFolder.getPath() + " -- pages: " + pageCount);
            System.out.println("Remaining pages:");

            Image img;
            PdfOutline outlineElem;
            for (int page = 1; page <= pageCount; page++) //There is no page 0!
            {
                try
                {
                    img = Image.getInstance(mergeImages(getLayersOfPageIn(page, picFolder)), null);
                    img.scaleToFit(pdfDoc.getPageSize());
                    img.setAbsolutePosition(0, 0); //img is offset otherwise
                    pdfDoc.add(img);

                    //TOC
                    String tocLine = getTocLineForPage(page, picFolder);
                    if (tocLine != null)
                        outlineElem = new PdfOutline(toc, new PdfDestination(PdfDestination.FITH), tocLine);

                    if (page + 1 < pages.size())
                        pdfDoc.newPage();
                    if ((page-1) % 25 == 0) // we start at page 1
                        System.out.println();
                    System.out.print(pageCount - page + ", ");
                } catch (IOException e)
                {
                    System.err.println("Current page " + page);
                    System.err.println("Skipping to next file");
                    e.printStackTrace();
                }
            }

            pdfDoc.close();
        } else
        {
            System.err.println("-f notebookfolder (-o output file)");
            System.err.println("Expected a folder as parameter (-f)");
        }

        System.out.println("Taken " + new DecimalFormat("#.##").format((System.currentTimeMillis() - time) /1000.0D) + " seconds!");
    }


    public static File getLayerOfPageIn(int layer, int page, File rootFolder)
    {
        return new File(rootFolder.getPath(), String.format("page%d%s.png", page, (layer > 1) ? "_" + layer : ""));
    }


    public static File[] getLayersOfPageIn(int page, File rootFolder)
    {
        int layerCount = getLayerCount(rootFolder.list());
        File[] layers = new File[layerCount];
        for (int layer = 0; layer < layerCount; layer++)
            layers[layer] = getLayerOfPageIn(layer+1, page, rootFolder);
        return layers;
    }


    public static int getLayerCount(String[] fileNames)
    {
        Pattern firstPagePat = Pattern.compile("page1_[0-9]+.png");
        List<String> sortedList = Arrays.asList(fileNames);
        Collections.sort(sortedList);
        int layers = 1;
        for (String fileName : sortedList)
        {
            if (firstPagePat.matcher(fileName).matches())
                layers++;
        }
        return layers;
    }


    public static int getPageCount(String[] fileNames)
    {
        if (fileNames == null)
            return -1;
        Pattern pagePat = Pattern.compile("page[0-9]+.png");
        List<String> sortedList = Arrays.asList(fileNames);
        int highestPage = -1;

        for (String fileName : sortedList)
        {
            if (pagePat.matcher(fileName).matches())
            {
                int newVal = Integer.parseInt(fileName.substring(4, fileName.length() - 4)); //Cut between page and .png
                if (newVal > highestPage)
                    highestPage = newVal;
            }
        }
        return highestPage;
    }


    /**
     * Get the position of an element in a list. This is roughly like contains() just that it returns the index.
     *
     * @param list   list
     * @param search search for this element
     *
     * @return position or -1 if not found
     */
    public static int getPos(List<?> list, Object search)
    {
        for (int i = 0; i < list.size(); i++)
        {
            Object listItem = list.get(i);
            if (listItem.equals(search))
                return i;
        }
        return -1;
    }


    /**
     * Get a value for a given argument
     *
     * @param list    arguments
     * @param argName argument name
     *
     * @return value or null if argument not supplied
     */
    public static String getArgValue(List<String> list, String argName)
    {
        int pos = getPos(list, argName);
        return pos > -1 && pos + 1 < list.size() ? list.get(pos + 1) : null;
    }


    public static java.awt.Image mergeImages(File... imageFiles) throws IOException
    {
        BufferedImage[] images = new BufferedImage[imageFiles.length];

        // load source images
        for (int i = 0; i < imageFiles.length; i++)
            try
            {
                images[i] = ImageIO.read(imageFiles[i]);
            } catch (IOException e)
            {
                System.err.println("Error reading file: " + imageFiles[i].getPath() + imageFiles[i].getName());
                throw (e); //throw so the calling method terminates
            }

        // create the new image to paint on, canvas size is the size of the first image, they are expected to be the same size
        int w = images[0].getWidth();
        int h = images[0].getHeight();
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        // paint both images, preserving the alpha channels
        Graphics g = combined.getGraphics();
        for (BufferedImage layer : images)
            g.drawImage(layer, 0, 0, null);

        return combined;
    }


    public static String getTocLineForPage(int page, File rootFolder)
    {
        StringBuilder strBuilder = new StringBuilder();
        File keyFile = new File (rootFolder, "key"+page+".txt");
        if (keyFile.exists())
        {
            try
            {
                //Read txt file line by line
                FileReader rd = new FileReader(keyFile);
                BufferedReader bfrReader = new BufferedReader(rd);
                for (String line; (line = bfrReader.readLine()) != null; )
                {
                    if (line.length() > 0)
                    {
                        if (strBuilder.length() > 0)
                            strBuilder.append(", ");
                        strBuilder.append(line);
                    }
                }
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            } catch (IOException e)
            {
                e.printStackTrace();
            }

        }
        return strBuilder.length() > 0 ? strBuilder.toString() : null;
    }
}
