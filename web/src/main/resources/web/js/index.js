(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $url = $('#server-url');
    var $btnGetFormList = $('#get-form-list');
    var $lstForm = $('#c-form-list');
    var $txtProperties = $('#c-properties-text');
    var $txtNumEntries = $('#c-numOfEntries');
    var $btnGenerateData = $('#c-btn-generate-data');
    var $checkDryRun = $('#c-dry-run');
    var $logTextArea = $('#messages');
    var $lblCurrentForm = $('#c-lbl-currentForm');
    var $expressionDiv = $('#c-generexExpressions');

    var selectedForm = null;
    var userId = null;


    function serverCreds(data) {
        if (!data) { data = {} }
        return $.extend(data, {
            username: $username.val(),
            password: $password.val(),
            url: $url.val(),
            userId: userId
        });
    }

    function getFormList() {
        // noinspection JSCheckFunctionSignatures
        $.ajax({
            url: "/formList",
            method: "POST",
            data: JSON.stringify(serverCreds())
        }).done(function (formList) {
            renderFormList(formList)
        }).fail(function (t) {
            bootbox.alert("Failed: " + t.responseText);
        })
    }

    function renderFormList(formList) {

        $lstForm.empty();

        formList.forEach(function (form) {
            var html = $(tmpl('form-list-widget', form)).on('click', function () {
                setCurrentForm(null);
                getProperties(form);
            });

            $lstForm.append(html)
        })
    }

    function setCurrentForm(form) {
        selectedForm = form;
        if (form)
            $lblCurrentForm.text(form.name);
        else
            $lblCurrentForm.text('');


    }

    function renderProperties(res, form) {
        $txtProperties.val(res);

        $expressionDiv.empty();

        res.questions.forEach(function (value) {
            $expressionDiv.append(tmpl('question-widget', value))
        });
        setCurrentForm(form);
    }

    function getProperties(form) {
        $.ajax({
            url: '/formProperties',
            method: 'POST',
            data: JSON.stringify(serverCreds({downloadUrl: form.downloadUrl}))
        }).done(function (res) {
            renderProperties(res, form);
        }).fail(function (err) {
            bootbox.alert("Failed: " + err.responseText);
        });
    }

    function generateData() {
        if (!selectedForm) {
            bootbox.alert("Please Select A Form");
            return
        }

        initWebSocket(function () {
            $.ajax({
                url: '/generateData',
                method: "POST",
                data: JSON.stringify(serverCreds({
                    dryRun: $checkDryRun.is(':checked'),
                    numberOfItems: parseInt($txtNumEntries.val()),
                    downloadUrl: selectedForm.downloadUrl,
                    generexProperties: $txtProperties.val()
                }))
            }).done(function () {
                $logTextArea.val('');
            }).fail(function (error) {
                bootbox.alert({title: "Failed To Generate Data:", message: error.responseText});
            });
        });


    }

    function getWsBase() {
        var loc = window.location, new_uri;
        if (loc.protocol === "https:") {new_uri = "wss:"; }
        else {new_uri = "ws:";}
        return new_uri + "//" + loc.host /*+ loc.pathname*/ + "/events";
    }


    var webSocket;

    function initWebSocket(callBack) {

        if (webSocket && webSocket.readyState !== webSocket.CLOSED) {
            if (callBack) callBack();
            return;
        }

        webSocket = new WebSocket(getWsBase());
        webSocket.onopen = function (event) { };

        webSocket.onmessage = function (event) {

            var message = event.data.toString();

            if (message.startsWith('::user:')) {
                console.log("received token" + message);
                userId = message.replace('::user:', '');
                if (callBack) callBack()
            } else {
                $logTextArea.val(event.data + "\n" + $logTextArea.val());
            }
        };

        webSocket.onclose = function () {userId = null};
    }

    var progressDialog = null;

    function init() {


        $(document).ajaxStart(function () {
            progressDialog = bootbox.dialog({closeButton: false, title: "Working!!", message: "Wait....", buttons: {}});
        }).ajaxStop(function () {
            progressDialog.modal('hide')
        });


        $btnGetFormList.on('click', function () { getFormList(); });

        $btnGenerateData.on('click', function () {generateData(); });

        initWebSocket();
    }


    init();


})(jQuery);