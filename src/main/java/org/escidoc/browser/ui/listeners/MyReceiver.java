package org.escidoc.browser.ui.listeners;

import java.io.IOException;
import java.io.OutputStream;

import com.vaadin.ui.Upload.Receiver;

public class MyReceiver implements Receiver {
    private String fileName;

    private String mtype;

    private boolean sleep;

    private int total = 0;

    final StringBuffer filecontent = new StringBuffer();

    @Override
    public OutputStream receiveUpload(String filename, String mimetype) {
        fileName = filename;
        mtype = mimetype;

        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                total++;
                filecontent.append((char) b);
                if (sleep && total % 10000 == 0) {
                    try {

                        Thread.sleep(100);
                        System.out.println(b);
                    }
                    catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public String getFileContent() {
        return filecontent.toString();
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mtype;
    }

    public void setSlow(boolean value) {
        sleep = value;
    }

}
