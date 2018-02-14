(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $url = $('#server-url');
    var $btnGetFormList = $('#get-form-list');
    var $lstForm = $('#c-form-list');


    function serverCreds() {
        return {
            username: $username.val(),
            password: $password.val(),
            url: $url.val()
        };
    }

    function getFormList() {
        var params = serverCreds();
        // noinspection JSCheckFunctionSignatures
        $.ajax({
            url: "/formList",
            method: "POST",
            data: JSON.stringify(params)
        }).done(function (formList) {
            renderFormList(formList)
        }).fail(function (t) {
            bootbox.alert("Failed: " + t.responseText);
        })
    }

    function renderFormList(formList) {
        formList.forEach(function (form) {
            var html =
                $(tmpl('form-list-widget', form)).on('click', function () {
                    getProperties(form);
                });

            $lstForm.append(html)
        })
    }

    function getProperties(form) {
        $.ajax({
            url: '/formProperties',
            method: 'POST',
            data: JSON.stringify( $.extend(serverCreds(),{url: form.downloadUrl}))
        }).done(function (t) {
            bootbox.alert(t);
        }).fail(function (t) {
            bootbox.alert("Failed: " + t.responseText);
        });
    }


    function init() {

        $btnGetFormList.on('click', function () {
            getFormList();
        })

    }


    init();


})(jQuery);