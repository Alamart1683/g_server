package g_server.g_server.application.service.documents;

import g_server.g_server.application.config.jwt.JwtProvider;
import g_server.g_server.application.entity.documents.DocumentKind;
import g_server.g_server.application.entity.documents.DocumentType;
import g_server.g_server.application.entity.forms.DocumentForm;
import g_server.g_server.application.entity.users.Users;
import g_server.g_server.application.repository.documents.DocumentKindRepository;
import g_server.g_server.application.repository.documents.DocumentTypeRepository;
import g_server.g_server.application.repository.users.UsersRepository;
import g_server.g_server.application.service.documents.crud.DocumentVersionService;
import g_server.g_server.application.service.documents.crud.ViewRightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentUploadService {
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private DocumentTypeRepository documentTypeRepository;

    @Autowired
    private DocumentKindRepository documentKindRepository;

    @Autowired
    private DocumentVersionService documentVersionService;

    @Autowired
    private ViewRightsService viewRightsService;

    public List<String> UploadDocument(DocumentForm documentForm) {
        List<String> messagesList = new ArrayList<String>() {
        };
        // Определим айди научного руководителя
        Integer creator_id = getCreatorId(documentForm.getToken());
        if (creator_id == null)
            messagesList.add("Пользователь не найден, загрузить файл невозможно");
        // Определим айди типа документа
        Integer type_id = getTypeId(documentForm.getDocumentFormType());
        if (type_id == null)
            messagesList.add("Указан несуществующий тип документа");
        // Определим айди вида документа
        Integer kind_id = getKindId(documentForm.getDocumentFormKind());
        if (kind_id == null)
            messagesList.add("Указан несуществующий вид докумета");
        // Проверим корректное разрешение файла
        if (!checkFileExtension(documentForm.getFile()))
            messagesList.add("Попытка загрузить документ с некорректным разрешением");
        // После этого разместим файл на сервере
        if (messagesList.size() == 0) {
            // Создание директории версий файла
            File documentDirectory = new File( "src" + File.separator + "main" + File.separator + "resources" + File.separator +
                    "users_documents" + File.separator + creator_id + " document " + documentForm.getFile().getOriginalFilename() + "versions");
            if (!documentDirectory.exists()) {
                documentDirectory.mkdir();
            }
            else {
                messagesList.add("Файл с таким именем уже существует");
            }
            if (messagesList.size() == 0) {

            }
            else {
                return messagesList;
            }
            messagesList.add("Документ был успешно загружен");
        }
        else {
            return messagesList;
        }
        return messagesList;
    }

    // Необходимо получить id пользователя-создателя документа из токена
    public Integer getCreatorId(String token) {
        String email = jwtProvider.getEmailFromToken(token);
        Users user = usersRepository.findByEmail(email);
        if (user != null) {
            return user.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо получить id типа документа
    public Integer getTypeId(String type) {
        DocumentType documentType = documentTypeRepository.getDocumentTypeByType(type);
        if (documentType != null) {
            return documentType.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо получить id вида документа
    public Integer getKindId(String kind) {
        DocumentKind documentKind = documentKindRepository.getDocumentKindByKind(kind);
        if (documentKind != null) {
            return documentKind.getId();
        }
        else {
            return null;
        }
    }

    // Необходимо опеределить корректность расширения файла
    public boolean checkFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
            if (extension.equals("docx") || extension.equals("pdf") || extension.equals("doc") ||
                extension.equals("txt") || extension.equals("rft")) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}