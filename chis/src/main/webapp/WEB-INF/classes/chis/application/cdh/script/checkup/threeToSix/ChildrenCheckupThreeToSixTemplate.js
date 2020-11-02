$package("chis.application.cdh.script.checkup.oneToTwo")
chis.application.cdh.script.checkup.threeToSix.ChildrenCheckupThreeToSixTemplate = {
	getCheckupThreeToSixHTML : function() {
		var html = '<div class="my_inOne">'
				+ '<input type="text" id="phrId'
				+ this.idPostfix
				+ '" name="phrId" hidden="true"/>'
				+ '<input type="text" id="checkupId'
				+ this.idPostfix
				+ '" name="checkupId" hidden="true"/>'
				+ '<input type="text" id="manaUnitId'
				+ this.idPostfix
				+ '" name="manaUnitId" hidden="true"/>'
				+ '<table width="800" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
				+ '<tr>'
				+ '<td  colspan="2" width="10%" align="center" ><strong><span id="YL'
				+ this.idPostfix
				+ '" class="red">月（年）龄</span></strong></td>'
				+ '<td><input type="text" id="checkupStage'
				+ this.idPostfix
				+ '" name="checkupStage" class="width80 input_btline" />月龄</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong><span id="SFRQ'
				+ this.idPostfix
				+ '" class="red">随访日期</span></strong></td>'
				+ '<td><div id="div_checkupDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong><span id="TZ'
				+ this.idPostfix
				+ '" class="red">体重（kg）</span></strong></td>'
				+ '<td><input type="text" id="weight'
				+ this.idPostfix
				+ '" name="weight" class="width60 input_btline" />'
				+ '<input type="radio" name="weightDevelopment" id="weightDevelopment_1'
				+ this.idPostfix
				+ '" value="1" disabled="true"/>上　　'
				+ '<input type="radio" name="weightDevelopment" id="weightDevelopment_2'
				+ this.idPostfix
				+ '" value="2" disabled="true"/>中　　'
				+ '<input type="radio" name="weightDevelopment" id="weightDevelopment_3'
				+ this.idPostfix
				+ '" value="3" disabled="true"/>下</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong><span id="SC'
				+ this.idPostfix
				+ '" class="red">身长（cm）</span></strong></td>'
				+ '<td><input type="text" id="height'
				+ this.idPostfix
				+ '" name="height" class="width60 input_btline" />'
				+ '<input type="radio" name="heightDevelopment"   id="heightDevelopment_1'
				+ this.idPostfix
				+ '" value="1" disabled="true"/>上　　'
				+ '<input type="radio" name="heightDevelopment"   id="heightDevelopment_2'
				+ this.idPostfix
				+ '" value="2" disabled="true"/>中　　'
				+ '<input type="radio" name="heightDevelopment"   id="heightDevelopment_3'
				+ this.idPostfix
				+ '" value="3" disabled="true"/>下</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong><span id="SC'
				+ this.idPostfix
				+ '">体格发育评价</span></strong></td>'
				+ '<td>'
				+ '<input type="checkbox" name="devEvaluation"   id="devEvaluation_1'
				+ this.idPostfix
				+ '" value="1" disabled="true"/>正常　　'
				+ '<input type="checkbox" name="devEvaluation"   id="devEvaluation_2'
				+ this.idPostfix
				+ '" value="2" disabled="true"/>低体重　　'
				+ '<input type="checkbox" name="devEvaluation"   id="devEvaluation_3'
				+ this.idPostfix
				+ '" value="3" disabled="true"/>消瘦　　'
				+ '<input type="checkbox" name="devEvaluation"   id="devEvaluation_4'
				+ this.idPostfix
				+ '" value="4" disabled="true"/>发育迟缓　　'
				+ '<input type="checkbox" name="devEvaluation"   id="devEvaluation_5'
				+ this.idPostfix
				+ '" value="5" disabled="true"/>超重</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td width="10%"  rowspan="7" align="center"><strong>体格检查</strong></td>'
				+ '<td><strong>视力</strong></td>'
				+ '<td><input type="text" name="eyesight" id="eyesight'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>听力</strong></td>'
				+ '<td><input type="radio" name="hearing"  id="hearing_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="hearing"  id="hearing_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  ><strong><span class="red">牙数（颗）</span>/龋齿数</strong></td>'
				+ '<td><input type="text" name="decayedTooth"  id="decayedTooth'
				+ this.idPostfix
				+ '" class="width80 input_btline" />/'
				+ '<input type="text" name="dentalCaries"  id="dentalCaries'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  ><strong>心肺</strong></td>'
				+ '<td><input type="radio" name="heartLung" id="heartLung_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="heartLung" id="heartLung_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>腹部</strong></td>'
				+ '<td><input type="radio" name="abdomen" id="abdomen_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="abdomen" id="abdomen_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td><strong>血红蛋白值</strong></td>'
				+ '<td><input type="text" name="hgb" id="hgb'
				+ this.idPostfix
				+ '" class="width80 input_btline" />g/L</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td><strong>其他</strong></td>'
				+ '<td><input type="text" name="other" id="other'
				+ this.idPostfix
				+ '"  class="width360 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td width="10%"  rowspan="3" align="center"><strong>听性行为观察</strong></td>'
				+ '<td ><strong>吐字不清或不会说</strong></td>'
				+ '<td><input type="radio" name="tzbqhbhs" id="tzbqhbhs_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="tzbqhbhs" id="tzbqhbhs_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>总要求别人重复讲</strong></td>'
				+ '<td><input type="radio" name="zyqbrcfj" id="zyqbrcfj_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="zyqbrcfj" id="zyqbrcfj_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>经常用手势表示主</strong></td>'
				+ '<td><input type="radio" name="jcyssbsz" id="jcyssbsz_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="jcyssbsz" id="jcyssbsz_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>两次随访间患病情况</strong></td>'
				+ '<td><div><input type="checkbox" checked="true" name="illnessType"  id="illnessType_0'
				+ this.idPostfix
				+ '" value="0"/>无</div>'
				+ '<div><input type="checkbox" name="illnessType"  id="illnessType_1'
				+ this.idPostfix
				+ '" value="1"/>肺炎<input type="text" name="pneumoniaCount" id="pneumoniaCount'
				+ this.idPostfix
				+ '" class="width80 input_btline" />次</div>'
				+ '<div><input type="checkbox" name="illnessType"  id="illnessType_2'
				+ this.idPostfix
				+ '" value="2"/>腹泻<input type="text" name="diarrheaCount" id="diarrheaCount'
				+ this.idPostfix
				+ '" class="width80 input_btline" />次</div>'
				+ '<div><input type="checkbox" name="illnessType"  id="illnessType_3'
				+ this.idPostfix
				+ '" value="3"/>外伤<input type="text" name="traumaCount" id="traumaCount'
				+ this.idPostfix
				+ '" class="width80 input_btline" />次</div>'
				+ '<div><input type="checkbox" name="illnessType"  id="illnessType_4'
				+ this.idPostfix
				+ '" value="4"/>其他<input type="text" name="otherCount" id="otherCount'
				+ this.idPostfix
				+ '" class="width80 input_btline" />次</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td rowspan="3" align="center"><strong>转诊建议</strong></td>'
				+ '<td ></td>'
				+ '<td><input type="radio" name="referral" id="referral_n'
				+ this.idPostfix
				+ '" value="n"/>无　　'
				+ '<input type="radio" name="referral" id="referral_y'
				+ this.idPostfix
				+ '" value="y"/>有</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td><strong>原因</strong></td>'
				+ '<td><input type="text" name="referralReason" id="referralReason'
				+ this.idPostfix
				+ '"  class="width360 input_btline"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td><strong>机构及科室</strong></td>'
				+ '<td><input type="text" name="referralUnit" id="referralUnit'
				+ this.idPostfix
				+ '"  class="width360 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>指导</strong></td>'
				+ '<td><input type="checkbox" name="guide" id="guide_1'
				+ this.idPostfix
				+ '" value="1"/>合理膳食   '
				+ '<input type="checkbox" name="guide" id="guide_2'
				+ this.idPostfix
				+ '" value="2"/>生长发育   '
				+ '<input type="checkbox" name="guide" id="guide_3'
				+ this.idPostfix
				+ '" value="3"/>疾病预防   '
				+ '<input type="checkbox" name="guide" id="guide_4'
				+ this.idPostfix
				+ '" value="4"/>预防意外伤害    '
				+ '<input type="checkbox" name="guide" id="guide_6'
				+ this.idPostfix
				+ '" value="6"/>口腔保健  '
				+ '<input type="checkbox" name="guide" id="guide_5'
				+ this.idPostfix
				+ '" value="5"/>其他'
				+ '<input type="text" name="otherGuide" id="otherGuide'
				+ this.idPostfix
				+ '" class="input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong>下次访视日期</strong></td>'
				+ '<td><div id="div_nextCheckupDate'
				+ this.idPostfix
				+ '"></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  colspan="2" align="center"><strong>随访医生签名</strong></td>'
				+ '<td><div id="div_checkDoctor' + this.idPostfix + '"></td>'
				+ '</tr>' + '</table></div>';
		return html
	}
}