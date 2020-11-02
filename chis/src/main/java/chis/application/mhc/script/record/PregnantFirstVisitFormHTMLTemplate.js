$package("chis.application.mhc.script.record")

chis.application.mhc.script.record.PregnantFirstVisitFormHTMLTemplate = {
	getPFVHTMLTemplate : function() {
		var html = '<body style=" padding:20px;">'
				+ '<div class="my">'
				+ ' <input type="hidden" id="empiId'+this.idPostfix+'" name="empiId"/>'
				+ '<table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
				+ '<tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">填表日期</strong></td>'
				+ '     <td colspan="2"><div id="div_createDate'+ this.idPostfix+ '"/></td>'
				+ '    <td style="font-weight:bold;" align="center"><strong style="font-weight:bold;">填表孕周</strong></td>'
				+ '    <td colspan="2"><input type="text" name="tbyz" class="width80 input_btline" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ ' id="quickeningWeek'+ this.idPostfix+ '"  value=""   disabled="true"/>周</td>'
				+ '</tr>'
				+ '<tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">孕妇年龄</strong></td>'
				+ '    <td colspan="5"><input type="text" name="sfrq" class="width80 input_btline" disabled="true" '+ ' id="age'+ this.idPostfix+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:200px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '     <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">丈夫姓名</strong></td>'
				+ '     <td  width="18%"><input type="text" name="zfxm" class="  input_btline" style="width:200px;" '+ ' id="husbandName'+ this.idPostfix+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  disabled="true"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;"  width="12%" align="center"><strong style="font-weight:bold;">丈夫年龄</strong></td>'
				+ '     <td  width="18%"><input type="text" name="zfnl" class="width80 input_btline" '
				+ ' id="husbandAGE'
				+ this.idPostfix
				+ '" value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   disabled="true"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;"  width="12%" align="center"><strong style="font-weight:bold;">丈夫电话</strong></td>'
				+ '     <td><input type="text" name="zfdh" class="width120 input_btline" '
				+ ' id="husbandPhone'
				+ this.idPostfix
				+ '" value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  disabled="true"  style="width:150px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">孕次</strong></td>'
				+ '     <td  width="18%"><input type="text" name="yc" class="width80 input_btline" " style="width:200px;"'
				+ ' id="gravidity'
				+ this.idPostfix
				+ '" value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  disabled="true"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;"  width="12%" align="center"><strong style="font-weight:bold;">产次</strong></td>'
				+ '     <td  width="18%" colspan="3">阴道分娩<input type="text" name="cc" class="width60 input_btline" '
				+ ' id="vaginalDelivery'
				+ this.idPostfix
				+ '"    style="width:90px;"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}" disabled="true"  value="0"'
				+ '/>次  &nbsp;&nbsp;&nbsp;&nbsp;剖宫产<input type="text" name="ydfmc" class="width60 input_btline" '
				+ ' id="abdominalDelivery'
				+ this.idPostfix
				+ '"   '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  value="0" disabled="true" style="width:130px;color:#999;"'
				+ '/>次</strong></td>'
				+ '    </tr>'

				+ '   <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong id="lastMenstrualPeriod_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">末次月经</strong></td>'
				+ '    <td ><div '
				+ ' id="div_lastMenstrualPeriod'
				+ this.idPostfix
				+ '" /> </td>'
				+ '     <td style="font-weight:bold;"  width="12%" align="center"><strong style="font-weight:bold;color:#EA0000;" >预产期</strong></td>'
				+ '     <td colspan="3" ><div '
				+ ' id="div_dateOfPrenatal'
				+ this.idPostfix
				+ '"/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '    <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">既往史</strong></td>'
				+ '     <td colspan="5"><input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_1'
				+ this.idPostfix
				+ '" value="1" disabled="true"/>无  <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_2'
				+ this.idPostfix
				+ '" value="2" disabled="true"/>心脏病  <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_3'
				+ this.idPostfix
				+ '" value="3" disabled="true"/>肾脏疾病  <input type="checkbox" name="pastHistory"'
				+ ' id="pastHistory_4'
				+ this.idPostfix
				+ '" value="4" disabled="true"/>肝脏疾病  <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_5'
				+ this.idPostfix
				+ '" value="5" disabled="true"/>高血压  <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_6'
				+ this.idPostfix
				+ '" value="6" disabled="true"/>贫血  <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_7'
				+ this.idPostfix
				+ '" value="7" disabled="true"/>糖尿病   <input type="checkbox" name="pastHistory" '
				+ ' id="pastHistory_8'
				+ this.idPostfix
				+ '" value="8" disabled="true"  onclick="doOnblur('
				+ "'pastHistory'"
				+ ',8,'
				+ "'checkbox'"
				+ ')"/>其他<input type="text" name="otherPastHistory" class="input_btline" style="width:180px;"'
				+ ' id="otherPastHistory'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'otherPastHistory'"
				+ ',9)"/></td>'

				+ '<tr>  <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">家族史</strong></td>  <td colspan="5"><input type="checkbox" name="familyHistory" '
				+ ' id="familyHistory_1'
				+ this.idPostfix
				+ '"   value="1"  disabled="true"/>遗传性疾病史 <input type="checkbox" name="familyHistory" '
				+ ' id="familyHistory_2'
				+ this.idPostfix
				+ '"   value="2" disabled="true" />精神疾病史  <input type="checkbox" name="familyHistory" '
				+ ' id="familyHistory_3'
				+ this.idPostfix
				+ '"   value="3" disabled="true" onclick="doOnblur('
				+ "'familyHistory'"
				+ ',3,'
				+ "'checkbox'"
				+ ')"/>其他<input type="text" name="other2" class="input_btline" style="width:410px;"'
				+ ' id="otherFamilyHistory'
				+ this.idPostfix
				+ '"  disabled="true"/></td></tr> '
				+ '<tr>'
				+ '<td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">个人史</strong></td>'
				+ ' <td colspan="5">' 
				+ '<input type="checkbox" name="personHistory"  id="personHistory_1'+this.idPostfix+'" value="1" disabled="true" />吸烟  ' 
				+ '<input type="checkbox" name="personHistory"  id="personHistory_2'+this.idPostfix+'" value="2" disabled="true"/>饮酒 ' 
				+ '<input type="checkbox" name="personHistory"  id="personHistory_3'+this.idPostfix+'" value="3" disabled="true"/>服用药物  '
				+ '<input type="checkbox" name="personHistory"  id="personHistory_4'+this.idPostfix+'" value="4" disabled="true"/>接触有毒有害物质   '
				+ '<input type="checkbox" name="personHistory"  id="personHistory_5'+this.idPostfix+'" value="5" disabled="true" />接触放射线  ' 
				+ '<input type="checkbox" name="personHistory"  id="personHistory_6'+this.idPostfix+'" value="6" disabled="true" onclick="doOnblur(' + "'personHistory'" + ',6)"/>其他' 
				+ '<input type="text" name="other3" class="input_btline"style="width:218px;" id="otherPersonHistory'+ this.idPostfix+ '" disabled="true"/>' 
				+'</td>'
				+ '</tr>'
				+ '<tr> <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">妇科手术史</strong></td> '
				+'<td colspan="5">' 
				+ '<input type="radio" name="gynecologyOPS" '+ ' id="gynecologyOPS_1'+ this.idPostfix+ '"onclick="doOnblur('+ "'gynecologyOPS'"+ ',1,'+ "'radio'"+ ')" value="1" disabled="true"/>无  '
				+'<input type="radio" name="gynecologyOPS" '+ ' id="gynecologyOPS_2'+ this.idPostfix+ '"onclick="doOnblur('+ "'gynecologyOPS'"+ ',2,'+ "'radio'"+ ')" value="2" disabled="true"/>有'
				+'<input type="text" name="gynecologyOPS_other" class="input_btline" '+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'+ ' id="gynecologyOPS_other'+ this.idPostfix
				+ '" disabled="true" onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:567px;" value="" /> ' 
				+'</td></tr>'
				+ '   <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">孕产史</strong></td>'
				+ '    <td colspan="5">流产<input type="text" name="lc1" class=" width60 input_btline" '
				+ ' id="trafficFlow'
				+ this.idPostfix
				+ '"   '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  value="0" disabled="true"'
				+ '/>'
				+ '  死胎<input type="text" name="st1" class=" width60 input_btline" '
				+ ' id="dyingFetus'
				+ this.idPostfix
				+ '"   '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   value="0" disabled="true"'
				+ '/>'
				+ '  死产<input type="text" name="sc1" class=" width60 input_btline" '
				+ ' id="stillBirth'
				+ this.idPostfix
				+ '"  '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  value="0"  disabled="true"'
				+ '/>'
				+ '  新生儿死亡<input type="text" name="xse1" class=" width60 input_btline" '
				+ ' id="newbronDied'
				+ this.idPostfix
				+ '"    '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   value="0" disabled="true"'
				+ '/>'
				+ '  出生缺陷儿<input type="text" name="qxe1" class=" width60 input_btline" '
				+ ' id="abnormality'
				+ this.idPostfix
				+ '"  '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   value="0" style="width:160px;" disabled="true"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong id="height_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">身高</strong></td>'
				+ '     <td colspan="2"><input type="text" name="sg" class="width80 input_btline" '
				+ ' id="height'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)([eE][+-]?[0-9]+)?$]/g,\'\')" onafterpaste="value=this.value.replace(/[^[+-]?([0-9]*\.?[0-9]+|[0-9]+\.?[0-9]*)([eE][+-]?[0-9]+)?$]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('
				+ "'height'"
				+ ',1)}else{doOnblur('
				+ "'height'"
				+ ',2)}" disabled="true" style="width:120px;" '
				+ '/>cm</td>'
				+ '     <td style="font-weight:bold;" align="center"><strong id="weight_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">体重</strong></td>'
				+ '     <td colspan="2"><input type="text" name="tz" class="width80 input_btline" '
				+ ' id="weight'
				+ this.idPostfix
				+ '"  value=""  '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('
				+ "'weight'"
				+ ',1)}else{doOnblur('
				+ "'weight'"
				+ ',2)}"   style="width:220px;"'
				+ '/>Kg</td>'
				+ '    </tr>'
				+ '     <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">体质指数</strong></td>'
				+ '     <td colspan="2"><input type="text" name="tzzs" class=" input_btline"  style="width:118px;" '
				+ ' id="bmi'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td style="font-weight:bold;" align="center"><strong id="weight_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">血压</strong></td>'
				+ '     <td colspan="2"><input type="text" name="xy" class="width60 input_btline" '
				+ ' id="sbp'
				+ this.idPostfix
				+ '"  value="收缩压" disabled="true"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('
				+ "'sbp'"
				+ ',1)}else{doOnblur('
				+ "'sbp'"
				+ ',2)}"   style="width:92px;color:#999;"'
				+ '/>/<input type="text" name="xy2" class="width60 input_btline" '
				+ ' id="dbp'
				+ this.idPostfix
				+ '"  value="舒张压" disabled="true"'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\';doOnblur('
				+ "'dbp'"
				+ ',1)}else{doOnblur('
				+ "'dbp'"
				+ ',2)}"   style="width:92px;color:#999;"'
				+ '/>mmHg</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" align="center"><strong style="font-weight:bold;">听诊</strong><input type="hidden"  id="JY_1'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td style="font-weight:bold;" colspan="2" align="center">心脏</td>'
				+ '     <td colspan="2"><input type="radio"  name="JY_101" '
				+ ' id="JY_101_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_101'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_101" '
				+ ' id="JY_101_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_101'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text" name="JY_101_3" class=" width80 input_btline" '
				+ ' id="JY_101_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:140px;" '
				+ '/></td>'
				+ '     <td style="font-weight:bold;" align="center">肺部</td>'
				+ '     <td colspan="2"><input type="radio" name="JY_102" '
				+ ' id="JY_102_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_102'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_102" '
				+ ' id="JY_102_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_102'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text" name="JY_102_other" class=" width80 input_btline"'
				+ ' id="JY_102_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"     style="width:120px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" rowspan="3" align="center"><strong style="font-weight:bold;">妇科检查</strong><input type="hidden"  id="JY_3'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td style="font-weight:bold;" colspan="2" align="center">外阴</td>'
				+ '     <td colspan="2"><input type="radio" name="JY_301" '
				+ ' id="JY_301_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_301'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_301" '
				+ ' id="JY_301_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_301'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text" name="JY_301_other" class=" width80 input_btline" '
				+ ' id="JY_301_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:140px;"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;" align="center">阴道</td>'
				+ '     <td colspan="2"><input type="radio" name="JY_302"'
				+ ' id="JY_302_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_302'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_302" '
				+ ' id="JY_302_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_302'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text" name="JY_302_other" class=" width80 input_btline" '
				+ ' id="JY_302_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:120px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="2" align="center">宫颈</td>'
				+ '    <td colspan="2"><input id="JY_303_1'
				+ this.idPostfix
				+ '"  onclick="doOnblur('
				+ "'JY_303'"
				+ ',1,'
				+ "'radio'"
				+ ')" type="radio" name="JY_303" value="1"/>未见异常  <input type="radio" name="JY_303" '
				+ ' id="JY_303_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_303'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text" name="JY_303_2" class=" width80 input_btline" '
				+ ' id="JY_303_other'
				+ this.idPostfix
				+ '"   value=""  '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"    style="width:140px;"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;" align="center">子宫</td>'
				+ '     <td colspan="2"><input type="radio" name="JY_304"'
				+ ' id="JY_304_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_304'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_304" '
				+ ' id="JY_304_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_304'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text"  class=" width80 input_btline" '
				+ ' id="JY_304_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"     style="width:120px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="2" align="center">附件</td>'
				+ '    <td colspan="5"><input type="radio" name="JY_305"'
				+ ' id="JY_305_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_305'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>未见异常  <input type="radio" name="JY_305" '
				+ ' id="JY_305_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_305'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>异常<input type="text"   class=" width80 input_btline" '
				+ ' id="JY_305_other'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"    style="width:140px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '    <td style="font-weight:bold;" rowspan="13" width="10%" align="center"><strong style="font-weight:bold;">辅助检查</strong><input type="hidden"  id="JY_5'
				+ this.idPostfix
				+ '"   /></td>'
				+ '    <td style="font-weight:bold;" colspan="2" align="center">血常规<input type="hidden"  id="JY_501'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td colspan="5">血红蛋白值<input type="text" name="sg" class="width60 input_btline" '
				+ ' id="JY_50101'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>g/L  '
				+ '白细胞计数值<input type="text" name="JY_50102" class="width60 input_btline" '
				+ ' id="JY_50102'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> /L'
				+ '血小板计数值 <input type="text" name="sg" class="width60 input_btline" '
				+ ' id="JY_50103'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>/L  '
				+ '其他<input type="text" name="JY_50104" class="width60 input_btline" '
				+ ' id="JY_50104'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:152px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;"  colspan="2" align="center">尿常规<input type="hidden"  id="JY_505'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td colspan="5">尿蛋白<input type="text" name="ndb" class="width60 input_btline" '
				+ ' id="JY_50501'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> '
				+ '尿糖<input type="text" name="nt" class="width60 input_btline" '
				+ ' id="JY_50502'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> '
				+ '尿酮体<input type="text" name="ntt" class="width60 input_btline" '
				+ ' id="JY_50503'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> '
				+ '尿潜血<input type="text" name="nqx" class="width60 input_btline" '
				+ ' id="JY_50504'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> '
				+ '其他<input type="text" name="other4" class="input_btline" '
				+ ' id="JY_50505'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"    style="width:198px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '   <tr>'
				+ '     <td style="font-weight:bold;" rowspan="2" width="8%" align="center">血型</td>'
				+ '    <td style="font-weight:bold;" width="6%">ABO</td>'
				+ '     <td colspan="5"><input type="text" name="sx" class="width80 input_btline"  style="color:#999;" '
				+ ' id="bloodTypeCode'
				+ this.idPostfix
				+ '"  value=""  /></td>'
				+ '   </tr>'
				+ '    <tr>'
				+ '    <td style="font-weight:bold;">Rh&nbsp;<span class="red"></span></td>'
				+ '     <td colspan="5"><input type="text" name="sg" class="width80 input_btline" style="color:#999;"  id="rhBloodCode'+ this.idPostfix+ '"  value=""  /></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '    <td style="font-weight:bold;color:#EA0000;"  colspan="2" align="center"   >血糖&nbsp;<span class="red"></span></td>'
				+ '     <td colspan="5"><input type="text" name="xt" class="width80 input_btline" id="JY_504'+ this.idPostfix+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   '
				+ '/>mmol/L</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '    <td style="font-weight:bold;"  colspan="2" align="center">肝功能<input type="hidden"  id="JY_506'
				+ this.idPostfix
				+ '"   /></td>'
				+ '    <td colspan="5" style="line-height:26px;">血清谷丙转氨酶 <input type="text" name="ggn" class="width60 input_btline" '
				+ ' id="JY_50601'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>'
				+ 'U/L  &nbsp;&nbsp;&nbsp;&nbsp;血清谷草转氨酶 <input type="text" name="ggn" class="width60 input_btline" '
				+ ' id="JY_50602'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>U/L'
				+ '	&nbsp;&nbsp;&nbsp;&nbsp;白蛋白 <input type="text" name="ggn" class="width60 input_btline" '
				+ ' id="JY_50603'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>'
				+ 'g/L<br />总胆红素<input type="text" name="ggn" class="width60 input_btline" '
				+ ' id="JY_50604'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:100px;color:#999;"'
				+ '/>'
				+ 'μmol/L  &nbsp;&nbsp;&nbsp;&nbsp;结合胆红素<input type="text" name="ggn" class="width60 input_btline" '
				+ ' id="JY_50605'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>μmol/L'
				+ '	</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;"  colspan="2" align="center">肾功能<input type="hidden"  id="JY_507'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td colspan="5">血清肌酐<input type="text" name="sgn" class="width80 input_btline" '
				+ ' id="JY_50701'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>'
				+ 'μmol/L    血尿素氮<input type="text" name="sgn" class="width80 input_btline" '
				+ ' id="JY_50702'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onkeyup="value=this.value.replace(/[^0-9]/g,\'\')" onafterpaste="value=this.value.replace(/[^0-9]/g,\'\')"'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>mmol/L</td>'
				+ '   </tr>'
				+ '   <tr>'
				+ '     <td style="font-weight:bold;color:#EA0000;" rowspan="2"  colspan="2" >阴道分泌物&nbsp;<span class="red"></span></td>'
				+ '     <td colspan="5"><div id="div_JY_508'+this.idPostfix+'"><input type="checkbox" name="JY_508" '
				+ ' id="JY_508_1'
				+ this.idPostfix
				+ '"  value="1" />未见异常 <input type="checkbox" name="JY_508" '
				+ ' id="JY_508_2'
				+ this.idPostfix
				+ '"   value="2" />滴虫  <input type="checkbox" name="JY_508" '
				+ ' id="JY_508_3'
				+ this.idPostfix
				+ '"  value="3" />假丝酵母菌  <input type="checkbox" name="JY_508" '
				+ ' onclick="doOnblur('
				+ "'JY_508'"
				+ ',4,'
				+ "'checkbox'"
				+ ')"'
				+ ' id="JY_508_4'
				+ this.idPostfix
				+ '"   value="4" />其他<input type="text" name="JY_508" class="input_btline" '
				+ ' id="JY_508_other'
				+ this.idPostfix
				+ '"/></div></td>'
				+ '    </tr>'
				+ '     <tr>'
				+ '     <td colspan="5"><strong  id="JY_509_type'
				+ this.idPostfix
				+ '"  style="font-weight:bold;">阴道清洁度: </strong><input type="radio" name="JY_509" '
				+ ' id="JY_509_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_509_1'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>Ⅰ度   <input type="radio" name="JY_509" '
				+ ' id="JY_509_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_509_2'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>Ⅱ度   <input type="radio" name="JY_509" '
				+ ' id="JY_509_3'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_509_3'"
				+ ',3,'
				+ "'radio'"
				+ ')" value="3">Ⅲ度  <input type="radio" name="JY_509" '
				+ ' id="JY_509_4'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_509_4'"
				+ ',4,'
				+ "'radio'"
				+ ')" value="4">Ⅳ度</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;"  colspan="2" align="center">乙型肝炎五项<input type="hidden"  id="JY_510'
				+ this.idPostfix
				+ '"   /></td>'
				+ '     <td colspan="5" style="line-height:26px;">乙型肝炎表面抗原<input type="text" name="xt" class="width120 input_btline" '
				+ ' id="JY_51001'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>'
				+ '  乙型肝炎表面抗体<input type="text" name="xt" class="width120 input_btline" '
				+ ' id="JY_51002'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/> '
				+ ' 乙型肝炎e抗原<input type="text" name="xt" class="width120 input_btline" '
				+ ' id="JY_51003'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '<br />'
				+ '乙型肝炎e抗体<input type="text" name="xt" class="width120 input_btline" '
				+ ' id="JY_51004'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/>   '
				+ '乙型肝炎核心抗体<input type="text" name="xt" class="width120 input_btline" '
				+ ' id="JY_51005'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td id="JY_511_type'
				+ this.idPostfix
				+ '"  colspan="2" align="center">梅毒血清学试验&nbsp;<span class="red"></span></td>'
				+ '     <td colspan="5"><input type="radio" name="JY_511" '
				+ ' id="JY_511_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_511'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>阴性  <input type="radio" name="JY_511" '
				+ ' id="JY_511_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_511'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>阳性</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td  id="JY_512_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;"  colspan="2" align="center">HIV抗体检测&nbsp;<span class="red"></span></td>'
				+ '     <td colspan="5"><div id="div_JY_512'+this.idPostfix+'"><input type="radio" name="JY_512" '
				+ ' id="JY_512_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_512_1'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>阴性  <input type="radio" name="JY_512" '
				+ ' id="JY_512_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'JY_512_2'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>阳性 </div></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;"  colspan="2" align="center">B超</td>'
				+ '     <td colspan="5"><input type="text" name="bc" class="width80 input_btline" '
				+ ' id="JY_514'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="color:#999"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong  id="diagnosisMethod_type'
				+ this.idPostfix
				+ '"  style="font-weight:bold;color:#EA0000;">妊娠确诊方法</strong></td>'
				+ '     <td colspan="2" ><div '
				+ ' id="div_diagnosisMethod'
				+ this.idPostfix
				+ '"  value="妊娠诊断方法"/></td>'
				+ '     <td style="font-weight:bold;" align="center"><strong style="font-weight:bold;color:#EA0000;">妊娠确诊时间</strong></td>'
				+ '     <td colspan="2"><div '
				+ 'id="div_diagnosisDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '   </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong id="highRiskScore_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">高危评分</strong></td>'
				+ '     <td colspan="2"><input type="text" name="gwpf" class="width80 input_btline" '
				+ ' id="highRiskScore'
				+ this.idPostfix
				+ '"  value=""  '
				+ ' onclick=" doOnblur('
				+ "'highRiskScore'"
				+ ',1) "'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:200px;"'
				+ '/></td>'
				+ '     <td style="font-weight:bold;" align="center"><strong  id="highRiskLevel_type'
				+ this.idPostfix
				+ '" style="font-weight:bold;color:#EA0000;">高危评级</strong></td>'
				+ '     <td colspan="2"><input type="text" name="gwpj" class="width80 input_btline" '
				+ ' id="highRiskLevel'
				+ this.idPostfix
				+ '"  value=""'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"    style="width:200px;"'
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">总体评估</strong></td>'
				+ '<td colspan="5">' 
				+ '<input type="radio" name="generalComment" value="1"  id="generalComment_1'+ this.idPostfix+ '"onclick="doOnblur('
				+ "'generalComment_1'"+ ',1,'+ "'radio'"+ ')"/>未见异常  ' 
				+ '<input type="radio" name="generalComment" value="2"  id="generalComment_2'+ this.idPostfix+ '"onclick="doOnblur('
				+ "'generalComment_2'"+ ',2,'+ "'radio'"+ ')" />异常'
				+ '<input type="text"'+ ' id="commentText'+ this.idPostfix+ '"name="commentText" class="input_btline" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  value=""'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   style="width:610px;"/>' 
				+' </td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">保健指导</strong></td>'
				+ '    <td colspan="5"><input type="checkbox" name="suggestion" '
				+ ' id="suggestion_1'
				+ this.idPostfix
				+ '" value="1" />个人卫生 <input type="checkbox" name="suggestion" '
				+ ' id="suggestion_2'
				+ this.idPostfix
				+ '"  value="2" />心理   <input type="checkbox" name="suggestion" '
				+ ' id="suggestion_3'
				+ this.idPostfix
				+ '" value="3" />营养  <input type="checkbox" name="suggestion" '
				+ ' id="suggestion_4'
				+ this.idPostfix
				+ '"   value="4" />避免致畸因素和疾病对胚胎的不良影响   <input type="checkbox" name="suggestion" '
				+ ' id="suggestion_5'
				+ this.idPostfix
				+ '"   value="5" />产前筛查宣传告知  <input type="checkbox" name="suggestion" '
				+ ' id="suggestion_6'
				+ this.idPostfix
				+ '"   value="6"  onclick="doOnblur('
				+ "'suggestion'"
				+ ',6,'
				+ "'checkbox'"
				+ ')"/>其他<input type="text" '
				+ ' id="otherSuggestion'
				+ this.idPostfix
				+ '" class="input_btline" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  value=""'
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"  style="width:610px;color:#999 ;"'
				+ '/>'
				+ '   </td>'

				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" rowspan="3" align="center"><strong style="font-weight:bold;">转诊</strong></td>'
				+ '     <td style="font-weight:bold;" colspan="2"></td>'
				+ '     <td colspan="5"><input type="radio" name="referral" '
				+ ' id="referral_1'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'referral_1'"
				+ ',1,'
				+ "'radio'"
				+ ')" value="1"/>无  <input type="radio" name="referral" '
				+ ' id="referral_2'
				+ this.idPostfix
				+ '"onclick="doOnblur('
				+ "'referral_2'"
				+ ',2,'
				+ "'radio'"
				+ ')" value="2"/>有</td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="2">原因</td>'
				+ '     <td colspan="5"><input type="text"  style="width:610px;color:#999 ;"  class="input_btline"   '
				+ ' id="reason'
				+ this.idPostfix
				+ '"  value="" '
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"    '
				+ '/></td>'
				+ '    </tr>'
				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="2">机构及科室</td>'
				+ '     <td colspan="5"><input  style="width:610px;color:#999;"    class="input_btline" '
				+ ' id="doccol'
				+ this.idPostfix
				+ '" type="text" size="50" value=""'
				+ ' onclick="if(value==defaultValue){value=\'\';this.style.color=\'#000\'}"  '
				+ ' onBlur="if(!value){value=defaultValue;this.style.color=\'#999\'}"   '
				+ '/></td>'
				+ '    </tr>'

				+ '    <tr>'
				+ '     <td style="font-weight:bold;" colspan="3" align="center"><strong style="font-weight:bold;">下次访视日期</strong></td>'
				+ '     <td colspan="2"><div id="div_visitPrecontractTime'
				+ this.idPostfix
				+ '"></div></td>'
				+ '     <td style="font-weight:bold;" align="center"><strong style="font-weight:bold;">随访医生签名</strong></td>'
				+ '     <td colspan="2"><div id="div_visitDoctorCode'
				+ this.idPostfix
				+ '"></div></td>'
				+ '    </tr>'
				+ '   </table>'
				+ '  </div>'
				+ '<input value="孕妇档案编号" type="hidden"  id="pregnantId'
				+ this.idPostfix
				+ '" />'
				+ '<input value="EMPIID" type="hidden"  id="empiId'
				+ this.idPostfix
				+ '" />'
				+ '<input value="随访机构" type="hidden"  id="visitUnitCode'
				+ this.idPostfix
				+ '" />'
				+ '<input value="录入机构" type="hidden"  id="createUnit'
				+ this.idPostfix
				+ '" />'
				+ '<input value="录入人" type="hidden"  id="createUser'
				+ this.idPostfix
				+ '" />'
				+ '<input value="最后修改机构" type="hidden"  id="lastModifyUnit'
				+ this.idPostfix
				+ '" />'
				+ '<input value="最后修改人" type="hidden"  id="lastModifyUser'
				+ this.idPostfix
				+ '" />'
				+ '<input value="最后修改日期" type="hidden"  id="lastModifyDate'
				+ this.idPostfix + '" />' + '</body>';
		return html;
	}
}