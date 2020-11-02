$package("chis.application.mhc.script.postnatalVisit");

chis.application.mhc.script.postnatalVisit.PostnatalVisitInfoHtmlTemplate = {
	getHTMLTemplate : function() {
		var html = '<div class="my">'
				+ '<input id="visitId'
				+ this.idPostfix
				+ '" name="visitId" type="hidden" title="检查单号"/>'
				+ '<input id="pregnantId'
				+ this.idPostfix
				+ '" name="pregnantId" type="hidden" title="孕妇档案编号"/>'
				+ '<input id="empiId'
				+ this.idPostfix
				+ '" name="empiId" type="hidden" title="EMPIID"/>'
				+ '<table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
				+ '<tr>'
				+ '<td width="20%" colspan="2" align="center"><strong style="color:red;">随访日期</strong></td>'
				+ '<td><div id="div_visitDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">出生日期</strong></td>'
				+ '<td><div id="div_birthDay'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>产后天数</strong></td>'
				+ '<td><input type="text" id="postnatalDays'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">体温</strong></td>'
				+ '<td><input type="text" id="temperature'
				+ this.idPostfix
				+ '" class="width80 input_btline" />℃</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">一般健康情况</strong></td>'
				+ '<td><input type="text" class="input_btline" id="healthState'
				+ this.idPostfix
				+ '" style="width:96%;" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">一般心理状况</strong></td>'
				+ '<td><input type="text" class="input_btline" id="psychologyState'
				+ this.idPostfix
				+ '" style="width:96%;" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">血压</strong></td>'
				+ '<td><input type="text" id="constriction'
				+ this.idPostfix
				+ '" title="收缩压" value="收缩压" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999" class="width60 input_btline" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')"  />/<input type="text" id="diastolic'
				+ this.idPostfix
				+ '" title="舒张压" value="舒张压" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999" class="width60 input_btline" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')"  />mmHg</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>乳房</strong></td>'
				+ '<td><input type="radio" name="breast" id="breast_1'
				+ this.idPostfix
				+ '" value="1" />未见异常　　<input type="radio" name="breast" id="breast_2'
				+ this.idPostfix
				+ '" value="2" />异常 <input type="text" id="breastText'
				+ this.idPostfix
				+ '" class="input_btline"  size="64"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">恶露</strong></td>'
				+ '<td><div id="div_lochia'
				+ this.idPostfix
				+ '"><input type="radio" name="lochia" id="lochia_1'
				+ this.idPostfix
				+ '" value="1" />未见异常　　<input type="radio" name="lochia" id="lochia_2'
				+ this.idPostfix
				+ '" value="2" />异常 <input type="text" id="lochiaText'
				+ this.idPostfix
				+ '" class="input_btline"  size="64"/></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">恶露色</strong></td>'
				+ '<td><div id="div_lochiaColor'
				+ this.idPostfix
				+ '"><input type="radio" name="lochiaColor" id="lochiaColor_1'
				+ this.idPostfix
				+ '" value="1" />红  <input type="radio" name="lochiaColor" id="lochiaColor_2'
				+ this.idPostfix
				+ '" value="2" />淡红  <input type="radio" name="lochiaColor" id="lochiaColor_3'
				+ this.idPostfix
				+ '" value="3" />白</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">恶露臭</strong></td>'
				+ '<td><div id="div_lochiaStench'
				+ this.idPostfix
				+ '"><input type="radio" name="lochiaStench" id="lochiaStench_y'
				+ this.idPostfix
				+ '" value="y" />是　　<input type="radio" name="lochiaStench" id="lochiaStench_n'
				+ this.idPostfix
				+ '" value="n" />否</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">恶露量</strong></td>'
				+ '<td><div id="div_lochiaMeasure'
				+ this.idPostfix
				+ '"><input type="radio" name="lochiaMeasure" id="lochiaMeasure_1'
				+ this.idPostfix
				+ '" value="1" />多　　<input type="radio" name="lochiaMeasure" id="lochiaMeasure_2'
				+ this.idPostfix
				+ '" value="2" />少</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">子宫</strong></td>'
				+ '<td><div id="div_uterus'
				+ this.idPostfix
				+ '"><input type="radio" name="uterus" id="uterus_1'
				+ this.idPostfix
				+ '" value="1" />未见异常　　<input type="radio" name="uterus" id="uterus_2'
				+ this.idPostfix
				+ '" value="2" />异常 <input type="text" id="uterusText'
				+ this.idPostfix
				+ '" class="input_btline"  size="64"/></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">伤口</strong></td>'
				+ '<td><div id="div_wound'
				+ this.idPostfix
				+ '"><input type="radio" name="wound" id="wound_1'
				+ this.idPostfix
				+ '" value="1" />未见异常　　<input type="radio" name="wound" id="wound_2'
				+ this.idPostfix
				+ '" value="2" />异常 <input type="text" id="woundText'
				+ this.idPostfix
				+ '" class="input_btline"  size="64"/></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">宫底高度</strong></td>'
				+ '<td><input type="text" id="palace'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">愈合情况</strong></td>'
				+ '<td><div id="div_healing'
				+ this.idPostfix
				+ '"><input type="radio" name="healing" id="healing_y'
				+ this.idPostfix
				+ '" value="y" />是　　<input type="radio" name="healing" id="healing_n'
				+ this.idPostfix
				+ '" value="n" />否</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong style="color:red;">感染</strong></td>'
				+ '<td><div id="div_infectant'
				+ this.idPostfix
				+ '"><input type="radio" name="infectant" id="infectant_y'
				+ this.idPostfix
				+ '" value="y" />是　　<input type="radio" name="infectant" id="infectant_n'
				+ this.idPostfix
				+ '" value="n" />否</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>其他</strong></td>'
				+ '<td><input type="text" id="other'
				+ this.idPostfix
				+ '" size="60" class="input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>分类</strong></td>'
				+ '<td><input type="radio" name="classification" id="classification_1'
				+ this.idPostfix
				+ '" value="1" />未见异常　　<input type="radio" name="classification" id="classification_2'
				+ this.idPostfix
				+ '" value="2" />异常 <input type="text" id="classificationText'
				+ this.idPostfix
				+ '" class="input_btline"  size="64"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>指导</strong></td>'
				+ '<td><input type="checkbox" name="suggestion" id="suggestion_01'
				+ this.idPostfix
				+ '" value="01" />个人卫生  <input type="checkbox" name="suggestion" id="suggestion_02'
				+ this.idPostfix
				+ '" value="02" />心理  <input type="checkbox" name="suggestion" id="suggestion_03'
				+ this.idPostfix
				+ '" value="03" />营养  <input type="checkbox" name="suggestion" id="suggestion_09'
				+ this.idPostfix
				+ '" value="09" />母乳喂养  <input type="checkbox" name="suggestion" id="suggestion_10'
				+ this.idPostfix
				+ '" value="10" />新生儿护理与喂养   <input type="checkbox" name="suggestion" id="suggestion_99'
				+ this.idPostfix
				+ '" value="99" />其他<input type="text" id="otherSuggestion'
				+ this.idPostfix
				+ '" class="input_btline" size="26"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td rowspan="3" colspan="2" align="center"><strong>转诊建议</strong></td>'
				+ '<td><input type="radio" id="referral_n'
				+ this.idPostfix
				+ '" name="referral" value="n"/>无　　<input type="radio" id="referral_y'
				+ this.idPostfix + '" name="referral" value="y" />有</td>'
				+ '</tr>' + '<tr>'
				+ '<td>原因:<input type="text" id="reason' + this.idPostfix
				+ '" style="width:91%;" class="input_btline"/></td>' + '</tr>'
				+ '<tr>'
				+ '<td>机构及科室:<input type="text" id="doccol' + this.idPostfix
				+ '" style="width:86%;" class="input_btline" /></td>' + '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>下次访视日期</strong></td>'
				+ '<td><div id="div_nextVisitDate' + this.idPostfix
				+ '"></div></td>' + '</tr>' + '<tr>'
				+ '<td colspan="2" align="center"><strong>随访医生签名</strong></td>'
				+ '<td colspan="3"><div id="div_checkDoctor' + this.idPostfix
				+ '"></div></td></tr></table></div>';
		return html;
	}
}