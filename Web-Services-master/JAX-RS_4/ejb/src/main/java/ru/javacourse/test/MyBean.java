public class MyBean {

    private static final Map<String, String> STATES_STRATEGIES =
            Collections.unmodifiableMap(new HashMap<String, String>() {
                {
                    put("InitiatorTask", "Initiator");
                    put("RevisionTask", "Initiator");
                    put("AssignInitiatorTask", "AssignInitiator");
                    put("ChiefApprovalTask", "ChiefApproval");
                    put("ControlTask", "Control");
                    put("ErrorTask", "Error");
                    put("ExecutorTask", "Executor");
                    put("ExecutorWaitTask", "Guest");
                    put("RevisionTask", "Revision");
                    put("ZSPChiefTask", "ZSP");
                    put("ZSPWorkerTask", "ZSP");
                    put("ZSPChiefApprovingTask", "ZSP");
                    put("GBJuristChiefTask", "Jurist");
                    put("GBUristTask", "Jurist");
                    put("ReviseTask", "Revise");
                    put("FilialUristChiefTask", "Jurist");
                    put("FilialUristTask", "Jurist");
                    put("FilialUristApprovingTask", "Jurist");
                    put("SPChiefTask", "SPChief");
                    put("SPWorkerTask", "SPWorker");
                    put("PredefineAnswerTask", "PredefineAnswer");
                    put("FamiliarizationTask", "Familiarization");
                }
            });

    public MyBean() {
    }

    //Метод, срабатаывающий при нажатии на кнопку ок диалогового окна,
    //который нужен для добавления вложения
    public void addAttachDialogListener(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().name().equals("ok")) {
            createInfoLog("Start upload attachs");
            String fileComment = "";
            //Поля для комментария к вложению
            if (getAttachComment().getValue() != null) {
                fileComment = getAttachComment().getValue().toString();
            }
            // InputFile в котором лежит вложение
            if (getAttachInputFile().getValue() == null) {
                addFacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Необходимо выбрать файл!");
                return;
            }
            UploadedFile uploadedFile =
                    (UploadedFile) getAttachInputFile().getValue();

            // Берем значение из FacesContext в котором мы можем замапить данные
            Long maxSize =
                    Long.parseLong(getInitParam("kz.eubank.UPLOAD_MAX_DISK_SPACE"));
            if (uploadedFile.getLength() > maxSize) {
                addFacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Невозможно загрузить файл. " +
                                "Общий максимально допустимый размер вложений: " +
                                convertBytes(maxSize, false));
                return;
            }
            createInfoLog("Upoload file to UCM start");


            if (attachmentType.equals("PI")) {
                FileUploader.uploadFileToUCM(getRequestIDNumber(),
                        uploadedFile,
                        uploadedFile.getContentType(),
                        fileComment, attachmentType,
                        productIdFU);
                refreshProductInfo();
                addPartialTarget(productAttachsTable);
                addPartialTarget(productsTable);

            } else {
                FileUploader.uploadFileToUCM(getRequestIDNumber(),
                        uploadedFile,
                        uploadedFile.getContentType(),
                        fileComment, attachmentType);
                if (attachmentType.equals("AF")) {
                    refreshAnswerFilesTable();
                    addPartialTarget(answerFilesTable);
                } else {
                    System.out.println("Nalsur ScanFile and type is " +
                            attachmentType);
                    refreshScanFilesTable();
                    addPartialTarget(scanFiles);
                }
            }
            getAttachInputFile().setValue(null);
            getAttachComment().setValue("");
            attachmentType = null;
            createInfoLog("Nalsur Upoload file to UCM end");
        }
    }

    //Метод для загрузки файла в Oracle UCM
    public static void uploadFileToUCM(Number applicationId, UploadedFile file,
                                       String docType, String comment,
                                       String attachType, String productId) {

        System.out.println("Nalsur uploadFileToUCM Start");
        UploadedFileWithVariableName uploadedFileWithVariableName =
                new UploadedFileWithVariableName();
        uploadedFileWithVariableName.setFile(file);
        uploadedFileWithVariableName.setFilename(file.getFilename());
        String documentId = null;
        try {
            String initiator = el("#{bindings.initiator}");
            IdcClient idcClient = getUCMConnection();
            IdcContext userContext = getUCMContext();
            DataBinder serviceBinder = idcClient.createBinder();
            Map<String, String> attrs = new HashMap<String, String>();

            attrs.put("IdcService", "CHECKIN_NEW");
            attrs.put("dDocType", "DOCUMENT");
            attrs.put("xCollectionID",
                    getReqFolderId(idcClient, userContext, applicationId));

            attrs.put("dDocTitle",
                    uploadedFileWithVariableName.getOriginalFilename());
            attrs.put("dDocAuthor", initiator);
            attrs.put("dDocCreator", initiator);
            attrs.put("dDocLastModifier", initiator);
            attrs.put("dFormat", file.getContentType());
            attrs.put("dSecurityGroup", "Public");

            ServiceResponse response;

            response = checkinFile(attrs, file);

            DataBinder responseBinder = response.getResponseAsBinder();
            DataObject localData = responseBinder.getLocalData();
            attrs.put("dID", localData.get("dID"));
            System.out.println("Nalsur uploadFileToUCM End");
            writeFileToDB(applicationId, attrs, file, comment, attachType,
                    productId);
        } catch (ServiceException serviceException) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    serviceException.getMessage());
            serviceException.printStackTrace();
        } catch (IdcClientException ice) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    ice.getMessage());
            ice.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Метод для записи данных о файле с UCM в СУБД
    public static void writeFileToDB(Number requestId,
                                     Map<String, String> attrs,
                                     UploadedFile file, String comment,
                                     String attachType, String productId) {
        DCBindingContainer lBindingContainer =
                (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding attachmentIterator =
                lBindingContainer.findIteratorBinding("AttachmentsView1Iterator");
        ViewObjectImpl attachmentViewObject =
                (ViewObjectImpl) attachmentIterator.getViewObject();
        Row row = attachmentViewObject.createRow();
        row.setAttribute("NrequestId", requestId);
        row.setAttribute("Vname", file.getFilename());
        row.setAttribute("Vtype", file.getContentType());
        System.out.println("Nalsur Write toDB Login: " +
                el("#{bindings.initiatorLogin}"));
        row.setAttribute("Vcreatorlogin", el("#{bindings.initiatorLogin}"));
        System.out.println("Nalsur Write toDB FullName: " +
                el("#{bindings.InitiatorFio}"));
        row.setAttribute("Vcreatorname", el("#{bindings.InitiatorFio}"));

        row.setAttribute("Nsize", file.getLength());
        row.setAttribute("Vpath", attrs.get("xCollectionID"));

        row.setAttribute("Did", attrs.get("dID"));
        row.setAttribute("Vdescription", comment);
        row.setAttribute("VattachmentType", attachType);
        row.setAttribute("Nproductid", productId);

        attachmentViewObject.insertRow(row);
        executeOperation("Commit");
    }

    //Метод, который вызывается при каждом открытии/перезагрузке/обновлении страницы
    public void beforePhaseListener(PhaseEvent phaseEvent) {

        if (phaseEvent.getPhaseId().equals(PhaseId.RENDER_RESPONSE) &&
                !pageRendered) {
            pageRendered = true;
            createInfoLog("NALSUR CARDINDEX2 OPEN");
            createInfoLog("beforePhaseListener");
            refreshScanFilesTable();
            refreshAnswerFilesTable();
            refreshRequestDBases();
            refreshClientsTable();
            getRequestEncumbrances();
            refreshConclusionsTable();
            refreshEncumbrancesTable();
            currentUser = el("#{securityContext.userName}");
            //Для каждого хуман таска создается свой класс стратегия
            MainStrategy strategy = new MainStrategy();
            strategy.applySettings();
        }

    }

    //Метод для установки операции/действия как ауткам хуман таска
    //ActionListener, который исполняется перед Action'ом
    public void setOperationActionListener(ActionEvent actionEvent) {
        //Атрибуты JSF, которые привязаны к каждой кнопке
        //Атрибут, хранящий название операции или действия
        String operationBinding =
                actionEvent.getComponent().getAttributes().get("DC_OPERATION_BINDING").toString();
        //Записываем в PayLoad значение операции
        setAttrValue("outcomeClicked", operationBinding);
        //Нужно ли подтверждение
        String confirmed =
                actionEvent.getComponent().getAttributes().get("DC_OPERATION_CONFIRMED").toString();
        //Пропуск валидации
        String skipValidation =
                actionEvent.getComponent().getAttributes().get("DC_SKIP_VALIDATION").toString();


        if (confirmed.equals("true")) {
            setAttrValue("isSubmitConfirmed", true);
        } else {
            setAttrValue("isSubmitConfirmed", false);
        }
        if (skipValidation.equals("true"))
            setAttrValue("skipValidation", true);
        else
            setAttrValue("skipValidation", false);
        String operation =
                operationBinding.substring(operationBinding.lastIndexOf(".") + 1);


        if (!(Boolean) getAttrValue("isSubmitConfirmed")) {
            createInfoLog("Nalsur isSubmitConfirmed is false");
            //Берем текст из бандл файла .properties
            String confirmMessage = getBundleText(operation + "_CONFIRM_MES");
            if (!confirmMessage.isEmpty()) {
                if (operation.equals("SEND_TO_REVISION") ||
                        operation.equals("RETURN_CONCLUSION") ||
                        operation.equals("CANCEL") ||
                        operation.equals("END_PROCESS") ||
                        operation.equals("FULFILL") ||
                        operation.equals("REJECT")) {
                    //Делаем комментарий обязательным для заполнения
                    getCommentText().setRequired(true);
                } else {
                    getCommentText().setRequired(false);
                }
                setAttrValue("skipConfirmation", false);
                getConfirmSubmitMessage().setMessage(confirmMessage);
                getСonfirmSubmitPopup().show(new RichPopup.PopupHints());
                return;
            }
        } else {
            setAttrValue("skipConfirmation", true);
        }
        Map map =
                FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        InvokeActionBean invokeActionBean =
                (oracle.bpel.services.workflow.worklist.adf.InvokeActionBean) map.get("invokeActionBean");
        if (operationBinding.equals("bindings.update") {
            invokeActionBean.setAction(actionEvent);
        }else{
            invokeActionBean.setOperation(actionEvent);
        }
    }

    //Метод, который уже исполняет установленное действие в ауткаме
    //Action
    public String invokeOperationAction() {
        if (!(Boolean) getAttrValue("isSubmitConfirmed") &&
                !(Boolean) getAttrValue("skipConfirmation"))
            return null;

        String operationBinding = getAttrStringValue("outcomeClicked");
        String operation =
                operationBinding.substring(operationBinding.lastIndexOf(".") + 1);
        createInfoLog("invokeOperation start: " + operation);
        if (!(Boolean) getAttrValue("skipValidation")) {
            if (operation.equals("SEND_TO_REVISION") ||
                    operation.equals("RETURN_CONCLUSION") ||
                    operation.equals("CANCEL") ||
                    operation.equals("END_PROCESS") ||
                    operation.equals("FULFILL") ||
                    operation.equals("WITHDRAW_APPROVAL")) {
            } else {
                //Получаем текущий класс стратегию
                AccessStrategy viewStrategy = getViewStrategy();
                Boolean dataIsValid = viewStrategy.validateData();

                if (!dataIsValid) {
                    if (!viewStrategy.getErrorsList().isEmpty()) {
                        showErrorsList(viewStrategy.getErrorsList());
                    }

                    setAttrValue("isSubmitConfirmed", false);
                    getСonfirmSubmitPopup().hide();
                    return null;
                }
            }
        }

        if (!objToString(getCommentText().getValue()).isEmpty()) {
            //Записываем коммент в пйэлоад
            executeOperation("CreateInsertComment");
            setAttrValue("comment", objToString(getCommentText().getValue()));
            setAttrValue("commentScope",
                    objToString(getCommentText().getValue()));
        }

        //Коммитим изменения
        executeOperation("Commit");

        Map map =
                FacesContext.getCurrentInstance().getExternalContext().getRequestMap();
        InvokeActionBean invokeActionBean =
                (oracle.bpel.services.workflow.worklist.adf.InvokeActionBean) map.get("invokeActionBean");

        if (operation.equals("update")) {
            return invokeActionBean.action();
        } else {
            return invokeActionBean.invokeOperation();
        }

    }


    //Метод для получения текущей стратегии
    private AccessStrategy getViewStrategy() {
        AccessStrategy strategy = null;

        String stage = getStage();
        createInfoLog("HumanTask: " + stage);
        try {
            Class<AccessStrategy> strategyClass;

            createInfoLog("Strategy Class: " +
                    this.getClass().getCanonicalName() + "$" +
                    STATES_STRATEGIES.get(stage) + "Strategy");

            strategyClass =
                    (Class<AccessStrategy>) Class.forName(this.getClass().getCanonicalName() +
                            "$" +
                            STATES_STRATEGIES.get(stage) +
                            "Strategy");
            Constructor<AccessStrategy> constructor =
                    strategyClass.getDeclaredConstructor(CardIndex2BeanClass.class);
            strategy = this.createNew(constructor);
        } catch (ClassNotFoundException e) {
            createInfoLog("Strategy class not found: " +
                    STATES_STRATEGIES.get(stage));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (strategy == null) {
                createInfoLog("Strategy is null");
                strategy = new GuestStrategy();
            }
        }
        createInfoLog("Current Strategy:" +
                strategy.getClass().getCanonicalName().substring(strategy.getClass().getCanonicalName().lastIndexOf(".") +
                        1));
        return strategy;
    }

    synchronized AccessStrategy createNew(Constructor<? extends AccessStrategy> constructor) throws Exception {
        return constructor.newInstance(this);
    }

    //ValueChangeListener при выборе файла

    public void fileExcelVCL(ValueChangeEvent valueChangeEvent) {
        RichInputFile inputFileComponent =
                (RichInputFile) valueChangeEvent.getComponent();
        UploadedFile newFile = (UploadedFile) valueChangeEvent.getNewValue();
        try {
            is = newFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ((!(newFile.getFilename().toLowerCase().endsWith("xls"))) &&
                (!(newFile.getFilename().toLowerCase().endsWith("xlsx")))) {
            FacesContext.getCurrentInstance().addMessage(inputFileComponent.getClientId(FacesContext.getCurrentInstance()),
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Поддерживаются только файлы MS Excel",
                            "Формат данного файла (" +
                                    newFile.getFilename() +
                                    ") не поддерживается."));
            inputFileComponent.resetValue();
            inputFileComponent.setValid(false);
        } else {
            getParsingDataToolbarButton().setDisabled(false);
            addPartialTarget(getParsingDataToolbarButton());
        }
    }

    //Метод для добавления клиентов из запроса
    public void addClients(ActionEvent actionEvent) {
        String clientType = "";
        List<String> productTypes = new ArrayList<>();
        //Получаем текущий биндинг
        DCBindingContainer lBindingContainer =
                (DCBindingContainer) BindingContext.getCurrent().getCurrentBindingsEntry();
        // Итаретор нашей вьюхи для таблицы с клиентами
        DCIteratorBinding clientIterator =
                lBindingContainer.findIteratorBinding("ClientsView1Iterator");
        //Вью
        ClientsViewImpl clientViewObject =
                (ClientsViewImpl) clientIterator.getViewObject();
        //Если пользователь в качестве источника информации выбрал файл эксель
        // то мы парсим файл эксель
        if (getSearchTypeSRB().getValue().toString().equals("excel")) {
            //Признак того, что у клиентов будет общий тип клиента ФЛ/ЮЛ/ФЛ или ЮЛ
            if (getCommonSearchSBC().getValue() == true) {
                if (getClientType().getValue() == null) {
                    addFacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Необходимо выбрать тип клиента!");
                    return;
                } else {
                    clientType =
                            getClientType().getValue().toString();
                }
                //Типы продукта, которые будут у клиента
                if (getInfType().getValue() == null) {
                    addFacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Необходимо выбрать тип продукта!");
                    return;
                } else {
                    productTypes =
                            (List<String>) getInfType().getValue();
                }
            }

            //Входящий поток данных берем из поля для хранения файла

            //is инициализируется уже при выборе файла
            try (InputStream inputStream = is) {
                //Мэп в котором храним id клиентов у которых не прошла валидация ИИН и ошибку
                Map<Number, String> errorsMap = new HashMap<Number, String>();

                if (getCommonSearchSBC().getValue() == true) {
                    //Парсим клиентов в документе эксель с общими параметрами клиентов
                    errorsMap =
                            parseExcelDocumentWithCommonCondition(inputStream,
                                    clientViewObject,
                                    getRequestIDNumber(),
                                    clientType,
                                    productTypes);
                    if (!errorsMap.isEmpty()) {
                        for (Map.Entry<Number, String> errorSet :
                                errorsMap.entrySet()) {
                            iinErrors.put(errorSet.getKey(),
                                    errorSet.getValue());
                        }

                    }
                } else {
                    //Парсим клиентов у которых тип клиента и типы продуктов указаны в файле эксель
                    errorsMap =
                            parseExcelDocumentWithSpecialConditions(inputStream,
                                    clientViewObject,
                                    getRequestIDNumber());

                    if (!errorsMap.isEmpty()) {
                        for (Map.Entry<Number, String> errorSet :
                                errorsMap.entrySet()) {
                            iinErrors.put(errorSet.getKey(),
                                    errorSet.getValue());
                        }
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            // В случае если нам дали портянку из мусора из которого нужно вытянуть ИИНы
        } else if (getSearchTypeSRB().getValue().toString().equals("iinField")) {

            if (getClientType().getValue() == null) {
                addFacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Необходимо выбрать тип клиента!");
                return;
            } else {
                clientType = getClientType().getValue().toString();
            }

            if (getInfType().getValue() == null) {
                addFacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Необходимо выбрать тип продукта!");
                return;
            } else {
                productTypes =
                        (List<String>) getInfType().getValue();
            }

            if (getClientsIIN().getValue() == null) {
                addFacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Необходимо ввести значение в поле ИИН/БИН!");
                return;
            }

            String clientsIINs = objToString(getClientsIIN().getValue());
            int count = 0;
            // С помощью регулярок забираем данные подходящие под условия:
            //123456789012
            //111 111 111 111
            //1111 1111 1111
            String pattern = "(\\b\\d{3}\\s\\d{3}\\s\\d{3}\\s\\d{3}\\b)|(\\b\\d{4}\\s\\d{4}\\s\\d{4}\\b)|\\b([0-9IiOoЧчБбЗз]){12}\\b";
            int patternCount = 0;
            LinkedHashSet iinSet = new LinkedHashSet<String>();

            Pattern r = Pattern.compile(pattern);

            // Now create matcher object.
            Matcher m = r.matcher(clientsIINs);

            while (m.find()) {
                String iin = m.group().replace(" ", "");
                iinSet.add(iin);
            }
            Iterator<String> itr = iinSet.iterator();
            while (itr.hasNext()) {
                String iin = itr.next();
                ClientsViewRowImpl row =
                        (ClientsViewRowImpl) clientViewObject.createRow();
                row.setAttribute("RequestId", getRequestIDNumber());
                row.setAttribute("Iin", iin);
                //Валидация ИИН
                String ErrorInIIN = ValidateIIN(iin);

                if (!ErrorInIIN.trim().isEmpty()) {
                    iinErrors.put(row.getId(), ErrorInIIN);
                }

                row.setClientType(clientType);
                for (String productType : productTypes) {
                    switch (productType) {
                        case "Открытые счета":
                            row.setOpenAccounts(new Number(1));
                            break;
                        case "Закрытые счета":
                            row.setCloseAccounts(new Number(1));
                            break;
                        case "Сейфовые ячейки":
                            row.setSafeBoxs(new Number(1));
                            break;
                        case "Наличие обременений на счете":
                            row.setEncumbrances(new Number(1));
                            break;
                        default:
                            break;
                    }

                }
                clientViewObject.insertRow(row);
                count++;

            }
            addFacesMessage(FacesMessage.SEVERITY_INFO,
                    "Найдено: " + count + " ИИН");
            createInfoLog("Nalsur End IIN Parsing");
        }
    }


    //Метод, который парсит клиентов в эксель. Параметры клиентов указаны в самом файле эксель
    //Использовалась библиотека Appache POI

    public static Map<Number, String> parseExcelDocumentWithSpecialConditions(InputStream is,
                                                                              ClientsViewImpl clientsVO,
                                                                              oracle.jbo.domain.Number requestId) {
        Map<Number, String> iinErrors = new HashMap<Number, String>();
        try {

            System.out.println("WorkbookFactory.class.getCanonicalName()" + WorkbookFactory.class.getCanonicalName());

            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Row> rowIter = sheet.rowIterator();
            int rowCounter = 0;
            while (rowIter.hasNext()) {
                System.out.println("Nalsur Row Counter Start:" + rowCounter);
                Row row = rowIter.next();
                if (rowCounter > 1) {
                    //   sheet.getRow(rowCounter);
                    System.out.println("");
                    if (!row.equals(null)) {
                        if (!((row.getCell(1).getCellType() ==
                                Cell.CELL_TYPE_BLANK) &&
                                row.getCell(2).getCellType() ==
                                        Cell.CELL_TYPE_BLANK)) {
                            ClientsViewRowImpl tableRow =
                                    (ClientsViewRowImpl) clientsVO.createRow();

                            tableRow.setRequestId(requestId);
                            Cell iinCell = row.getCell(0);
                            Cell fullNameCell = row.getCell(1);
                            Cell bdayCell = row.getCell(2);
                            Cell cTypeCell = row.getCell(3);
                            Cell onDate = row.getCell(4);
                            Cell openAccsCell = row.getCell(7);
                            Cell closeAccsCell = row.getCell(8);
                            Cell safeBoxesCell = row.getCell(9);
                            Cell encumbrancesCell = row.getCell(10);


                            if (!isEmpty(iinCell)) {
                                iinCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                long iin = (long) iinCell.getNumericCellValue();
                                String iinStr = String.valueOf(iin);
                                String error = ValidateIIN(iinStr);
                                if (!error.trim().isEmpty()) {
                                    iinErrors.put(tableRow.getId(), error);
                                }
                                System.out.println("Nalsur iin:" + iinStr);
                                tableRow.setIin(iinStr);
                            }
                            if (!isEmpty(fullNameCell)) {
                                System.out.println("Nalsur fullName:" +
                                        fullNameCell.getStringCellValue());
                                tableRow.setFullname(fullNameCell.getStringCellValue());
                            }
                            if (!isEmpty(bdayCell)) {
                                tableRow.setAttribute("BirthdayDate",
                                        bdayCell.getDateCellValue());
                            }

                            if (!isEmpty(cTypeCell)) {
                                tableRow.setClientType(cTypeCell.getStringCellValue().toUpperCase());
                            }

                            if (!isEmpty(onDate)) {
                                tableRow.setAttribute("OnDate",
                                        onDate.getDateCellValue());
                            }

                            if (!isEmpty(openAccsCell)) {
                                if (openAccsCell.getStringCellValue().equals("Да"))
                                    tableRow.setOpenAccounts(new Number(1));
                            }
                            if (!isEmpty(closeAccsCell)) {
                                if (closeAccsCell.getStringCellValue().equals("Да"))
                                    tableRow.setCloseAccounts(new Number(1));
                            }
                            if (!isEmpty(safeBoxesCell)) {
                                if (safeBoxesCell.getStringCellValue().equals("Да"))
                                    tableRow.setSafeBoxs(new Number(1));
                            }
                            if (!isEmpty(encumbrancesCell)) {
                                if (encumbrancesCell.getStringCellValue().equals("Да"))
                                    tableRow.setEncumbrances(new Number(1));
                            }
                        }

                    }
                }
                System.out.println("Nalsur Row Counter End:" + rowCounter);
                rowCounter++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return iinErrors;
    }

    //Метод для парсинга Эксель файла с общим типом данных клиента

    public static Map<Number, String> parseExcelDocumentWithCommonCondition(InputStream is,
                                                                            ClientsViewImpl clientsVO,
                                                                            oracle.jbo.domain.Number requestId,
                                                                            String clientType,
                                                                            List<String> productTypes) {
        Map<Number, String> iinErrors = new HashMap<Number, String>();
        try {
            //   ThemeDocument td =
            Workbook wb = WorkbookFactory.create(is);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Row> rowIter = sheet.rowIterator();
            int rowCounter = 0;
            while (rowIter.hasNext()) {
                System.out.println("Nalsur Row Counter Start:" + rowCounter);
                Row row = rowIter.next();
                if (rowCounter > 1) {
                    //   sheet.getRow(rowCounter);
                    System.out.println("");
                    if (!row.equals(null)) {
                        if (!((row.getCell(1).getCellType() ==
                                Cell.CELL_TYPE_BLANK) &&
                                row.getCell(2).getCellType() ==
                                        Cell.CELL_TYPE_BLANK)) {
                            ClientsViewRowImpl tableRow =
                                    (ClientsViewRowImpl) clientsVO.createRow();

                            tableRow.setRequestId(requestId);
                            Cell iinCell = row.getCell(0);
                            Cell fullNameCell = row.getCell(1);
                            Cell bdayCell = row.getCell(2);

                            if (!isEmpty(iinCell)) {
                                iinCell.setCellType(Cell.CELL_TYPE_NUMERIC);
                                long iin = (long) iinCell.getNumericCellValue();
                                String iinStr = String.valueOf(iin);
                                String error = ValidateIIN(iinStr);
                                System.out.println("Nalsur IIN:" + iinStr +
                                        " Error:" + error);
                                if (!error.trim().isEmpty()) {
                                    iinErrors.put(tableRow.getId(), error);
                                }
                                System.out.println("Nalsur iin:" + iinStr);
                                tableRow.setIin(iinStr);
                            }
                            if (!isEmpty(fullNameCell)) {
                                System.out.println("Nalsur fullName:" +
                                        fullNameCell.getStringCellValue());
                                tableRow.setFullname(fullNameCell.getStringCellValue());
                            }
                            if (!isEmpty(bdayCell)) {
                                tableRow.setAttribute("BirthdayDate",
                                        bdayCell.getDateCellValue());
                            }

                            tableRow.setClientType(clientType);
                            for (String productType : productTypes) {
                                switch (productType) {
                                    case "Открытые счета":
                                        tableRow.setOpenAccounts(new Number(1));
                                        break;
                                    case "Закрытые счета":
                                        tableRow.setCloseAccounts(new Number(1));
                                        break;
                                    case "Сейфовые ячейки":
                                        tableRow.setSafeBoxs(new Number(1));
                                        break;
                                    case "Наличие обременений на счете":
                                        tableRow.setEncumbrances(new Number(1));
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }

                    }
                }
                System.out.println("Nalsur Row Counter End:" + rowCounter);
                rowCounter++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }
        return iinErrors;
    }

    //Проверка ячейки эксель на пустоту
    private static boolean isEmpty(Cell cell) {
        boolean result = true;
        if (cell != null) {
            if (cell.getCellType() != Cell.CELL_TYPE_BLANK) {
                result = false;
            }
        }
        return result;
    }

    //Метод для формирования ПДФ
    //BIPublisher
    public void generatePDF(FacesContext facesContext,
                            OutputStream outputStream) {
        createInfoLog("Start generatePDF");
        RequestsViewImpl vo =
                (RequestsViewImpl) getViewByIterator("RequestsView1Iterator");

        if (vo.first() != null) {
            //Предоставляем данные во вью в XML Формате
            Node data = vo.writeXML(-1, XMLInterface.XML_OPT_ALL_ROWS);
            File templateFile = null;

            if (data != null) {
                Source source = new DOMSource(data);

                try (OutputStream downloadStream = outputStream;
                     ByteArrayOutputStream out = new ByteArrayOutputStream();
                     //Берем наш шаблон, который будем заполнять данными из XML
                     InputStream rtfInputStream =
                             this.getClass().getClassLoader().getResourceAsStream("objects/rtf/template.rtf.properties")) {

                    java.util.Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("HHmmss");
                    Result result = new StreamResult(out);
                    TransformerFactory factory =
                            TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer();
                    transformer.transform(source, result);
                    byte[] inputData = out.toByteArray();
                    RTFProcessor rtfProcessor =
                            new RTFProcessor(rtfInputStream);
                    templateFile =
                            File.createTempFile("ApplicationTemplate" + dateFormat.format(date), ".tmp");
                    rtfProcessor.setOutput(templateFile.getAbsolutePath());

                    System.out.println("Nalsur " +
                            templateFile.getAbsolutePath());
                    //преобразуем в XSL
                    rtfProcessor.process();
                    FOProcessor processor = new FOProcessor();
                    //Указываем данные для заполнения шаблона
                    processor.setData(new java.io.ByteArrayInputStream(inputData));
                    //Устанавливаем наш шаблон
                    processor.setTemplate(templateFile.getAbsolutePath());
                    //Указываем куда нужно выводить
                    processor.setOutput(downloadStream);
                    //Указываем формат данных
                    processor.setOutputFormat(FOProcessor.FORMAT_PDF);
                    //Генерируем файл
                    processor.generate();


                } catch (Exception ex) {
                    addFacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {
                    if (templateFile != null) {
                        templateFile.delete();
                    }
                }

            }
        }
        createInfoLog("End generatePDF");

    }

    //Метод, который выдает и присваивает исходящий номер текущей заявке
    public void assignOutgoingNumber(ActionEvent actionEvent) {
        if (getAttrStringValue("FullRequestNumber").isEmpty()) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Невозможно присвоить исходящий номер: Заявка еще не зарегистрирована в системе BPM!");
            return;
        }

        if (getAttrStringValue("ExecutorLogin").isEmpty()) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Невозможно присвоить исходящий номер: Не назначен испонитель заявки");
            return;
        }

        if (getAttrStringValue("OutgoingDocumentDate").isEmpty()) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Необходимо заполнить поле «Дата документа»!");
            return;
        }


        String query =
                "select 'BPM/02/' ||  T.ABS_NUM || '/' || to_char(R.OUTGOING_DOCUMENT_DATE, 'YYYY') || '/' || R.REQUEST_NUMBER " +
                        "from CardIndex2.Requests R " +
                        "left outer join EUB_BPM.EMPLOYEE_VIEW T " +
                        "on lower(T.login) = lower(R.Executor_Login) " +
                        "where R.request_id = " + getRequestIDString();
        try {
            ResultSet rs = getAppModule().executeQuery(query);
            if (rs.next()) {
                String outNumber = rs.getString(1);
                if (!outNumber.equals(null)) {
                    setAttrValue("OutgoingNumber", outNumber);
                    addPartialTarget(getOutNumber());
                }
            }

        } catch (SQLException e) {
            addFacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ошибка в SQL: " + e.getMessage());
        }
    }


    //Генерация файла реестра в Эксель файл
    //BIPublisher
    public void generateReestrXLSX(FacesContext facesContext,
                                   OutputStream outputStream) {
        //Условия
        String whereClause = getRequestIds().getValue().toString();
        AppModuleImpl AppModule = getAppModule();
        String MASTER_QUERY =
                "SELECT Requests.REQUEST_ID, " + "Requests.Claimant_Address, " +
                        "Requests.Correspondent, " + "Requests.Executor_Fullname, " +
                        "Requests.Outgoing_Number, " +
                        "to_char(Requests.Outgoing_Document_Date, 'dd.MM.yyyy') Outgoing_Document_Date, " +
                        "Requests.Outgoing_Index, " +
                        "CardIndex2.C2_Package.Number2Text(T.counts, 0) Request_count " +
                        "FROM CARDINDEX2.REQUESTS Requests, (" +
                        "     SELECT count(*) counts " +
                        "FROM CARDINDEX2.REQUESTS Requests " +
                        "WHERE Requests.Request_Id in (" + whereClause + ")) T " +
                        "WHERE Requests.Request_Id in (" + whereClause + ")";
        ;
        //Создаем временный ViewObject с нужным SQL-запросом
        ViewObject vo =
                AppModule.createViewObjectFromQueryStmt("Request", MASTER_QUERY);
        vo.executeQuery();
        if (vo.first() != null) {
            Node data = vo.writeXML(-1, XMLInterface.XML_OPT_ALL_ROWS);
            if (data != null) {
                System.out.println("data is not null");
                File templateFile = null;
                Source source = new DOMSource(data);
                try (OutputStream downloadStream = outputStream;
                     ByteArrayOutputStream out = new ByteArrayOutputStream();
                     InputStream rtfInputStream =
                             this.getClass().getClassLoader().getResourceAsStream("objects/rtf/reestr.rtf.properties")) {

                    Result result = new StreamResult(out);


                    TransformerFactory factory =
                            TransformerFactory.newInstance();
                    Transformer transformer = factory.newTransformer();
                    transformer.transform(source, result);

                    byte[] inputData = out.toByteArray();

                    RTFProcessor rtfProcessor =
                            new RTFProcessor(rtfInputStream);
                    java.util.Date date = Calendar.getInstance().getTime();
                    SimpleDateFormat dateFormat =
                            new SimpleDateFormat("HHmmss");
                    templateFile =
                            File.createTempFile("ApplicationTemplate" + dateFormat.format(date),
                                    ".tmp");
                    rtfProcessor.setOutput(templateFile.getAbsolutePath());

                    System.out.println("Nalsur " +
                            templateFile.getAbsolutePath());
                    //преобразуем в XSL
                    rtfProcessor.process();
                    FOProcessor processor = new FOProcessor();
                    //Указываем данные для заполнения шаблона
                    processor.setData(new java.io.ByteArrayInputStream(inputData));
                    //Устанавливаем наш шаблон
                    processor.setTemplate(templateFile.getAbsolutePath());

                    //Указываем куда нужно выводить
                    processor.setOutput(downloadStream);
                    //Указываем формат данных
                    System.out.println("Nalsur Export Format PDF");
                    processor.setOutputFormat(FOProcessor.FORMAT_XLSX);
                    //Генерируем файл
                    processor.generate();
                } catch (Exception ex) {
                    addFacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Ошибка: " + ex.getMessage());
                    ex.printStackTrace();
                } finally {

                    if (templateFile != null)
                        templateFile.delete();
                    if (vo != null) {
                        vo.remove();
                    }

                }
            }
        }
    }


    //Внутренние классы стратегии
    //Главный класс-стартер, который ищет нужную стратегию
    class MainStrategy extends AccessStrategy {
        public MainStrategy() {
            super();
            createInfoLog("MainStrategy");
        }
        //принимаем настройки стратегии
        public void applySettings() {
            createInfoLog("Main Strategy apply settings start");

            if (getWfContext().getIsAdmin()) {
                createInfoLog(getWfContext().getUserDisplayName() +
                        " is Admin");
                getSystemInfoTab().setVisible(true);
            } else {
                createInfoLog(getWfContext().getUserDisplayName() +
                        " isn't Admin");
            }

            String state = getAttrStringValue("state");
            //Проверяем статус заявки и если она назначена, то проверяем является ли текущий пользователь -
            //визирующим
            if (!state.equals("ASSIGNED") || !isAssignee()) {
                accessStrategy = new GuestStrategy();
            } else {
                accessStrategy = getViewStrategy();
            }
            accessStrategy.applySettings();
        }
        //валидация полей
        public boolean validateData() {
            return true;
        }
    }
    //Стратегия для инициатора
    class InitiatorStrategy extends AccessStrategy {
        public InitiatorStrategy() {
            super();
            createInfoLog("InitiatorStrategy");
        }

        public void applySettings() {
            createInfoLog("InitiatorStrategy apply settings start");

            getHistoryTab().setVisible(false);
            getDocumentDate().setReadOnly(false);
            getRequestTypeSOC().setReadOnly(false);
            getRequestTypeSOC().setReadOnly(false);
            getCorrespondent().setReadOnly(false);
            getQuerySheets().setReadOnly(false);
            getDestination().setReadOnly(false);
            getClaimentAddress().setReadOnly(false);
            getOnDate().setReadOnly(false);


            getAddAttachmentButton().setVisible(true);
            getAddDBaseTB().setVisible(true);
            getDeleteSFColumn().setVisible(true);

            getHistoryTab().setVisible(true);


            UIComponent[] components =
                    new UIComponent[]{getDocumentDate(), getCorrespondent(),
                            getQuerySheets(), getDestination(),
                            getClaimentAddress(), getRequestTypeSOC(),
                            getAddAttachmentButton(),
                            getHistoryTab()};
            addPartialTarget(components);
            createInfoLog("getParamValue" + getParamValue("initConfirmed"));
            System.out.println("getParamValue" +
                    getParamValue("initConfirmed"));
            if (!getParamBooleanValue("initConfirmed")) {
                getConfirmInitPopup().show(new RichPopup.PopupHints());
            }
            createInfoLog("InitiatorStrategy apply settings end");
        }

        public boolean validateData() {
            List<String> errors = new ArrayList<String>();
            if (getDocumentDate().getValue() == null ||
                    getDocumentDate().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Дата документа» является обязательным для заполнения");
            }
            if (getRequestTypeSOC().getValue() == null ||
                    getRequestTypeSOC().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Тип заявки» является обязательным для заполнения");
            }
            if (getCorrespondent().getValue() == null ||
                    getCorrespondent().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Корреспондент» является обязательным для заполнения");
            }
            if (getQuerySheets().getValue() == null ||
                    getQuerySheets().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Количество листов запроса» является обязательным для заполнения");
            }
            if (getDestination().getValue() == null ||
                    getDestination().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Направление» является обязательным для заполнения");
            }
            if (getClaimentAddress().getValue() == null ||
                    getClaimentAddress().getValue().toString().trim().isEmpty()) {
                errors.add("Поле «Адрес взыскателя» является обязательным для заполнения");
            }
            ScanFilesVOImpl scanFilesViewObject =
                    (ScanFilesVOImpl) getViewByIterator("ScanFilesVO1Iterator");

            Row[] rows = scanFilesViewObject.getAllRowsInRange();

            if (rows.length == 0) {
                errors.add("Необходимо добавить скан/файл!");
            }

            RequestDocumentBasesViewImpl dbasesVO =
                    (RequestDocumentBasesViewImpl) getViewByIterator("RequestDocumentBasesView1Iterator");

            Row[] dbaseRows = dbasesVO.getAllRowsInRange();

            if (dbaseRows.length == 0) {
                errors.add("Необходимо добавить документ основание!");
            }
            setErrorsList(errors);
            if (getErrorsList().isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }
    //Стратегия для гостя
    class GuestStrategy extends AccessStrategy {
        public GuestStrategy() {
            super();
            createInfoLog("GuestStrategy");
        }

        public void applySettings() {
            setAllElementsReadonly();
        }

        public boolean validateData() {
            return true;
        }
    }
    //Стратегия для исполнителя
    class ExecutorStrategy extends AccessStrategy {
        public ExecutorStrategy() {
            super();
            createInfoLog("Executor Strategy");
        }

        public void applySettings() {
            getConclusionsTab().setVisible(true);
            getEncumbrancesTab().setVisible(true);
            getSearchResultsTab().setVisible(true);
            getHistoryTab().setVisible(true);


            getSelectChangeIIN().setReadOnly(false);
            getDisplayProductInReplySOC().setReadOnly(false);
            getWithdrawArrestSOC().setReadOnly(false);
            getOutDocumentDate().setReadOnly(false);
            getPostIndexField().setReadOnly(false);

            getAddClientButton().setVisible(true);
            getAddClientsTB().setVisible(true);
            getAddProductTB().setVisible(true);
            getRecheckIINTB().setVisible(true);
            getEditClientTB().setVisible(true);
            getDeleteClientTB().setVisible(true);
            getExportClientsToXLSTB().setVisible(true);
            getExportToAnswerFileTB().setVisible(true);
            getAttachProductFileTB().setVisible(true);
            getAddArestTB().setVisible(true);
            getAddC2TB().setVisible(true);
            getDeleteProductFileTB().setVisible(true);
            getViewEncumbranceTB().setVisible(true);
            getEditEncumbranceTB().setVisible(true);
            getEditEncumbranceSumTB().setVisible(true);
            getDeleteEncumbranceTB().setVisible(true);
            getAssignOutNumber().setVisible(true);
            getAttachAnswerFileTB().setVisible(true);
            getDeleteAFColumn().setVisible(true);

            String prevStageId = getAttrStringValue("prevStageId");
            int i = comps.length;
            switch (prevStageId) {
                case "":
                    getSendToControlOperation().setVisible(false);
                    getSendForJuristApprovalOperation().setVisible(false);
                    getSendToJuristOperation().setVisible(false);
                    getCloseRequestOperation().setVisible(false);
                    break;
                case "ZSPStage":
                    getSendToZSPOperation().setVisible(false);
                    getSendForJuristApprovalOperation().setVisible(false);
                    break;
                case "FilialUristStage":
                    getSendToZSPOperation().setVisible(false);
                    getSendToJuristOperation().setVisible(false);
                    break;
                case "GBUristStage":
                    getSendForJuristApprovalOperation().setVisible(false);
                    getSendToJuristOperation().setVisible(false);
                    getSendToZSPOperation().setVisible(false);
                    break;
                case "RevisionFromControl":
                    getSendForJuristApprovalOperation().setVisible(false);
                    getSendToJuristOperation().setVisible(false);
                    getCloseRequestOperation().setVisible(false);
                    getSendToZSPOperation().setVisible(false);
                    break;
                case "ConclusionFromControl":
                    getSendToControlOperation().setVisible(false);
                    break;
                default:
                    break;
            }
        }

        public boolean validateData() {
            List<String> errors = new ArrayList<String>();

            ClientsViewImpl clientsVO =
                    (ClientsViewImpl) getViewByIterator("ClientsView1Iterator");
            Row[] clients = clientsVO.getAllRowsInRange();
            createInfoLog("Clients Count " + clients.length);
            if (clients.length == 0) {
                errors.add("Необходимо добавить клиентов!");
            }

            RequestEncumbrancesImpl encumbrancesVO =
                    (RequestEncumbrancesImpl) getViewByIterator("RequestEncumbrances1Iterator");
            Row[] encumbrances = encumbrancesVO.getAllRowsInRange();
            createInfoLog("Encums Count " + encumbrances.length);
            if (encumbrances.length == 0) {
                errors.add("Необходимо добавить обременения!");
            }


            setErrorsList(errors);
            if (getErrorsList().isEmpty()) {
                return true;
            } else {
                return false;
            }
        }
    }


}