(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $url = $('#server-url');
    var $btnGetFormList = $('#get-form-list');
    var $lstForm = $('#c-form-list');
    var $txtProperties = $('#c-properties-text');
    var $txtNumEntries = $('#c-numOfEntries');
    var $btnGenerateData = $('#c-btn-generate-data');
    var $checkDryRun = $('#c-btn-generate-data');

    var selectedForm = null;


    function serverCreds(data) {
        if (!data) {
            data = {}
        }
        return $.extend(data, {
            username: $username.val(),
            password: $password.val(),
            url: $url.val()
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
                selectedForm = null;
                getProperties(form);
            });

            $lstForm.append(html)
        })
    }

    function getProperties(form) {
        $.ajax({
            url: '/formProperties',
            method: 'POST',
            data: JSON.stringify(serverCreds({downloadUrl: form.downloadUrl}))
        }).done(function (res) {
            $txtProperties.val(res);
            selectedForm = form;
        }).fail(function (err) {
            bootbox.alert("Failed: " + err.responseText);
        });
    }

    function generateData() {
        if (!selectedForm) {
            bootbox.alert("Please Select A Form");
            return
        }

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
            bootbox.alert("Request Submitted");
        }).fail(function (value) {
            bootbox.alert({title: "Failed To Generate Data:", message: value.responseText});
        })


    }


    var progressDialog = null;

    function init() {


        $(document).ajaxStart(function () {
            progressDialog = bootbox.dialog({
                closeButton: false, title: "Working!!", message: "Wait....", buttons: {}
            });
        }).ajaxStop(function () {
            progressDialog.modal('hide')
        });


        $btnGetFormList.on('click', function () {
            getFormList();
        });

        $btnGenerateData.on('click', function () {
            generateData();
        })
    }


    init();


})(jQuery);