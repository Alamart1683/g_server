package g_server.g_server.application.service.documents.text_processor;

import com.aspose.words.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class Splitter {
    public Splitter(LayoutCollector collector) throws Exception {
        mPageNumberFinder = new PageNumberFinder(collector);
        mPageNumberFinder.SplitNodesAcrossPages();
    }
    public Document getDocumentOfPage(int pageIndex) throws Exception {
        return getDocumentOfPageRange(pageIndex, pageIndex);
    }
    public Document getDocumentOfPageRange(int startIndex, int endIndex) throws Exception {
        Document result = (Document) getDocument().deepClone(false);
        for (Section section : (Iterable<Section>) mPageNumberFinder.RetrieveAllNodesOnPages(startIndex, endIndex, NodeType.SECTION))
            result.appendChild(result.importNode(section, true));
        return result;
    }
    private Document getDocument() {
        return mPageNumberFinder.getDocument();
    }
    private PageNumberFinder mPageNumberFinder;
    public String getDocText(Document document) {
        String text = "";
        for(Run run: (Iterable<? extends Run>) document.getChildNodes(NodeType.RUN, true)) {
           text = text + run.getText();
        }
        return text;
    }
}

class PageNumberFinder {
    public PageNumberFinder(LayoutCollector collector) {
        mCollector = collector;
    }

    public int GetPage(Node node) throws Exception {
        if (mNodeStartPageLookup.containsKey(node))
            return (Integer) mNodeStartPageLookup.get(node);
        return mCollector.getStartPageIndex(node);
    }

    public int GetPageEnd(Node node) throws Exception {
        if (mNodeEndPageLookup.containsKey(node))
            return (Integer) mNodeEndPageLookup.get(node);
        return mCollector.getEndPageIndex(node);
    }

    public int PageSpan(Node node) throws Exception {
        return GetPageEnd(node) - GetPage(node) + 1;
    }

    public ArrayList RetrieveAllNodesOnPages(int startPage, int endPage, int nodeType) throws Exception {
        if (startPage < 1 || startPage > getDocument().getPageCount())
            throw new Exception("startPage");
        if (endPage < 1 || endPage > getDocument().getPageCount() || endPage < startPage)
            throw new Exception("endPage");
        CheckPageListsPopulated();
        ArrayList pageNodes = new ArrayList();
        for (int page = startPage; page <= endPage; page++) {
            if (!mReversePageLookup.containsKey(page))
                continue;
            for (Node node : (Iterable<Node>) (ArrayList) mReversePageLookup.get(page)) {
                if (node.getParentNode() != null && ((nodeType == NodeType.ANY) || (nodeType == node.getNodeType())) && !pageNodes.contains(node))
                    pageNodes.add(node);
            }
        }
        return pageNodes;
    }

    public void SplitNodesAcrossPages() throws Exception {
        getDocument().accept(new SectionSplitter(this));
    }

    public Document getDocument() {
        return mCollector.getDocument();
    }

    void AddPageNumbersForNode(Node node, int startPage, int endPage) {
        if (startPage > 0)
            mNodeStartPageLookup.put(node, startPage);
        if (endPage > 0)
            mNodeEndPageLookup.put(node, endPage);
    }

    private void CheckPageListsPopulated() throws Exception {
        if (mReversePageLookup != null)
            return;
        mReversePageLookup = new Hashtable();
        for (Node node : (Iterable<Node>) getDocument().getChildNodes(NodeType.ANY, true)) {
            if (IsHeaderFooterType(node))
                continue;
            int startPage = GetPage(node);
            int endPage = GetPageEnd(node);
            for (int page = startPage; page <= endPage; page++) {
                if (!mReversePageLookup.containsKey(page))
                    mReversePageLookup.put(page, new ArrayList());
                ((ArrayList) mReversePageLookup.get(page)).add(node);
            }
        }
    }

    private static boolean IsHeaderFooterType(Node node) {
        return node.getNodeType() == NodeType.HEADER_FOOTER || node.getAncestor(NodeType.HEADER_FOOTER) != null;
    }
    private Hashtable mNodeStartPageLookup = new Hashtable();
    private Hashtable mNodeEndPageLookup = new Hashtable();
    private Hashtable mReversePageLookup;
    private LayoutCollector mCollector;
}

