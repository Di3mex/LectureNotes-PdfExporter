package de.diemex.pdfexport;


import java.io.File;
import java.util.Comparator;

/**
 * @author Diemex
 */
public class Unused
{
    /**
     * A simple Comparator that will sort files like they are shown in a file explorer, so
     * file1, file2, file11
     * becomes
     * file1, file11, file2
     */
    static class LN_FileComparator implements Comparator<File>
    {
        @Override
        public int compare(File o1, File o2)
        {
            int n1 = extractNumber(o1.getName());
            int n2 = extractNumber(o2.getName());
            return n1 - n2;
        }


        private int extractNumber(String name)
        {
            int i = 0;
            try
            {
                int s = name.contains("_") ? name.indexOf('_') : name.indexOf(".png"); //strip file extension
                String number = name.substring(4, s); //page is 4 chars long
                i = Integer.parseInt(number);
            } catch (Exception e)
            {
                i = 0; // if filename does not match the format
                // then default to 0
            }
            return i;
        }
    }
}
