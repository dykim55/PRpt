<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

	PROM_ALERT = (function() {

        return {
            init: function(type, msg) {
            	
            	if (!msg) {
            		msg = 'ì²ë¦¬ì¤ ì¤ë¥ê° ë°ìíìµëë¤.';
            	}
            	$("#prom_alert_msg").html(msg);
            	if (type == 1) {
            		$("#prom_alert_body").removeClass();
            		$("#prom_alert_body").addClass("dialog_Body success");
            	} else if (type == 2) {
                    $("#prom_alert_body").removeClass();
                    $("#prom_alert_body").addClass("dialog_Body fail");
                } else if (type == 3) {
                    $("#prom_alert_body").removeClass();
                    $("#prom_alert_body").addClass("dialog_Body warning");
                }
            } //End init();
        }; //End return;
		
	})(); //End P11_GROUP
	
jQuery(document).ready(function(){    
}); //End jQuery(document).ready

</script>

<div id="prom_alert_body" class="dialog_Body success">
    <span id="prom_alert_msg"></span>
</div>
 