class SectionSplitter extends DocumentVisitor {
    public SectionSplitter(PageNumberFinder pageNumberFinder) {
        mPageNumberFinder = pageNumberFinder;
    }
    public int visitParagraphStart(Paragraph paragraph) throws Exception {
        if (paragraph.isListItem()) {
            List paraList = paragraph.getListFormat().getList();
            ListLevel currentLevel = paragraph.getListFormat().getListLevel();
            int currentListLevelNumber = paragraph.getListFormat().getListLevelNumber();
            for (int i = currentListLevelNumber + 1; i < paraList.getListLevels().getCount(); i++) {
                ListLevel paraLevel = paraList.getListLevels().get(i);
                if (paraLevel.getRestartAfterLevel() >= currentListLevelNumber) {
                    mListLevelToListNumberLookup.put(paraLevel, paraLevel.getStartAt());
                }
            }
            if (ContainsListLevelAndPageChanged(paragraph)) {
                List copyList = paragraph.getDocument().getLists().addCopy(paraList);
                mListLevelToListNumberLookup.put(currentLevel, paragraph.getListLabel().getLabelValue());
                for (int i = 0; i < paraList.getListLevels().getCount(); i++) {
                    ListLevel paraLevel = paraList.getListLevels().get(i);
                    if (mListLevelToListNumberLookup.containsKey(paraLevel))
                        copyList.getListLevels().get(i).setStartAt((Integer) mListLevelToListNumberLookup.get(paraLevel));
                }
                mListToReplacementListLookup.put(paraList, copyList);
            }
            if (mListToReplacementListLookup.containsKey(paraList)) {
                paragraph.getListFormat().setList((List) mListToReplacementListLookup.get(paraList));
                paragraph.getListFormat().setListLevelNumber(paragraph.getListFormat().getListLevelNumber() + 0);
            }
            mListLevelToPageLookup.put(currentLevel, mPageNumberFinder.GetPage(paragraph));
            mListLevelToListNumberLookup.put(currentLevel, paragraph.getListLabel().getLabelValue());
        }
        Section prevSection = (Section) paragraph.getParentSection().getPreviousSibling();
        Paragraph prevBodyPara = null;
        if (paragraph.getPreviousSibling() != null && paragraph.getPreviousSibling().getNodeType() == NodeType.PARAGRAPH)
            prevBodyPara = (Paragraph) paragraph.getPreviousSibling();
        Paragraph prevSectionPara = prevSection != null && paragraph == paragraph.getParentSection().getBody().getFirstChild() ? prevSection.getBody().getLastParagraph() : null;
        Paragraph prevParagraph = prevBodyPara != null ? prevBodyPara : prevSectionPara;
        if (paragraph.isEndOfSection() && !paragraph.hasChildNodes())
            paragraph.remove();
        if (prevParagraph != null) {
            if (mPageNumberFinder.GetPage(paragraph) != mPageNumberFinder.GetPageEnd(prevParagraph)) {
                if (paragraph.isListItem() && prevParagraph.isListItem() && !prevParagraph.isEndOfSection())
                    prevParagraph.getParagraphFormat().setSpaceAfter(0);
                else if (prevParagraph.getParagraphFormat().getStyleName() == paragraph.getParagraphFormat().getStyleName() && paragraph.getParagraphFormat().getNoSpaceBetweenParagraphsOfSameStyle())
                    paragraph.getParagraphFormat().setSpaceBefore(0);
                else if (paragraph.getParagraphFormat().getPageBreakBefore() || (prevParagraph.isEndOfSection() && prevSection.getPageSetup().getSectionStart() != SectionStart.NEW_COLUMN))
                    paragraph.getParagraphFormat().setSpaceBefore(Math.max(paragraph.getParagraphFormat().getSpaceBefore() - prevParagraph.getParagraphFormat().getSpaceAfter(), 0));
                else
                    paragraph.getParagraphFormat().setSpaceBefore(0);
            }
        }
        return VisitorAction.CONTINUE;
    }

