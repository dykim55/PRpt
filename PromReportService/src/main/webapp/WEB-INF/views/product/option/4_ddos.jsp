<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script>

FORM_OPTION = (function() {

    var _formCode, _formType, _reportType, _assetCode, _data;
    
    return {
        title : function() {
            return $("#formTitle").html();
        },
        done : function() {
            var data = {};
            $("#frm").serializeArray().map(function(x){data[x.name] = x.value;});
            return { "data": data, "formInfo": { formCode:_formCode, formType:_formType, formReportType:_reportType}, "etc": undefined };
        },
        formData : function() {
            return { formCode:_formCode, formType:_formType, formReportType:_reportType, assetCode:_assetCode};
        },
        init : function(formData, assetCode) {
            _data = formData.data;
            
            if (formData.formInfo) {
                _formCode = formData.formInfo.formCode;
                _formType = formData.formInfo.formType;
                _reportType = formData.formInfo.formReportType;
            }

            _assetCode = assetCode;
            $("#btn_save_div").css('display', _assetCode ? '' : 'none');
            
            $.each(Object.keys(_data), function() {
                if (this.indexOf("opt") != -1) {
                    $("#"+this).prop("checked", true);
                    $("#"+this).nextAll('div').children().prop("disabled", false);
                } else if (this.indexOf("rd") != -1) {
                    $("input:radio[name="+this+"]:input[value="+_data[this]+"]").prop("checked", true);
                } else if (this.indexOf("ck") != -1) {
                    $("#"+this).prop("checked", true);
                }
            });
            
            $("#formSaveAs").button({icons: {primary: "ui-icon-save"},}).click(function( event ) {
                formSaveAs();
            });
        }
    };
})();

$(document).ready(function(){

    $(".subpanel").children().prop("disabled", true);
    
    $("label").bind("mouseover", function () {
        $(this).addClass("labelHover");
    });
    
    $("label").bind("mouseleave", function () {
        $(this).removeClass("labelHover");
    });

    $(":input:checkbox").bind("click", function () {
        $(this).nextAll('div').children().prop("disabled", !$(this).is(":checked"));
    });

});

</script>

<span id="formTitle" style="display:none;">DDoS 임의기간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">탐지로그 발생추이</h2>
<div class="mb10">
  <input type="checkbox" id="opt1" name="opt1"><label for="opt1"> 전체 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt2" name="opt2"><label for="opt2"> 외부에서 내부로의 전체 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt3" name="opt3"><label for="opt3"> 외부에서 내부로의 허용 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt4" name="opt4"><label for="opt4"> 외부에서 내부로의 차단 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt5" name="opt5"><label for="opt5"> 내부에서 외부로의 전체 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt6" name="opt6"><label for="opt6"> 내부에서 외부로의 허용 로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt7" name="opt7"><label for="opt7"> 내부에서 외부로의 차단 로그 발생추이 (차트)</label>
</div>

