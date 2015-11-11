<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script type="text/javascript">

	PROM_CONFIRM = (function() {

        return {
            init: function(msg) {
            	$("#prom_confirm_msg").html(msg);
            } //End init();
        }; //End return;
		
	})(); //End P11_GROUP
	
jQuery(document).ready(function(){    
}); //End jQuery(document).ready

</script>

<div id="prom_confirm_body" class="dialog_Body warning">
    <span id="prom_confirm_msg"></span>
</div>
 