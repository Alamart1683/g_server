package g_server.g_server.application.controller.documents;

import g_server.g_server.application.entity.forms.DocumentForm;
import g_server.g_server.application.entity.forms.DocumentVersionForm;
import g_server.g_server.application.service.documents.DocumentUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class DocumentUploadController {
    @Autowired
    private DocumentUploadService documentUploadService;

    @GetMapping("/scientific_advisor/document/upload")
    public String PreparingUploadDocument(Model model) {
        DocumentForm documentForm = new DocumentForm();
        model.addAttribute("documentForm", documentForm);
        return "documentForm";
    }

    @PostMapping("/scientific_advisor/document/upload")
    public List<String> UploadDocument(@ModelAttribute("documentForm") @Validated DocumentForm documentForm) {
        return documentUploadService.uploadDocument(documentForm);
    }

    @GetMapping("/scientific_advisor/document/upload/version")
    public String PreparingUploadDocumentVersion(Model model) {
        DocumentVersionForm documentVersionForm = new DocumentVersionForm();
        model.addAttribute("documentVersionForm", documentVersionForm);
        return "documentVersionForm";
    }

    @PostMapping("/scientific_advisor/document/upload/version")
    public List<String> UploadDocumentVersion(@ModelAttribute("documentVersionForm")
        @Validated DocumentVersionForm documentVersionForm ) {
        return documentUploadService.uploadDocumentVersion(documentVersionForm);
    }
}