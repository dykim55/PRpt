<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
    import = "java.util.HashMap
            , java.util.List
            , java.util.ArrayList
            , java.util.Calendar
            , java.util.GregorianCalendar
            , java.util.TimeZone
            , java.util.Locale
            , java.util.Date
            , java.text.SimpleDateFormat
            , com.cyberone.report.Constants
            , com.cyberone.report.utils.StringUtil"
%>

<%
List<HashMap<String, Object>> slctAssetList = (List<HashMap<String, Object>>)request.getAttribute("slctAssetList");
if (slctAssetList == null) {
	slctAssetList = new ArrayList<HashMap<String, Object>>();
}
String sReportType = (String)request.getParameter("reportType");
String sGroupCode = (String)request.getParameter("groupCode");
String sParentName = (String)request.getParameter("pGroup");
String sClientName = (String)request.getParameter("cGroup");
String sCiType = (String)request.getParameter("ciType");
String sCiText = (String)request.getParameter("ciText");
%>
<script type="text/javascript">
console.log("보고서 작성 Dialog");
REPORT_OPTION = (function() {
	var _Dlg;
    var _formData = {};

<% if (sReportType.equals("1")) { %>    
    $("#rptopt_search_period").datepicker({
        showHour: false,
        showMinute: false,
        showSecond: false,
        showTime: false,
        dateFormat: 'yy-mm-dd',
    });
    
    var today = new Date();
    today.setDate(today.getDate() - 1);
    $("#rptopt_search_period").datetimepicker('setDate', today);
    
<% } else if (sReportType.equals("2")) { %>

	function makeWeekSelectOptions() {
	    
		var base = 0;
	    var slctMonth = $("#rptopt_search_period").val();
	    var slctArr = slctMonth.split("-");
	    var year = slctArr[0];
	    var month = slctArr[1];
	 
	    var today = new Date();

	    var sdate = new Date(year, month-1, 01);
	    var lastDay = (new Date(sdate.getFullYear(), sdate.getMonth()+1, 0)).getDate();
	    var endDate = new Date(sdate.getFullYear(), sdate.getMonth(), lastDay);
	 
	    var week = sdate.getDay() >= base ? (sdate.getDay() - base) : sdate.getDay() + (7 - base);
	    sdate.setDate(sdate.getDate() - week);
	    var edate = new Date(sdate.getFullYear(), sdate.getMonth(), sdate.getDate());
	 
	    var obj = document.getElementById("rptopt_sh_week");
	    obj.options.length = 0;
	    var seled = "";
	    while(endDate.getTime() >= edate.getTime()) {
	 
	        var sYear = sdate.getFullYear();
	        var sMonth = (sdate.getMonth()+1);
	        var sDay = sdate.getDate();
	 
	        sMonth = (sMonth < 10) ? "0"+sMonth : sMonth;
	        sDay = (sDay < 10) ? "0"+sDay : sDay;
	 
	        var stxt = sYear + "-" + sMonth + "-" + sDay;
	 
	        edate.setDate(sdate.getDate() + 6);
	 
	        var eYear = edate.getFullYear();
	        var eMonth = (edate.getMonth()+1);
	        var eDay = edate.getDate();
	 
	        eMonth = (eMonth < 10) ? "0"+eMonth : eMonth;
	        eDay = (eDay < 10) ? "0"+eDay : eDay;
	 
	        var etxt = eYear + "-" + eMonth + "-" + eDay;
	 
	        if(today.getTime() >= sdate.getTime() && today.getTime() <= edate.getTime()) {
	            if(seled) obj.value = seled;
	            return;
	        }
	 
	        obj.options[obj.options.length] = new Option(stxt+"~"+etxt, stxt+"|"+etxt);
	        seled = stxt+"|"+etxt;
	        
	        sdate = new Date(edate.getFullYear(), edate.getMonth(), edate.getDate() + 1);
	        edate = new Date(sdate.getFullYear(), sdate.getMonth(), sdate.getDate());
	    }
	}

	makeWeekSelectOptions();
	
    $("#rptopt_search_period").change(function() {
        makeWeekSelectOptions();
    });

<% } else if (sReportType.equals("4")) { %>
    
$("#rptopt_search_period1, #rptopt_search_period2").datepicker({
    showHour: false,
    showMinute: false,
    showSecond: false,
    showTime: false,
    dateFormat: 'yy-mm-dd',
});

var today = new Date();
today.setDate(today.getDate() - 1);
$("#rptopt_search_period2").datetimepicker('setDate', today);
today.setDate(today.getDate() - 1);
$("#rptopt_search_period1").datetimepicker('setDate', today);
    
<% } %>


	$("#accordion").accordion({ 
		active: false,
		header: "> div > h3",
		heightStyle: "content",
		collapsible: true,
		animate: 300,
		activate: function( event, ui ) {
			try {
			    _formData[ui.oldHeader.attr("assetCode")] = FORM_OPTION.done();
			} catch(e) {}
			
			ui.oldPanel.children().remove();
			ui.newPanel.load("/product/formUI?option=" + ui.newHeader.attr("reportType") + "_" + ui.newHeader.attr("prefix") , function() {

				if (!_formData[ui.newHeader.attr("assetCode")]) {
                       $.ajax({
                           url : "/product/assetFormInfo",
                           type : 'POST',
                           data : {
                                 "prefix": ui.newHeader.attr("prefix").toLowerCase(), 
                                 "reportType": ui.newHeader.attr("reportType"), 
                                 "assetCode": ui.newHeader.attr("assetCode")
                           },
                           dataType: 'json',
                           success : function(data){
                           	_formData[ui.newHeader.attr("assetCode")] = { "data" : data.formMap, "formInfo" : data.formInfo };
                           	FORM_OPTION.init(_formData[ui.newHeader.attr("assetCode")], ui.newHeader.attr("assetCode"));
                           }
                       });
				} else {
					var formInfo = {
						    "prefix": ui.newHeader.attr("prefix").toLowerCase(), 
						    "reportType": ui.newHeader.attr("reportType"), 
						    "assetCode": ui.newHeader.attr("assetCode")
						    };
					FORM_OPTION.init(_formData[ui.newHeader.attr("assetCode")],  ui.newHeader.attr("assetCode"));
				} 			
			});
		}
	})
	.sortable({
        axis: "y",
        handle: "h3",
        items: ".Group",
        cancel: ".DontMove",
        stop: function( event, ui ) {
          ui.item.children( "h3" ).triggerHandler( "focusout" );
        }
    });
	
	$("#rptopt_chk_review").change(function() {
	    if ($("#rptopt_chk_review").is(":checked")) {
	    	$("#rptopt_review_tr").show();
	    } else {
	    	$("#rptopt_review_tr").hide();
	    }	
	});
	
	$("input[name=rptopt_rptSct]").change(function() {
		if ($("input[name=rptopt_rptSct]:checked").val() == '1') {
            $(".DontMove").show();
            $(".Group").hide();
		} else if ($("input[name=rptopt_rptSct]:checked").val() == '2') {
            $(".DontMove").hide();
            $(".Group").show();
		} else if ($("input[name=rptopt_rptSct]:checked").val() == '3') {
			$(".DontMove").show();
			$(".Group").show();
		}
	});
	
    return {
        init : function(Dlg) {
        	_Dlg = Dlg;
            
            _Dlg.dialog({
                autoOpen: false,
                resizable: false,
                width: 1050, 
                height: 700, 
                modal: true,
                title: "보고서출력",
                buttons: {
                    "보고서출력": function() {
                    	stop();
                    	console.log(_formData);
                    	
                        if ($("#accordion .ui-accordion-header[aria-selected=true]").attr("assetCode")) {
                        	_formData[$("#accordion .ui-accordion-header[aria-selected=true]").attr("assetCode")] = FORM_OPTION.done();	
                        }
                                            	
                    	var formDatas = [];
                    	$.each($("#accordion").find("h3"), function(idx, temp) {
                    		console.log($(temp).attr("assetCode"));
                    		var param = {"assetCode": $(temp).attr("assetCode"), "prefix": $(temp).attr("prefix"), "formData": _formData[$(temp).attr("assetCode")]}
                    		formDatas.push(param);
                    	});
                    	
                    	console.log(formDatas);

                        var printInfo = {};
                        printInfo["ciType"] = "<%=sCiType%>";
                        printInfo["ciText"] = "<%=StringUtil.convertString(sCiText)%>";
                        printInfo["groupCode"] = "<%=sGroupCode%>";
                        printInfo["reportType"] = "<%=sReportType%>";
                    	printInfo["reportSct"] =  $("input[name=rptopt_rptSct]:checked").val();
                        printInfo["formDatas"] = formDatas;
                
                    <% if (sReportType.equals("1")) { %>
                    	printInfo["searchDate"] = $("#rptopt_search_period").val();
                    <% } else if (sReportType.equals("2")) { %>
                        printInfo["searchDate"] = $("#rptopt_sh_week :selected").val();
                    <% } else if (sReportType.equals("3")) { %>
                        printInfo["searchDate"] = $("#rptopt_search_period").val();
                    <% } else if (sReportType.equals("4")) { %>
                        printInfo["searchDate"] = $("#rptopt_search_period1").val() +"|"+ $("#rptopt_search_period2").val();
                    <% } %>
                    
                        if ($("#rptopt_chk_review").is(":checked")) {
                        	printInfo["reviewText"] = $("#rptopt_review").val();
                        }
                        printInfo["format"] =  $("input[name=rptopt_format_radio]:checked").val();
                        
                        console.log(printInfo);
                    	
                        $.ajax({
                            url : "/report/printReport",
                            type : 'POST',
                            data : { printInfo : JSON.stringify(printInfo) },
                            dataType: 'json',
                            success : function(data){
                                if (data.status == 'success') {
                                } else {
                                }
                            }
                        });
                    	
                    },
                    "취소": function() {
                        _Dlg.dialog("close");
                    }
                },
                close: function( event, ui ) {
                    _Dlg.parent().remove();
                    _Dlg.children().remove();
                },
                open: function( event, ui ) {
                    $('.ui-dialog-buttonpane').find('button:contains("보고서출력")').button({icons: { primary: 'ui-icon-check' }});
                    $('.ui-dialog-buttonpane').find('button:contains("취소")').button({icons: { primary: 'ui-icon-closethick' }});
                }            
            });
            _Dlg.dialog("open");    
            _Dlg.dialog('option', 'position', 'center');                
        }
    };
})();

