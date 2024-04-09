<%-- 
    Document   : captcha.jsp
    Created on : 04 8, 24, 2:16:57 PM
    Author     : Dayao, Leonne Matthew H. // UST - 1CSC
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Captcha Generator</title>
        <script>
            // para hindi macopy
            function preventCopy(event) {
                event.preventDefault();
            }
        </script>
        <style>
          #captcha {
            user-select: none; 
            -moz-user-select: none;
            -webkit-user-select: none;
            -ms-user-select: none;
          }
        </style>
    </head>
    <%  // Removes cache from success.jsp to prevent form resubmission
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma","no-cache"); 
        response.setHeader("Expires","0");
        response.setDateHeader("Expires",-1);
    %>
    <%      
    response.setContentType("text/html;charset=UTF-8");
    String isCaptchaValid;
    try {
        isCaptchaValid = session.getAttribute("isCaptchaValid").toString();
        }
    catch (Exception e) {
        isCaptchaValid = "";
        }
    if (isCaptchaValid.equals("true")) {
            response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/"));
            session.invalidate();
            return;
        }
    %>
    <body> <!-- disables right click (para sure na walang option to copy) -->
        <input type="hidden" id="refreshed" value="no">
        <script type="text/javascript">
               onload=function(){
                   var e=document.getElementById("refreshed");
                   if(e.value=="no")e.value="yes";
                   else{e.value="no";location.reload();}
               }
        </script>
        <div oncontextmenu="return false;" class="form">
            <form action="captchaVerif" method="POST">
                <h2>Login Page</h2>
                <p id="captcha" oncopy="preventCopy(event)">${sessionScope.captcha}</p>
                <input id="captchaVerif" name="captchaVerif" placeholder="Verify captcha..." autocomplete="off">
                <input class="btn" type="submit" id="submit" value="Submit">
            </form>
        </div>
    </body>
</html>
