(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $url = $('#server-url');
    var $btnGetFormList = $('#get-form-list');
    var $lstForm = $('#c-form-list');
    var $txtNumEntries = $('#c-numOfEntries');
    var $btnGenerateData = $('#c-btn-generate-data');
    var $checkDryRun = $('#c-dry-run');
    var $logTextArea = $('#messages');
    var $lblCurrentForm = $('#c-lbl-currentForm');
    var $expressionDiv = $('#c-generexExpressions');
    var $logs = $('#c-logs');
    var $logsDialog = $('#c-log-model');
    var $btnHelp = $('#c-help-button');

    var selectedForm = null;
    var userId = null;
    var tourGuide = new TourGuide();


    function serverCreds(data) {
        if (!data) {
            data = {}
        }
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
        if (form) {
            $lblCurrentForm.text(form.name);
            loadExpressions();
        }
        else {
            $lblCurrentForm.text('');
        }

    }

    function renderProperties(res, form) {

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

    function extractGenerexProperties() {
        var props = [];
        $('.c-question-generex').each(function (index, domElement) {
            var $this = $(domElement);
            var generex = $this.val();
            var items = {questionId: $this.data('question-id'), generex: generex, browserQnId: $this.attr('id')};
            props.push(items);
        });
        return props;
    }

    function generateData() {
        clearAllErrors();

        if (!selectedForm) {
            bootbox.alert("Please Select A Form");
            return;
        }

        initWebSocket(function () {

            var props = extractGenerexProperties();

            $.ajax({
                url: '/generateData',
                method: "POST",
                data: JSON.stringify(serverCreds({
                    dryRun: $checkDryRun.is(':checked'),
                    numberOfItems: parseInt($txtNumEntries.val()),
                    downloadUrl: selectedForm.downloadUrl,
                    generex: props
                }))
            }).done(function () {
                $logs.empty();
                $logsDialog.modal('show')
            }).fail(function (error) {
                bootbox.alert({title: "Failed To Generate Data:", message: error.responseText});
            });
        });


    }

    function getWsBase() {
        var loc = window.location, new_uri;
        if (loc.protocol === "https:") {
            new_uri = "wss:";
        }
        else {
            new_uri = "ws:";
        }
        return new_uri + "//" + loc.host /*+ loc.pathname*/ + "/events";
    }


    var webSocket;

    function initWebSocket(callBack) {

        if (webSocket && webSocket.readyState !== webSocket.CLOSED) {
            if (callBack) callBack();
            return;
        }

        webSocket = new WebSocket(getWsBase());
        webSocket.onopen = function (event) {
        };

        webSocket.onmessage = function (event) {

            var message = event.data.toString();

            if (message.startsWith('::user:')) {
                console.log("received token" + message);
                userId = message.replace('::user:', '');
                if (callBack) callBack()
            } else {
                $logs.append(tmpl('log-widget', {text: event.data}));
                tailScroll();
                // $logTextArea.val(event.data + "\n" + $logTextArea.val());
            }
        };

        webSocket.onclose = function () {
            $logs.append(tmpl('log-widget', {text: 'Connection has broken'}));

            userId = null;
        };
    }

    function tailScroll() {
        var height = $logs.get(0).scrollHeight;
        $logs.animate({scrollTop: height}, 500);
    }

    function makeDangerous($w) {
        $w.addClass('in-error alert alert-danger');
    }

    function scrollTo($w) {
        $('html, body').animate({scrollTop: $w.offset().top}, 1000);
    }

    var progressDialog = null;

    function clearAllErrors() {
        $('.in-error').removeClass('in-error alert alert-danger');
    }

    function focusOnQuestion(selector) {
        var $w =
            $expressionDiv
                .find(selector)
                .closest('.question-label');

        makeDangerous($w);

        scrollTo($w);
    }

    function persistExpressions() {
        var generexProperties = extractGenerexProperties();
        localStorage.setItem(selectedForm.downloadUrl, JSON.stringify(generexProperties))
    }

    function loadExpressions() {

        var storedExpressions = localStorage.getItem(selectedForm.downloadUrl);

        if (!storedExpressions) return;

        try {
            var expressions = JSON.parse(storedExpressions);
            expressions.forEach(function (value) {
                try {
                    var questionWidget = $expressionDiv.find('#' + value.browserQnId);
                    if (questionWidget)
                        questionWidget.val(value.generex);
                }
                catch (error) {
                    console.error("error setting generex for " + value.browserQnId);
                    console.error(error);
                }
            })
        }
        catch (error) {
            console.error("Could not load stored expressions");
            console.error(error);
        }
    }

    function init() {


        $(document).ajaxStart(function () {
            progressDialog = bootbox.dialog({closeButton: false, title: "Working!!", message: "Wait....", buttons: {}});
        }).ajaxStop(function () {
            progressDialog.modal('hide')
        });


        $btnGetFormList.on('click', function () {
            getFormList();
        });

        $btnGenerateData.on('click', function () {
            generateData();
        });

        $btnHelp.on('click', function () {
            tourGuide.restart();
        });

        $expressionDiv.on('change', '.c-question-generex', function () {
            persistExpressions();
        });

        $logs.on('click', '.c-element-link', function () {
            $logsDialog.modal('hide');
            var attr = $(this).attr('href');
            focusOnQuestion(attr);
        });


        initWebSocket();

        tourGuide.start();
    }


    init();


})(jQuery);