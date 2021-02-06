package g_server.g_server.application.entity.view;

import java.util.ArrayList;
import java.util.List;

public class AcademicRecordDynamicForm {
    private List<String> columnsHeaders = new ArrayList<>();
    private List<List<String>> rowsContent = new ArrayList<>();

    AcademicRecordDynamicForm() {

    }

    AcademicRecordDynamicForm(List<String> columnsHeaders, List<List<String>> rowsContent) {
        this.columnsHeaders = columnsHeaders;
        this.rowsContent = rowsContent;
    }

    public List<String> getColumnsHeaders() {
        return columnsHeaders;
    }

    public void setColumnsHeaders(List<String> columnsHeaders) {
        this.columnsHeaders = columnsHeaders;
    }

    public List<List<String>> getRowsContent() {
        return rowsContent;
    }

    public void setRowsContent(List<List<String>> rowsContent) {
        this.rowsContent = rowsContent;
    }
}