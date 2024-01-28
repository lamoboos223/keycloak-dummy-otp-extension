<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=false; section>
    <#if section = "header">
        OTP
    <#elseif section = "form">
    <div>
        Please Enter OTP
    </div>
    <form class="form-actions" action="${url.loginAction}" method="POST">
        <input name="otp-value" id="kc-accept" type="text"/>
        <input class="${properties.kcButtonClass!} ${properties.kcButtonDefaultClass!} ${properties.kcButtonLargeClass!}" name="otp-btn" id="kc-decline" type="submit" value="submit"/>
    </form>
    <div class="clearfix"></div>
    </#if>
</@layout.registrationLayout>