    public int visitSectionStart(Section section) throws Exception {
        mSectionCount++;
        Section previousSection = (Section) section.getPreviousSibling();
        if (previousSection != null) {
            if (!section.getPageSetup().getRestartPageNumbering()) {
                section.getPageSetup().setRestartPageNumbering(true);
                section.getPageSetup().setPageStartingNumber(previousSection.getPageSetup().getPageStartingNumber() + (int) mPageNumberFinder.PageSpan(previousSection));
            }
            for (HeaderFooter previousHeaderFooter : previousSection.getHeadersFooters()) {
                if (section.getHeadersFooters().getByHeaderFooterType(previousHeaderFooter.getHeaderFooterType()) == null) {
                    HeaderFooter newHeaderFooter = (HeaderFooter) previousSection.getHeadersFooters().getByHeaderFooterType(previousHeaderFooter.getHeaderFooterType()).deepClone(true);
                    section.getHeadersFooters().add(newHeaderFooter);
                }
            }
        }
        for (HeaderFooter headerFooter : section.getHeadersFooters()) {
            for (Field field : headerFooter.getRange().getFields()) {
                if (field.getType() == FieldType.FIELD_SECTION || field.getType() == FieldType.FIELD_SECTION_PAGES) {
                    field.setResult((field.getType() == FieldType.FIELD_SECTION) ? Integer.toString(mSectionCount) :
                            Integer.toString(mPageNumberFinder.PageSpan(section)));
                    field.isLocked(true);
                }
            }
        }
        for (Field field : section.getBody().getRange().getFields())
            field.isLocked(true);
        return VisitorAction.CONTINUE;
    }

    public int visitDocumentEnd(Document doc) throws Exception {
        doc.updateFields();
        for (HeaderFooter headerFooter : (Iterable<HeaderFooter>) doc.getChildNodes(NodeType.HEADER_FOOTER, true)) {
            for (Field field : headerFooter.getRange().getFields())
                field.isLocked(true);
        }
        return VisitorAction.CONTINUE;
    }

    public int visitSmartTagEnd(SmartTag smartTag) throws Exception {
        if (IsCompositeAcrossPage(smartTag))
            SplitComposite(smartTag);
        return VisitorAction.CONTINUE;
    }

    public int visitStructuredDocumentTagEnd(StructuredDocumentTag sdt) throws Exception {
        if (IsCompositeAcrossPage(sdt))
            SplitComposite(sdt);
        return VisitorAction.CONTINUE;
    }

    public int visitCellEnd(Cell cell) throws Exception {
        if (IsCompositeAcrossPage(cell))
            SplitComposite(cell);
        return VisitorAction.CONTINUE;
    }

    public int visitRowEnd(Row row) throws Exception {
        if (IsCompositeAcrossPage(row))
            SplitComposite(row);
        return VisitorAction.CONTINUE;
    }

    public int visitTableEnd(Table table) throws Exception {
        if (IsCompositeAcrossPage(table)) {
            Row[] rows = table.getRows().toArray();
            for (Table cloneTable : (Iterable<Table>) SplitComposite(table)) {
                for (Row row : rows) {
                    if (row.getRowFormat().getHeadingFormat())
                        cloneTable.prependChild(row.deepClone(true));
                }
            }
        }
        return VisitorAction.CONTINUE;
    }

    public int visitParagraphEnd(Paragraph paragraph) throws Exception {
        if (IsCompositeAcrossPage(paragraph)) {
            for (Paragraph clonePara : (Iterable<Paragraph>) SplitComposite(paragraph)) {
                if (paragraph.isListItem()) {
                    double textPosition = clonePara.getListFormat().getListLevel().getTextPosition();
                    clonePara.getListFormat().removeNumbers();
                    clonePara.getParagraphFormat().setLeftIndent(textPosition);
                }
                clonePara.getParagraphFormat().setSpaceBefore(0);
                paragraph.getParagraphFormat().setSpaceAfter(0);
            }
        }
        return VisitorAction.CONTINUE;
    }

    public int visitSectionEnd(Section section) throws Exception {
        if (IsCompositeAcrossPage(section)) {
            for (FieldStart start : (Iterable<FieldStart>) section.getChildNodes(NodeType.FIELD_START, true)) {
                if (start.getFieldType() == FieldType.FIELD_TOC) {
                    Field field = start.getField();
                    Node node = field.getSeparator();
                    while ((node = node.nextPreOrder(section)) != field.getEnd())
                        if (node.getNodeType() == NodeType.RUN)
                            ((Run) node).getFont().clearFormatting();
                }
            }
            for (Section cloneSection : (Iterable<Section>) SplitComposite(section)) {
                cloneSection.getPageSetup().setSectionStart(SectionStart.NEW_PAGE);
                cloneSection.getPageSetup().setRestartPageNumbering(true);
                cloneSection.getPageSetup().setPageStartingNumber(section.getPageSetup().getPageStartingNumber() + (section.getDocument().indexOf(cloneSection) - section.getDocument().indexOf(section)));
                cloneSection.getPageSetup().setDifferentFirstPageHeaderFooter(false);

                RemovePageBreaksFromParagraph(cloneSection.getBody().getLastParagraph());
            }
            RemovePageBreaksFromParagraph(section.getBody().getLastParagraph());
            mPageNumberFinder.AddPageNumbersForNode(section.getBody(), mPageNumberFinder.GetPage(section), mPageNumberFinder.GetPageEnd(section));
        }

        return VisitorAction.CONTINUE;
    }

