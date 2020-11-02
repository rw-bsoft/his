$package("chis.application.cdh.script.checkup.inOne")

chis.application.cdh.script.checkup.inOne.ChildrenCheckupInOneTemplate = {
	getCheckupInOneHTML : function() {
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
				+ '<td colspan="2" width="10%" align="center" >'
				+ '<strong><span id="YL'
				+ this.idPostfix
				+ '" class="red">月（年）龄</span></strong></td>'
				+ '<td><input type="text" id="checkupStage'
				+ this.idPostfix
				+ '" name="checkupStage" class="width80 input_btline" />月龄</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong><span id="SFRQ'
				+ this.idPostfix
				+ '" class="red">随访日期</span></strong></td>'
				+ '<td><div id="div_checkupDate'
				+ this.idPostfix
				+ '"></div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong><span id="TZ'
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
				+ '<td colspan="2" align="center"><strong><span id="SC'
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
				+ '<td colspan="2" align="center"><strong><span id="TW'
				+ this.idPostfix
				+ '" class="red">头围（cm）</span></strong></td>'
				+ '<td><input type="text" name="headMeasurement" id="headMeasurement'
				+ this.idPostfix
				+ '" class="width80 input_btline" /></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td width="10%" rowspan="16" align="center">'
				+ '<strong>体格检查</strong></td>'
				+ '<td width="12%"><strong>面色</strong></td>'
				+ '<td ><input type="radio" name="face" id="face_1'
				+ this.idPostfix
				+ '" value="1"/>红润    '
				+ '<input type="radio" name="face" id="face_2'
				+ this.idPostfix
				+ '" value="2"><span id="face_2_text'
				+ this.idPostfix
				+ '">黄染</span>   '
				+ '<input type="radio" name="face" id="face_9'
				+ this.idPostfix
				+ '" value="9"/>其他</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>皮肤</strong></td>'
				+ '<td ><input type="radio" name="skin" id="skin_1'
				+ this.idPostfix
				+ '" value="1" />未见异常  '
				+ '<input type="radio" name="skin" id="skin_2'
				+ this.idPostfix
				+ '" value="2" />异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><span id="QJ'
				+ this.idPostfix
				+ '" class="red"><strong>前囟</strong></span></td>'
				+ '<td><div id="div_bregmaClose'
				+ this.idPostfix
				+ '"><input type="radio" name="bregmaClose" id="bregmaClose_1'
				+ this.idPostfix
				+ '" value="1" />闭合   '
				+ '<input type="radio" name="bregmaClose" id="bregmaClose_2'
				+ this.idPostfix
				+ '" value="2" />未闭　'
				+ '<input type="text" name="bregmaTransverse" id="bregmaTransverse'
				+ this.idPostfix
				+ '" class="width60 input_btline" />cm×'
				+ '<input type="text" name="bregmaLongitudinal" id="bregmaLongitudinal'
				+ this.idPostfix
				+ '" class="width60 input_btline" />cm　</div> </td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>颈部包块</strong></td>'
				+ '<td><input type="radio" name="neckMass" id="neckMass_1'
				+ this.idPostfix
				+ '" value="1" />有　　'
				+ '<input type="radio" name="neckMass" id="neckMass_2'
				+ this.idPostfix
				+ '" value="2" />无</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>眼外观</strong></td>'
				+ '<td><input type="radio" name="pupil" id="pupil_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="pupil" id="pupil_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>耳外观</strong></td>'
				+ '<td ><input type="radio" name="ear"  id="ear_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="ear"  id="ear_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
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
				+ '<td ><strong>口  腔</strong></td>'
				+ '<td><input type="radio" name="mouse"  id="mouse_1'
				+ this.idPostfix
				+ '" value="1"/><span id="mouse_1_text'
				+ this.idPostfix
				+ '">未见异常 </span> '
				+ '<input type="radio" name="mouse"  id="mouse_2'
				+ this.idPostfix
				+ '" value="2"/><span id="mouse_2_text'
				+ this.idPostfix
				+ '">异常</span>'
				+ '<span id="decayedTooth_text'
				+ this.idPostfix
				+ '"  hidden="true">出牙数（颗）</span>'
				+ '<input type="text" name="decayedTooth" hidden="true" id="decayedTooth'
				+ this.idPostfix
				+ '" class="width60 input_btline"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>心肺</strong></td>'
				+ '<td><input type="radio" name="heartLung"  id="heartLung_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="heartLung"  id="heartLung_2'
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
				+ '<td ><strong>脐部</strong></td>'
				+ '<td><input type="radio" name="navel" id="navel_1'
				+ this.idPostfix
				+ '" value="1"/><span id="navel_1_text'
				+ this.idPostfix
				+ '">未脱 </span><span id="navel_11_text'
				+ this.idPostfix
				+ '"  hidden="true">未见异常 </span> '
				+ '<input type="radio" name="navel" id="navel_2'
				+ this.idPostfix
				+ '" value="2"/><span id="navel_2_text'
				+ this.idPostfix
				+ '">脱落 </span> <span id="navel_12_text'
				+ this.idPostfix
				+ '"  hidden="true">异常  </span> '
				+ '<input type="radio" name="navel" id="navel_3'
				+ this.idPostfix
				+ '" value="3"/><span id="navel_3_text'
				+ this.idPostfix
				+ '">脐部有渗出 </span> '
				+ '<input type="radio" name="navel" id="navel_9'
				+ this.idPostfix
				+ '" value="9"/><span id="navel_9_text'
				+ this.idPostfix
				+ '">其他</span>'
				+ '<tr>'
				+ '<td ><strong>四肢</strong></td>'
				+ '<td><input type="radio" name="extremities" id="extremities_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="extremities" id="extremities_2'
				+ this.idPostfix
				+ '" value="2"/>异常  </td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>可疑佝偻病症状</strong></td>'
				+ '<td><input type="radio" name="kylgbz"  id="kylgbz_1'
				+ this.idPostfix
				+ '" value="1"/>无  '
				+ '<input type="radio" name="kylgbz"  id="kylgbz_2'
				+ this.idPostfix
				+ '" value="2"/>夜惊  '
				+ '<input type="radio" name="kylgbz"  id="kylgbz_3'
				+ this.idPostfix
				+ '" value="3"/>多汗  '
				+ '<input type="radio" name="kylgbz"  id="kylgbz_4'
				+ this.idPostfix
				+ '" value="4"/>烦躁</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>可疑佝偻病体征</strong></td>'
				+ '<td><input type="radio" name="kyglbtz" id="kyglbtz_1'
				+ this.idPostfix
				+ '" value="1"/><span id="kyglbtz_11_text'
				+ this.idPostfix
				+ '">无  </span><span id="kyglbtz_12_text'
				+ this.idPostfix
				+ '" hidden="true">肋串珠  </span>'
				+ '<input type="radio" name="kyglbtz" id="kyglbtz_2'
				+ this.idPostfix
				+ '" value="2"/><span id="kyglbtz_21_text'
				+ this.idPostfix
				+ '">颅骨软化  </span><span id="kyglbtz_22_text'
				+ this.idPostfix
				+ '" hidden="true">肋外翻  </span>'
				+ '<input type="radio" name="kyglbtz" id="kyglbtz_3'
				+ this.idPostfix
				+ '" value="3"/><span id="kyglbtz_31_text'
				+ this.idPostfix
				+ '">方颅  </span><span id="kyglbtz_32_text'
				+ this.idPostfix
				+ '" hidden="true">肋软骨沟  </span>'
				+ '<input type="radio" name="kyglbtz" id="kyglbtz_4'
				+ this.idPostfix
				+ '" value="4"/><span id="kyglbtz_41_text'
				+ this.idPostfix
				+ '">枕秃  </span><span id="kyglbtz_42_text'
				+ this.idPostfix
				+ '" hidden="true">鸡胸  </span>'
				+ '<input type="radio" name="kyglbtz" id="kyglbtz_5'
				+ this.idPostfix
				+ '" value="5"  hidden="true"/><span id="kyglbtz_5_text'
				+ this.idPostfix
				+ '" hidden="true">手镯征  </span></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>肛门/外生殖器</strong></td>'
				+ '<td><input type="radio" name="genitals"  id="genitals_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="genitals"  id="genitals_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>血红蛋白值</strong></td>'
				+ '<td><input type="text" name="hgb" id="hgb'
				+ this.idPostfix
				+ '" class="width80 input_btline" />g/L</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td align="center"><strong>听性行为观察</strong></td>'
				+ '<td><strong>会寻找声源</strong></td>'
				+ '<td><input type="radio" name="tlxwgc" id="tlxwgc_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="tlxwgc" id="tlxwgc_2'
				+ this.idPostfix
				+ '" value="2"/>不通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>户外活动</strong></td>'
				+ '<td><input type="text" name="hwhd" id="hwhd'
				+ this.idPostfix
				+ '" class="width80 input_btline" />小时/日</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>服用维生素D</strong></td>'
				+ '<td><input type="text" name="fywss" id="fywss'
				+ this.idPostfix
				+ '" class="width80 input_btline" />IU/日</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong><span id="FYPG'
				+ this.idPostfix
				+ '" class="red">发育评估</span></strong></td>'
				+ '<td><div id="div_development'
				+ this.idPostfix
				+ '"><input type="radio" name="development" id="development_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="development" id="development_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</div></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>两次随访间患病情况</strong></td>'
				+ '<td><input type="radio" name="hbqk"  id="hbqk_1'
				+ this.idPostfix
				+ '" value="1"/>未患病  '
				+ '<input type="radio" name="hbqk"  id="hbqk_2'
				+ this.idPostfix
				+ '" value="2"/>患病</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>其他</strong></td>'
				+ '<td><input type="text" name="other" id="other'
				+ this.idPostfix
				+ '" class="width360 input_btline"/></td>'
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
				+ this.idPostfix + '"  class="width360 input_btline"/></td>' + '</tr>'
				+ '<tr>' + '<td><strong>机构及科室</strong></td>'
				+ '<td><input type="text" name="referralUnit" id="referralUnit'
				+ this.idPostfix + '"  class="width360 input_btline"/></td>' + '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>指导</strong></td>'
				+ '<td><input type="checkbox" name="guide" id="guide_1'
				+ this.idPostfix + '" value="1"/>科学喂养   '
				+ '<input type="checkbox" name="guide" id="guide_2'
				+ this.idPostfix + '" value="2"/>生长发育   '
				+ '<input type="checkbox" name="guide" id="guide_3'
				+ this.idPostfix + '" value="3"/>疾病预防   '
				+ '<input type="checkbox" name="guide" id="guide_4'
				+ this.idPostfix + '" value="4"/>预防意外伤害    '
				+ '<input type="checkbox" name="guide" id="guide_6'
				+ this.idPostfix + '" value="6"/>口腔保健  '
				+ '<input type="checkbox" name="guide" id="guide_5'
				+ this.idPostfix + '" value="5"/>其他'
				+ '<input type="text" name="otherGuide" id="otherGuide'
				+ this.idPostfix + '" class="input_btline" /></td>' + '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>下次访视日期</strong></td>'
				+ '<td><div id="div_nextCheckupDate' + this.idPostfix
				+ '"></td>' + '</tr>' + '<tr>'
				+ '<td colspan="2" align="center"><strong>随访医生签名</strong></td>'
				+ '<td><div id="div_checkDoctor' + this.idPostfix + '"></td>'
				+ '</tr>' + '</table>' + '</div>';
		return html
	}
}