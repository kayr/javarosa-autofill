(function () {
    var $username = $('#username');
    var $password = $('#password');
    var $url = $('#server-url');
    var $btnGetFormList = $('#get-form-list')


    function getFormList() {
        var params = {
            username: $username.val(),
            password: $password.val(),
            url: $url.val()
        };
        // noinspection JSCheckFunctionSignatures
        $.ajax({
            url: "/formList",
            method: "POST",
            data: JSON.stringify(params)

        }).done(function (t) {
            bootbox.alert(t);
        }).fail(function (t) {
            bootbox.alert("Faile: " + t.responseText);
        })
    }


    function init() {

        $btnGetFormList.on('click', function () {
            getFormList();
        })

    }


    init();


})(jQuery);