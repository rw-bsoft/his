$package("chis.application.hr.script")

chis.application.hr.script.BasicPersonalInformationTemplate = {
	getBasicInformationHTML : function() {
		var html = ' <div class="basicInfo" width="900" align="center" style="background-color:#eee;">'
				+ '<input type="text" id="phrId'
				+ this.idPostfix
				+ '" name="phrId" hidden="true"/>'
				+ '<input type="text" id="empiId'
				+ this.idPostfix
				+ '" name="empiId" hidden="true"/>'
				+ '<input type="text" id="familyId'
				+ this.idPostfix
				+ '" name="familyId" hidden="true"/>'
				+ '<input type="text" id="middleId'
				+ this.idPostfix
				+ '" name="middleId" hidden="true"/>'
				+ '<table width="900" border="0"'
				+ ' cellpadding="0" cellspacing="0"> '
				+ '<tbody> <tr> <td colspan="5" width="100%">'
				+ ' <table width="100%" cellpadding="0" cellspacing="0" border="0" class="basicInfo_in"><tbody><tr><td><strong >档案备注说明'
				+ '</strong></td> <td colspan="5"><input type="text" id="definePhrid'
				+ this.idPostfix
				+ '" class="input_btline"/></td></tr><tr><td class="red"><strong >身份证号'
				+ '</strong></td> <td class="basicInfo_in_td"><input style="width:80%" type="text" id="idCard'
				+ this.idPostfix
				+ '" class="input_btline"/></td>'
			    + '<td class="red"><strong >现住址'
				+ '</strong></td> <td colspan="3" ><input style="width:99%" type="text" id="address'
				+ this.idPostfix
				+ '" class="input_btline"/></td></tr> <tr><td class="none">'
				+ '<strong><span id="XM'
				+ this.idPostfix
				+ '" class="red">姓名</span></strong></td> '
				+ '<td><input type="text" id="personName'
				+ this.idPostfix
				+ '" class="input_btline"/></td> <td ><strong><span id="XB'
				+ this.idPostfix
				+ '" class="red">性别</span></strong></td>'
				+ '<td colspan="3" class="basicInfo_in_td"><div id="div_sexCode'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio"  name="sexCode" value="0" id="sexCode_0'
				+ this.idPostfix
				+ '"/> 未知的性别 '
				+ '<input type="radio"  name="sexCode" value="1" id="sexCode_1'
				+ this.idPostfix
				+ '"/> 男 <input type="radio"  name="sexCode" value="2" id="sexCode_2'
				+ this.idPostfix
				+ '"/> 女 <input type="radio"  name="sexCode" value="9" id="sexCode_9'
				+ this.idPostfix
				+ '"/> 未说明的性别</div></td> </tr> '
				+ '<tr> <td  width="15%"><strong><span id="CSRQ'
				+ this.idPostfix
				+ '" class="red">出生日期</span></strong></td>'
				+ '<td colspan="1"> <div id="div_birthday'
				+ this.idPostfix
				+ '" ></div></td><td class="red"><strong >工作单位</strong></td>'
				+ ' <td colspan="3" class="basicInfo_in_td"><input type="text" id="workPlace'
				+ this.idPostfix
				+ '" style="width:99%" class="input_btline"/></td></tr>'
				+ '<tr><td  width="15%"><strong><span id="BRDH'
				+ this.idPostfix
				+ '" class="red">本人电话</span></strong></td>'
				+ '<td><input type="text" id="mobileNumber'
				+ this.idPostfix
				+ '" class="input_btline" /></td> <td  width="11%">'
				+ '<strong><span id="LXRXM'
				+ this.idPostfix
				+ '" class="red">联系人姓名</span></strong></td>'
				+ '<td width="15%"><input type="text" id="contact'
				+ this.idPostfix
				+ '" class="input_btline" /></td>'
				+ '<td  width="9%"><strong><span id="LXRDH'
				+ this.idPostfix
				+ '" class="red">联系人电话</span></strong></td>'
				+ '<td  class="basicInfo_in_td"><input type="text" id="contactPhone'
				+ this.idPostfix
				+ '" class="input_btline"/></td></tr>'
				+ '<tr> <td><strong><span id="CZLX'
				+ this.idPostfix
				+ '" class="red">常住类型</span></strong></td>'
				+ '<td colspan="2"><div id="div_registeredPermanent'
				+ this.idPostfix
				+ '"><input type="radio" name="registeredPermanent" id="registeredPermanent_1'
				+ this.idPostfix
				+ '" value="1"/>户籍 <input type="radio" name="registeredPermanent" id="registeredPermanent_2'
				+ this.idPostfix
				+ '" value="2"/>非户籍</div></td><td><strong><span id="MZ'
				+ this.idPostfix
				+ '" class="red">民族</span></strong></td>'
				+ '<td colspan="2" class="basicInfo_in_td"><div id="div_nationCode'
				+ this.idPostfix
				+ '"></td></tr><tr><td class="basicInfo_in_td_bot"><strong><span id="XX'
				+ this.idPostfix
				+ '" class="red">血型</span></strong></td>'
				+ '<td colspan="2" class="basicInfo_in_td_bot"><div id="div_bloodTypeCode'
				+ this.idPostfix
				+ '"><input type="radio" name="bloodTypeCode" id="bloodTypeCode_1'
				+ this.idPostfix
				+ '" value="1"/>A型 <input type="radio" name="bloodTypeCode" id="bloodTypeCode_2'
				+ this.idPostfix
				+ '" value="2"/>B型<input type="radio" name="bloodTypeCode" id="bloodTypeCode_3'
				+ this.idPostfix
				+ '" value="3"/>O型 <input type="radio" name="bloodTypeCode" id="bloodTypeCode_4'
				+ this.idPostfix
				+ '" value="4"/>AB型 <input type="radio" name="bloodTypeCode" id="bloodTypeCode_5'
				+ this.idPostfix
				+ '" value="5"/>不详</div></td>'
				+ '<td  class="basicInfo_in_td_bot"><strong><span id="RHYX'
				+ this.idPostfix
				+ '" class="red">RH</span></strong></td><td colspan="2" class="basicInfo_in_td basicInfo_in_td_bot"><div id="div_rhBloodCode'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" name="rhBloodCode" id="rhBloodCode_2'
				+ this.idPostfix
				+ '" value="2" />阴性<input type="radio" name="rhBloodCode" id="rhBloodCode_1'
				+ this.idPostfix
				+ '" value="1" />阳性<input type="radio" name="rhBloodCode" id="rhBloodCode_3'
				+ this.idPostfix
				+ '" value="3" />不详</div></td></tr>'
				+'</tbody></table></td></tr>'
				+ '<tr><td ><strong><span id="WHCD'
				+ this.idPostfix
				+ '" class="red">文化程度</span></strong></td>'
				+ '<td colspan="4" valign="top"><div id="div_educationCode'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" name="educationCode" id="educationCode_7'
				+ this.idPostfix
				+ '" value="7"  />研究生 '
				+ '<input type="radio" name="educationCode" id="educationCode_8'
				+ this.idPostfix
				+ '" value="8"  />大学本科 '
				+ '<input type="radio" name="educationCode" id="educationCode_5'
				+ this.idPostfix
				+ '" value="5" />大学专科和专科学校'
				+ '<input type="radio" name="educationCode" id="educationCode_9'
				+ this.idPostfix
				+ '" value="9" />中等专业学校'
				+ '<input type="radio" name="educationCode" id="educationCode_10'
				+ this.idPostfix
				+ '" value="10" />技工学校'
				+ '<input type="radio" name="educationCode" id="educationCode_4'
				+ this.idPostfix
				+ '" value="4" />高中'
				+ '<input type="radio" name="educationCode" id="educationCode_3'
				+ this.idPostfix
				+ '" value="3"/>初中 '
				+ '<input type="radio" name="educationCode" id="educationCode_2'
				+ this.idPostfix
				+ '" value="2"  />小学 '
				+ '<input type="radio" name="educationCode" id="educationCode_1'
				+ this.idPostfix
				+ '" value="1" />文盲或半文盲 '
				+ '<input type="radio" name="educationCode" id="educationCode_6'
				+ this.idPostfix
				+ '"  value="6" />不详</div></td></tr><tr><td  class="red">'
				+ '<strong>职 业</strong></td><td colspan="4"><div id="div_workCode'
			    + this.idPostfix
				+ '"><input type="radio" name="workCode" id="workCode_0'
				+ this.idPostfix
				+ '" value="0" />国家机关、党群组织、企业、事业单位负责人 '
				+ '<input type="radio" name="workCode" id="workCode_1/2'
				+ this.idPostfix
				+ '" value="1/2" />专业技术人员 '
				+ '<input type="radio" name="workCode" id="workCode_3'
				+ this.idPostfix
				+ '" value="3" />办事人员和有关人员 '
				+ '<input type="radio" name="workCode" id="workCode_4'
				+ this.idPostfix
				+ '" value="4" />商业、服务业人员 <br />'
				+ '<input type="radio" name="workCode" id="workCode_5'
				+ this.idPostfix
				+ '" value="5" />农、林、牧、渔、水利业生产人员 '
				+ '<input type="radio" name="workCode" id="workCode_9-9'
				+ this.idPostfix
				+ '" value="9-9" />生产、运输设备操作人员及有关人员 '
				+ '<input type="radio" name="workCode" id="workCode_X'
				+ this.idPostfix
				+ '" value="X" />军人 <input type="radio" name="workCode" id="workCode_Y'
				+ this.idPostfix
				+ '" value="Y" />不便分类的其他从业人员' 
				+	'<input type="radio" name="workCode" id="workCode_W'
				+ this.idPostfix
				+ '" value="W" />无职业 </div></td></tr><tr><td >'
				+ '<strong><span id="HYZK'
				+ this.idPostfix
				+ '" class="red">婚姻状况</span></strong></td><td colspan="4"><div id="div_maritalStatusCode'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" name="maritalStatusCode" id="maritalStatusCode_1'
				+ this.idPostfix
				+ '" value="1" />未婚 '
				+ '<input type="radio"  name="maritalStatusCode" id="maritalStatusCode_2'
				+ this.idPostfix
				+ '" value="2" />已婚'
				+ '<input type="radio"  name="maritalStatusCode" id="maritalStatusCode_3'
				+ this.idPostfix
				+ '" value="3" />丧偶 '
				+ '<input type="radio"  name="maritalStatusCode" id="maritalStatusCode_4'
				+ this.idPostfix
				+ '" value="4" />离婚 '
				+ '<input type="radio"  name="maritalStatusCode" id="maritalStatusCode_5'
				+ this.idPostfix
				+ '" value="5" />未说明的婚姻状况</div></td></tr><tr><td >'
				+ '<strong><span id="YLFYZFFS'
				+ this.idPostfix
				+ '" class="red">医疗费用支付方式</span></strong></td><td colspan="4"><div id="div_insuranceCode'
				+ this.idPostfix
				+ '">'
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_01'
				+ this.idPostfix
				+ '" value="01"/>城镇职工基本医疗保险 '
				+ '<input type="checkbox"  name="insuranceCode" id="insuranceCode_02'
				+ this.idPostfix
				+ '" value="02"/>城乡居民基本医疗保险 '
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_04'
				+ this.idPostfix
				+ '" value="04"/>贫困救助 '
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_05'
				+ this.idPostfix
				+ '" value="05"/>商业医疗保险 '
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_06'
				+ this.idPostfix
				+ '" value="06"/>全公费 '
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_07'
				+ this.idPostfix
				+ '" value="07"/>全自费 '
				+ '<input type="checkbox" name="insuranceCode" id="insuranceCode_99'
				+ this.idPostfix
				+ '" value="99"/>其他:'
				+ '<input type="text" id="insuranceCode1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td class="red"><strong>药物过敏史</strong>'
				+ '</td><td colspan="4">' 
				+ '<div id="div_diseasetext_check_gm'
				+ this.idPostfix
				+ '">'
				+ '<input id="diseasetext_check_gm_0101'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_gm" value="0101" />无'
				+ ' <input id="diseasetext_check_gm_0102'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_gm" value="0102" />青霉素 '
				+ '<input id="diseasetext_check_gm_0103'
				+ this.idPostfix
				+ '"type="checkbox" name="diseasetext_check_gm" value="0103" />磺胺 '
				+ '<input id="diseasetext_check_gm_0104'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_gm" value="0104" />链霉素 '
				+ '<input id="diseasetext_check_gm_0109'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_gm" value="0109" />其他:'
				+ '<input id="a_qt1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td class="red"><strong>暴 露 史</strong></td>'
				+ '<td colspan="4">' 
				+ '<div id="div_diseasetext_check_bl'
				+ this.idPostfix
				+ '"><input id="diseasetext_check_bl_1201'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_bl" value="1201" />无 '
				+ '<input type="checkbox" name="diseasetext_check_bl" value="1202" id="diseasetext_check_bl_1202'
				+ this.idPostfix
				+ '"/>化学品 '
				+ '<input type="checkbox" id="diseasetext_check_bl_1203'
				+ this.idPostfix
				+ '" name="diseasetext_check_bl" value="1203" />毒物 '
				+ '<input type="checkbox" id="diseasetext_check_bl_1204'
				+ this.idPostfix
				+ '" name="diseasetext_check_bl" value="1204" />射线</div></td></tr><tr><td class="red">'
				+ '<strong>既 往 史</strong></td><td colspan="4">'
				+ '<table width="100%" height="100%" cellpadding="0" cellspacing="0" border="0" class="basicInfo_in">'
				+ '<tbody><tr><td width="12%" rowspan="9" ><strong>疾病</strong></td>'
				+ '<td colspan="2" class="basicInfo_in_td"><div id="div_diseasetext_radio_jb' 
				+ this.idPostfix
				+	'"><input type="radio" id="diseasetext_radio_jb_0201'
				+ this.idPostfix
				+ '" name="diseasetext_radio_jb" value="0201" />无 '
				+ '<input type="radio" id="diseasetext_radio_jb_02'
				+ this.idPostfix
				+ '" name="diseasetext_radio_jb" value="02" />有</div></td></tr><tr><td>'
				+ '<input id="diseasetext_check_jb_0202'
				+ this.idPostfix
				+ '" type="checkbox" name="diseasetext_check_jb" value="0202" />高血压：确诊时间年月 '
				+ '<div id="div_confirmdate_gxy'
				+ this.idPostfix
				+ '" style="display:inline-block;"/></td><td class="basicInfo_in_td">'
				+ '<input type="checkbox" id="diseasetext_check_jb_0203'
				+ this.idPostfix
				+ '"  name="diseasetext_check_jb" value="0203" />糖尿病：确诊时间年月'
				+ '<div id="div_confirmdate_tnb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<input type="checkbox" id="diseasetext_check_jb_0204'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0204" />冠心病：确诊时间年月'
				+ '<div id="div_confirmdate_gxb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td><td class="basicInfo_in_td">'
				+ '<input type="checkbox" id="diseasetext_check_jb_0205'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0205" />慢性阻塞性肺疾病：确诊时间年月'
				+ '<div id="div_confirmdate_mxzsxfjb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<input type="checkbox" id="diseasetext_check_jb_0206'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0206" />恶性肿瘤：确诊时间年月'
				+ '<div id="div_confirmdate_exzl'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td><td class="basicInfo_in_td">'
				+ '<input type="checkbox" id="diseasetext_check_jb_0207'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0207" />脑卒中：确诊时间年月'
				+ '<div id="div_confirmdate_nzz'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<input type="checkbox" id="diseasetext_check_jb_0208'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0208" />严重精神障碍：确诊时间年月'
				+ '<div id="div_confirmdate_zxjsjb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td><td class="basicInfo_in_td">'
				+ '<input type="checkbox" id="diseasetext_check_jb_0209'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0209" />结核病：确诊时间年月'
				+ '<div id="div_confirmdate_jhb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<input type="checkbox" id="diseasetext_check_jb_0210'
				+ this.idPostfix
				+ '" name="diseasetext_check_jb" value="0210" />肝炎：确诊时间年月'
				+ '<div id="div_confirmdate_gzjb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td colspan="4" class="basicInfo_in_td">'
				+ '<input type="checkbox"  id="diseasetext_check_jb_0212'
				+ this.idPostfix
				+ '"  name="diseasetext_check_jb" value="0212" />职业病：'
				+ '<input type="text" id="diseasetext_zyb'
				+ this.idPostfix
				+ '"  class="input_btline"/> 确诊时间年月'
				+ '<div id="div_confirmdate_zyb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td colspan="2" class="basicInfo_in_td">'
				+ '<input type="checkbox"  id="diseasetext_check_jb_0298'
				+ this.idPostfix
				+ '"  name="diseasetext_check_jb" value="0298" />其他法定传染病：'
				+ '<input type="text" id="diseasetext_qtfdcrb'
				+ this.idPostfix
				+ '"  class="input_btline"/> 确诊时间年月'
				+ '<div id="div_confirmdate_qtfdcrb'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td colspan="2" class="basicInfo_in_td">'
				+ '<input type="checkbox"  id="diseasetext_check_jb_0299'
				+ this.idPostfix
				+ '"  name="diseasetext_check_jb" value="0299" />其他：'
				+ '<input type="text" id="diseasetext_qt'
				+ this.idPostfix
				+ '"  class="input_btline"/> 确诊时间年月'
				+ '<div id="div_confirmdate_qt'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<strong>手 术</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_diseasetext_ss'
				+ this.idPostfix
				+ '"><input type="radio" id="diseasetext_ss_0301'
				+ this.idPostfix
				+ '"  name="diseasetext_ss" value="0301" />无 '
				+ '<input type="radio" id="diseasetext_ss_0302'
				+ this.idPostfix
				+ '" name="diseasetext_ss" value="0302" />有：名称'
				+ '<input type="text" id="diseasetext_ss0'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_ss0'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div>/名称'
				+ '<input type="text" id="diseasetext_ss1'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_ss1'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td>'
				+ '<strong>外 伤</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_diseasetext_ws' 
				+ this.idPostfix
				+ '"><input type="radio" id ="diseasetext_ws_0601'
				+ this.idPostfix
				+ '"  name="diseasetext_ws" value="0601" />无 '
				+ '<input type="radio" id ="diseasetext_ws_0602'
				+ this.idPostfix
				+ '" name="diseasetext_ws" value="0602" />有：名称'
				+ '<input type="text" id="diseasetext_ws0'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_ws0'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div>/名称'
				+ '<input type="text" id="diseasetext_ws1'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_ws1'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr><tr><td class="basicInfo_in_td_bot">'
				+ '<strong>输 血</strong></td><td colspan="2" class="basicInfo_in_td basicInfo_in_td_bot"><div id="div_diseasetext_sx'
				+ this.idPostfix
				+ '"><input type="radio" id ="diseasetext_sx_0401'
				+ this.idPostfix
				+ '"  name="diseasetext_sx" value="0401" />无 '
				+ '<input type="radio" id ="diseasetext_sx_0402'
				+ this.idPostfix
				+ '" name="diseasetext_sx" value="0402" />有：原因'
				+ '<input type="text" id="diseasetext_sx0'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_sx0'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div>/原因'
				+ '<input type="text" id="diseasetext_sx1'
				+ this.idPostfix
				+ '" class="width100 input_btline"/>时间<div id="div_startdate_sx1'
				+ this.idPostfix
				+ '" style="display:inline-block;"></div></td></tr></tbody></table></td></tr>'
				+ '<tr><td class="red"><strong>家 族 史</strong></td><td colspan="4">'
				+ '<table width="100%" height="100%" class="basicInfo_in" cellpadding="0" cellspacing="0" border="0"><tbody><tr>'
				+ '<td width="12%"><strong>父 亲</strong></td><td colspan="3" class="basicInfo_in_td"><div id="div_diseasetext_check_fq'
				+ this.idPostfix
				+ '"><input type="checkbox" id="diseasetext_check_fq_0701'
				+ this.idPostfix
				+ '" name="diseasetext_check_fq" value="0701" />无 '
				+ '<input type="checkbox" id="diseasetext_check_fq_0702'
				+ this.idPostfix
				+ '" name="diseasetext_check_fq" value="0702" />高血压 '
				+ '<input type="checkbox" id="diseasetext_check_fq_0703'
				+ this.idPostfix
				+ '" name="diseasetext_check_fq" value="0703" />糖尿病 '
				+ '<input type="checkbox" id="diseasetext_check_fq_0704'
				+ this.idPostfix
				+ '" name="diseasetext_check_fq" value="0704" />冠心病 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0705'
				+ this.idPostfix
				+ '" value="0705" />慢性阻塞性肺疾病 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0706'
				+ this.idPostfix
				+ '" value="0706" />恶性肿瘤 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0707'
				+ this.idPostfix
				+ '" value="0707" />脑卒中 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0708'
				+ this.idPostfix
				+ '" value="0708" />严重精神障碍 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0709'
				+ this.idPostfix
				+ '" value="0709" />结核病 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0710'
				+ this.idPostfix
				+ '" value="0710" />肝炎 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0711'
				+ this.idPostfix
				+ '" value="0711" />先天畸形 '
				+ '<input type="checkbox" name="diseasetext_check_fq" id="diseasetext_check_fq_0799'
				+ this.idPostfix
				+ '" value="0799" />其他:<input id="qt_fq1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td><strong>母 亲</strong></td><td colspan="3" class="basicInfo_in_td"><div id="div_diseasetextCheckMQ'
				+ this.idPostfix
				+ '"><input type="checkbox" name="diseasetextCheckMQ" id="diseasetextCheckMQ_0801'
				+ this.idPostfix
				+ '" value="0801" />无  '
				+ '<input type="checkbox" name="diseasetextCheckMQ" value="0802" id="diseasetextCheckMQ_0802'
				+ this.idPostfix
				+ '"/>高血压 <input type="checkbox" name="diseasetextCheckMQ" value="0803" id="diseasetextCheckMQ_0803'
				+ this.idPostfix
				+ '"/>糖尿病 <input type="checkbox" name="diseasetextCheckMQ" value="0804" id="diseasetextCheckMQ_0804'
				+ this.idPostfix
				+ '"/>冠心病 <input type="checkbox" name="diseasetextCheckMQ" value="0805" id="diseasetextCheckMQ_0805'
				+ this.idPostfix
				+ '"/>慢性阻塞性肺疾病<input type="checkbox" name="diseasetextCheckMQ" value="0806" id="diseasetextCheckMQ_0806'
				+ this.idPostfix
				+ '"/>恶性肿瘤 <input type="checkbox" name="diseasetextCheckMQ" value="0807" id="diseasetextCheckMQ_0807'
				+ this.idPostfix
				+ '"/>脑卒中 <input type="checkbox" name="diseasetextCheckMQ" value="0808" id="diseasetextCheckMQ_0808'
				+ this.idPostfix
				+ '"/>严重精神障碍<input type="checkbox" name="diseasetextCheckMQ" value="0809" id="diseasetextCheckMQ_0809'
				+ this.idPostfix
				+ '"/>结核病 <input type="checkbox" name="diseasetextCheckMQ" value="0810" id="diseasetextCheckMQ_0810'
				+ this.idPostfix
				+ '"/>肝炎 <input type="checkbox" name="diseasetextCheckMQ" value="0811" id="diseasetextCheckMQ_0811'
				+ this.idPostfix
				+ '"/>先天畸形 <input type="checkbox" name="diseasetextCheckMQ" value="0899" id="diseasetextCheckMQ_0899'
				+ this.idPostfix
				+ '"/>其他:<input id="qt_mq1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td><strong>兄弟姐妹</strong></td><td colspan="3" class="basicInfo_in_td"><div id="div_diseasetextCheckXDJM'
				+ this.idPostfix
				+ '"><input type="checkbox" name="diseasetextCheckXDJM" value="0901" id="diseasetextCheckXDJM_0901'
				+ this.idPostfix
				+ '"/>无  <input type="checkbox" name="diseasetextCheckXDJM" value="0902" id="diseasetextCheckXDJM_0902'
				+ this.idPostfix
				+ '"/>高血压 <input type="checkbox" name="diseasetextCheckXDJM" value="0903" id="diseasetextCheckXDJM_0903'
				+ this.idPostfix
				+ '"/>糖尿病 <input type="checkbox" name="diseasetextCheckXDJM" value="0904" id="diseasetextCheckXDJM_0904'
				+ this.idPostfix
				+ '"/>冠心病 <input type="checkbox" name="diseasetextCheckXDJM" value="0905" id="diseasetextCheckXDJM_0905'
				+ this.idPostfix
				+ '"/>慢性阻塞性肺疾病 <input type="checkbox" name="diseasetextCheckXDJM" value="0906" id="diseasetextCheckXDJM_0906'
				+ this.idPostfix
				+ '"/>恶性肿瘤 <input type="checkbox" name="diseasetextCheckXDJM" value="0907" id="diseasetextCheckXDJM_0907'
				+ this.idPostfix
				+ '"/>脑卒中 <input type="checkbox" name="diseasetextCheckXDJM" value="0908" id="diseasetextCheckXDJM_0908'
				+ this.idPostfix
				+ '"/>严重精神障碍<input type="checkbox" name="diseasetextCheckXDJM" value="0909" id="diseasetextCheckXDJM_0909'
				+ this.idPostfix
				+ '"/>结核病 <input type="checkbox" name="diseasetextCheckXDJM" value="0910" id="diseasetextCheckXDJM_0910'
				+ this.idPostfix
				+ '"/>肝炎 <input type="checkbox" name="diseasetextCheckXDJM" value="0911" id="diseasetextCheckXDJM_0911'
				+ this.idPostfix
				+ '"/>先天畸形 <input type="checkbox" name="diseasetextCheckXDJM" value="0999" id="diseasetextCheckXDJM_0999'
				+ this.idPostfix
				+ '"/>其他:<input id="qt_xdjm1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td class="basicInfo_in_td_bot"><strong>子 女</strong></td><td colspan="3" class="basicInfo_in_td_bot basicInfo_in_td"><div id="div_diseasetextCheckZN'
				+ this.idPostfix
				+ '"><input type="checkbox" id="diseasetextCheckZN_1001'
				+ this.idPostfix
				+ '" name="diseasetextCheckZN" value="1001" />无  '
				+ '<input type="checkbox" id="diseasetextCheckZN_1002'
				+ this.idPostfix
				+ '" name="diseasetextCheckZN" value="1002" />高血压 '
				+ '<input type="checkbox" name="diseasetextCheckZN" value="1003" id="diseasetextCheckZN_1003'
				+ this.idPostfix
				+ '"/>糖尿病 <input type="checkbox" name="diseasetextCheckZN" value="1004" id="diseasetextCheckZN_1004'
				+ this.idPostfix
				+ '"/>冠心病 <input type="checkbox" name="diseasetextCheckZN" value="1005" id="diseasetextCheckZN_1005'
				+ this.idPostfix
				+ '"/>慢性阻塞性肺疾病 <input type="checkbox" name="diseasetextCheckZN" value="1006" id="diseasetextCheckZN_1006'
				+ this.idPostfix
				+ '"/>恶性肿瘤 <input type="checkbox" name="diseasetextCheckZN" value="1007" id="diseasetextCheckZN_1007'
				+ this.idPostfix
				+ '"/>脑卒中 <input type="checkbox" name="diseasetextCheckZN" value="1008" id="diseasetextCheckZN_1008'
				+ this.idPostfix
				+ '"/>严重精神障碍<input type="checkbox" name="diseasetextCheckZN" value="1009" id="diseasetextCheckZN_1009'
				+ this.idPostfix
				+ '"/>结核病 <input type="checkbox" name="diseasetextCheckZN" value="1010" id="diseasetextCheckZN_1010'
				+ this.idPostfix
				+ '"/>肝炎 <input type="checkbox" name="diseasetextCheckZN" value="1011" id="diseasetextCheckZN_1011'
				+ this.idPostfix
				+ '"/>先天畸形 <input type="checkbox" name="diseasetextCheckZN" value="1099" id="diseasetextCheckZN_1099'
				+ this.idPostfix
				+ '"/>其他:<input id="qt_zn1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr></table></td></tr><tr><td class="red"><strong>遗传病史</strong></td> <td colspan="4"><div id="div_diseasetextRedioYCBS'
				+ this.idPostfix
				+ '"><input type="radio" id="diseasetextRedioYCBS_0501'
				+ this.idPostfix
				+ '" name="diseasetextRedioYCBS" value="0501" />无 '
				+ '<input type="radio" id="diseasetextRedioYCBS_0502'
				+ this.idPostfix
				+ '" name="diseasetextRedioYCBS" value="0502" />有：疾病名称'
				+ '<input type="text" id="diseasetextYCBS'
				+ this.idPostfix
				+ '" class="input_btline"/></div></td></tr><tr><td class="red"><strong>残疾情况</strong></td>'
				+ '<td colspan="4"><div id="div_diseasetextCheckCJ'
				+ this.idPostfix
				+'"><input type="checkbox" id="diseasetextCheckCJ_1101'
				+ this.idPostfix
				+ '" name="diseasetextCheckCJ" value="1101" />无残疾 '
				+ '<input type="checkbox" name="diseasetextCheckCJ" value="1102" id="diseasetextCheckCJ_1102'
				+ this.idPostfix
				+ '"/>视力残疾 <input type="checkbox" name="diseasetextCheckCJ" value="1103" id="diseasetextCheckCJ_1103'
				+ this.idPostfix
				+ '"/>听力残疾 <input type="checkbox" name="diseasetextCheckCJ" value="1104" id="diseasetextCheckCJ_1104'
				+ this.idPostfix
				+ '"/>言语残疾 <input type="checkbox" name="diseasetextCheckCJ" value="1105" id="diseasetextCheckCJ_1105'
				+ this.idPostfix
				+ '"/>肢体残疾 <input type="checkbox" name="diseasetextCheckCJ" value="1106" id="diseasetextCheckCJ_1106'
				+ this.idPostfix
				+ '"/>智力残疾 <input type="checkbox" name="diseasetextCheckCJ" value="1107" id="diseasetextCheckCJ_1107'
				+ this.idPostfix
				+ '"/>精神残疾<input type="checkbox" name="diseasetextCheckCJ" value="1199" id="diseasetextCheckCJ_1199'
				+ this.idPostfix
				+ '" />其他残疾:<input id="cjqk_qtcj1'
				+ this.idPostfix
				+ '" class="input_btline"></div></td></tr><tr><td class="red"><strong>生活环境</strong></td>'
				+ '<td colspan="4"><table width="100%" cellpadding="0" class="basicInfo_in" cellspacing="0" border="0">'
				+ '<tbody><tbody><tr><td width="12%" class="red"><strong>厨房排风设施</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_shhjCheckCFPFSS'
				+ this.idPostfix
				+ '"><input type="radio" id="shhjCheckCFPFSS_1'
				+ this.idPostfix
				+ '"  name="shhjCheckCFPFSS" value="1" />无 '
				+ '<input type="radio" name="shhjCheckCFPFSS" value="2" id="shhjCheckCFPFSS_2'
				+ this.idPostfix
				+ '" />油烟机 <input type="radio" name="shhjCheckCFPFSS" value="3" id="shhjCheckCFPFSS_3'
				+ this.idPostfix
				+ '" />换气扇 <input type="radio" name="shhjCheckCFPFSS" value="4"  id="shhjCheckCFPFSS_4'
				+ this.idPostfix
				+ '"/>烟囱</div></td></tr><tr><td class="red"><strong>燃料类型</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_shhjCheckRLLX'
				+ this.idPostfix
				+ '"><input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_1'
				+ this.idPostfix
				+ '" value="1" />液化气 <input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_2'
				+ this.idPostfix
				+ '" value="2" />煤 <input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_3'
				+ this.idPostfix
				+ '" value="3" /> 天然气 <input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_4'
				+ this.idPostfix
				+ '" value="4" /> 沼气<input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_5'
				+ this.idPostfix
				+ '" value="5" />柴火 <input type="radio" name="shhjCheckRLLX" id="shhjCheckRLLX_9'
				+ this.idPostfix
				+ '" value="9" />其他</div></td></tr><tr><td class="red"><strong>饮水</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_shhjCheckYS'
				+ this.idPostfix
				+ '"><input type="radio" name="shhjCheckYS" id="shhjCheckYS_1'
				+ this.idPostfix
				+ '" value="1" />自来水 <input type="radio" name="shhjCheckYS" id="shhjCheckYS_2'
				+ this.idPostfix
				+ '" value="2" />经净化过滤的水 <input type="radio" name="shhjCheckYS" id="shhjCheckYS_3'
				+ this.idPostfix
				+ '" value="3" />井水 <input type="radio" name="shhjCheckYS" id="shhjCheckYS_4'
				+ this.idPostfix
				+ '" value="4" />河湖水 <input type="radio" name="shhjCheckYS" id="shhjCheckYS_5'
				+ this.idPostfix
				+ '" value="5" />塘水 <input type="radio" name="shhjCheckYS" id="shhjCheckYS_9'
				+ this.idPostfix
				+ '" value="9" />其他</td></tr><tr><td class="red"><strong>厕所</strong></td><td colspan="2" class="basicInfo_in_td"><div id="div_shhjCheckCS'
				+ this.idPostfix
				+ '"><input type="radio" name="shhjCheckCS" id="shhjCheckCS_1'
				+ this.idPostfix
				+ '" value="1" />卫生厕所 <input type="radio" name="shhjCheckCS" id="shhjCheckCS_2'
				+ this.idPostfix
				+ '" value="2" />一格或二格粪池式 <input type="radio" name="shhjCheckCS" id="shhjCheckCS_3'
				+ this.idPostfix
				+ '" value="3" />马桶 <input type="radio" name="shhjCheckCS" id="shhjCheckCS_4'
				+ this.idPostfix
				+ '" value="4" />露天粪坑 <input type="radio" name="shhjCheckCS" id="shhjCheckCS_5'
				+ this.idPostfix
				+ '" value="5" />简易棚厕 </div></td></tr><tr><td class="red" class="basicInfo_in_td_bot"><strong>禽畜栏</strong></td><td colspan="2" class="basicInfo_in_td basicInfo_in_td_bot"><div id="div_shhjCheckQCL'
				+ this.idPostfix
				+ '"><input type="radio" name="shhjCheckQCL" id="shhjCheckQCL_4'
				+ this.idPostfix
				+ '" value="4" />无 <input type="radio" name="shhjCheckQCL" id="shhjCheckQCL_1'
				+ this.idPostfix
				+ '" value="1" />单设 <input type="radio" name="shhjCheckQCL" id="shhjCheckQCL_2'
				+ this.idPostfix
				+ '" value="2" />室内 <input type="radio" name="shhjCheckQCL" id="shhjCheckQCL_3'
				+ this.idPostfix
				+ '" value="3" />室外</div></td></tr></tbody></table></td></tr> '
				+ '<tr><td><strong>死亡标志</strong></td><td>'
				+ '<input type="radio" id="deadFlag_y'
				+ this.idPostfix
				+ '" name="deadFlag" value="y"/>是'
				+ '<input type="radio" name="deadFlag" value="n" id="deadFlag_n'
				+ this.idPostfix
				+ '"/>否</td>'
				+ '<td width="15%"><strong ><span id="SWRQ'
				+ this.idPostfix
				+ '">死亡日期</span></strong></td><td><div id="div_deadDate'
				+ this.idPostfix
				+ '" style="display:inline-block;"/></div></td></tr>'
				+ '<tr> <td ><strong ><span id="SWYY'
				+ this.idPostfix
				+ '">死亡原因</span></strong></td><td colspan="5">'
				+ '<input type="text" id="deadReason'
				+ this.idPostfix
				+ '" class="input_btline" style="width:99%"/></td></tr>'
				+ '<tr><td width="15%"><strong><span id="WGDZ'
				+ this.idPostfix
				+ '" class="red">网格地址</span></strong></td><td width="38%">'
				+ '<div id="div_regionCode'
				+ this.idPostfix
				+ '"></div></td><td width="15%"><strong><span id="SFHZ'
				+ this.idPostfix
				+ '" class="red">是否户主</span></strong></td><td colspan="2"><div id="div_masterFlag'
				+ this.idPostfix
				+ '">'
				+ '<input type="radio" name="masterFlag" value="y" id="masterFlag_y'
				+ this.idPostfix
				+ '"/> 是 <input type="radio" name="masterFlag" value="n" id="masterFlag_n'
				+ this.idPostfix
				+ '"/> 否</div></td></tr><tr><td><strong><span id="ZRYS'
				+ this.idPostfix
				+ '" class="red">责任医生</span></strong></td><td>'
				+ '<div id="div_manaDoctorId' + this.idPostfix
				+ '"></div></td> <td ><strong>管辖机构</strong></td><td>'
				+ '<div id="div_manaUnitId' + this.idPostfix
				+ '"></div></td></tr></tbody></table>';
		return html;
	}
}