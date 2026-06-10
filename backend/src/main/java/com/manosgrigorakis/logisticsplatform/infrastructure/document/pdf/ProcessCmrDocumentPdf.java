package com.manosgrigorakis.logisticsplatform.infrastructure.document.pdf;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.lowagie.text.DocumentException;
import com.manosgrigorakis.logisticsplatform.common.exception.DocumentProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.io.IOException;


@Slf4j
public class ProcessCmrDocumentPdf {
    /**
     * Process the provided CMR document file to return the CMR number from the QR Code
     *
     * @param file The CMR document PDF converted to {@code byte[]}
     * @return The CMR number from the QR Code of the CMR document if exists
     * @throws DocumentProcessingException If the PDF cannot be processed or the PDF does not contain a valid QR Code
     */
    public static String decodeCmrDocumentQrCode(byte[] file) {
        try (PDDocument document = Loader.loadPDF(file)) {
            for (PDPage page : document.getPages()) {
                // Get images
                PDResources resources = page.getResources();
                Iterable<COSName> iterable = resources.getXObjectNames();

                for (COSName cosName : iterable) {
                    PDXObject xObject = resources.getXObject(cosName);

                    // Validate image is a QR Code
                    if (xObject instanceof PDImageXObject image) {
                        try {
                            BufferedImage bufferedImage = image.getImage();
                            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
                            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                            // Decode
                            QRCodeReader reader = new QRCodeReader();
                            Result result = reader.decode(bitmap);

                            // Validate the result with the prefix
                            if (result != null && result.getText().startsWith("CMR-")) {
                                return result.getText();
                            }
                        } catch (NotFoundException ignored) {
                            // Current image is not a QR Code (fallback if the document contains multiple images)
                        }
                    }
                }
            }
        } catch (IOException | ChecksumException | FormatException ex) {
            log.error("Failed to process uploaded CMR document PDF", ex);
            throw new DocumentProcessingException("Unable to process CMR document PDF", "INVALID_DOCUMENT");
        }

        log.warn("Uploaded CMR document does not contain a valid QR Code");
        throw new DocumentProcessingException("The uploaded CMR document does not contain a valid QR Code",
                                              "MISSING_QR_CODE");
    }
}
