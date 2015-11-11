stop = function() {
	console.log("stop()");
}

DIALOG = (function() { 
    return {
        Open : function() { 
            var dlg = undefined;
            var len = $('body').find('.templateDialog').length;
            $('body').find('.templateDialog').each(function() {
            	try {
            		if ($(this).dialog("isOpen")) {
                    }            		
            	} catch(e) { dlg = $(this); return false; }
            });
            
            if (dlg == undefined) {
                $('body').append('<div id="templateDialog'+(len+1)+'" class="templateDialog" title="" style="padding: 8px 8px 3px 8px;overflow: hidden; display:none;"></div>');
                $("#templateDialog"+(len+1)).dialog({autoOpen: false});
                dlg = $("#templateDialog"+(len+1));
            }

            return dlg;
        }
    };
})();

promAlert = function (type, msg, obj, w, h) {

    DIALOG.Open().load("/main/promAlert", function() {
        var Dlg = $(this);

        PROM_ALERT.init(type, msg);
        
        $("#dialog_promMsg").css('width', '360px');
        $("#dialog_promMsg").css('height','auto');
        $("#dialog_promMsg").html(msg);
        h = 140 + (parseInt($("#dialog_promMsg").css('height')) - 15);
        $("#dialog_promMsg").html("");
        
        Dlg.dialog({
            autoOpen: false,
            resizable: false,
            width: w ? w : 380,
            //height: h ? h : 150,
            modal: true,
            title: "알림",
            buttons: {
                "확인": function() {
                    $(this).dialog("close");
                }
            },
            close: function( event, ui ) {
                if (obj) {
                    obj();
                }
                Dlg.children().remove();
            },
            open: function( event, ui ) {
                $('.ui-dialog-buttonpane').
                find('button:contains("확인")').button({
                    icons: { primary: 'ui-icon-check' }
                });
                $(this).parent().find('.ui-dialog-buttonset>button:contains("확인")').css('float','right');
            }            
        });
        Dlg.dialog('option', 'position', 'center');
        Dlg.dialog("open");    
    });
}

$.fn.emptySelect = function() {
	return this.each(function() {
		if (this.tagName=='SELECT') {
			this.options.length = 0;
		}
	});
}

$.fn.loadSelect = function(optionsDataArray, display, value, first) {
	return this.emptySelect().each(function() {
		if (this.tagName=='SELECT') {
			var selectElement = this;
			
			if (first != undefined) {
				var optionData = {};
				eval("optionData." + display + "= first");
				eval("optionData." + value + "= ''");
				var option = new Option(eval("optionData." + display), eval("optionData." + value));
				if ($.browser.msie) {
					selectElement.add(option);
				} else {
					selectElement.add(option, null);
				}
			}
			
	        $.each(optionsDataArray, function(idx, optionData) {
				var option = new Option(eval("optionData." + display), eval("optionData." + value));
				if ($.browser.msie) {
					selectElement.add(option);
				} else {
					selectElement.add(option, null);
				}
			});
		}
	});
}

function getFomrType(prefix) {
	var fType = {"ddos":1, "ddos_dp":2, "fw":3, "waf":4, "ips":5, "ids":6, "servicedesk":9};
	return fType[prefix];
}

function formSaveAs() {
    DIALOG.Open().load("/main/promConfirm", function() {
        var Dlg = $(this);
        
        PROM_CONFIRM.init("양식을 수정하면 해당 양식이 적용된 모든 자산에 적용이 됩니다.");
        
        var formData = FORM_OPTION.formData();
        
        Dlg.dialog({
            autoOpen: false,
            resizable: false,
            width: 380,
            height: 150,
            modal: true,
            title: "알림",
            buttons: {
                "양식수정": function() {
                    Dlg.dialog( "close" );
                    $.ajax({
                        url : "/product/formDataSave",
                        type : 'POST',
                        data : { "formCode":formData.formCode, "data": JSON.stringify(FORM_OPTION.done().data) },
                        dataType: 'json',
                        success : function(data){
                            if (data.status == 'success') {
                            } else {
                            	promAlert(1, data.message);
                            }
                        }
                    });
                },
                "다른이름저장": function() {
                	Dlg.dialog("close");
                    DIALOG.Open().load("/report/formSaveSimpleDlg", function() {
                        FORM_DLG.init($(this), {"assetCode":formData.assetCode, "formType":formData.formType, "reportType":formData.reportType, "data": JSON.stringify(FORM_OPTION.done().data)} );
                    });                            	
                }
            },
            open: function () {
                $('.ui-dialog-buttonpane').
                find('button:contains("확인")').button({
                    icons: { primary: 'ui-icon-check' }
                });
                $('.ui-dialog-buttonpane').
                find('button:contains("취소")').button({
                    icons: { primary: 'ui-icon-closethick' }
                });
            },
            close: function( event, ui ) {
                Dlg.children().remove();
            }
        });
        Dlg.dialog("open");    
        Dlg.dialog('option', 'position', 'center');                
    });
}




