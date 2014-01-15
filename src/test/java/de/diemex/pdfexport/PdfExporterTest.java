package de.diemex.pdfexport;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Diemex
 */
public class PdfExporterTest
{
    @Test
    public void getLayersTest()
    {
        String [] layers = {"page1_2.png", "page1_3.png"};
        assertEquals(3, PdfExporter.getLayerCount(layers));

        layers = new String[] {"page1_2.png", "page1_3.png", "page2_1.png", "derp"};
        assertEquals(3, PdfExporter.getLayerCount(layers));
    }


    @Test
    public void getPageCount_noLayers_Test()
    {
        String [] fileNames = {"page1.png", "page2.png"};
        assertEquals(2, PdfExporter.getPageCount(fileNames));
    }


    @Test
    public void getPageCount_Layers_Test()
    {
        String [] fileNames = {"page1.png", "page1_2.png", "page2.png", "page2_2.png",};
        assertEquals(2, PdfExporter.getPageCount(fileNames));
    }

    @Test
    public void getPageCount_emptyInput_Test()
    {
        String [] fileNames = {};
        assertEquals(-1, PdfExporter.getPageCount(fileNames));
    }

    @Test
    public void getPageCount_NPE_suppress_Test()
    {
        assertEquals(-1, PdfExporter.getPageCount(null));
    }

    @Test
    public void testingStub()
    {
        assertEquals(1, 1);
    }
}
