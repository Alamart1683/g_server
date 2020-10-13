package g_server.g_server.application.service.documents;

import g_server.g_server.application.service.documents.TextReplace.TextReplacer;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.*;

public class WordReplaceService {
    private XWPFDocument document;
    private TextReplacer replacer;

    public WordReplaceService(File docxFile) throws IOException {
        InputStream inputStream = new FileInputStream(docxFile);
        init(new XWPFDocument(inputStream));
    }

    public WordReplaceService(XWPFDocument xwpfDoc) {
        init(xwpfDoc);
    }

    private void init(XWPFDocument xwpfDoc) {
        if (xwpfDoc == null) throw new NullPointerException();
        document = xwpfDoc;
        replacer = new TextReplacer();
    }

    public void replaceWordsInText(String bookmark, String replacement) {
        replacer.replaceInText(document, bookmark, replacement);
    }

    public void replaceWordsInTables(String bookmark, String replacement) {
        replacer.replaceInTable(document, bookmark, replacement);
    }

    public File saveAndGetModdedFile(String path) throws Exception {
        File file = new File(path);
        return saveToFile(file);
    }

    public File saveAndGetModdedFile(File file) throws Exception {
        return saveToFile(file);
    }

    public XWPFDocument getModdedXWPFDoc() {
        return document;
    }

    private File saveToFile(File file) throws Exception {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file, false);
            document.write(out);
            document.close();
            return file;
        } catch (Exception e) {
            throw e;
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}