<h2 class="mb10">이벤트 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt8" name="opt8"><label for="opt8"> 외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd8" id="rd8-1" value="10" checked><label for="rd8-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-2" value="20"><label for="rd8-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-3" value="30"><label for="rd8-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt9" name="opt9"><label for="opt9"> 외부에서 내부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd9" id="rd9-1" value="10" checked><label for="rd9-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-2" value="20"><label for="rd9-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-3" value="30"><label for="rd9-3"> TOP 30</label></br>
    <input type="checkbox" id="ck9" name="ck9"><label for="ck9"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 외부에서 내부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd10" id="rd10-1" value="10" checked><label for="rd10-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-2" value="20"><label for="rd10-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd10" id="rd10-3" value="30"><label for="rd10-3"> TOP 30</label></br>
    <input type="checkbox" id="ck10" name="ck10"><label for="ck10"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd11" id="rd11-1" value="10" checked><label for="rd11-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-2" value="20"><label for="rd11-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd11" id="rd11-3" value="30"><label for="rd11-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt12" name="opt12"><label for="opt12"> 내부에서 외부로의 허용 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd12" id="rd12-1" value="10" checked><label for="rd12-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-2" value="20"><label for="rd12-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd12" id="rd12-3" value="30"><label for="rd12-3"> TOP 30</label></br>
    <input type="checkbox" id="ck12" name="ck12"><label for="ck12"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt13" name="opt13"><label for="opt13"> 내부에서 외부로의 차단 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd13" id="rd13-1" value="10" checked><label for="rd13-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-2" value="20"><label for="rd13-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd13" id="rd13-3" value="30"><label for="rd13-3"> TOP 30</label></br>
    <input type="checkbox" id="ck13" name="ck13"><label for="ck13"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<h2 class="mb10">출발지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt14" name="opt14"><label for="opt14"> 외부에서 내부로의 전체 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd14" id="rd14-1" value="10" checked><label for="rd14-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-2" value="20"><label for="rd14-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd14" id="rd14-3" value="30"><label for="rd14-3"> TOP 30</label></br>
    <input type="checkbox" id="ck14" name="ck14"><label for="ck14"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt15" name="opt15"><label for="opt15"> 외부에서 내부로의 허용 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd15" id="rd15-1" value="10" checked><label for="rd15-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-2" value="20"><label for="rd15-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd15" id="rd15-3" value="30"><label for="rd15-3"> TOP 30</label></br>
    <input type="checkbox" id="ck15" name="ck15"><label for="ck15"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt16" name="opt16"><label for="opt16"> 외부에서 내부로의 차단 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd16" id="rd16-1" value="10" checked><label for="rd16-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-2" value="20"><label for="rd16-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd16" id="rd16-3" value="30"><label for="rd16-3"> TOP 30</label></br>
    <input type="checkbox" id="ck16" name="ck16"><label for="ck16"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt17" name="opt17"><label for="opt17"> 내부에서 외부로의 전체 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd17" id="rd17-1" value="10" checked><label for="rd17-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd17" id="rd17-2" value="20"><label for="rd17-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd17" id="rd17-3" value="30"><label for="rd17-3"> TOP 30</label></br>
    <input type="checkbox" id="ck17" name="ck17"><label for="ck17"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt18" name="opt18"><label for="opt18"> 내부에서 외부로의 허용 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd18" id="rd18-1" value="10" checked><label for="rd18-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd18" id="rd18-2" value="20"><label for="rd18-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd18" id="rd18-3" value="30"><label for="rd18-3"> TOP 30</label></br>
    <input type="checkbox" id="ck18" name="ck18"><label for="ck18"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt19" name="opt19"><label for="opt19"> 내부에서 외부로의 차단 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd19" id="rd19-1" value="10" checked><label for="rd19-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd19" id="rd19-2" value="20"><label for="rd19-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd19" id="rd19-3" value="30"><label for="rd19-3"> TOP 30</label></br>
    <input type="checkbox" id="ck19" name="ck19"><label for="ck19"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<h2 class="mb10">목적지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt20" name="opt20"><label for="opt20"> 외부에서 내부로의 전체 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd20" id="rd20-1" value="10" checked><label for="rd20-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd20" id="rd20-2" value="20"><label for="rd20-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd20" id="rd20-3" value="30"><label for="rd20-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt21" name="opt21"><label for="opt21"> 외부에서 내부로의 허용 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd21" id="rd21-1" value="10" checked><label for="rd21-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd21" id="rd21-2" value="20"><label for="rd21-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd21" id="rd21-3" value="30"><label for="rd21-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt22" name="opt22"><label for="opt22"> 외부에서 내부로의 차단 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd22" id="rd22-1" value="10" checked><label for="rd22-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd22" id="rd22-2" value="20"><label for="rd22-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd22" id="rd22-3" value="30"><label for="rd22-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt23" name="opt23"><label for="opt23"> 내부에서 외부로의 전체 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd23" id="rd23-1" value="10" checked><label for="rd23-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd23" id="rd23-2" value="20"><label for="rd23-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd23" id="rd23-3" value="30"><label for="rd23-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt24" name="opt24"><label for="opt24"> 내부에서 외부로의 허용 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd24" id="rd24-1" value="10" checked><label for="rd24-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd24" id="rd24-2" value="20"><label for="rd24-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd24" id="rd24-3" value="30"><label for="rd24-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt25" name="opt25"><label for="opt25"> 내부에서 외부로의 차단 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd25" id="rd25-1" value="10" checked><label for="rd25-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd25" id="rd25-2" value="20"><label for="rd25-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd25" id="rd25-3" value="30"><label for="rd25-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">서비스 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt26" name="opt26"><label for="opt26"> 외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck26" name="ck26"><label for="ck26"> 서비스 TOP10 증감현황 및 이벤트유형 (표)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt27" name="opt27"><label for="opt27"> 내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck27" name="ck27"><label for="ck27"> 서비스 TOP10 증감현황 및 이벤트유형 (표)</label>
  </div>
</div>

<h2 class="mb10">성능정보</h2>
<div class="mb10">
  <input type="checkbox" id="opt99" name="opt99"><label for="opt99"> 성능정보 (차트)</label>
</div>

</form>

<div id="btn_save_div" style="display:none;"> 
    <table class="tbl_button marginT5">
        <tr>
            <td class="alignL">
            </td>
            <td class="alignR">
                <button id="formSaveAs">양식 저장</button>
            </td>
        </tr>
    </table>
</div>
