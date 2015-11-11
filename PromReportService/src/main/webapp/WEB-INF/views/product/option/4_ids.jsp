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

<span id="formTitle" style="display:none;">침입탐지(IDS) 임의기간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">탐지로그 발생추이</h2>
<div class="mb10">
  <input type="checkbox" id="opt1" name="opt1"><label for="opt1"> 전체 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt2" name="opt2"><label for="opt2"> 외부에서 내부로의 전체 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt3" name="opt3"><label for="opt3"> 내부에서 외부로의 전체 탐지로그 발생추이 (차트)</label>
</div>

<h2 class="mb10">이벤트 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt4" name="opt4"><label for="opt4"> 외부에서 내부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd4" id="rd4-1" value="10" checked><label for="rd4-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd4" id="rd4-2" value="20"><label for="rd4-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd4" id="rd4-3" value="30"><label for="rd4-3"> TOP 30</label></br>
    <input type="checkbox" id="ck4" name="ck4"><label for="ck4"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt5" name="opt5"><label for="opt5"> 내부에서 외부로의 전체 탐지로그 & 이벤트 TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd5" id="rd5-1" value="10" checked><label for="rd5-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd5" id="rd5-2" value="20"><label for="rd5-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd5" id="rd5-3" value="30"><label for="rd5-3"> TOP 30</label></br>
    <input type="checkbox" id="ck5" name="ck5"><label for="ck5"> 이벤트 TOP10 발생추이 (차트)</label>
  </div>
</div>

<h2 class="mb10">출발지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt6" name="opt6"><label for="opt6"> 외부에서 내부로의 전체 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd6" id="rd6-1" value="10" checked><label for="rd6-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd6" id="rd6-2" value="20"><label for="rd6-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd6" id="rd6-3" value="30"><label for="rd6-3"> TOP 30</label></br>
    <input type="checkbox" id="ck6" name="ck6"><label for="ck6"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt7" name="opt7"><label for="opt7"> 내부에서 외부로의 전체 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd7" id="rd7-1" value="10" checked><label for="rd7-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-2" value="20"><label for="rd7-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd7" id="rd7-3" value="30"><label for="rd7-3"> TOP 30</label></br>
    <input type="checkbox" id="ck7" name="ck7"><label for="ck7"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
  </div>
</div>

<h2 class="mb10">목적지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt8" name="opt8"><label for="opt8"> 외부에서 내부로의 전체 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd8" id="rd8-1" value="10" checked><label for="rd8-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-2" value="20"><label for="rd8-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd8" id="rd8-3" value="30"><label for="rd8-3"> TOP 30</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt9" name="opt9"><label for="opt9"> 내부에서 외부로의 전체 탐지로그 & DIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd9" id="rd9-1" value="10" checked><label for="rd9-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-2" value="20"><label for="rd9-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd9" id="rd9-3" value="30"><label for="rd9-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">서비스 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt10" name="opt10"><label for="opt10"> 외부에서 내부로의 전체 탐지로그 & 서비스 TOP10 (차트)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck10" name="ck10"><label for="ck10"> 서비스 TOP10 증감현황 및 이벤트유형 (표)</label>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt11" name="opt11"><label for="opt11"> 내부에서 외부로의 전체 탐지로그 & 서비스 TOP10 (차트)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="checkbox" id="ck11" name="ck11"><label for="ck11"> 서비스 TOP10 증감현황 및 이벤트유형 (표)</label>
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
