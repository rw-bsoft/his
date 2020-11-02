$package("chis.application.tr.script.highRisk")

chis.application.tr.script.highRisk.TumourHighRiskVisitTemplate = {
	getTumourHighRiskVisitHTML : function(){
		var html = '<table id="THRV" cellpadding="0" cellspacing="0" border="0">'+
							'<tr>'+
								'<td>'+
									'高危类别'+
								'</td>'+
								'<td>'+
								   '<select id="highRiskType" name="highRiskType" >'+
										'<option value="1">大肠</option>'+
										'<option value="2">胃</option>'+
										'<option value="3">肝</option>'+
										'<option value="4">肺</option>'+
									'</select>'+
								'</td>'+
								'<td>'+
									'级别'+
								'</td>'+
								'<td>'+
									'<select id="fixGroup" name="fixGroup">'+
										'<option value="1">常规组</option>'+
										'<option value="2">高危组</option>'+
									'</select>'+
								'</td>'+
					            '<td>'+
					               '本次随访日期'+
					            '</td>'+
					            '<td>'+
					            	'<input type="text" id="visitDate" name="visitDate"/> '+
					            '</td>'+
					            '<td>下次预约日期</td>'+
					            '<td>'+
					            	'<input type="text" id="nextDate" name="nextDate"/> '+
					            '</td>'+
							'</tr>'+
					        '<tr>'+
					        	'<td><font color="#FF0000">管理年限</font></td>'+
					            '<td><input type="text" id="year" name="year"/> </td>'+
					            '<td><font color="#FF0000">随访方式</font></td>'+
					            '<td>'+
					            	'<select id="visitWay" name="visitWay">'+
					                    '<option value="1">门诊</option>'+
					                    '<option value="2">家庭</option>'+
					                    '<option value="3">电话</option>'+
					                    '<option value="4">短信</option>'+
					                    '<option value="5">网络</option>'+
					                    '<option value="9">其他</option>'+
					                '</select>'+
					            '</td>'+
					            '<td><font color="#FF0000">随访情况</font></td>'+
					            '<td colspan="3">'+
					            	'<input id="visitEffect1" name="visitEffect" type="radio" value="1">继续随访'+
					                '<input id="visitEffect2" name="visitEffect" type="radio" value="2">暂时失访'+
					                '<input id="visitEffect3" name="visitEffect" type="radio" value="3">拒访'+
					            '</td>'+
					        '</tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            	'<fieldset>'+
					                	'<legend>妇科症状</legend>'+
					                	'<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">'+
					                    	'<tr>'+
					                        	'<td width="110">'+
					                            '<input id="gynecologySymptom2" name="gynecologySymptom" type="checkbox" value="02">阴道不规则出血'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="irregularVaginalBleeding" name="irregularVaginalBleeding" type="text">'+
					                            '</td>'+
					                            '<td width="85">'+
					                            '<input id="gynecologySymptom3" name="gynecologySymptom" type="checkbox" value="03">间断性腰疼'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="intermittentBackache" name="intermittentBackache" type="text">'+
					                            '</td>'+
					                            '<td width="75">'+
					                            '<input id="gynecologySymptom4" name="gynecologySymptom" type="checkbox" value="04">阴道流液'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="vaginalDischarge" name="vaginalDischarge" type="text">'+
					                            '</td>'+
					                            '<td width="120">'+
					                            '<input id="gynecologySymptom5" name="gynecologySymptom" type="checkbox" value="05">尿急以及肛门下坠'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="urgentUrinationAnusDrop" name="urgentUrinationAnusDrop" type="text">'+
					                            '</td>'+
					                          '</tr>'+
					                          '<tr>'+
					                            '<td>'+
					                            '<input id="gynecologySymptom6" name="gynecologySymptom" type="checkbox" value="06">下腹疼'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="lowerAbdomenPain" name="lowerAbdomenPain" type="text">'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="gynecologySymptom7" name="gynecologySymptom" type="checkbox" value="07">下肢浮肿'+
					                            '</td>'+
					                            '<td>'+
					                            '<input id="edemaOfLowerLimbs" name="edemaOfLowerLimbs" type="text">'+
					                            '</td>'+
					                            '<td colspan="4">'+
					                            '<span>'+
					                            '<input id="gynecologySymptom1" name="gynecologySymptom" type="checkbox" value="01">无'+
					                            '</span>'+
					                            '</td>'+
					                        '</tr>'+
					                    '</table>'+
					                '</fieldset>'+
					            '</td>'+
					        '<tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            	'<fieldset>'+
					                	'<legend><font color="#FF0000">全身症状</font></legend>'+
					                    '<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">'+
					                    '<tr>'+
					                    	'<td  align="right">'+
					                        	'<input id="bodySymptom2" name="bodySymptom" type="checkbox" value="02">乏力'+
					                        '</td>'+
					                        '<td>'+
					                        	'<input id="weak" name="weak" type="text">'+
					                        '</td>'+
					                        '<td  align="right">'+
					                        	'<input id="bodySymptom3" name="bodySymptom" type="checkbox" value="03">食欲减退'+
					                        '</td>'+
					                        '<td>'+
					                        	'<input id="anorexia" name="anorexia" type="text">'+
					                        '</td>'+
					                        '<td align="right">'+
					                        	'<input id="bodySymptom4" name="bodySymptom" type="checkbox" value="04">肝区疼痛'+
					                        '</td>'+
					                        '<td>'+
					                        	'<input id="hepatalgia" name="hepatalgia" type="text">'+
					                        '</td>'+
					                        '<td align="right">'+
					                        	'<input id="bodySymptom5" name="bodySymptom" type="checkbox" value=05"">消瘦'+
					                        '</td>'+
					                        '<td>'+
					                        	'<input id="emaciation" name="emaciation" type="text">'+
					                        '</td>'+
					                    '</tr>'+
					                    '<tr>'+
					                    	'<td align="right">'+
					                        	'<input id="bodySymptom6" name="bodySymptom" type="checkbox" value="06">发热'+
					                        '</td>'+
					                        '<td>'+
					                        	'<input id="fever" name="fever" type="text">'+
					                        '</td>'+
					                        '<td colspan="6">'+
					                            '<span style="margin-left:15px;">'+
					                            '<input id="bodySymptom1" name="bodySymptom" type="checkbox" value="01">无'+
					                            '</span>'+
					                         '</td>'+
					                    '</tr>'+
					                    '</table>'+
					                '</fieldset>'+
					            '</td>'+
					        '<tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            '<fieldset>'+
					                '<legend><font color="#FF0000">就诊医院</font></legend>'+
					                '<input id="diagnoseHospital1" name="diagnoseHospital" type="radio" value="01">'+
					                '<label style="margin-right:30px;">本区一级医院</label>'+
					                '<input id="diagnoseHospital2" name="diagnoseHospital" type="radio" value="02">'+
					                '<label style="margin-right:30px;">本区二级医院</label>'+
					                '<input id="diagnoseHospital3" name="diagnoseHospital" type="radio" value="03">'+
					                '<label style="margin-right:30px;">外区一级医院</label>'+
					                '<input id="diagnoseHospital4" name="diagnoseHospital" type="radio" value="04">'+
					                '<label style="margin-right:30px;">外区二级医院</label>'+
					                '<input id="diagnoseHospital5" name="diagnoseHospital" type="radio" value="05">'+
					                '<label style="margin-right:30px;">外区三级医院</label>'+
					                '<input id="diagnoseHospital6" name="diagnoseHospital" type="radio" value="06">'+
					                '<label>未就诊</label>'+
					            '</fieldset>	'+
					            '</td>'+
					        '<tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            	'<label><font color="#FF0000">无症状自主监测</font></label>'+
					                '<input id="superviseMyself1" name="superviseMyself" type="radio" value="y">是'+
					                '<input id="superviseMyself2" name="superviseMyself" type="radio" value="n">否'+
					                '<label>检查结果</label>'+
					                '<input id="checkResult" name="checkResult" type="text" style="width:700px;">'+
					            '</td>'+
					        '<tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            '<fieldset>'+
					                '<legend><font color="#FF0000">危险因素情况</font></legend>'+
					                '<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">'+
					                '<tr>'+
					                '<td>'+
					                '家族史'+
					                '<input id="familyHistory1" name="familyHistory" type="radio" value="1">有'+
					                '<input id="familyHistory2" name="familyHistory" type="radio" value="2">无'+
					                '<input id="familyHistory3" name="familyHistory" type="radio" value="3">不详'+
					                '</td>'+
					                '<td width="200">'+
					                	'部位<input id="part" name="part" type="text" style="width:150px;">'+
					                '</td>'+
					                '<td>'+
					                	'与患者的关系'+
					                    '<input id="relationship1" name="relationship" type="checkbox" value="01">外祖父母'+
					                    '<input id="relationship2" name="relationship" type="checkbox" value="02">父母'+
					                    '<input id="relationship3" name="relationship" type="checkbox" value="03">兄妹'+
					                    '<input id="relationship4" name="relationship" type="checkbox" value="04">子女'+
					                '</td>'+
					                '<td>'+
					                	'备注 <input id="relationRemark" name="relationRemark" type="text" style="width:200px;"/>'+
					                '</td>'+
					                '</tr>'+
					                '<tr>'+
					                	'<td>'+
					                    	'<font color="#FF0000">身高</font><input id="height" name="height" type="text" style="width:30px;">CM'+
					                    '</td>'+
					                    '<td>'+
					                    	'<font color="#FF0000">体重</font><input id="weight" name="weight" type="text" style="width:30px;">KG'+
					                    '</td>'+
					                    '<td>'+
					                    	'身体指数<input id="BMI" name="BMI" type="text" style="width:100px;" disabled/>'+
					                    '</td>'+
					                    '<td>'+
					                    	'<font color="#FF0000">腰围</font><input id="waistLine" name="waistLine" type="text" style="width:100px;">CM'+
					                    '</td>'+
					                '</tr>'+
					                '<tr>'+
					                	'<td colspan="2">'+
					                    	'<font color="#FF0000">吸烟</font>'+
					                        '<input id="smoke1" name="smoke" type="radio" value="1">≥20支/日'+
					                        '<input id="smoke2" name="smoke" type="radio" value="2">≤20支/日'+
					                        '<input id="smoke3" name="smoke" type="radio" value="3">戒烟'+
					                        '<input id="smoke4" name="smoke" type="radio" value="4">不吸'+
					                    '</td>'+
					                    '<td colspan="2">'+
					                    	'<font color="#FF0000">烤制品</font>'+
					                    	'<input id="bakeryProduct1" name="bakeryProduct" type="radio" value="1">1-2次/周'+
					                        '<input id="bakeryProduct2" name="bakeryProduct" type="radio" value="2">3-4次/周'+
					                        '<input id="bakeryProduct3" name="bakeryProduct" type="radio" value="3">5-6次/周'+
					                        '<input id="bakeryProduct4" name="bakeryProduct" type="radio" value="4">不食'+
					                    '</td>'+
					                '</tr>'+
					                '<tr>'+
					                	'<td colspan="3">'+
					                    	'<font color="#FF0000">饮酒</font>'+
					                        '<input id="drink1" name="drink" type="radio" value="1">≥5次/周'+
					                        '<input id="drink2" name="drink" type="radio" value="2">3-4次/周'+
					                        '<input id="drink3" name="drink" type="radio" value="3">≤2次/周'+
					                        '<input id="drink4" name="drink" type="radio" value="4">戒酒'+
					                        '<input id="drink5" name="drink" type="radio" value="5">不饮'+
					                    '</td>'+
					                    '<td>'+
					                    	'<font color="#FF0000">较强情绪波动</font>'+
					                        '<input id="moodSwing1" name="moodSwing" type="radio" value="y">有'+
					                        '<input id="moodSwing2" name="moodSwing" type="radio" value="n">否'+
					                    '</td>'+
					                '</tr>'+
					                '<tr>'+
					                	'<td colspan="4">'+
					                    '<font color="#FF0000">锻炼情况</font>'+
					                    '<input id="takeExerciseCase1" name="takeExerciseCase" type="radio" value="1">不太活动(看电视、读报纸)'+
					                    '<input id="takeExerciseCase2" name="takeExerciseCase" type="radio" value="2">轻度活动(种花或家务等)'+
					                    '<input id="takeExerciseCase3" name="takeExerciseCase" type="radio" value="3">中度活动(慢跑、跳舞、羽毛球)'+
					                    '<input id="takeExerciseCase4" name="takeExerciseCase" type="radio" value="4">重度活动(举杠铃等)'+
					                    '</td>'+
					                '</tr>'+
					                '</table>'+
					            '</fieldset>'+
					            '</td>'+
					        '<tr>'+
					        '<tr>'+
					        	'<td colspan="8">'+
					            '<fieldset>'+
					                '<legend><font color="#FF0000">随访建议</font></legend>'+
					                '<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">'+
					                '<tr>'+
					                '<td colspan="3">'+
					                	'<input id="controlWeight" name="controlWeight" type="checkbox" value="y">控制体重'+
					                    '<input id="rationalDiet" name="rationalDiet" type="checkbox" value="y">合理膳食'+
					                    '<input id="moderateExercise" name="moderateExercise" type="checkbox" value="y">适量运动'+
					                    '<input id="quitSmoking" name="quitSmoking" type="checkbox" value="y">戒烟或控制吸烟数量'+
					                    '<input id="adjustEmotions" name="adjustEmotions" type="checkbox" value="y">调节情绪'+
					                    '<input id="regularMonitor" name="regularMonitor" type="checkbox" value="y">定期监测'+
					                '</td>'+
					                '</tr>'+
					                '<tr>'+
					                '<td>'+
					                '随访转诊'+
					                '<input id="visitReferral1" name="visitReferral" type="radio" value="1">常规组'+
					                '<input id="visitReferral2" name="visitReferral" type="radio" value="2">高危组'+
					                '<input id="visitReferral3" name="visitReferral" type="radio" value="3">未转'+
					                '</td>'+
					                '<td>'+
					                	'其他<input id="offerOther" name="offerOther" type="text">'+
					                '</td>'+
					                '<td>'+
					                	'随访医生<input id="visitDoctor" name="visitDoctor" type="text">'+
					                '</td>'+
					                '</tr>'+
					                '</table>'+
					            '</fieldset>'+
					            '</td>'+
					        '<tr>'+
						'</table>'
					  
					  return html;
	}
};