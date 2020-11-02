$package("chis.application.hivs.script")

chis.application.hivs.script.HIVSTemplate = {
	getBasicInformationHTML : function(jobTitle) {
		var res = "";
		if(jobTitle == "chis.14"){
			res='  <tr id="screenResTr">'+
			'    <td class="red"><strong>筛查结果</strong></td>'+
			'    <td colspan="2">'+
			'		<div id="div_screeningResult'+this.idPostfix+'">'+
			'        <input type="radio" name="screeningResult" value="0" id="screeningResult_0'+this.idPostfix+'" />阴性'+
			'        <input type="radio" name="screeningResult" value="1" id="screeningResult_1'+this.idPostfix+'" />待复检'+
			'		</div>'+
			'    </td>'+
			'  </tr>';
		}
		var html = '<form id="HivsForm"><div class="basicInfo" width="900" align="center" style="background-color:#eee;">'+
		'<table width="663" border="1">'+
		'  <tr>'+
		'    <td width="215" class="red"><strong>筛查日期</strong>'+
		'    </td>'+
		'    </td>'+
		'    <td width="231">'+
		'		<div id="div_screeningDate'+this.idPostfix+'" style="display:inline-block;"/>'+
		'    </td>'+
		'  </tr>'+
		'  <tr>'+
		'    <td width="215" class="red"><strong>近10年外出史</strong><br>(如果有，请选择期间连续未返乡时长，否则选无)'+
		'    </td>'+
		'    <td width="231">'+
		'		<div id="div_outHistory'+this.idPostfix+'">'+
		'    		    <input type="radio" name="outHistory" value="0" id="outHistory_0'+this.idPostfix+'" />无'+
		'      		<br><input type="radio" name="outHistory" value="1" id="outHistory_1'+this.idPostfix+'" />3个月以下'+
		'    		<br><input type="radio" name="outHistory" value="2" id="outHistory_2'+this.idPostfix+'" />3-6个月'+
		'    		<br><input type="radio" name="outHistory" value="3" id="outHistory_3'+this.idPostfix+'" />6-12个月'+
		'    		<br><input type="radio" name="outHistory" value="4" id="outHistory_4'+this.idPostfix+'" />1年以上'+
		'		</div>'+
		'    </td>'+
		'  </tr>'+
		'  <tr>'+
		'    <td colspan="3">'+
		'      <input type="checkbox" name="seperationTM" id="seperationTM_1'+this.idPostfix+'" value="1"/>夫妻分居超过三个月'+
		'	 </td>'+
		'  </tr>'+
		'  <tr>'+
		'    <td colspan="3">'+
		'		<input type="checkbox" name="widowedHY" id="widowedHY_1'+this.idPostfix+'" value="1" />丧偶超过半年'+
		'    </td>'+
		'  </tr>'+
		'  <tr>'+
		'    <td colspan="3">'+
		'		<input type="checkbox" name="otherCheckbox" id="otherCheckbox_1'+this.idPostfix+'" value="1" />其他'+
		'		<input type="text" class="input_btline" name="other" id="other'+this.idPostfix+'" />'+
		'    </td>'+
		'  </tr>'+
		'  <tr>'+
		'    <td class="red"><strong>手术</strong></td>'+
		'    <td colspan="2">'+
		'		<div id="div_operation'+this.idPostfix+'">'+
		'        <input type="radio" name="operation" value="1" id="operation_1'+this.idPostfix+'" />有'+
		'        <input type="radio" name="operation" value="0" id="operation_0'+this.idPostfix+'" />无'+
		'		</div>'+
		'    </td>'+
		'    </tr>'+
		'  <tr>'+
		'    <td class="red"><strong>输血</strong></td>'+
		'    <td colspan="2">'+
		'		<div id="div_transfusion'+this.idPostfix+'">'+
		'        <input type="radio" name="transfusion" value="1" id="transfusion_1'+this.idPostfix+'" />有'+
		'        <input type="radio" name="transfusion" value="0" id="transfusion_0'+this.idPostfix+'" />无'+
		'		</div>'+
		'	</td>'+
		'  </tr>'+ res	
		'</table></div></form>';
		return html;
	}
}