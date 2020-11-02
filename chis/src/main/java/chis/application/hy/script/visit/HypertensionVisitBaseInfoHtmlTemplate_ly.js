$package("chis.application.hy.script.visit");

chis.application.hy.script.visit.HypertensionVisitBaseInfoHtmlTemplate_ly = {
	getHTMLTemplate : function() {
		
		var tpl = '<div class="my">'
				+ '<input id="recordId'
				+ this.idPostfix
				+ '" name="recordId" type="hidden" title="检查单号"/>'
				+ '<table width="800" cellpadding="0" cellspacing="0" border="0" id="info_tables2">'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">随访日期</strong></td>'
				+ '<td colspan="2" align="left" class="info_tables"><div id="div_visitDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">随访方式</strong></td>'
				+ '<td colspan="2"><div id="div_visitWay'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="visitWay'
				+ this.idPostfix
				+ '" />门诊就诊'
				+ '<input type="radio" value="2" name="visitWay'
				+ this.idPostfix
				+ '" />站点就诊'
				+ '<input type="radio" value="3" name="visitWay'
				+ this.idPostfix
				+ '" />社区随访'
				+ '<input type="radio" value="4" name="visitWay'
				+ this.idPostfix
				+ '" />上门随访'
				+ '<input type="radio" value="5" name="visitWay'
				+ this.idPostfix
				+ '" />电话随访'
				+ '<input type="radio" value="6" name="visitWay'
				+ this.idPostfix
				+ '" />群组随访'
				+ '<input type="radio" value="9" name="visitWay'
				+ this.idPostfix
				+ '" />其他</div></td>'
				+ '</tr>'
				+ '<tr class="info_tables">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">转归</strong></td>'
				+ '<td><div id="div_visitEffect'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="visitEffect'
				+ this.idPostfix
				+ '" onclick="onVisitEffectChange(value, div_noVisitReason'
				+ this.idPostfix
				+ ',div_visitWay'
				+ this.idPostfix
				+ ',div_medicine'
				+ this.idPostfix
				+ ')"/>'
				+ '继续随访'
				+ '<input type="radio" value="2" name="visitEffect'
				+ this.idPostfix
				+ '" onclick="onVisitEffectChange(value, div_noVisitReason'
				+ this.idPostfix
				+ ',div_visitWay'
				+ this.idPostfix
				+ ',div_medicine'
				+ this.idPostfix
				+ ')"/>'
				+ '暂时失访'
				+ '<input type="radio" value="9" name="visitEffect'
				+ this.idPostfix
				+ '" onclick="onVisitEffectChange(value, div_noVisitReason'
				+ this.idPostfix
				+ ',div_visitWay'
				+ this.idPostfix
				+ ',div_medicine'
				+ this.idPostfix
				+ ')"/>'
				+ '终止管理 </div></td>'
				+ '<td><span id="ZGYY'
				+ this.idPostfix
				+ '">&nbsp;&nbsp;&nbsp;&nbsp; 原因：</span><div id="div_noVisitReason'
				+ this.idPostfix
				+ '" style="display: inline;" title="原因">'
				+ '<input type="radio" name="noVisitReason'
				+ this.idPostfix
				+ '" id="noVisitReason_1'
				+ this.idPostfix
				+ '" value="1" onclick="onVisitReasonChange(value)"/>死亡'
				+ '<input type="radio" name="noVisitReason'
				+ this.idPostfix
				+ '" id="noVisitReason_2'
				+ this.idPostfix
				+ '" value="2" onclick="onVisitReasonChange(value)"/>迁出'
				+ '<input type="radio" name="noVisitReason'
				+ this.idPostfix
				+ '" id="noVisitReason_3'
				+ this.idPostfix
				+ '" value="3" onclick="onVisitReasonChange(value)"/>失访'
				+ '<input type="radio" name="noVisitReason'
				+ this.idPostfix
				+ '" id="noVisitReason_4'
				+ this.idPostfix
				+ '" value="4" onclick="onVisitReasonChange(value)"/>拒绝'
				+ '</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">症状</strong></td>'
				+ '<td colspan="2"><input type="checkbox" value="1" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value,otherSymptoms'
				+ this.idPostfix
				+ ')"/>无症状 '
				+ '<input type="checkbox" value="2" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>头痛头晕'
				+ '<input type="checkbox" value="3" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>恶心呕吐'
				+ '<input type="checkbox" value="4" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>眼花耳鸣'
				+ '<input type="checkbox" value="5" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>呼吸困难'
				+ '<input type="checkbox" value="6" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>心悸胸闷'
				+ '<input type="checkbox" value="7" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>鼻衄出血不止'
				+ '<input type="checkbox" value="8" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>四肢发麻'
				+ '<input type="checkbox" value="9" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value)"/>下肢水肿'
				+ '<input type="checkbox" value="10" id="currentSymptoms_10'
				+ this.idPostfix
				+ '" name="currentSymptoms'
				+ this.idPostfix
				+ '" onclick="onCurrentSymptomsClick(value,otherSymptoms'
				+ this.idPostfix
				+ ')"/>其他&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;其他症状:'
				+ '<input type="text" class="input_btline" id="otherSymptoms'
				+ this.idPostfix
				+ '" size="56" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td width="10%" rowspan="5" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">体征</strong></td>'
				+ '<td width="13%" style="font-weight:bold;">血压（mmHg）</td>'
				+ '<td colspan="2"><input type="text" id="constriction'
				+ this.idPostfix
				+ '" class="input_btline widt80" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')" value="收缩压"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}" style="color:#999"/>'
				+ '/'
				+ '<input type="text" id="diastolic'
				+ this.idPostfix
				+ '" class="input_btline widt80" onchange="onXYChange(constriction'
				+ this.idPostfix
				+ ',diastolic'
				+ this.idPostfix
				+ ')" value="舒张压"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}" style="color:#999"/>'
				+ '</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">体重（kg)</td>'
				+ '<td colspan="2"><input type="text" id="weight'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="当前体重"'
				+ ' onkeypress="value=this.value.replace(/[^0-9\.]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9\.]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}" style="color:#999"'
				+ ' onkeyup="onWeightChange(value,bmi'
				+ this.idPostfix
				+ ')"/>/'
				+ '<input type="text" id="targetWeight'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="目标体重"'
				+ ' onkeypress="value=this.value.replace(/[^0-9\.]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9\.]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}" style="color:#999"'
				+ ' onkeyup="onWeightChange(value,targetBmi'
				+ this.idPostfix
				+ ')"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">体质指数</td>'
				+ '<td colspan="2"><input type="text" id="bmi'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="当前体质指数" disabled=true'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '/'
				+ '<input type="text" id="targetBmi'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="目标体质指数" disabled=true'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}" style="color:#999"/>'
				+ '</td>'
				+ '</tr>'
				+ '<tr>'
				// class="classinput"
				+ '<td style="font-weight:bold;">心率</td>'
				+ '<td colspan="2"><input type="text" id="heartRate'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="当前心率"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '/'
				+ '<input type="text" id="targetHeartRate'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="目标心率"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">其他</td>'
				+ '<td colspan="2"><input id="otherSigns'
				+ this.idPostfix
				+ '" type="text" class="input_btline" size="44"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td rowspan="6" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">生活指导方式</strong></td>'
				+ '<td style="font-weight:bold;">日吸烟量（支）</td>'
				+ '<td colspan="2"><input type="text" id="smokeCount'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="当前日吸烟量"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '/'
				+ '<input type="text" id="targetSmokeCount'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="目标日吸烟量"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">日饮酒量（两）</td>'
				+ '<td colspan="2"><input type="text" id="drinkCount'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="当前日饮酒量"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '/'
				+ '<input type="text" id="targetDrinkCount'
				+ this.idPostfix
				+ '" class="input_btline widt80" value="目标日饮酒量"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">运动</td>'
				+ '<td colspan="2">'
				+ '<input type="text" id="trainTimesWeek'
				+ this.idPostfix
				+ '" class="input_btline" value="当前次数" size="5"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '次/周'
				+ '<input type="text" id="trainMinute'
				+ this.idPostfix
				+ '" class="input_btline" value="当前时长" size="5"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '分钟/次&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;'
				+ '<input type="text" id="targetTrainTimesWeek'
				+ this.idPostfix
				+ '" class="input_btline" value="目标次数" size="5"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '次/周'
				+ '<input type="text" id="targetTrainMinute'
				+ this.idPostfix
				+ '" class="input_btline" value="目标时长" size="5"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"'
				+ ' style="color:#999"/>'
				+ '分钟/次</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">摄盐情况（咸淡）</td>'
				+ '<td colspan="2"><div id="div_salt'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="salt'
				+ this.idPostfix
				+ '" />轻  '
				+ '<input type="radio" value="2" name="salt'
				+ this.idPostfix
				+ '" />中  '
				+ '<input type="radio" value="3" name="salt'
				+ this.idPostfix
				+ '" />重/目标值：'
				+ '<input type="radio" value="1" name="targetSalt'
				+ this.idPostfix
				+ '" />轻'
				+ '<input type="radio" value="2" name="targetSalt'
				+ this.idPostfix
				+ '" />中'
				+ '<input type="radio" value="3" name="targetSalt'
				+ this.idPostfix
				+ '" />重</div>  </td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">心理调整</td>'
				+ ' <td colspan="2"><div id="div_psychologyChange'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="psychologyChange'
				+ this.idPostfix
				+ '" />良好  '
				+ '<input type="radio" value="2" name="psychologyChange'
				+ this.idPostfix
				+ '" />一般  '
				+ '<input type="radio" value="3" name="psychologyChange'
				+ this.idPostfix
				+ '" />差</div></td> '
				+ '</tr>'
				+ '<tr>'
				+ '<td style="font-weight:bold;">遵医行为</td>'
				+ ' <td colspan="2"><div id="div_obeyDoctor'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="obeyDoctor'
				+ this.idPostfix
				+ '" />良好  '
				+ '<input type="radio" value="2" name="obeyDoctor'
				+ this.idPostfix
				+ '" />一般  '
				+ '<input type="radio" value="3" name="obeyDoctor'
				+ this.idPostfix
				+ '" />差</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">辅助检查</strong></td>'
				+ '<td colspan="2"><input type="text" class="input_btline" id="auxiliaryCheck'
				+ this.idPostfix
				+ '" class="widt50" size="56"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">服药依从性</strong></td>'
				+ '<td colspan="2"><div id="div_medicine'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="medicine'
				+ this.idPostfix
				+ '" onclick="onMedicineClick(value'
				+ ',medicineBadEffectText'
				+ this.idPostfix
				+ ')"/>规律   '
				+ '<input type="radio" value="2" name="medicine'
				+ this.idPostfix
				+ '" onclick="onMedicineClick(value'
				+ ',medicineBadEffectText'
				+ this.idPostfix
				+ ')"/>间断   '
				+ '<input type="radio" value="3" name="medicine'
				+ this.idPostfix
				+ '" onclick="onMedicineClick(value'
				+ ',medicineBadEffectText'
				+ this.idPostfix
				+ ')"/>不服药</div><span id="BGLYY' 
				+ this.idPostfix
				+ '">&nbsp;&nbsp;不规律服药原因：</span><div id="div_medicineNot'
				+ this.idPostfix
				+ '" style="display: inline;"><input type="radio" value="1" name="medicineNot'
				+ this.idPostfix
				+ '" onclick="onMedicineNotClick(value)"/>经济原因'
				+ '<input type="radio" value="2" name="medicineNot'
				+ this.idPostfix
				+ '" onclick="onMedicineNotClick(value)"/>忘记'
				+ '<input type="radio" value="3" name="medicineNot'
				+ this.idPostfix
				+ '" onclick="onMedicineNotClick(value)"/>不良反应'
				+ '<input type="radio" value="4" name="medicineNot'
				+ this.idPostfix
				+ '" onclick="onMedicineNotClick(value)"/>配药不方便'
				+ '<input type="radio" value="99" name="medicineNot'
				+ this.idPostfix
				+ '" onclick="onMedicineNotClick(value)"/>其他</div>'
				+ '<input type="text" class="input_btline width170" id="medicineOtherNot'
				+ this.idPostfix
				+ '" disabled="disabled" onblur="onTextMustBlur(value,medicineOtherNot'
				+ this.idPostfix
				+ ')"/></td></tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">药物不良反应</strong></td>'
				+ '<td><input type="radio" value="n" id="medicineBadEffect_n'
				+ this.idPostfix
				+ '" name="medicineBadEffect'
				+ this.idPostfix
				+ '" onclick="onMedicineBadEffectChange(value, medicineBadEffectText'
				+ this.idPostfix
				+ ')"/>无   '
				+ '<input type="radio" value="y" id="medicineBadEffect_y'
				+ this.idPostfix
				+ '" name="medicineBadEffect'
				+ this.idPostfix
				+ '" onclick="onMedicineBadEffectChange(value, medicineBadEffectText'
				+ this.idPostfix
				+ ')"/>有</td>'
				+ '<td colspan="2">不良反应:<input type="text" class="input_btline" id="medicineBadEffectText'
				+ this.idPostfix
				+ '" class="widt50"  size="35" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">此次随访分类</strong></td>'
				+ '<td colspan="2"><div id="div_visitEvaluate'
				+ this.idPostfix
				+ '"><input type="radio" value="1" name="visitEvaluate'
				+ this.idPostfix
				// + '" onclick="onVisitEvaluateClick(value)'
				+ '"/>控制满意   '
				+ '<input type="radio" value="2" name="visitEvaluate'
				+ this.idPostfix
				// + '" onclick="onVisitEvaluateClick(value)'
				+ '"/>控制不满意 '
				+ '<input type="radio" value="3" name="visitEvaluate'
				+ this.idPostfix
				// + '" onclick="onVisitEvaluateClick(value)'
				+ '"/>不良反应   '
				+ '<input type="radio" value="4" name="visitEvaluate'
				+ this.idPostfix
				// + '" onclick="onVisitEvaluateClick(value)'
				+ '"/>并发症</div></td>'
				+ '</tr>'
				//--yx 隐藏
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">健康处方建议</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="checkbox" value="1-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>去医院确定治疗方案'
				+ '<input type="checkbox" value="1-2" name="healthProposal'
				+ this.idPostfix
				+ '"/>坚持按医嘱服药'
				+ '<input type="checkbox" value="1-3" name="healthProposal'
				+ this.idPostfix
				+ '"/>需要调整方案'
				+ '<input type="checkbox" value="1-4" name="healthProposal'
				+ this.idPostfix
				+ '"/>去医院进一步确诊'
				+ '<input type="checkbox" value="2-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>定期测量血压'
				+ '<input type="checkbox" value="2-2" name="healthProposal'
				+ this.idPostfix
				+ '"/>增加测量血压频率'
				+ '<input type="checkbox" value="2-3" name="healthProposal'
				+ this.idPostfix
				+ '"/>接受技能指导'
				+ '<input type="checkbox" value="3-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>阅读发放的宣传材料'
				+ '<input type="checkbox" value="4-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>限制烟量或戒烟'
				+ '<input type="checkbox" value="4-2" name="healthProposal'
				+ this.idPostfix
				+ '"/>戒烟'
				+ '<input type="checkbox" value="4-3" name="healthProposal'
				+ this.idPostfix
				+ '"/>避免被动吸烟'
				+ '<input type="checkbox" value="5-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>减少或不要饮酒'
				+ '<input type="checkbox" value="6-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>限钠盐(＜1斤/3人*月)'
				+ '<input type="checkbox" value="6-2" name="healthProposal'
				+ this.idPostfix
				+ '"/>减少脂肪食品摄入'
				+ '<input type="checkbox" value="6-3" name="healthProposal'
				+ this.idPostfix
				+ '"/>增加鱼、禽、奶制品摄入'
				+ '<input type="checkbox" value="6-4" name="healthProposal'
				+ this.idPostfix
				+ '"/>增加新鲜水果蔬菜摄入'
				+ '<input type="checkbox" value="6-5" name="healthProposal'
				+ this.idPostfix
				+ '"/>减少谷类，面制品摄入'
				+ '<input type="checkbox" value="7-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>开始低强度的运动'
				+ '<input type="checkbox" value="7-2" name="healthProposal'
				+ this.idPostfix
				+ '"/>接受技能指导'
				+ '<input type="checkbox" value="7-3" name="healthProposal'
				+ this.idPostfix
				+ '"/>逐步增加运动强度或延长运动时间'
				+ '<input type="checkbox" value="7-4" name="healthProposal'
				+ this.idPostfix
				+ '"/>逐步减少运动强度或缩短运动时间'
				+ '<input type="checkbox" value="8-1" name="healthProposal'
				+ this.idPostfix
				+ '"/>放松心情，调节睡眠，注意休息'
				+ '</td>'
				+ '</tr>'
				+ '<tr hidden="true">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">危险因素</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="checkbox" value="1" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>男性＞55 岁或女性＞65 岁'
				+ '<input type="checkbox" value="2" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>吸烟'
				+ '<input type="checkbox" value="3" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>糖耐量受损'
				+ '<input type="checkbox" value="4" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>血脂异常;TC≥5.7mmol/L(220mg/dL);或LDL-C＞3.6mmol/L(140mg/dL);或HDL-C＜1.0mmol/L(40mg/dL)'
				+ '<input type="checkbox" value="5" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>早发心血管病家族史(一级亲属发病年龄男性小于55岁，女性小于65岁)'
				+ '<input type="checkbox" value="6" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>腹型肥胖'
				+ '<input type="checkbox" value="7" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>血同型半胱氨酸升高'
				+ '<input type="checkbox" value="8" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>糖尿病伴微白蛋白尿'
				+ '<input type="checkbox" value="9" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>以静息为主的生活方式'
				+ '<input type="checkbox" value="10" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>血浆纤维蛋白原增高'
				+ '<input type="checkbox" value="11" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>高敏C反应蛋白≥3mg/L或C反应蛋白≥10mg/L'
				+ '<input type="checkbox" value="12" name="riskiness'
				+ this.idPostfix
				+ '" onclick="onRiskinessClick(value)"/>无'
				+ '</td>'
				+ '</tr>'
				+ '<tr hidden="true">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">并发症</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="checkbox" value="1" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>缺血性卒中'
				+ '<input type="checkbox" value="2" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>脑出血'
				+ '<input type="checkbox" value="3" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>短暂性脑缺血发作(TIA)'
				+ '<input type="checkbox" value="4" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>心肌梗死'
				+ '<input type="checkbox" value="5" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>心绞痛'
				+ '<input type="checkbox" value="6" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>冠状动脉血运重建史'
				+ '<input type="checkbox" value="7" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>慢性心力衰竭'
				+ '<input type="checkbox" value="8" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>充血性心力衰竭'
				+ '<input type="checkbox" value="9" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>糖尿病肾病'
				+ '<input type="checkbox" value="10" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>肾功能衰竭(血肌酐:男性>=1.5mg/dl,女性>=1.4mg.dl,蛋白尿>=300mg/24h)'
				+ '<input type="checkbox" value="11" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>外周血管疾病'
				+ '<input type="checkbox" value="12" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>视网膜病变(出血或渗出，视乳头水肿)'
				+ '<input type="checkbox" value="13" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>糖尿病'
				+ '<input type="checkbox" value="14" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>夹层动脉瘤'
				+ '<input type="checkbox" value="15" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>症状性动脉疾病'
				+ '<input type="checkbox" value="16" name="complication'
				+ this.idPostfix
				+ '" onclick="onComplicationClick(value)"/>以上都无'
				+ '</td>'
				+ '</tr>'
				+ '<tr hidden="true">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">靶器官损害</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="checkbox" value="1" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>左心室肥厚(心电图、超声心动图或X线)'
				+ '<input type="checkbox" value="2" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>颈动脉超声IMT>=0.9mm或动脉粥样斑块;超声或X线证实有动脉粥样斑块(颈、髂、股或主动脉)'
				+ '<input type="checkbox" value="3" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>颈-股动脉搏波速度 ＞12m/s'
				+ '<input type="checkbox" value="4" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>踝/臂血压指数 ＜ 0.9'
				+ '<input type="checkbox" value="5" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>eGFR降低或血清肌酐轻度升高'
				+ '<input type="checkbox" value="6" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>微量白蛋白尿'
				+ '<input type="checkbox" value="7" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>白蛋白/肌酐比:男性≥22mg/g(2.5mg/mmol)女性≥31mg/g(3.5mg/mmol)'
				+ '<input type="checkbox" value="8" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>蛋白尿和／或血浆肌酐浓度轻度升高 106～177μmol／L(1.2～2.0mg/dl)'
				+ '<input type="checkbox" value="9" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>视网膜普遍或灶性动脉狭窄'
				+ '<input type="checkbox" value="10" name="targetHurt'
				+ this.idPostfix
				+ '" onclick="onTargetHurtClick(value)"/>以上都无'
				+ '</td>'
				+ '</tr>'
				+ '<tr hidden="true">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">原并发症加重</strong></td>'
				+ '<td colspan="2"><input type="radio" value="y" name="complicationIncrease'
				+ this.idPostfix
				+ '" />是'
				+ '<input type="radio" value="n" name="complicationIncrease'
				+ this.idPostfix
				+ '" />否</td>'
				+ '</tr>'
				+ '<tr hidden="true">'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">危险分层</strong></td>'
				+ '<td colspan="2"><input type="radio" value="1" name="riskLevel'
				+ this.idPostfix
				+ '" disabled/>低危'
				+ '<input type="radio" value="2" name="riskLevel'
				+ this.idPostfix
				+ '" disabled/>中危'
				+ '<input type="radio" value="3" name="riskLevel'
				+ this.idPostfix
				+ '" disabled/>高危'
				+ '<input type="radio" value="4" name="riskLevel'
				+ this.idPostfix
				+ '" disabled/>很高危'
				+ '</td>'
				+ '</tr>'
				//--yx 
				+ '<tr>'
				+ '<td rowspan="8" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">用药情况</strong></td>'
				+ '<td align="center" style="font-weight:bold;">药物名称1</td>'
				+ ' <td colspan="2"><div id="div_drugNames1'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">用法用量</td>'
				+ '<td>每日'
				+ '<input type="text" id="everyDayTime1'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '次</td>'
				+ '<td>每次'
				+ '<input type="text" id="oneDosage1'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '<input type="text" id="medicineUnit1'
				+ this.idPostfix
				+ '" class="input_btline2" size="2" disabled="true"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">药物名称2</td>'
				+ ' <td colspan="2"><div id="div_drugNames2'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">用法用量</td>'
				+ '<td>每日'
				+ '<input type="text" id="everyDayTime2'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '次</td>'
				+ '<td>每次'
				+ '<input type="text" id="oneDosage2'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '<input type="text" id="medicineUnit2'
				+ this.idPostfix
				+ '" class="input_btline2" size="2" disabled="true"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">药物名称3</td>'
				+ '<td colspan="2"><div id="div_drugNames3'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">用法用量</td>'
				+ '<td>每日'
				+ '<input type="text" id="everyDayTime3'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '次</td>'
				+ '<td>每次'
				+ '<input type="text" id="oneDosage3'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '<input type="text" id="medicineUnit3'
				+ this.idPostfix
				+ '" class="input_btline2" size="2" disabled="true"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">其他药物</td>'
				+ '<td colspan="2"><div id="div_drugNames4'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">用法用量</td>'
				+ '<td>每日'
				+ '<input type="text" id="everyDayTime4'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '次</td>'
				+ '<td>每次'
				+ '<input type="text" id="oneDosage4'
				+ this.idPostfix
				+ '" class="input_btline widt80"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"/>'
				+ '<input type="text" id="medicineUnit4'
				+ this.idPostfix
				+ '" class="input_btline2" size="2" disabled="true"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td rowspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">转诊</strong></td>'
				+ '<td align="center" style="font-weight:bold;">原因</td>'
				+ '<td colspan="2"><input type="text" class="input_btline" id="referralReason'
				+ this.idPostfix
				+ '" class="widt50" size="58"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center" style="font-weight:bold;">机构及科别</td>'
				+ '<td colspan="2"><input type="text" class="input_btline" id="agencyAndDept'
				+ this.idPostfix
				+ '" class="widt50" size="58"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">下次随访日期</strong></td>'
				+ '<td colspan="2"><div id="div_nextDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">随访医生</strong></td>'
				+ '<td colspan="2"><div id="div_visitDoctor' + this.idPostfix
				+ '"></div></tr>'
				'<tr>'
				+ '<td colspan="2" align="center" style="font-weight:bold;"><strong style="font-weight:bold;">备注</strong></td>'
				+ '<td colspan="2"><input type="text" class="input_btline" id="notes'
				+ this.idPostfix
				+ '" class="widt50" size="56"/></td>'
				+ '</tr></table>';
		return tpl;
	}
}