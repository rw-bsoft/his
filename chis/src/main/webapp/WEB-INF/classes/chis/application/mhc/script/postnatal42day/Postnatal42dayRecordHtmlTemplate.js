$package("chis.application.mhc.script.postnatal42day");

chis.application.mhc.script.postnatal42day.Postnatal42dayRecordHtmlTemplate = {
	getHTMLTemplate : function() {
		var html = '<div class="my">'
				+ '<input id="recordId'
				+ this.idPostfix
				+ '" name="recordId" type="hidden" title="检查单号"/>'
				+ '<input id="pregnantId'
				+ this.idPostfix
				+ '" name="pregnantId" type="hidden" title="孕妇档案编号"/>'
				+ '<input id="empiId'
				+ this.idPostfix
				+ '" name="empiId" type="hidden" title="EMPIID"/>'
				+ '<input id="checkManaUnit'
				+ this.idPostfix
				+ '" name="checkManaUnit" type="hidden" title="检查管理单元"/>'
				+ '<table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
				+ '<tr>'
				+ '<td width="15%" colspan="2" align="center"><strong style="color:red;">随访日期</strong></td>'
				+ '<td colspan="3"><div id="div_checkDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">产后天数</strong></td>'
				+ '<td colspan="3"><input type="text" id="postnatalDays'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">一般健康情况</strong></td>'
				+ '<td colspan="3"><input type="text" id="healthState'
				+ this.idPostfix
				+ '" style="width:95%;" class="input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">一般心理状况</strong></td>'
				+ '<td colspan="3"><input type="text" id="psychologyState'
				+ this.idPostfix
				+ '" style="width:95%;" class="input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">血压</strong></td>'
				+ '<td colspan="3"><input type="text" title="收缩压(mmHg)" id="constriction'
				+ this.idPostfix
				+ '" class="width60 input_btline" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')" value="收缩压" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"/>/<input type="text" title="舒张压(mmHg)" id="diastolic'
				+ this.idPostfix
				+ '" class="width60 input_btline" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')" value="舒张压" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999" />mmHg </td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">体温</strong></td>'
				+ '<td colspan="3"><input type="text" id="temperature'
				+ this.idPostfix
				+ '" class="width80 input_btline" />℃</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">体重</strong></td>'
				+ '<td><input type="text" id="weight'
				+ this.idPostfix
				+ '" class="width80 input_btline" />kg</td>'
				+ '<td align="center"><strong style="color:red;">脉搏</strong></td>'
				+ '<td><input type="text" id="pulse'
				+ this.idPostfix
				+ '" class="width80 input_btline" />'
				+ 'bpm</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>乳房</strong></td>'
				+ '<td colspan="3"><input type="radio" id="breast_1'
				+ this.idPostfix
				+ '" name="breast" value="1" checked="true"/>未见异常　　<input type="radio" id="breast_2'
				+ this.idPostfix
				+ '" name="breast" value="2" />异常 <input type="text" id="breastText'
				+ this.idPostfix
				+ '" class="input_btline" size="59"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>恶露</strong></td>'
				+ '<td colspan="3"><input type="radio" id="lochia_1'
				+ this.idPostfix
				+ '" name="lochia" value="1" checked="true"/>未见异常　　<input type="radio" id="lochia_2'
				+ this.idPostfix
				+ '" name="lochia" value="2" />异常 <input type="text" id="lochiaText'
				+ this.idPostfix
				+ '" class="input_btline" size="59"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">子宫</strong></td>'
				+ '<td colspan="3"><div id="div_uterus'
				+ this.idPostfix
				+ '"><input type="radio" id="uterus_1'
				+ this.idPostfix
				+ '" name="uterus" value="1" checked="true"/>未见异常　　<input type="radio" id="uterus_2'
				+ this.idPostfix
				+ '" name="uterus" value="2" />异常 <input type="text" id="uterusText'
				+ this.idPostfix
				+ '" class="input_btline" size="59"/></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">伤口</strong></td>'
				+ '<td colspan="3"><div id="div_wound'
				+ this.idPostfix
				+ '"><input type="radio" id="wound_1'
				+ this.idPostfix
				+ '" name="wound" value="1" checked="true"/>未见异常　　<input type="radio" id="wound_2'
				+ this.idPostfix
				+ '" name="wound" value="2" />异常 <input type="text" id="woundText'
				+ this.idPostfix
				+ '" class="input_btline" size="59"/></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">愈合情况</strong></td>'
				+ '<td colspan="3"><div id="div_healing'
				+ this.idPostfix
				+ '"><input type="radio" id="healing_y'
				+ this.idPostfix
				+ '" name="healing" value="y" />是　　<input type="radio" id="healing_n'
				+ this.idPostfix
				+ '" name="healing" value="n" />否</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">感染</strong></td>'
				+ '<td colspan="3"><div id="div_infectant'
				+ this.idPostfix
				+ '"><input type="radio" id="infectant_y'
				+ this.idPostfix
				+ '" name="infectant" value="y" />是　　<input type="radio" id="infectant_n'
				+ this.idPostfix
				+ '" name="infectant" value="n" />否</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>其他</strong></td>'
				+ '<td colspan="3"><input type="text" id="other'
				+ this.idPostfix
				+ '" style="width:95%;" class="input_btline"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>分类</strong></td>'
				+ '<td colspan="3"><input type="radio" id="classification_1'
				+ this.idPostfix
				+ '" name="classification" value="1" />已恢复　　<input type="radio" id="classification_2'
				+ this.idPostfix
				+ '" name="classification" value="2" />未恢复 <input type="text" id="classificationText'
				+ this.idPostfix
				+ '" class="input_btline" size="59"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>指导</strong></td>'
				+ '<td colspan="3"><input type="checkbox" id="suggestion_11'
				+ this.idPostfix
				+ '" name="suggestion" value="11" />性保健  <input type="checkbox" id="suggestion_12'
				+ this.idPostfix
				+ '" name="suggestion" value="12" />避孕  <input type="checkbox" id="suggestion_13'
				+ this.idPostfix
				+ '" name="suggestion" value="13" />婴儿喂养及营养  <input type="checkbox" id="suggestion_99'
				+ this.idPostfix
				+ '" name="suggestion" value="99" />其他 <input type="text" id="suggestionText'
				+ this.idPostfix
				+ '" class="input_btline" size="41"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" rowspan="3" align="center"><strong>处理</strong></td>'
				+ '<td colspan="3"><input type="radio" id="treat_1'
				+ this.idPostfix
				+ '" name="treat" value="1" />结案　　<input type="radio" id="treat_2'
				+ this.idPostfix + '" name="treat" value="2" />转诊</td>'
				+ '</tr>' + '<tr>'
				+ '<td colspan="3">原因:<input type="text" id="reason'
				+ this.idPostfix
				+ '" style="width:91%;" class="input_btline"/></td>' + '</tr>'
				+ '<tr>'
				+ '<td colspan="3">机构及科室:<input type="text" id="doccol'
				+ this.idPostfix
				+ '" style="width:86%;" class="input_btline"/></td>' + '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>随访医生签名</strong></td>'
				+ '<td colspan="3"><div id="div_checkDoctor' + this.idPostfix
				+ '"></div></td></tr></table></div>';
		return html;
	}
}