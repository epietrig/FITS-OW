/*   Copyright (c) INRIA, 2016. All Rights Reserved
 *   Licensed under the GNU LGPL. For full terms see the file LICENSE.
 *
 * $Id: $
 */

package fr.inria.ilda.fitsow;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.exceptions.PDFException;
import org.icepdf.core.exceptions.PDFSecurityException;

import fr.inria.zvtm.glyphs.IcePDFPageImg;
import fr.inria.zvtm.glyphs.ZPDFPage;

class PDFLoader {

    FITSOW app;

    PDFLoader(FITSOW app){
        this.app = app;
    }

    void loadPDF(String pdfFileName){
        // assumes that the file is in the local data dir served with NanoHTTPD
        String urlS = "http://" + Config.HTTPD_IP + ":" + Config.HTTPD_PORT + "/" + pdfFileName;
        try {
            URL pdfURL = new URL(urlS);
            loadPDF(pdfURL, 0, 0);
        }
        catch (MalformedURLException mue){
            System.out.println("Error loading PDF document from " + urlS);
        }
    }

    void loadPDF(URL url, double vx, double vy){
        Document document = new Document();
        try {
            document.setUrl(url);
        } catch (PDFException ex) {
            System.out.println("Error parsing PDF document " + ex);
            return;
        } catch (PDFSecurityException ex) {
            System.out.println("Error encryption not supported " + ex);
            return;
        } catch (IOException ex) {
            System.out.println("Error handling PDF document " + ex);
            return;
        }
        IcePDFPageImg page = new IcePDFPageImg(vx, vy, Config.Z_FITS_IMG,
                                               document, 0, 2, 2);
        page.setType(Config.T_PDF);
        addPage(page);
    }

    void addPage(ZPDFPage page){
        if (page != null){
            app.dSpace.addGlyph(page);
            page.setDrawBorder(true);
            page.setBorderColor(Config.PDF_BORDER_COLOR);
            page.setCursorInsideHighlightColor(Config.PDF_BORDER_COLOR_CI);
        }
    }

}
