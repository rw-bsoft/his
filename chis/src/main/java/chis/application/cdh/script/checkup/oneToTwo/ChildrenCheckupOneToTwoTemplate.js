$package("chis.application.cdh.script.checkup.oneToTwo")

chis.application.cdh.script.checkup.oneToTwo.ChildrenCheckupOneToTwoTemplate = {
	getCheckupOneToTwoHTML : function() {
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
				+ '<td width="10%"  rowspan="13" align="center"><strong>体格检查</strong></td>'
				+ '<td  width="12%"><strong>面色</strong></td>'
				+ '<td ><input type="radio"  name="face" id="face_1'
				+ this.idPostfix
				+ '" value="1"/>红润    '
				+ '<input type="radio" name="face" id="face_9'
				+ this.idPostfix
				+ '" value="9"/>其他</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  ><strong>皮肤</strong></td>'
				+ '<td ><input type="radio" name="skin" id="skin_1'
				+ this.idPostfix
				+ '" value="1" />未见异常  '
				+ '<input type="radio" name="skin" id="skin_2'
				+ this.idPostfix
				+ '" value="2" />异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>前囟</strong></td>'
				+ '<td><input type="radio" name="bregmaClose" id="bregmaClose_1'
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
				+ '" class="width60 input_btline" />cm </td>'
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
				+ '<td  ><strong>出牙/龋齿数（颗）</strong></td>'
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
				+ '<td ><strong>四肢</strong></td>'
				+ '<td><input type="radio" name="extremities" id="extremities_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="extremities" id="extremities_2'
				+ this.idPostfix
				+ '" value="2"/>异常  </td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  ><strong>步态</strong></td>'
				+ '<td><input type="radio" name="gait" id="gait_1'
				+ this.idPostfix
				+ '" value="1"/>未见异常  '
				+ '<input type="radio" name="gait" id="gait_2'
				+ this.idPostfix
				+ '" value="2"/>异常</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td  ><strong>可疑佝偻病体征</strong></td>'
				+ '<td><input type="radio" name="kyglbtz" id="kyglbtz_10'
				+ this.idPostfix
				+ '" value="10"/>“O”型腿     '
				+ '<input type="radio" name="kyglbtz" id="kyglbtz_11'
				+ this.idPostfix
				+ '" value="11"/>“X”型腿</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>血红蛋白值</strong></td>'
				+ '<td><input type="text" name="hgb" id="hgb'
				+ this.idPostfix
				+ '" class="width80 input_btline" />g/L</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td width="10%"  rowspan="4" align="center"><strong>听性行为观察</strong></td>'
				+ '<td ><strong>对近旁的有呼唤反应或能发单字词音</strong></td>'
				+ '<td><input type="radio" name="yhhfy" id="yhhfy_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="yhhfy" id="yhhfy_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>按照成人的指令指认物体或自己的身体部位</strong></td>'
				+ '<td><input type="radio" name="zrwt" id="zrwt_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="zrwt" id="zrwt_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>按照成人的指令完成相关动作</strong></td>'
				+ '<td><input type="radio" name="wcdz" id="wcdz_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="wcdz" id="wcdz_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td ><strong>模仿成人说话（不看口型）或者说话</strong></td>'
				+ '<td><input type="radio" name="mfsh" id="mfsh_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="mfsh" id="mfsh_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
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
				+ '<td colspan="2" align="center"><strong>发育评估</strong></td>'
				+ '<td><input type="radio" name="development" id="development_1'
				+ this.idPostfix
				+ '" value="1"/>通过  '
				+ '<input type="radio" name="development" id="development_2'
				+ this.idPostfix
				+ '" value="2"/>未通过</td>'
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
				+ '"  class="width360 input_btline" /></td>'
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
				+ '"  class="width360 input_btline"/></td>'
				+ '</tr>'
				+ '<tr>'
				+ '<td colspan="2" align="center"><strong>指导</strong></td>'
				+ '<td><input type="checkbox" name="guide" id="guide_1'
				+ this.idPostfix
				+ '" value="1"/>科学喂养   '
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