$(document).ready(function () {
	connect("dykim");
}); 

</script>

        <div class="dialog_Body" style="width: 485px;height: 580px;float: left;overflow-x: hidden;overflow-y: auto;">
            <div style="width:100%;padding-bottom:10px;">
                <h7 style="margin-bottom: 5px;padding-top: 10px;color: #386abb;font-weight: bold;font-size: 16px;"><%=Constants.getReportTypeName(sReportType) %></h7>
                <h8 style="margin-bottom: 5px;padding-top: 10px;color: #747474;font-weight: bold;font-size: 14px;">ㅣ <span id="rptopt_group"><%=sParentName %></span>&nbsp;&gt;&nbsp;<span id="rptopt_client"><%=sClientName %></span></h8>
            </div>
            <div class="rptOptInfo">
            <table width="100%" cellspacing="0" cellpadding="0" summary="보고서리스트">
                <colgroup>
                   <col width="80" />
                   <col width="50" />
                   <col width="70" />
                   <col width="*" />
                </colgroup>
                <tr>
                    <th>보고서제목</th>
                    <td colspan="3">
                <% if (sCiType.equals("text")) { %>
                        <%=StringUtil.convertString(sCiText) %>
                <% } else { %>
                        <img src="imageDownload?type=report&code=<%=sGroupCode%>&pos=ci">
                <% } %>
                    </td>
                </tr>
                <tr>
                    <th>보고유형</th>
                    <td colspan="3">
                        <input id="rptopt_lb13" name="rptopt_rptSct" type="radio" class="chk_rdo_none" value="3" checked/><label for="rptopt_lb13">관제실적+원시로그통계</label>
                        <input id="rptopt_lb14" name="rptopt_rptSct" type="radio" class="chk_rdo_none" value="1" /><label for="rptopt_lb14">관제실적</label>
                        <input id="rptopt_lb15" name="rptopt_rptSct" type="radio" class="chk_rdo_none" value="2" /><label for="rptopt_lb15">원시로그 통계</label>
                    </td>
                </tr>
                <tr>
                    <th>조회기간</th>
                    <td colspan="3">
                <% if (sReportType.equals("1")) { %>
                        <input type="text" name="timepicker" id="rptopt_search_period" readonly class="datetime">&nbsp;&nbsp;&nbsp;&nbsp;
                <% } else if (sReportType.equals("2")) { %>
                        <select id="rptopt_search_period">
				<%                         
				        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
				        
				        Calendar startCal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
				        startCal.setTime(new Date());
				        startCal.add(Calendar.MONTH, -13);
				
				        Calendar endCal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
				        endCal.setTime(new Date());
				        //endCal.add(Calendar.MONTH, -1);
				
				        List<HashMap<String, String>> period = new ArrayList<HashMap<String, String>>();
				        while (endCal.after(startCal)) {
				%>                        
                            <option value="<%=sdf.format(endCal.getTime())%>"><%=sdf.format(endCal.getTime())%></option>
				                            
				<%
				            endCal.add(Calendar.MONTH, -1);
				        } 
				%>                            
                        </select>&nbsp;

                        <select name="rptopt_sh_week" id="rptopt_sh_week">
                        </select>
                <% } else if (sReportType.equals("3")) { %>
                        <select id="rptopt_search_period">
                <%                         
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
                        
                        Calendar startCal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
                        startCal.setTime(new Date());
                        startCal.add(Calendar.MONTH, -6);
                
                        Calendar endCal = new GregorianCalendar( TimeZone.getTimeZone( "GMT+09:00"), Locale.KOREA );
                        endCal.setTime(new Date());
                        endCal.add(Calendar.MONTH, -1);
                
                        List<HashMap<String, String>> period = new ArrayList<HashMap<String, String>>();
                        while (endCal.after(startCal)) {
                %>                        
                            <option value="<%=sdf.format(endCal.getTime())%>"><%=sdf.format(endCal.getTime())%></option>
                                            
                <%
                            endCal.add(Calendar.MONTH, -1);
                        } 
                %>                            
                        </select>&nbsp;
                <% } else if (sReportType.equals("4")) { %>
                    <input type="text" name="timepicker" id="rptopt_search_period1" readonly class="datetime">&nbsp;&nbsp;~&nbsp;&nbsp;<input type="text" name="timepicker" id="rptopt_search_period2" readonly class="datetime">
                <% } %>
                    </td>
                </tr>
                <tr>
                    <th>총평쓰기</th>
                    <td colspan="3"><p><input id="rptopt_chk_review" type="checkbox" class="chkbox" checked/></p></td>
                </tr>
                <tr id="rptopt_review_tr">
                    <th>총평</th>
                    <td colspan="3">
                        <textarea id="rptopt_review" style="width:95%;height:100px"></textarea>
                    </td>
                </tr>
                <tr>
                    <th>출력포맷</th>
                    <td colspan="3">
                        <input id="rptopt_lb11" name="rptopt_format_radio" type="radio" class="chk_rdo_none" value="docx" checked/><img class="checkFormat" src="/img/icon_word.png" alt="워드아이콘" width="16" height="16" /><label for="rptopt_lb11">MS WORD</label> 
                        <input id="rptopt_lb12" name="rptopt_format_radio" type="radio" class="chk_rdo_none" value="pdf" /><img class="checkFormat" src="/img/icon_pdf.png" width="16" height="16" /><label for="rptopt_lb12">PDF</label>
                    </td>
                </tr>
            </table>
            </div>
        </div>

        <div id="report_forms_div"  class="dialog_Body" style="width: 490px;height: 580px;float: left;overflow-y: scroll;margin-left: 10px;">
        
			<div id="accordion">
	            <div class="DontMove">
		            <h3 reportType="<%=sReportType %>" prefix="servicedesk" assetCode="<%=sGroupCode%>">서비스데스크</h3>
		            <div>
		            </div>
			    </div>
			<% for (HashMap<String, Object> hMap : slctAssetList) { %>
			    <div class="Group">
					<h3 reportType="<%=sReportType %>" prefix="<%=StringUtil.convertString(hMap.get("prefix")).toLowerCase() %>" assetCode="<%=StringUtil.convertString(hMap.get("id"))%>">
		                <%=StringUtil.convertString(hMap.get("name")) %>
		                <div style="float:right;"><%=StringUtil.convertString(hMap.get("assetType")) %></div>
		                <div style="float:right;width:80px;"><%=StringUtil.convertString(hMap.get("prefix")) %></div>
		            </h3>
	    			<div>
		       		</div>
				</div>
			<% } %>
			
			</div>
        
        
        </div>
            