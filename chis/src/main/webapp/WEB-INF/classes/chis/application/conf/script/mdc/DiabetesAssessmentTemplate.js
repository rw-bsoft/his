$package("chis.application.conf.script.mdc")

chis.application.conf.script.mdc.DiabetesAssessmentTemplate = {
	getDiabetesAssessmenHTML : function() {
		var html = '<div class="assess"><table width="800"><tr><td  width="400">'
				+ '<input type="radio" value="1" name="assessType" id="assessType_1'
				+ this.idPostfix
				+ '"/>'
				+ '随访评估(单个病人在随访完成后，系统自动执行)</td><td>'
				+ '<input type="radio" value="2" name="assessType" id="assessType_2'
				+ this.idPostfix
				+ '"/>'
				+ '年度评估(人工启动，所有病人一起评估)</td>'
				+ '</tr><tr><td colspan="2">'
				+ '<input type="checkbox" value="1" name="assessYearCon" id="assessYearCon_1'
				+ this.idPostfix
				+ '"/>'
				+ '排除糖尿病档案已注销或者是个人档案已注销的病人</td>'
				+ '</tr><tr><td>'
				+ '<input type="checkbox" value="2" name="assessYearCon" id="assessYearCon_2'
				+ this.idPostfix
				+ '"/>'
				+ '新病人不评价(维持原组不变)</td><td>'
				+ '<input type="checkbox" value="3" name="assessYearCon" id="assessYearCon_3'
				+ this.idPostfix
				+ '"/>'
				+ '未规范管理的病人不进行年度评估</td>'
				+ '</tr></table>'
				+ '<fieldset style="width:800px;">'
				+ '<legend style="border:#808080 1px solid">规范管理</legend>'
				+ '<table width="800" ><tr><td width="400">'
				+ '<input type="checkbox" value="1" name="normManage"  id="normManage_1'
				+ this.idPostfix
				+ '"/>'
				+ '一组随访次数最低标准</td>'
				+ '<td>占计划随访数的比例(%)'
				+ '<input type="text"  id="normScale1'
				+ this.idPostfix
				+ '"/></td>'
				+ '</tr><tr><td>'
				+ '<input type="checkbox" value="2" name="normManage" id="normManage_2'
				+ this.idPostfix
				+ '"/>'
				+ '二组随访次数最低标准</td>'
				+ '<td>占计划随访数的比例(%)'
				+ '<input type="text"  id="normScale2'
				+ this.idPostfix
				+ '"/></td>'
				+ '</tr><tr><td>'
				+ '<input type="checkbox" value="3" name="normManage" id="normManage_3'
				+ this.idPostfix
				+ '"/>'
				+ '三组随访次数最低标准</td>'
				+ '<td>占计划随访数的比例(%)'
				+ '<input type="text"  id="normScale3'
				+ this.idPostfix
				+ '"/></td>'
				+ '</tr></table></fieldset>'
				+ '<table width="800" border="1" class="assessCenter"><tr>'
				+ '<td style="width:80px;" rowspan="3">控制情况</td>'
				+ '<td width="160">血糖</td><td width="220">优良</td>'
				+ '<td width="170">尚可(<=)</td><td>不良(>)</td>'
				+ '</tr><tr><td>空腹血糖</td><td>'
				+ '<input type="text"  id="fineScore11'
				+ this.idPostfix
				+ '" style="width:80px;"/> ～ '
				+ '<input type="text"  id="fineScore12'
				+ this.idPostfix
				+ '" style="width:80px;"/></td><td>'
				+ '<input type="text"  id="fairScore1'
				+ this.idPostfix
				+ '" style="width:80px;"/></td><td>'
				+ '<input type="text"  id="badScore1'
				+ this.idPostfix
				+ '" style="width:80px;"/></td>'
				+ '</tr><tr><td>餐后血糖</td><td>'
				+ '<input type="text"  id="fineScore21'
				+ this.idPostfix
				+ '" style="width:80px;"/> ～ '
				+ '<input type="text"  id="fineScore22'
				+ this.idPostfix
				+ '" style="width:80px;"/></td><td>'
				+ '<input type="text"  id="fairScore2'
				+ this.idPostfix
				+ '" style="width:80px;"/></td><td>'
				+ '<input type="text"  id="badScore2'
				+ this.idPostfix
				+ '" style="width:80px;"/></td>'
				+ '</tr></table>'
				+ '<table width="800" border="1" class="assessCenter"><tr>'
				+ '<td width="140" rowspan="3">一、二组转组标准</td>'
				+ '<td width="140">原组别</td><td width="180">控制情况</td>'
				+ '<td width="200">次数比例(%)</td><td>转组</td>'
				+ '</tr><tr><td>一组</td><td>控制优良</td><td>'
				+ '<input type="text"  id="scaleNum1'
				+ this.idPostfix
				+ '" style="width:80px;"/></td>'
				+ '<td>二组</td></tr>'
				+ '<tr><td>二组</td><td>控制不良</td><td>'
				+ '<input type="text"  id="scaleNum2'
				+ this.idPostfix
				+ '" style="width:80px;"/></td>'
				+ '<td>一组</td></tr></table>'
				+ '<fieldset style="width:800px;">'
				+ '<legend style="border:#808080 1px solid">三组转组标准</legend>'
				+ '<table width="800"  ><tr><td>'
				+ '<input type="checkbox" value="1" name="threeTurn" id="threeTurn_1'
				+ this.idPostfix
				+ '"/>'
				+ '本年度高危筛查有上级诊断医院，转到二组</td>'
				+ '</tr><tr><td>'
				+ '<input type="checkbox" value="2" name="threeTurn" id="threeTurn_2'
				+ this.idPostfix
				+ '"/>'
				+ '按血糖值转到一组或二组</td>' + '</tr></table></fieldset><fieldset style="width:800px;">'
				+ '<legend style="border:#808080 1px solid">年度评估时间</legend>'
				+ '<table width="800"  ><tr><td>距年末的最大可评估天数：'
				+ '<input type="text" id="assessDays'
				+ this.idPostfix
				+ '"/>'
				+ '天</td></tr><tr><td>可评估时间段：'
				+ '<input type="text" id="assessHour1'
				+ this.idPostfix
				+ '"/>时 ― '
				+ '<input type="text" id="assessHour2'
				+ this.idPostfix
				+ '"/>'
				+ '时</td></tr></table></fieldset></div>';
		return html
	}
}