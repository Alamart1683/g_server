package g_server.g_server.application.service.documents.TextReplace;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class DocxTextReplaceService {
    private String searchValue;
    private String replacement;

    public DocxTextReplaceService() { }

    public DocxTextReplaceService(String searchValue, String replacement) {
        this.searchValue = searchValue;
        this.replacement = replacement;
    }

    public void replace(XWPFDocument document) {
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph xwpfParagraph : paragraphs) {
            replace(xwpfParagraph);
        }
    }

    private void replace(XWPFParagraph paragraph) {
        if (hasReplaceableItem(paragraph.getText())) {
            String replacedText = StringUtils.replace(paragraph.getText(), searchValue, replacement);
            removeAllRuns(paragraph);
            insertReplacementRuns(paragraph, replacedText);
        }
    }

    private void insertReplacementRuns(XWPFParagraph paragraph, String replacedText) {
        String[] replacementTextSplitOnCarriageReturn = StringUtils.split(replacedText, "\n");
        for (int j = 0; j < replacementTextSplitOnCarriageReturn.length; j++) {
            String part = replacementTextSplitOnCarriageReturn[j];
            XWPFRun newRun = paragraph.insertNewRun(j);
            newRun.setText(part);
            if (j + 1 < replacementTextSplitOnCarriageReturn.length) {
                newRun.addCarriageReturn();
            }
        }
    }

    private void removeAllRuns(XWPFParagraph paragraph) {
        int size = paragraph.getRuns().size();
        for (int i = 0; i < size; i++) {
            paragraph.removeRun(0);
        }
    }

    private boolean hasReplaceableItem(String runText) {
        return StringUtils.contains(runText, searchValue);
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public XWPFDocument openDocument(String file) throws Exception {
        XWPFDocument document = null;
        InputStream is = new FileInputStream(file);
        document = new XWPFDocument(is);
        return document;
    }

    // Сохранить документ
    public void saveDocument(XWPFDocument doc, String file) {
        try (FileOutputStream out = new FileOutputStream(file)) {
            doc.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
