package de.joshavg.pdfwatermark.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

public class Watermarker {

    private final File watermarkFile;
    private final File pdfFile;
    private final File targetFile;

    public Watermarker(final String watermarkPath, final String pdfPath, final String targetPath)
            throws FileNotFoundException {
        this.watermarkFile = new File(watermarkPath);
        this.pdfFile = new File(pdfPath);
        this.targetFile = new File(targetPath);

        if (!this.watermarkFile.exists()) {
            throw new FileNotFoundException("watermark file not found");
        }

        if (!this.pdfFile.exists()) {
            throw new FileNotFoundException("pdf file not found");
        }
    }

    public void transform() throws IOException, DocumentException {
        final PdfReader reader = new PdfReader(this.pdfFile.getAbsolutePath());
        final int pages = reader.getNumberOfPages();
        final PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(this.targetFile.getAbsolutePath()));

        final Image watermark = Image.getInstance(this.watermarkFile.getAbsolutePath());
        watermark.setAbsolutePosition(0, 0);

        for (int i = 1; i <= pages; ++i) {
            final PdfContentByte under = stamper.getUnderContent(i);
            final Rectangle pagesize = reader.getPageSize(i);

            watermark.scaleAbsolute(pagesize.getWidth(), pagesize.getHeight());

            under.saveState();
            under.addImage(watermark);
            under.restoreState();
        }

        stamper.close();
        reader.close();
    }

}
