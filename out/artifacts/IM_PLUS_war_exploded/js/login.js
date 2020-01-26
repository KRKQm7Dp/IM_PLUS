var signUpButton = document.getElementById('signUp');
var signInButton = document.getElementById('signIn');
var container = document.getElementById('container');

signUpButton.onclick = function (ev) {
    container.classList.add("right-panel-active");
}

signInButton.onclick = function (ev) {
    container.classList.remove("right-panel-active");
}

function onLogin(){
    var loginID = $('#loginID').val();
    var password = $.md5($('#password').val());
    var checkCode = $('#checkCode').val();
    var user = {
        loginID: loginID,
        password: password,
        checkCode: checkCode
    }
    $.ajax({
        url: 'login',  // ajax 提交到 LoginServlet 进行验证
        type: 'POST',
        data: JSON.stringify(user),
        dataType: 'JSON',
        contentType: 'application/json;charset=utf-8',
        success: function(msg) {
            if (msg.status != null && msg.status == "success") {
                window.location.href = "./chat.jsp";
            }else {
                alert(msg.reason);
            }
        },
        error :function(msg) {
            console.log(msg);
        }
    })
    return false
}

function onRegister(){
    var rloginID = $('#rloginID').val();
    var rnickName = $('#rnickName').val();
    var pwd1 = $.md5($('#pwd1').val());
    var pwd2 = $.md5($('#pwd2').val());
    var user = {
        loginID: rloginID+"",
        nickName: rnickName+"",
        pwd1: pwd1+"",
        pwd2: pwd2+""
    }
    $.ajax({
        url: 'register',  // ajax 提交到 RegisterServlet 进行验证
        type: 'POST',
        data: JSON.stringify(user),
        dataType: 'JSON',
        contentType: 'application/json;charset=utf-8',
        success: function(msg) {
            if (msg.status != null && msg.status == "success") {
                alert("注册 " + rloginID +" 账号成功");
            }else {
                alert(msg.reason);
            }
        },
        error :function(msg) {
            console.log(msg);
        }
    })
    return false
}

function changeCheckCode(){
    $("#checkCode_img").attr('src', 'createCheckcode?r=' + Math.random());
}