    private boolean IsCompositeAcrossPage(CompositeNode composite) throws Exception {
        return (mPageNumberFinder.PageSpan(composite) > 1);
    }

    private boolean ContainsListLevelAndPageChanged(Paragraph para) throws Exception {
        return mListLevelToPageLookup.containsKey(para.getListFormat().getListLevel()) && (Integer) mListLevelToPageLookup.get(para.getListFormat().getListLevel()) != mPageNumberFinder.GetPage(para);
    }

    private void RemovePageBreaksFromParagraph(Paragraph para) throws Exception {
        if (para != null) {
            for (Run run : para.getRuns())
                run.setText(run.getText().replace(ControlChar.PAGE_BREAK, ""));
        }
    }

    private ArrayList SplitComposite(CompositeNode composite) throws Exception {
        ArrayList splitNodes = new ArrayList();
        for (Node splitNode : (Iterable<Node>) FindChildSplitPositions(composite))
            splitNodes.add(SplitCompositeAtNode(composite, splitNode));
        return splitNodes;
    }

    private ArrayList FindChildSplitPositions(CompositeNode node) throws Exception {
        ArrayList splitList = new ArrayList();
        int startingPage = mPageNumberFinder.GetPage(node);
        Node[] childNodes = node.getNodeType() == NodeType.SECTION ?
                ((Section) node).getBody().getChildNodes().toArray() : node.getChildNodes().toArray();
        for (Node childNode : childNodes) {
            int pageNum = mPageNumberFinder.GetPage(childNode);
            if (pageNum > startingPage) {
                splitList.add(childNode);
                startingPage = pageNum;
            }
            if (mPageNumberFinder.PageSpan(childNode) > 1)
                mPageNumberFinder.AddPageNumbersForNode(childNode, pageNum, pageNum);
        }
        Collections.reverse(splitList);
        return splitList;
    }

    private CompositeNode SplitCompositeAtNode(CompositeNode baseNode, Node targetNode) throws Exception {
        CompositeNode cloneNode = (CompositeNode) baseNode.deepClone(false);
        Node node = targetNode;
        int currentPageNum = mPageNumberFinder.GetPage(baseNode);
        if (baseNode.getNodeType() != NodeType.ROW) {
            CompositeNode composite = cloneNode;
            if (baseNode.getNodeType() == NodeType.SECTION) {
                cloneNode = (CompositeNode) baseNode.deepClone(true);
                Section section = (Section) cloneNode;
                section.getBody().removeAllChildren();
                composite = section.getBody();
            }
            while (node != null) {
                Node nextNode = node.getNextSibling();
                composite.appendChild(node);
                node = nextNode;
            }
        } else {
            int targetPageNum = mPageNumberFinder.GetPage(targetNode);
            Node[] childNodes = baseNode.getChildNodes().toArray();
            for (Node childNode : childNodes) {
                int pageNum = mPageNumberFinder.GetPage(childNode);
                if (pageNum == targetPageNum) {
                    cloneNode.getLastChild().remove();
                    cloneNode.appendChild(childNode);
                } else if (pageNum == currentPageNum) {
                    cloneNode.appendChild(childNode.deepClone(false));
                    if (cloneNode.getLastChild().getNodeType() != NodeType.CELL)
                        ((CompositeNode) cloneNode.getLastChild()).appendChild(((CompositeNode) childNode).getFirstChild().deepClone(false));
                }
            }
        }
        baseNode.getParentNode().insertAfter(cloneNode, baseNode);
        int currentEndPageNum = mPageNumberFinder.GetPageEnd(baseNode);
        mPageNumberFinder.AddPageNumbersForNode(baseNode, currentPageNum, currentEndPageNum - 1);
        mPageNumberFinder.AddPageNumbersForNode(cloneNode, currentEndPageNum, currentEndPageNum);
        for (Node childNode : (Iterable<Node>) cloneNode.getChildNodes(NodeType.ANY, true))
            mPageNumberFinder.AddPageNumbersForNode(childNode, currentEndPageNum, currentEndPageNum);
        return cloneNode;
    }
    private Hashtable mListLevelToListNumberLookup = new Hashtable();
    private Hashtable mListToReplacementListLookup = new Hashtable();
    private Hashtable mListLevelToPageLookup = new Hashtable();
    private PageNumberFinder mPageNumberFinder;
    private int mSectionCount;
}