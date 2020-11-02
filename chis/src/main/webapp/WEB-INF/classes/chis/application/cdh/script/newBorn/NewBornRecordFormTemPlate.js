$package("chis.application.cdh.script.newBorn")
chis.application.cdh.script.newBorn.NewBornRecordFormTemPlate = {

	getNewBornRecordFormTemPlate: function() {
				var tpl =
						 '<div class="my"> '
						+ '<table width="900" border="0" align="center" cellpadding="0" cellspacing="0" class="table1">'
						+ '<tbody>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">性 别</strong></td>'
						+ '<td colspan="4"><input type="radio" name="babySex'
						+ this.idPostfix
						+ '" value="0"/>未知的性别 <input type="radio" name="babySex'
						+ this.idPostfix
						+ '" value="1"/>男 <input type="radio" name="babySex'
						+ this.idPostfix
						+ '" value="2"/>女　　<input type="radio" name="babySex'
						+ this.idPostfix
						+ '" value="9"/>未说明的性别</td>'
						+ '<td  align="center" ><strong class="fieldColor">出生日期</strong></td>'
						+ '<td colspan="4"><div  id="babyBirth'
						+ this.idPostfix
						+ '" class="width60 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">身份证号</strong></td>'
						+ '<td><input  id="babyIdCard'
						+ this.idPostfix
						+ '" class="input_btline" /></td>'
						+ '<td align="center"><strong class="fieldColor">出生证号</strong></td>'
						+ '<td colspan="2"><input type="text" id="certificateNo'
						+ this.idPostfix
						+ '" class="input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">家庭住址</strong></td>'
						+ '<td colspan="4"><input type="text" id="babyAddress'
						+ this.idPostfix
						+ '" class="width200 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  width="15%" align="center"><strong class="fieldColor">父亲姓名</strong></td>'
						+ '<td width="10%"><div id="fatherName'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '<td  width="12%" align="center"><strong class="fieldColor">职业</strong></td>'
						+ '<td width="8%"><div  id="fatherJob'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '<td  width="15%" align="center"><strong class="fieldColor">联系电话</strong></td>'
						+ '<td width="11%"><input type="text" id="fatherPhone'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '<td  width="10%" align="center"><strong class="fieldColor">出生日期</strong></td>'
						+ '<td><div id="fatherBirth'
						+ this.idPostfix
						+ '" class="width40 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">母亲姓名</strong></td>'
						+ '<td><div id="motherName'
						+ this.idPostfix
						+ '" class=" input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">职业</strong></td>'
						+ '<td><div id="motherJob'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">联系电话</strong></td>'
						+ '<td><input type="text" id="motherPhone'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">出生日期</strong></td>'
						+ '<td><div  id="motherBirth'
						+ this.idPostfix
						+ '" class="width40 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">出生孕周</strong></td>'
						+ '<td><input type="text" id="gestation'
						+ this.idPostfix
						+ '" class="width125 input_btline" />周</td>'
						+ '<td  colspan="2" align="center"><strong class="fieldColor">母亲妊娠期患病情况</strong></td>'
						+ '<td colspan="4"><input type="radio" name="pregnancyDisease'
						+ this.idPostfix
						+ '" value="1"/>糖尿病 <input type="radio" name="pregnancyDisease'
						+ this.idPostfix
						+ '" value="2"/>妊娠期高血压 <input type="radio" name="pregnancyDisease'
						+ this.idPostfix
						+ '" value="3"/>其他<input type="text" id="otherDisease'
						+ this.idPostfix
						+ '" class="input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">助产机构名称</strong></td>'
						+ '<td><input type="text" id="deliveryUnit'
						+ this.idPostfix
						+ '" class=" input_btline" /></td>'
						+ '<td  colspan="2" align="center"><strong class="fieldColor">出生情况</strong></td>'
						+ '<td colspan="4"><input type="checkbox" id="birthStatus_1'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="1"/>顺产 <input type="checkbox" id="birthStatus_2'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="2"/>胎头吸引 <input type="checkbox" id="birthStatus_3'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="3"/>产钳 <input type="checkbox" id="birthStatus_4'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="4"/>剖宫 <input type="checkbox" id="birthStatus_5'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="5"/>双多胎 <input type="checkbox" id="birthStatus_6'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="6"/>臀位 <br /><input type="checkbox" id="birthStatus_7'+this.idPostfix+'" name="birthStatus'
						+ this.idPostfix
						+ '" value="7"/>其他<input type="text" id="otherStatus'
						+ this.idPostfix
						+ '" class="width320 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">新生儿窒息</strong></td>'
						+ '<td colspan="3"><input type="radio" name="asphyxia'
						+ this.idPostfix
						+ '" value="n"/>无 <input type="radio" name="asphyxia'
						+ this.idPostfix
						+ '" value="y"/>有</td>'
						+ '<td  rowspan="2" align="center"><strong class="fieldColor">是否有畸型</strong></td>'
						+ '<td colspan="3" rowspan="2"><input type="radio" name="malforMation'
						+ this.idPostfix
						+ '" value="n"/>无 <input type="radio" name="malforMation'
						+ this.idPostfix
						+ '" value="y"/>有<input type="text" id="malforMationDescription'
						+ this.idPostfix
						+ '" class="width230 input_btline"/></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">Apgar评分</strong></td>'
						+ '<td colspan="3">1分钟<input type="text" id="apgar1'
						+ this.idPostfix
						+ '" class="width80 input_btline"/>5分钟<input type="text" id="apgar5'
						+ this.idPostfix
						+ '" class="width80 input_btline"/></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">新生儿听力筛查</strong></td>'
						+ '<td colspan="7"><input type="radio" name="hearingTest'
						+ this.idPostfix
						+ '" value="1"/>通过 <input type="radio" name="hearingTest'
						+ this.idPostfix
						+ '" value="2"/>未通过 <input type="radio" name="hearingTest'
						+ this.idPostfix
						+ '" value="3"/>未筛查 <input type="radio" name="hearingTest'
						+ this.idPostfix
						+ '" value="4"/>不详 </td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">新生儿疾病筛查</strong></td>'
						+ '<td colspan="7"><input type="radio" name="illnessScreening'
						+ this.idPostfix
						+ '" value="1"/>甲低 <input type="radio" name="illnessScreening'
						+ this.idPostfix
						+ '" value="2"/>苯丙酮尿症 <input type="radio" name="illnessScreening'
						+ this.idPostfix
						+ '" value="3"/>其他遗传代谢病<input type="text" id="otherIllness'
						+ this.idPostfix
						+ '" class="width460 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
//						+ '<td colspan="8" class="tdnone">'
//						+ '<table width="100%" cellpadding="0" cellspacing="0" border="0" style="border:none;">'
//						+ '<tbody>'
//						+ '<tr>'
						+ '<td   align="center"><strong class="fieldColor">新生儿出生体重</strong></td>'
						+ '<td><input type="text" id="weight'
						+ this.idPostfix
						+ '" class="width80 input_btline" />kg</td>'
						+ '<td align="center"><strong class="fieldColor">目前体重</strong></td>'
						+ '<td colspan="2"><input type="text" id="weightNow'
						+ this.idPostfix
						+ '" class="width80 input_btline" />kg</td>'
						+ '<td align="center"><strong class="fieldColor">出生身长</strong></td>'
						+ '<td colspan="2"><input type="text" id="length'
						+ this.idPostfix
						+ '" class="width80 input_btline" />cm</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">喂养方式</strong></td>'
						+ '<td><input type="radio" name="feedWay'
						+ this.idPostfix
						+ '" value="1"/>纯母乳<input type="radio" name="feedWay'
						+ this.idPostfix
						+ '" value="2"/>混合 <input type="radio" name="feedWay'
						+ this.idPostfix
						+ '" value="3"/>人工</td>'
						+ '<td  align="center"><strong class="fieldColor">&nbsp;吃奶量</strong></td>'
						+ '<td colspan="2"><input type="text" id="eatNum'
						+ this.idPostfix
						+ '" class="width80 input_btline" />ml/次</td>'
						+ '<td  align="center"><strong class="fieldColor">&nbsp;吃奶次数</strong></td>'
						+ '<td colspan="2"><input type="text" id="eatCount'
						+ this.idPostfix
						+ '" class="width80 input_btline" />次/日</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">&nbsp;呕吐</strong></td>'
						+ '<td><input type="radio" name="vomit'
						+ this.idPostfix
						+ '" value="n"/>无 <input type="radio" name="vomit'
						+ this.idPostfix
						+ '" value="y"/>有</td>'
						+ '<td  align="center"><strong class="fieldColor">&nbsp;大便</strong></td>'
						+ '<td colspan="2"><input type="radio" name="stoolStatus'
						+ this.idPostfix
						+ '" value="2"/>糊状 <input type="radio" name="stoolStatus'
						+ this.idPostfix
						+ '" value="3"/>稀</td>'
						+ '<td  align="center"><strong class="fieldColor">&nbsp;大便次数</strong></td>'
						+ '<td colspan="2"><input type="text" id="stoolTimes'
						+ this.idPostfix
						+ '" class="width80 input_btline" />次/日</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">体温</strong></td>'
						+ '<td><input type="text" id="temperature'
						+ this.idPostfix
						+ '" class="width80 input_btline" />℃</td>'
						+ '<td  align="center"><strong class="fieldColor">脉率</strong></td>'
						+ '<td colspan="2"><input type="text" id="pulse'
						+ this.idPostfix
						+ '" class="width80 input_btline" />次/分钟</td>'
						+ '<td  align="center"><strong class="fieldColor">呼吸频率</strong></td>'
						+ '<td colspan="2"><input type="text" id="respiratoryFrequency'
						+ this.idPostfix
						+ '" class="width80 input_btline" />次/分钟</td>'
//						+ '</tr>'
//						+ '</tbody>'
//						+ '</table> </td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">面色</strong></td>'
						+ '<td colspan="3"><input type="radio" name="face'
						+ this.idPostfix
						+ '" value="1"/>红润 <input type="radio" name="face'
						+ this.idPostfix
						+ '" value="2"/>黄染 <input type="radio" name="face'
						+ this.idPostfix
						+ '" value="9"/>其他<input type="text" id="faceOther'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">黄疸部位</strong></td>'
						+ '<td colspan="3"><input type="radio" name="jaundice'
						+ this.idPostfix
						+ '" value="1"/>面部 <input type="radio" name="jaundice'
						+ this.idPostfix
						+ '" value="2"/>躯干 <input type="radio" name="jaundice'
						+ this.idPostfix
						+ '" value="3"/>四肢 <input type="radio" name="jaundice'
						+ this.idPostfix
						+ '" value="4"/>手足</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">前囟</strong></td>'
						+ '<td colspan="7"><input type="text" id="bregmaTransverse'
						+ this.idPostfix
						+ '" class="width60 input_btline" />cm&times;<input type="text" id="bregmaLongitudinal'
						+ this.idPostfix
						+ '" class="width60 input_btline" />cm <input type="radio" name="bregmaStatus'
						+ this.idPostfix
						+ '" value="1"/>正常 <input type="radio" name="bregmaStatus'
						+ this.idPostfix
						+ '" value="2"/>膨隆 <input type="radio" name="bregmaStatus'
						+ this.idPostfix
						+ '" value="3"/>凹陷 <input type="radio" name="bregmaStatus'
						+ this.idPostfix
						+ '" value="4"/>其他<input type="text" id="otherStatus1'
						+ this.idPostfix
						+ '" class="width320 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">眼外观</strong></td>'
						+ '<td colspan="3"><input type="radio" name="eye'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="eye'
						+ this.idPostfix
						+ '" value="99"/>异常<input type="text" id="eyeAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">四肢活动度</strong></td>'
						+ '<td colspan="3"><input type="radio" name="limbs'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="limbs'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="limbsAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">耳外观</strong></td>'
						+ '<td colspan="3"><input type="radio" name="ear'
						+ this.idPostfix
						+ '" value="n"/>未见异常 <input type="radio" name="ear'
						+ this.idPostfix
						+ '" value="y"/>异常<input type="text" id="earAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">颈部包块</strong></td>'
						+ '<td colspan="3"><input type="radio" name="neck'
						+ this.idPostfix
						+ '" value="n"/>无　　<input type="radio" name="neck'
						+ this.idPostfix
						+ '" value="y"/>有<input type="text" id="neck1'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">鼻</strong></td>'
						+ '<td colspan="3"><input type="radio" name="nose'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="nose'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="noseAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">皮肤</strong></td>'
						+ '<td colspan="3"><input type="radio" name="skin'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="skin'
						+ this.idPostfix
						+ '" value="7"/>湿疹 <input type="radio" name="skin'
						+ this.idPostfix
						+ '" value="8"/>糜烂 <input type="radio" name="skin'
						+ this.idPostfix
						+ '" value="99"/>其他<input type="text" id="skinAbnormal'
						+ this.idPostfix
						+ '" class="width80 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">口 腔</strong></td>'
						+ '<td colspan="3"><input type="radio" name="mouse'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="mouse'
						+ this.idPostfix
						+ '" value="14"/>异常<input type="text" id="mouseAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">肛门</strong></td>'
						+ '<td colspan="3"><input type="radio" name="anal'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="anal'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="analAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">心肺听诊</strong></td>'
						+ '<td colspan="3"><input type="radio" name="heartlung'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="heartlung'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="heartLungAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">外生殖器</strong></td>'
						+ '<td colspan="3"><input type="radio" name="genitalia'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="genitalia'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="genitaliaAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">腹部触诊</strong></td>'
						+ '<td colspan="3"><input type="radio" name="abdominal'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="abdominal'
						+ this.idPostfix
						+ '" value="5"/>异常<input type="text" id="abdominalabnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">脊柱</strong></td>'
						+ '<td colspan="3"><input type="radio" name="spine'
						+ this.idPostfix
						+ '" value="1"/>未见异常 <input type="radio" name="spine'
						+ this.idPostfix
						+ '" value="2"/>异常<input type="text" id="spineAbnormal'
						+ this.idPostfix
						+ '" class="width120 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">脐带</strong></td>'
						+ '<td colspan="7"><input type="radio" name="umbilical'
						+ this.idPostfix
						+ '" value="1"/>未脱 <input type="radio" name="umbilical'
						+ this.idPostfix
						+ '" value="2"/>脱落 <input type="radio" name="umbilical'
						+ this.idPostfix
						+ '" value="3"/>脐部有渗出 <input type="radio" name="umbilical'
						+ this.idPostfix
						+ '" value="9"/>其他<input type="text" id="umbilicalOther'
						+ this.idPostfix
						+ '" class="width460 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">转诊建议</strong></td>'
						+ '<td colspan="7"><input type="radio" name="referral'
						+ this.idPostfix
						+ '" value="n"/>无　　<input type="radio" name="referral'
						+ this.idPostfix
						+ '" value="y"/>有</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">原因</strong></td>'
						+ '<td colspan="7"><input type="text" id="referralReason'
						+ this.idPostfix
						+ '"  class="width560 input_btline"/></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">机构及科室</strong></td>'
						+ '<td colspan="7"><input type="text" id="referralUnit'
						+ this.idPostfix
						+ '"  class="width560 input_btline"/></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">指导</strong></td>'
						+ '<td colspan="7"><input type="checkbox" name="guide'
						+ this.idPostfix
						+ '" value="1"/>喂养指导 <input type="checkbox" name="guide'
						+ this.idPostfix
						+ '" value="2"/>发育指导 <input type="checkbox" name="guide'
						+ this.idPostfix
						+ '" value="3"/>防病指导 <input type="checkbox" name="guide'
						+ this.idPostfix
						+ '" value="4"/>预防伤害指导 <input type="checkbox" name="guide'
						+ this.idPostfix
						+ '" value="5"/>口腔保健指导</td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">本次访视日期</strong></td>'
						+ '<td colspan="3"><div id="visitDate'
						+ this.idPostfix
						+ '" class="width60 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">下次随访地点</strong></td>'
						+ '<td colspan="3"><input type="text" id="nextVisitAddress'
						+ this.idPostfix
						+ '" class="width280 input_btline" /></td>'
						+ '</tr>'
						+ '<tr>'
						+ '<td  align="center"><strong class="fieldColor">下次访视日期</strong></td>'
						+ '<td colspan="3"><div id="nextVisitDate'
						+ this.idPostfix
						+ '" class="width60 input_btline" /></td>'
						+ '<td  align="center"><strong class="fieldColor">随访医生签名</strong></td> '
						+ '<td colspan="3"><div  id="visitDoctor' + this.idPostfix
						+ '"  /></div></td>' + '</tr>' + '</tbody>'
						+ '</table>' + '</div>';

				return tpl;
	}
	}