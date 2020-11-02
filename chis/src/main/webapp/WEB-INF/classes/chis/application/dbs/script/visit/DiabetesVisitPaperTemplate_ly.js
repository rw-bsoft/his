$package("chis.application.dbs.script.visit")

chis.application.dbs.script.visit.DiabetesVisitPaperTemplate_ly = {
	DBSVisitMedicineList : [{
				id : "medicineName",
				type : "div"
			}, {
				id : "recordId",
				type : "hidden"
			}, {
				id : "days",
				type : "hidden"
			}, {
				id : "medicineFrequency",
				type : "text"
			}, {
				id : "medicineDosage",
				type : "text"
			}, {
				id : "medicineUnit",
				type : "text"
			}, {
				id : "otherMedicineDesc",
				type : "hidden"
			}],
	getDBSVisitTemplate : function() {
		var html = '<table id="diabetesVisit_table" width="800" border="0" align="left" cellpadding="0" cellspacing="0" class="info_tables"> '
				+ '<input type="hidden" id="visitId'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<input type="hidden" id="empiId'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<input type="hidden" id="phrId'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<input type="hidden" id="planId'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<input type="hidden" id="height'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<input type="hidden" id="visitUnit'
				+ this.idPostfix
				+ '" class="input_btline widt80"  />'
				+ '<div style="display:none">'
				+ '<input id="diabetesGroup_01'
				+ this.idPostfix
				+ '" type="radio" name="diabetesGroup" value="01"/>一组 '
				+ '<input id="diabetesGroup_02'
				+ this.idPostfix
				+ '" type="radio" name="diabetesGroup" value="02"/>二组 '
				+ '<input id="diabetesGroup_03'
				+ this.idPostfix
				+ '" type="radio" name="diabetesGroup" value="03"/>三组 '
				+ '<input id="diabetesGroup_99'
				+ this.idPostfix
				+ '" type="radio" name="diabetesGroup" value="99"/>一般管理对象 '
				+ '</div>'
				+ '<tr><td colspan="2" align="center"><span id="SFRQ'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">随访日期</strong></span></td>'
				+ '<td colspan="2"> <div id="div_visitDate'
				+ this.idPostfix
				+ '"></div></td></tr>'
				+ '<tr> <td colspan="2" align="center"><span id="SFFS'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">随访方式</strong></span>'
				+ '</td><td colspan="2"><div  id="div_visitWay'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" id="visitWay_1'
				+ this.idPostfix
				+ '" name="visitWay" value="1"/>门诊就诊'
//				+ '<input type="radio" id="visitWay_2'
//				+ this.idPostfix
//				+ '" name="visitWay" value="2"/>站点就诊'
//				+ '<input type="radio" id="visitWay_3'
//				+ this.idPostfix
//				+ '" name="visitWay" value="3"/>社区随访'
				+ '<input type="radio" id="visitWay_4'
				+ this.idPostfix
				+ '" name="visitWay" value="4"/>家庭随访'
				+ '<input type="radio" id="visitWay_5'
				+ this.idPostfix
				+ '" name="visitWay" value="5"/>电话随访'
//				+ '<input type="radio" id="visitWay_6'
//				+ this.idPostfix
//				+ '" name="visitWay" value="6"/>群组随访'
//				+ '<input type="radio" id="visitWay_9'
//				+ this.idPostfix
//				+ '" name="visitWay" value="9"/>其他'
				+ '</div></td></tr>'
				+ '<tr><td colspan="2" align="center"><strong style="font-weight:bold;">转归</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="radio" id="visitEffect_1'
				+ this.idPostfix
				+ '" name="visitEffect" value="1"/>继续随访'
				+ '<input type="radio" id="visitEffect_2'
				+ this.idPostfix
				+ '" name="visitEffect" value="2"/>暂时失访'
				+ '<input type="radio" id="visitEffect_3'
				+ this.idPostfix
				+ '" name="visitEffect" value="9"/> 终止管理'
				+ ' <span id="ZGYY'
				+ this.idPostfix
				+ '">&nbsp;&nbsp;&nbsp;&nbsp; 原因：</span><div id="div_noVisitReason'
				+ this.idPostfix
				+ '" style="display: inline;" title="原因">'
				+ '<input type="radio" name="noVisitReason" id="noVisitReason_1'
				+ this.idPostfix
				+ '" value="1"/>死亡'
				+ '<input type="radio" name="noVisitReason" id="noVisitReason_2'
				+ this.idPostfix
				+ '" value="2"/>迁出'
				+ '<input type="radio" name="noVisitReason" id="noVisitReason_3'
				+ this.idPostfix
				+ '" value="3"/>失访'
				+ '<input type="radio" name="noVisitReason" id="noVisitReason_4'
				+ this.idPostfix
				+ '" value="4"/>拒绝</div>' 
//				+ '<br/>&nbsp;病例种类：'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_1'
				+ this.idPostfix
				+ '" value="1"/>' 
				//+ '1型糖尿病'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_2'
				+ this.idPostfix
				+ '" value="2"/>' 
//				+ '2型糖尿病'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_3'
				+ this.idPostfix
				+ '" value="3"/>' 
//				+ '营养不良型'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_5'
				+ this.idPostfix
				+ '" value="5"/>' 
//				+ 'IGT'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_6'
				+ this.idPostfix
				+ '" value="6"/>' 
//				+ 'IFT'
				+ '<input hidden="true" type="radio" name="diabetesType" id="diabetesType_9'
				+ this.idPostfix
				+ '" value="9"/>' 
//				+ '其他&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;发生转型：'
				+ '<input hidden="true" type="radio" name="diabetesChange" id="diabetesType_y'
				+ this.idPostfix
				+ '" value="y" disabled="true"/>' 
//				+ '是'
				+ '<input  hidden="true" type="radio" name="diabetesChange" id="diabetesType_n'
				+ this.idPostfix
				+ '" value="n" disabled="true" checked="checked"/>' 
//				+ '否' 
				+ '</td></tr>'
				+ '<tr><td colspan="2" align="center"><strong style="font-weight:bold;">症  状</strong></td>'
				+ '<td colspan="2">'
				+ '<input type="checkbox" id="symptoms_1'
				+ this.idPostfix
				+ '" name="symptoms" value="1" />无症状'
				+ '<input type="checkbox" id="symptoms_2'
				+ this.idPostfix
				+ '" name="symptoms" value="2" />多饮'
				+ '<input type="checkbox" id="symptoms_3'
				+ this.idPostfix
				+ '" name="symptoms" value="3" />多食'
				+ '<input type="checkbox" id="symptoms_4'
				+ this.idPostfix
				+ '" name="symptoms" value="4" />多尿'
				+ '<input type="checkbox" id="symptoms_5'
				+ this.idPostfix
				+ '" name="symptoms" value="5" />视力模糊'
				+ '<input type="checkbox" id="symptoms_6'
				+ this.idPostfix
				+ '" name="symptoms" value="6" />感染'
				+ '<input type="checkbox" id="symptoms_7'
				+ this.idPostfix
				+ '" name="symptoms" value="7" />手脚麻木<br />'
				+ '<input type="checkbox" id="symptoms_8'
				+ this.idPostfix
				+ '" name="symptoms" value="8" />下肢浮肿'
				+ '<input type="checkbox" id="symptoms_9'
				+ this.idPostfix
				+ '" name="symptoms" value="9" />体重明显下降'
				+ '<input type="checkbox" id="symptoms_99'
				+ this.idPostfix
				+ '" name="symptoms" value="99"/>其他'
				+ ' <input type="text" class="input_btline" style="width:300px;" name="otherSymptoms" id="otherSymptoms'
				+ this.idPostfix
				+ '"  value="其他" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"/>'
				+ '</td></tr>'
				+ '<tr> <td width="43" rowspan="5" align="center"  style="font-weight:bold;"  ><strong style="font-weight:bold;">体征</strong></td>'
				+ '<td width="117" align="center"> <span id="XY'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">血压（mmHg）</strong></span> </td>'
				+ ' <td colspan="2" >'
				+ '<input type="text" id="constriction'
				+ this.idPostfix
				+ '" name="constriction" class="input_btline widt80"  value="收缩压" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"  /> /'
				+ '<input type="text" id="diastolic'
				+ this.idPostfix
				+ '" name="diastolic" class="input_btline widt80" value="舒张压" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>'
				+ '</td> </tr> '
				+ '<tr> <td align="center"> <span id="TZ'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">体重（kg）</strong></span> </td> '
				+ '<td colspan="2">'
				+ '<input type="text" id="weight'
				+ this.idPostfix
				+ '" name="weight"  class="input_btline widt80"  value="实际体重" onkeyup="ondbsWeightChange(value,bmi'+this.idPostfix+')" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/> /'
				+ '<input type="text" id="targetWeight'
				+ this.idPostfix
				+ '" name="targetWeight" class="input_btline widt80"  value="目标体重" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}" style="color:#999"/>'
				+ ' </td> </tr> '
				+ '<tr> <td align="center"> <span id="TZZS'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">体质指数</strong></span> </td>'
				+ ' <td colspan="2" name="tiz" id="tiz09">'
				+ '<input type="text" id="bmi'
				+ this.idPostfix
				+ '" name="bmi" class="input_btline widt80"  value="实际指数" title="实际指数" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"/> /'
				+ '<input type="text" id="mbBmi'
				+ this.idPostfix
				+ '" name="mbBmi" class="input_btline widt80" value="目标指数" title="目标指数" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"/> '
				+ '</td></tr> '
				+ '<tr><td align="center"  style="font-weight:bold;"   >足背动脉搏动</td>'
				+ '<td colspan="2"  id="pulsation'
				+ this.idPostfix
				+ '" >'
				+ '<input type="radio" id="pulsation_1'
				+ this.idPostfix
				+ '" name="pulsation" value="1"/>未触及'
				+ '<input type="radio" id="pulsation_2'
				+ this.idPostfix
				+ '" name="pulsation" value="2"/>触及'
				+ '</td></tr> '
				+ '<tr><td align="center"  style="font-weight:bold;"  >其  他 </td> '
				+ '<td colspan="2"  >'
				+ '<input type="text" id="otherSigns'
				+ this.idPostfix
				+ '" name="otherSigns" size="60"  value="" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:322px;" class="input_btline" />'
				+ '</td></tr>'
				+ '<tr><td rowspan="6" align="center"  style="width:80px;"><strong style="font-weight:bold;">生活方式指导</strong></td>'
				+ ' <td align="center"> <span id="RXYL'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">日吸烟量(支)</strong></span> </td>'
				+ ' <td  colspan="2">'
				+ '<input type="text" id="smokeCount'
				+ this.idPostfix
				+ '" name="smokeCount" class="input_btline widt80"  value="实际吸烟量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/> /'
				+ '<input type="text" id="targetSmokeCount'
				+ this.idPostfix
				+ '" name="targetSmokeCount" class="input_btline widt80"  value="目标吸烟量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>'
				+ '</td> </tr>'
				+ '<tr> <td align="center"> <span id="RYJL'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">日饮酒量（两）</strong></span> </td>'
				+ '<td colspan="2" >'
				+ '<input type="text" id="drinkCount'
				+ this.idPostfix
				+ '" name="drinkCount"  class="input_btline widt80"  value="实际饮酒量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>/'
				+ '<input type="text" id="targetDrinkCount'
				+ this.idPostfix
				+ '" name="targetDrinkCount" class="input_btline widt80"  value="目标饮酒量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>'
				+ '</td></tr> '
				+ '<tr><td align="center"> <span id="YD'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">运  动</strong></span> </td>'
				+ ' <td colspan="2"  name="shfszd" id="shfszd09">'
				+ '<input type="text" id="trainTimesWeek'
				+ this.idPostfix
				+ '" name="trainTimesWeek" class="input_btline widt80"  value="实际次数" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>次/周'
				+ '<input type="text" id="trainMinute'
				+ this.idPostfix
				+ '" name="trainMinute" class="input_btline widt80" value="实际时间" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/> 分钟/次'
				+ '<input type="text" id="targetTrainTimesWeek'
				+ this.idPostfix
				+ '" name="targetTrainTimesWeek" class="input_btline widt80"  value="目标次数" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>次/周'
				+ '<input type="text" id="targetTrainMinute'
				+ this.idPostfix
				+ '" name="targetTrainMinute"  class="input_btline widt80"  value="目标时间" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/> 分钟/次'
				+ '</td> </tr> '
				+ '<tr> <td align="center"> <span id="ZS'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">主食（克/天）</strong></span> </td> '
				+ '<td colspan="2" ">'
				+ '<input type="text"  id="food'
				+ this.idPostfix
				+ '" name="food"  class="input_btline widt80"  value="实际量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/> /'
				+ '<input type="text"  id="targetFood'
				+ this.idPostfix
				+ '" name="targetFood"  class="input_btline widt80"  value="目标量" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}"/>'
				+ ' </td> </tr>'
				+ '<tr><td align="center"  style="font-weight:bold;"  >心理调整</td>'
				+ '<td colspan="2" id="psychologyChange_'
				+ this.idPostfix
				+ '" >'
				+ '<div id="div_psychologyChange'
				+ this.idPostfix
				+ '"><input type="radio" id="psychologyChange_1'
				+ this.idPostfix
				+ '" name="psychologyChange" value="1"/>良好'
				+ '<input type="radio" id="psychologyChange_2'
				+ this.idPostfix
				+ '" name="psychologyChange" value="2"/>一般'
				+ '<input type="radio" id="psychologyChange_3'
				+ this.idPostfix
				+ '" name="psychologyChange" value="3"/>差'
				+ '</div></td></tr> '
				+ '<tr><td align="center"  style="font-weight:bold;"   >遵医行为</td>'
				+ '<td colspan="2" id="obeyDoctor_'
				+ this.idPostfix
				+ '">'
				+ '<div id="div_obeyDoctor'
				+ this.idPostfix
				+ '"><input type="radio" id="obeyDoctor_1'
				+ this.idPostfix
				+ '" name="obeyDoctor" value="1"/>良好'
				+ '<input type="radio" id="obeyDoctor_2'
				+ this.idPostfix
				+ '" name="obeyDoctor" value="2"/>一般'
				+ '<input type="radio" id="obeyDoctor_3'
				+ this.idPostfix
				+ '" name="obeyDoctor" value="3"/>差'
				+ '</div></td></tr> '
				+ '<tr> <td rowspan="3" align="center"  style="font-weight:bold;"   ><strong style="font-weight:bold;">辅助检查</strong></td>'
				+ '<td align="center"  style="font-weight:bold;"><span id="KFXT'
				+ this.idPostfix
				+ '">空腹血糖值</span></td> '
				+ '<td colspan="2">'
				+ '<input type="text" id="fbs'
				+ this.idPostfix
				+ '" name="fbs" class="input_btline"  value=""/>mmol/L'
				+ '&nbsp;&nbsp;<span>测量方式:</span>'
				+ '<input type="radio" id="fbsTest_1'
				+ this.idPostfix
				+ '" name="fbsTest" value="1"/>末梢血'
				+ '<input type="radio" id="fbsTest_2'
				+ this.idPostfix
				+ '" name="fbsTest" value="2"/>血浆'
				+ '</td> </tr> '
				+ '<td hidden="true" align="center" style="font-weight:bold;"><span id="CHXT'
				+ this.idPostfix
				+ '">餐后血糖值</span></td>'
				+ '<td hidden="true" colspan="2"  >'
				+ '<input type="text" id="pbs'
				+ this.idPostfix
				+ '" name="pbs" class="input_btline"  value=""/>mmol/L'
				+ '&nbsp;&nbsp;<span>测量方式:</span>'
				+ '<input type="radio" id="pbsTest_1'
				+ this.idPostfix
				+ '" name="pbsTest" value="1"/>末梢血'
				+ '<input type="radio" id="pbsTest_2'
				+ this.idPostfix
				+ '" name="pbsTest" value="2"/>血浆'
				+ '</td> </tr> '
				+ '<tr> <td align="center"  style="font-weight:bold;"   >其他检查*</td> '
//				+ '<td colspan="2"><table width="100%"><tr><td width="180" style="border:none;"> 糖化血红蛋白<input type="text" id="hbA1c'
				+ '<td colspan="2"><table width="100%"><tr><td width="180" style="border:none;"><input type="text" id="othercheck'
				+ this.idPostfix
//				+ '"  name="hbA1c" class="input_btline widt80" value="请输入值" onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';}" /> ％</td>'
				+ '"  name="othercheck" class="input_btline" size="60" value="" " /></td>'
				+ '<td style="border:none;"><div diplay="none" id="div_testDate'
				+ this.idPostfix
				+ '"></div></td></tr></table></td>'
				+ '</tr>'
				+ '<tr><td colspan="2" align="center"> <span id="FYYCX'
				+ this.idPostfix
				+ '"><strong style="font-weight:bold;">服药依从性</strong></span> </td>'
				+ '<td colspan="2"><div id="div_medicine'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" id="medicine_1'
				+ this.idPostfix
				+ '" name="medicine" value="1"/>规律'
				+ '<input type="radio" id="medicine_2'
				+ this.idPostfix
				+ '" name="medicine" value="2"/>间断'
				+ '<input type="radio" id="medicine_3'
				+ this.idPostfix
				+ '" name="medicine" value="3"/>不服药'
				+ '</div><span id="BGLYY'
				+ this.idPostfix
				+ '">' 
				//+ '&nbsp;&nbsp;不规律服药原因：' 
				+ '</span><div id="div_medicineNot' 
				+ this.idPostfix
				+ '" style="display: none;"><input type="radio" value="1" name="medicineNot" id="medicineNot_1'
				+ this.idPostfix
				+ '"/>经济原因'
				+ '<input type="radio" value="2" name="medicineNot" id="medicineNot_2'
				+ this.idPostfix
				+ '"/>忘记'
				+ '<input type="radio" value="3" name="medicineNot" id="medicineNot_3'
				+ this.idPostfix
				+ '"/>不良反应'
				+ '<input type="radio" value="4" name="medicineNot" id="medicineNot_4'
				+ this.idPostfix
				+ '"/>配药不方便'
				+ '<input type="radio" value="99" name="medicineNot" id="medicineNot_99'
				+ this.idPostfix
				+ '"/>其他</div>'
				+ '<input hidden="true" type="text" class="input_btline width170"  name="medicineOtherNot" id="medicineOtherNot'
				+ this.idPostfix
				+ '" disabled="disabled"/></td></tr>'
				+ '<tr><td colspan="2" align="center"><strong style="font-weight:bold;"  id="yaoWBLFY_'
				+ this.idPostfix
				+ '">药物不良反应</strong></td>'
				+ '<td colspan="2" id="adverseReactions_'
				+ this.idPostfix
				+ '" >'
				+ '<input type="radio" id="adverseReactions_1'
				+ this.idPostfix
				+ '" name="adverseReactions" value="1"/>无 '
				+ '<input type="radio" id="adverseReactions_2'
				+ this.idPostfix
				+ '" name="adverseReactions" value="2"/>有 '
				+ '</td></tr>'
				+ '<tr><td colspan="2" align="center"  style="font-weight:bold;"   ><strong style="font-weight:bold;">低血糖反应</strong></td>'
				+ '<td colspan="2" id="glycopenia_'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" id="glycopenia_1'
				+ this.idPostfix
				+ '" name="glycopenia" value="1"/>无 '
				+ '<input type="radio" id="glycopenia_2'
				+ this.idPostfix
				+ '" name="glycopenia" value="2"/>偶尔'
				+ '<input type="radio" id="glycopenia_3'
				+ this.idPostfix
				+ '" name="glycopenia" value="3"/>频繁'
				+ '</td></tr>'
				+ '<tr><td colspan="2" align="center"  style="font-weight:bold;"  ><strong style="font-weight:bold;">此次随访分类</strong></td>'
				+ '<td  colspan="2">'
				+ '<input type="radio" id="visitType_1'
				+ this.idPostfix
				+ '" name="visitType" value="1"/>控制满意'
				+ '<input type="radio" id="visitType_2'
				+ this.idPostfix
				+ '" name="visitType" value="2"/>控制不满意'
				+ '<input type="radio" id="visitType_3'
				+ this.idPostfix
				+ '" name="visitType" value="3"/>不良反应 '
				+ '<input type="radio" id="visitType_4'
				+ this.idPostfix
				+ '" name="visitType" value="4"/>并发症'
				+ '&nbsp;&nbsp;&nbsp;&nbsp;'
				+ '<input hidden="true" type="radio" id="complicationChange_1'
				+ this.idPostfix
				+ '" name="complicationChange" value="1"/>' 
//				+ '有新并发症'
				+ '<input hidden="true" type="radio" id="complicationChange_2'
				+ this.idPostfix
				+ '" name="complicationChange" value="2"/>' 
//				+ '原有并发症加重'
				+ '<input hidden="true" type="radio" id="complicationChange_3'
				+ this.idPostfix
				+ '" name="complicationChange" value="3"/>' 
//				+ '无并发症'
				+ '</td>'
				+ '</tr>'
				+ '<tr hidden="true" ><td colspan="2" align="center"  style="font-weight:bold;"  ><strong style="font-weight:bold;">健康处方建议</strong></td>'
				+ '<td  colspan="2">'
				+ '<input type="checkbox" id="healthProposal_1'
				+ this.idPostfix
				+ '" name="healthProposal" value="1"/>控制饮食'
				+ '<input type="checkbox" id="healthProposal_2'
				+ this.idPostfix
				+ '" name="healthProposal" value="2"/>戒烟戒酒'
				+ '<input type="checkbox" id="healthProposal_3'
				+ this.idPostfix
				+ '" name="healthProposal" value="3"/>减轻体重'
				+ '<input type="checkbox" id="healthProposal_4'
				+ this.idPostfix
				+ '" name="healthProposal" value="4"/>规律活动'
				+ '<input type="checkbox" id="healthProposal_5'
				+ this.idPostfix
				+ '" name="healthProposal" value="5"/>放松情绪'
				+ '<input type="checkbox" id="healthProposal_6'
				+ this.idPostfix
				+ '" name="healthProposal" value="6"/>定期检查'
				+ '<input type="checkbox" id="healthProposal_7'
				+ this.idPostfix
				+ '" name="healthProposal" value="7"/>遵医嘱服药'
				+ '<input type="checkbox" id="healthProposal_8'
				+ this.idPostfix
				+ '" name="healthProposal" value="8"/>其他'
				+ '<input type="text" id="otherHealthProposal'
				+ this.idPostfix
				+ '" class="input_btline widt200"/>'
				+ '</td>'
				+ '</tr>'
				+ ' <tr > '
				+ '<td rowspan="8" align="center"><strong style="font-weight:bold;">用药情况 </strong></td> '
				+ '<td align="center"  style="font-weight:bold;">药物名称1</td>'
				+ '<td colspan="2" ><div id="div_medicineName_1'
				+ this.idPostfix
				+ '"></div>'
				+ '<input type="hidden" id="recordId_1'
				+ this.idPostfix
				+ '"/><input type="hidden" id="days_1'
				+ this.idPostfix
				+ '" value="1"/>'
				+ '</td></tr> '
				+ '<tr  >'
				+ '<td align="center"  style="font-weight:bold;">用法用量</td>'
				+ '<td colspan="2">每日 <input type="text" id="medicineFrequency_1'
				+ this.idPostfix
				+ '" class="input_btline widt200" value="" /> 次&nbsp;'
				+ '每次 <input type="text"  id="medicineDosage_1'
				+ this.idPostfix
				+ '" class="input_btline widt200" value=""/>'
				+ '<input type="text"  id="medicineUnit_1'
				+ this.idPostfix
				+ '" style="border: none;" class="widt80" value="" disabled/>'
				+ '<input type="hidden"  id="otherMedicineDesc_1'
				+ this.idPostfix
				+ '" value=""/>'
				+ '</td> '
				+ '</tr> '
				+ '<tr  >'
				+ '<td align="center"  style="font-weight:bold;">药物名称2</td>'
				+ '<td colspan="2"  ><div id="div_medicineName_2'
				+ this.idPostfix
				+ '"></div>'
				+ '<input type="hidden" id="recordId_2'
				+ this.idPostfix
				+ '"/><input type="hidden" id="days_2'
				+ this.idPostfix
				+ '" value="1"/>'
				+ '</td> </tr> '
				+ '<tr >'
				+ '<td align="center"  style="font-weight:bold;">用法用量</td>'
				+ '<td colspan="2">每日 <input type="text" id="medicineFrequency_2'
				+ this.idPostfix
				+ '" class="input_btline widt200" value=""/> 次&nbsp;'
				+ '每次 <input type="text"  id="medicineDosage_2'
				+ this.idPostfix
				+ '" class="input_btline widt200"  value=""/>'
				+ '<input type="text"  id="medicineUnit_2'
				+ this.idPostfix
				+ '" style="border: none;" class="widt80" value="" disabled/>'
				+ '<input type="hidden"  id="otherMedicineDesc_2'
				+ this.idPostfix
				+ '" value=""/>'
				+ '</td>'
				+ ' </tr>'
				+ '<tr >'
				+ '<td align="center"  style="font-weight:bold;"  >药物名称3</td> '
				+ '<td colspan="2"><div id="div_medicineName_3'
				+ this.idPostfix
				+ '"></div>'
				+ '<input   id="recordId_3'
				+ this.idPostfix
				+ '"/><input type="hidden" id="days_3'
				+ this.idPostfix
				+ '" value="1"/>'
				+ '</td> </tr> '
				+ '<tr  >'
				+ '<td align="center"  style="font-weight:bold;"  >用法用量</td> '
				+ '<td colspan="2">每日 <input type="text" id="medicineFrequency_3'
				+ this.idPostfix
				+ '" class="input_btline widt200"   value=""/> 次&nbsp;'
				+ '每次 <input type="text"  id="medicineDosage_3'
				+ this.idPostfix
				+ '"  class="input_btline widt200"  value=""/>'
				+ '<input type="text"  id="medicineUnit_3'
				+ this.idPostfix
				+ '" style="border: none;" class="widt80" value="" disabled/>'
				+ '<input type="hidden"  id="otherMedicineDesc_3'
				+ this.idPostfix
				+ '" value=""/>'
				+ '</td>'
				+ ' </tr>'
				+ '<tr >'
				+ '<td align="center"  style="font-weight:bold;" rowspan="2">胰岛素</td> '
				+ '<td colspan="2" name="yyqk" id="yyqk20" ><div id="div_medicineName_4'
				+ this.idPostfix
				+ '"></div>'
				+ '<input type="hidden" id="recordId_4'
				+ this.idPostfix
				+ '"/><input type="hidden" id="days_4'
				+ this.idPostfix
				+ '" value="1"/>'
				+ '</td> '
				+ '</tr>'
				+ '<tr >'
				+ '<td colspan="2">每日 <input type="text" id="medicineFrequency_4'
				+ this.idPostfix
				+ '"  value="" class="input_btline widt200"/>次&nbsp;'
				+ '每次 <input type="text"  id="medicineDosage_4'
				+ this.idPostfix
				+ '" value="" class="input_btline widt200"/>'
				+ '<input type="text"  id="medicineUnit_4'
				+ this.idPostfix
				+ '" style="border: none;" class="widt80" value="" disabled/>'
				+ '<input type="hidden"  id="otherMedicineDesc_4'
				+ this.idPostfix
				+ '" value="胰岛素"/>'
				+ '</td>'
				+ '</tr>'
				+ '<tr> <td align="center"  style="font-weight:bold;"  ><strong style="font-weight:bold;">转诊</strong></td>'
				+ '<td align="center"  style="font-weight:bold;"   >原  因</td> '
				+ '<td colspan="2"  ><input type="text" id="referralReason'
				+ this.idPostfix
				+ '" style="width:480px;"  class="input_btline" value=""/></td> '
				+ '</tr> '
				+ '<tr>'
				+ '<td align="center"  style="font-weight:bold;">&nbsp;</td>'
				+ ' <td align="center"  style="font-weight:bold;">机构及科别</td> '
				+ '<td colspan="2"  >'
				+ '<input type="text" id="referralOffice'
				+ this.idPostfix
				+ '" style="width:480px;" class="input_btline"  value="" />'
				+ '</td>'
				+ '</tr>'
				+ '<tr><td colspan="2" align="center"  style="font-weight:bold;">下次随访日期</td> <td colspan="2"><div id="div_nextDate'
				+ this.idPostfix
				+ '"></div></td></tr> '
				+ '<tr><td colspan="2" align="center"  style="font-weight:bold;">随访医生签名</td><td colspan="2"><div id="div_visitDoctor'
				+ this.idPostfix + '"></div></td></tr>' 
				+ '<tr>'
				+ ' <td colspan="2" align="center"  style="font-weight:bold;">备注</td> '
				+ '<td colspan="2" ><input type="text" id="notes'
				+ this.idPostfix
				+ '" style="width:480px;" class="input_btline"  value="" />'
				+ '</td></tr>'
				+ '</table> '
		return html;
	}
}