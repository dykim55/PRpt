<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

	FILE_UPLOAD = (function() {
	    var _Dlg;

	    return {
        	done: function() {
        		var formData = new FormData($("#upload_form")[0]);
        	    return formData;        	    
        	},
            init: function(code, pos, dlg) {
            	_Dlg = dlg;
            	$("#uploadClientCode").val(code);
            	$("#uploadPositionType").val(pos);
            	
            	if (pos == "ci") {
            		$(".fileSearch").append('<div class="fileSearch_info">이미지 Size는<span class="chart_seriesData03"> (w)300 * (h)80</span> 를 권장하며, 파일크기는<span class="chart_seriesData03">3MB</span>이하로 제한합니다.</div>');            		
            	} else {
            		$(".fileSearch").append('<div class="fileSearch_info">이미지 Size는<span class="chart_seriesData03"> (w)300 * (h)28</span> 를 권장하며, 파일크기는<span class="chart_seriesData03">3MB</span>이하로 제한합니다.</div>');            		
            	}
            	
            	_Dlg.dialog({                
                    autoOpen: false, 
                    resizable: false, 
                    width: 460, 
                    height: 150, 
                    modal: true,
                    title: "파일찾기",
                    buttons: {
                        "취소": function() {
                        	_Dlg.dialog("close");
                        },                  
                        "저장": function() {
                            $.ajax({
                                url : "/report/imageUpload",
                                type : 'POST',
                                data : FILE_UPLOAD.done(),
                                dataType: 'json',
                                processData: false,
                                contentType: false,                        
                                success : function(data){
                                	_Dlg.dialog("close");
                                }
                            });
                        }
                    },
                    open: function () {
                        $('.ui-dialog-buttonpane').
                        find('button:contains("저장")').button({
                            icons: { primary: 'ui-icon-check' }
                        });
                        $('.ui-dialog-buttonpane').
                        find('button:contains("취소")').button({
                            icons: { primary: 'ui-icon-closethick' }
                        });
                        $(this).parent().find('.ui-dialog-buttonset>button:contains("취소")').css('float','right');
                        $(this).parent().find('.ui-dialog-buttonset>button:contains("저장")').css('float','right');
                    },
                    close: function( event, ui ) {
                        $("#rptopt_ci_img")[0].src = $("#rptopt_ci_img")[0].src;
                        _Dlg.children().remove();
                    }
                });
            	_Dlg.dialog('option', 'position', 'center');
            	_Dlg.dialog("open");             	
            	
            } //End init();
        }; //End return;
		
	})(); //End INPUT_FILENAME
	
jQuery(document).ready(function(){    
}); //End jQuery(document).ready

</script>

<form method="post" name="upload_form" id="upload_form" enctype="multipart/form-data">
<input type="hidden" name="uploadType" value="2"/>
<input type="hidden" name="positionType" id="uploadPositionType"/>
<input type="hidden" name="clientCode" id="uploadClientCode"/>
 
<div class="fileSearch">
    <div class="fileSearchWrap">
        <input type="text" id="file_route" class="fileSearch_txt" readonly="readonly" title="첨부된 파일경로"/>
        <span class="fileopenWrap">
            <input type="file" name="upload_file" id="upload_file" class="fileAddBtn" onchange="javascript:document.getElementById('file_route').value=this.value" />
        </span>
    </div>
</div>
</form>