<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%
    String base = request.getContextPath();
//    String captchaId = UUID.randomUUID().toString();
//    ApplicationContext applicationContext = SpringUtils.getApplicationContext();
//    Setting setting = SettingUtils.get();
//    if (applicationContext != null) {
%>
<html>
<head>
    <title>在线考试系统</title>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" >
    <link href="<%=base%>/resources/css/reset.css" rel="stylesheet" type="text/css" />
    <link href="<%=base%>/resources/css/login.css" rel="stylesheet" type="text/css" />
    <script src="<%=base%>/resources/js/jquery.min.js" ></script>
    <script type="text/javascript" src="<%=base%>/resources/js/jquery.validate.js"></script>
    <script type="text/javascript" src="<%=base%>/resources/js/jquery.placeholder.js"></script>
    <script type="text/javascript" src="<%=base%>/resources/js/rng.js"></script>
    <script type="text/javascript" src="<%=base%>/resources/js/common.js"></script>
    <script type="text/javascript" src="<%=base%>/resources/js/message.js"></script>
    <script src="<%=base%>/resources/js/jquery.backstretch.min.js" ></script>
</head>

<body class="login" onload="loadTopWindow()">
<div class="top_div"></div>
<div style="margin:-300px auto auto;width: 100%; height: 300px; text-align: center;color:#fff">
    <h1>在线考试系统</h1>
</div>
<div style="background: #fff; margin: -180px auto auto; border: 1px solid #e7e7e7; width: 450px; height: 370px; text-align: center;">
    <div style="width: 165px; height: 96px; position: absolute;">

    </div>
    <form id="loginForm" action="login.jsp" method="post">
        <input type="hidden" id="enPassword" name="enPassword"/>
        <input type="hidden" id="localUrl"/>
        <%--<%if (ArrayUtils.contains(setting.getCaptchaTypes(), CaptchaType.LOGIN)) {%>--%>
        <%--<input type="hidden" name="captchaId" value="<%=captchaId%>"/>--%>
        <%--<%}%>--%>
        <br><br><br>
        <p style="padding: 0 0 17px; position: relative;">
            <span title="用户名" class="p_logo"></span>
            <input class="ipt" type="text" id="username" name="username" placeholder="请输入用户名" maxlength="30">
        </p>
        <p style="padding: 0 0 17px;position: relative;">
            <span title="密码" class="p_logo"></span>
            <input class="ipt" type="password" id="password" placeholder="请输入密码">
        </p>
        <p style="padding: 0 0 17px;position: relative;">
            <input class="ipt" type="text" id="captcha" name="captcha" placeholder="请输入验证码"
                   style="padding: 10px 0 10px 10px;width: 239px;">
            <%--<img class="captchaImg" id="captchaImage"--%>
                 <%--src="<%=base%>/console/common/captcha.jhtml?captchaId=<%=captchaId%>"--%>
                 <%--title="<%=SpringUtils.getMessage("ov.captcha.imageTitle")%>"/>--%>
        </p>
        <div id="alertError" class="alert alert-error hide">
            <button class="close" data-dismiss="alert"></button>
            <span>Enter any username and password.</span>
        </div>
        <div style="height: 45px; line-height: 50px; margin-top: 15px; border-top-color: #e7e7e7; border-top-width: 1px; border-top-style: solid;">
            <p style="margin: 0 35px 20px 45px;">

                <span style="float: right;">
                    <a class="loginBtn" id="loginBtnID" href="#">登 录</a>
                </span>
            </p>
        </div>
    </form>
</div>
<script type="text/javascript">
    function loadTopWindow(){
        if (window.top!=null && window.top.document.URL!=document.URL)
        { window.top.location= document.URL; }
    }
</script>
</body>
</html>
