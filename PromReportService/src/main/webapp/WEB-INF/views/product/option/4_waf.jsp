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

<span id="formTitle" style="display:none;">웹방화벽(WAF) 임의기간보고서 설정</span>

<form id="frm" style="font-weight: bold;">

<h2 style="margin-top:0px;">탐지로그 발생추이</h2>
<div class="mb10">
  <input type="checkbox" id="opt01" name="opt01"><label for="opt01"> 전체 탐지로그 발생추이 (차트, 표)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt02" name="opt02"><label for="opt02"> 전체 탐지로그 & 도메인 TOP10 발생추이 (차트)</label>
</div>

<h2 class="mb10">이벤트 TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt03" name="opt03"><label for="opt03"> 전체 탐지로그 & Event TOP (차트, 표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd3" id="rd3-1" value="10" checked><label for="rd3-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd3" id="rd3-2" value="20"><label for="rd3-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd3" id="rd3-3" value="30"><label for="rd3-3"> TOP 30</label>
  </div>
</div>

<h2 class="mb10">출발지IP TOP#N</h2>
<div class="mb10">
  <input type="checkbox" id="opt04" name="opt04"><label for="opt04"> 전체 탐지로그 & SIP TOP (표)</label>
  <div style="margin-left: 20px;" class="subpanel">
    <input type="radio" name="rd4" id="rd4-1" value="10" checked><label for="rd4-1"> TOP 10</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd4" id="rd4-2" value="20"><label for="rd4-2"> TOP 20</label>&nbsp;&nbsp;&nbsp;&nbsp;
    <input type="radio" name="rd4" id="rd4-3" value="30"><label for="rd4-3"> TOP 30</label></br>
  </div>
</div>

<div class="mb10">
  <input type="checkbox" id="opt05" name="opt05"><label for="opt05"> 출발지IP TOP10 탐지로그 발생추이 (차트)</label>
</div>

<h2 class="mb10">도메인별 상세통계</h2>
<div class="mb10">
  <input type="checkbox" id="opt06" name="opt06"><label for="opt06"> 도메인 별 탐지로그 & 탐지로그 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt07" name="opt07"><label for="opt07"> 도메인 별 탐지로그 & EVT TOP10 발생추이 (차트)</label>
</div>

<div class="mb10">
  <input type="checkbox" id="opt08" name="opt08"><label for="opt08"> 도메인 별 탐지로그 & EVT TOP10 통계 (차트, 표)</label>
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
