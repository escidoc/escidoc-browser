package org.escidoc.browser.ui.listeners;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.ui.Upload.Receiver;

@SuppressWarnings("serial")
public class MetadataFileReceiver implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataFileReceiver.class);

    private String fileName;

    private String mimeType;

    private boolean sleep;

    private int total = 0;

    private final StringBuffer filecontent = new StringBuffer();

    private boolean isWellFormed;

    @Override
    public OutputStream receiveUpload(final String fileName, final String mimeType) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        return new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                total++;
                filecontent.append((char) b);
                if (sleep && total % 10000 == 0) {
                    try {
                        Thread.sleep(100);
                        LOG.debug(Integer.toString(b));
                    }
                    catch (final InterruptedException e) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
        };
    }

    public String getFileContent() {
        return filecontent.toString();
    }

    public void clearBuffer() {
        filecontent.delete(0, filecontent.length());
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setSlow(final boolean value) {
        sleep = value;
    }

    public void setWellFormed(final boolean isWellFormed) {
        this.isWellFormed = isWellFormed;
    }

    public boolean isWellFormed() {
        return isWellFormed;
    }
}