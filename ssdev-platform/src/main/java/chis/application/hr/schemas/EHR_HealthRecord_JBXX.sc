<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.hr.EHR_HealthRecord_JBXX" alias="个人基本信息" >
	<item id="empiId" alias="empiid" type="string" length="32" queryable="true" fixed="true" notDefaultValue="true" display="0" lb="jbxx"/>
	<item id="phrId" alias="健康档案号" type="string" length="30" width="160" display="0"  lb="jbxx">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" index="1" length="5" startPos="1" seedRel="areaCode" type="increase"/>
		</key>
	</item>
	<item id="middleId" alias="中间表id" type="string" length="17"  queryable="true" width="160" display="0" lb="jbxx">
		<key>
			<Rule name="areaCode" defaultFill="0" length="12" fillPos="after" type="string">
      		%codeCtx.regionCode
			</Rule>
			<Rule name="increaseId" defaultFill="0" type="increase" seedRel="areaCode" length="5" startPos="1"/>
		</key>
	</item>
	<item id="definePhrid" alias="档案备注说明" type="string"  length="60"  width="100"  tag="text" lb="jbxx"/>
	<item id="personName" alias="姓名" type="string" length="20"   not-null="1"  width="94"  tag="text" lb="jbxx"/>
	<item id="sexCode" alias="性别"   type="string" length="10" width="540" not-null="1" defalutValue = "9" tag="radioGroup" colspan="3" lb="jbxx">
		<dic id="chis.dictionary.gender"/>
	</item>
	<item id="birthday" alias="出生日期"   type="date" width="150"   not-null="1" lb="jbxx" tag="text" maxValue="%server.date.today"/>
	<item id="idCard" alias="身份证号" type="string" length="18" width="250" not-null="1" queryable="true" lb="jbxx" tag="text"/>
	<item id="address" alias="现住址" type="string" length="50" width="230" colspan="3" not-null="1"  lb="jbxx" tag="text"/>
	<item id="workPlace" alias="工作单位" type="string" length="50" not-null="1" width="230" colspan="3"  lb="jbxx" tag="text"/>
	<item id="mobileNumber" alias="本人电话" type="string" length="25" width="150" not-null="1" lb="jbxx" tag="text"/>
	<item id="contact" alias="联系人姓名" type="string" length="20" width="100" not-null="1" lb="jbxx" tag="text"/>
	<item id="contactPhone" alias="联系人电话" type="string" length="25" width="90" not-null="1" lb="jbxx" tag="text"/>
	<item id="registeredPermanent" alias="常住类型" type="string" length="50"   not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.registeredPermanent"/>
	</item>
	<item id="nationCode" alias="民族" type="string" length="10" defaultValue="01"  not-null="1"  lb="jbxx" tag="selectgroup">
		<dic id="chis.dictionary.ethnic"/>
	</item>
	<item id="bloodTypeCode" alias="血型" type="string" length="10"   not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.blood"/>
	</item>
	<item id="rhBloodCode" alias="RH阴性" type="string" length="10"   not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.rhBlood"/>
	</item>
	<item id="educationCode" alias="文化程度" type="string" length="20"   not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.educationjbxx" render="Tree" onlySelectLeaf="true"/>
	</item>	
	<item id="workCode" alias="职业" type="string" length="20"  not-null="1"   lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.jobtitlejbxx" onlySelectLeaf="true"/>
	</item>
	<item id="maritalStatusCode" alias="婚姻状况" type="string" length="20"   not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.maritals" render="Tree" minChars="1" onlySelectLeaf="true"/>
	</item>
	
	<item id="insuranceCode" alias="医疗费用支付方式" type="string" length="20" not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.payModejbxx"/>
	</item>
	<item id="insuranceCode1" alias="其他的支付" type="string" length="50"   lb="jbxx" tag="text"/>
	<item id="masterFlag" alias="是否户主" type="string" length="10"  not-null="1" lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.yesOrNo" render="Tree"/>
	</item>
	<item id="familyId" alias="所属家庭" type="string" length="30" hidden="true" lb="jbxx"/>
	<item id="diseasetext_check_gm" alias="药物过敏史" type="string" length="50" not-null="1"  lb="ywgms" tag="checkBox">
		<dic>
			<item key="0101" text="无药物过敏史" />
			<item key="0102" text="青霉素" />
			<item key="0103" text="磺胺" />
			<item key="0104" text="链霉素" />
			<item key="0109" text="其他" />
		</dic>
	</item>
	<item id="a_qt1" alias="其他过敏史" type="string" length="50"   lb="ywgms" tag="text"/>
	<item id="diseasetext_check_bl" alias="暴露史" type="string" not-null="1" length="50" lb="bls" tag="checkBox">
		<dic>
			<item key="1201" text="无暴露史" />
			<item key="1202" text="化学品" />
			<item key="1203" text="毒物" />
			<item key="1204" text="射线" />
		</dic>
	</item>
	<item id="diseasetext_radio_jb" alias="是否有疾病" type="string" length="50" not-null="1"  lb="jbs" tag="radioGroup">
		<dic id="chis.dictionary.yesOrNo" render="Tree"/>
	</item>
	<item id="diseasetext_check_jb" alias="疾病" type="string" length="50" lb="jbs"  tag="checkBox">
		<dic>
			<item  id="diseasetext_radio_jb" key="0201" text="无疾病史" />
			<item key="0202" text="高血压" />
			<item key="0203" text="糖尿病" />
			<item key="0204" text="冠心病" />
			<item key="0205" text="慢性阻塞性肺疾病" />
			<item key="0206" text="恶性肿瘤" />
			<item key="0207" text="脑卒中" />
			<item key="0208" text="严重精神障碍" />
			<item key="0209" text="结核病" />
			<item key="0210" text="肝炎" />
			<item key="0212" text="职业病" />
			<item key="0298" text="其他法定传染病" />
			<item key="0299" text="其他" />
		</dic>
	</item>
	<item id="confirmdate_gxy" alias="确诊时间" type="date"   width="50"  lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_gxb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_exzl" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_zxjsjb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_gzjb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_zyb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_qt" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_tnb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_mxzsxfjb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_nzz" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_jhb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="confirmdate_qtfdcrb" alias="确诊时间" type="date" width="50"   lb="jbs" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_zyb" alias="职业病" type="string" length="50"   lb="jbs" tag="text"/>
	<item id="diseasetext_qtfdcrb" alias="其他法定传染病" type="string" length="50"   lb="jbs" tag="text"/>
	<item id="diseasetext_qt" alias="其他" type="string" length="50"   lb="jbs" tag="text"/>
	<item id="diseasetext_ss" alias="手术" type="string" length="20"   not-null="1"  lb="ss" tag="radioGroup">
		<dic>
			<item key="0301" text="无手术史" />
			<item key="0302" text="有手术史" />
		</dic>
	</item>
	<item id="diseasetext_ss0" alias="名称" type="string" length="25"   lb="ss" tag="text"/>
	<item id="startdate_ss0" alias="时间" type="date" length="50"   lb="ss" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_ss1" alias="名称" type="string" length="25"   lb="ss" tag="text"/>
	<item id="startdate_ss1" alias="时间" type="date" length="50"   lb="ss" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_ws" alias="外伤" type="string" length="20" not-null="1"  lb="ws" tag="radioGroup">
		<dic>
			<item key="0601" text="无外伤史" />
			<item key="0602" text="有外伤史" />
		</dic>
	</item>
	<item id="diseasetext_ws0" alias="名称" type="string" length="25"   lb="ws" tag="text"/>
	<item id="startdate_ws0" alias="时间" type="date" length="50"   lb="ws" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_ws1" alias="名称" type="string" length="25"   lb="ws" tag="text"/>
	<item id="startdate_ws1" alias="时间" type="date" length="50"   lb="ws" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_sx" alias="输血" type="string" length="20"  not-null="1" lb="sx" tag="radioGroup">
		<dic>
			<item key="0401" text="无输血史" />
			<item key="0402" text="有输血史" />
		</dic>
	</item>
	<item id="diseasetext_sx0" alias="名称" type="string" length="25"   lb="sx" tag="text"/>
	<item id="startdate_sx0" alias="时间" type="date" length="50"   lb="sx" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_sx1" alias="名称" type="string" length="25"   lb="sx" tag="text"/>
	<item id="startdate_sx1" alias="时间" type="date" length="50"   lb="sx" tag="text" maxValue="%server.date.today"/>
	<item id="diseasetext_check_fq" alias="家族疾病史-父亲" type="string" length="50" not-null="1"  lb="jzjbs" tag="checkBox">
		<dic>
			<item key="0701" text="无父亲疾病史" />
			<item key="0702" text="高血压" />
			<item key="0703" text="糖尿病" />
			<item key="0704" text="冠心病" />
			<item key="0705" text="慢性阻塞性肺疾病" />
			<item key="0706" text="恶性肿瘤" />
			<item key="0707" text="脑卒中" />
			<item key="0708" text="严重精神障碍" />
			<item key="0709" text="结核病" />
			<item key="0710" text="肝炎" />
			<item key="0711" text="先天畸形" />
			<item key="0799" text="其他" />
		</dic>
	</item>
	<item id="qt_fq1" alias="父亲其他家族疾病史" type="string" length="50"   lb="jzjbs" tag="text"/>	
	<item id="diseasetextCheckMQ" alias="家族疾病史-母亲" type="string" length="50"  not-null="1" lb="jzjbs" tag="checkBox">
		<dic>
			<item key="0801" text="无母亲疾病史" />
			<item key="0802" text="高血压" />
			<item key="0803" text="糖尿病" />
			<item key="0804" text="冠心病" />
			<item key="0805" text="慢性阻塞性肺疾病" />
			<item key="0806" text="恶性肿瘤" />
			<item key="0807" text="脑卒中" />
			<item key="0808" text="严重精神障碍" />
			<item key="0809" text="结核病" />
			<item key="0810" text="肝炎" />
			<item key="0811" text="先天畸形" />
			<item key="0899" text="其他" />
		</dic>
	</item>
	<item id="qt_mq1" alias="母亲其他家族疾病史" type="string" length="50"   lb="jzjbs" tag="text"/>
	<item id="diseasetextCheckXDJM" alias="家族疾病史-兄弟姐妹" type="string" length="50" not-null="1" lb="jzjbs" tag="checkBox">
		<dic>
			<item key="0901" text="无兄弟姐妹疾病史" />
			<item key="0902" text="高血压" />
			<item key="0903" text="糖尿病" />
			<item key="0904" text="冠心病" />
			<item key="0905" text="慢性阻塞性肺疾病" />
			<item key="0906" text="恶性肿瘤" />
			<item key="0907" text="脑卒中" />
			<item key="0908" text="严重精神障碍" />
			<item key="0909" text="结核病" />
			<item key="0910" text="肝炎" />
			<item key="0911" text="先天畸形" />
			<item key="0999" text="其他" />
		</dic>
	</item>
	<item id="qt_xdjm1" alias="兄弟姐妹其他家族疾病史" type="string" length="50"   lb="jzjbs" tag="text"/>	
	<item id="diseasetextCheckZN" alias="家族疾病史-子女" type="string" length="50" not-null="1" lb="jzjbs" tag="checkBox">
		<dic>
			<item key="1001" text="无子女疾病史" />
			<item key="1002" text="高血压" />
			<item key="1003" text="糖尿病" />
			<item key="1004" text="冠心病" />
			<item key="1005" text="慢性阻塞性肺疾病" />
			<item key="1006" text="恶性肿瘤" />
			<item key="1007" text="脑卒中" />
			<item key="1008" text="严重精神障碍" />
			<item key="1009" text="结核病" />
			<item key="1010" text="肝炎" />
			<item key="1011" text="先天畸形" />
			<item key="1099" text="其他" />
		</dic>
	</item>
	<item id="qt_zn1" alias="子女其他家族疾病史" type="string" length="50"   lb="jzjbs" tag="text"/>
	<item id="diseasetextRedioYCBS" alias="是否有遗传病史" type="string" length="20" not-null="1" lb="ycbs" tag="radioGroup">
		<dic>
			<item key="0501" text="无遗传病史" />
			<item key="0502" text="有遗传病史" />
		</dic>
	</item>
	<item id="diseasetextYCBS" alias="遗传病史" type="string" length="50"   lb="ycbs" tag="text">  
	</item>
	<item id="diseasetextCheckCJ" alias="残疾状况" type="string" length="50"  not-null="1" lb="cj" tag="checkBox">
		<dic>
			<item key="1101" text="无残疾" />
			<item key="1102" text="视力残疾" />
			<item key="1103" text="听力残疾 " />
			<item key="1104" text="言语残疾" />
			<item key="1105" text="肢体残疾" />
			<item key="1106" text="智力残疾" />
			<item key="1107" text="精神残疾" />
			<item key="1199" text="其他残疾" />
		</dic>
	</item>
	<item id="cjqk_qtcj1" alias="其他残疾" type="string" length="50"   lb="cj" tag="text"/>
	<item id="shhjCheckCFPFSS" alias="厨房排风设施" type="string" length="50"  not-null="1"  lb="shhj" tag="radioGroup">
		<dic id="chis.dictionary.cookAirTooljbxx"/>
	</item>
	<item id="shhjCheckRLLX" alias="燃料类型" type="string" length="50"  not-null="1"  lb="shhj" tag="radioGroup">
		<dic id="chis.dictionary.fuelType"/>
	</item>
	<item id="shhjCheckYS" alias="饮水" type="string" length="50"  not-null="1"  lb="shhj" tag="radioGroup">
		<dic id="chis.dictionary.waterSourceCodebxx"/>
	</item>
	<item id="shhjCheckCS" alias="厕所" type="string" length="50"  not-null="1"  lb="shhj" tag="radioGroup">
		<dic id="chis.dictionary.washroom"/>
	</item>
	<item id="shhjCheckQCL" alias="禽畜栏" type="string" length="50"  not-null="1"  lb="shhj" tag="radioGroup">
		<dic id="chis.dictionary.livestockColumn"/>
	</item>
	<item id="regionCode_text" alias="网格地址" type="string" length="50"  width="360" display="0"  lb="jbxx">	
	</item>
	<item id="deadFlag" alias="死亡标志" type="string" length="25" width="150" not-null="1"  lb="jbxx" tag="radioGroup">
		<dic id="chis.dictionary.yesOrNo"/>
	</item>
	<item id="deadDate" alias="死亡日期" type="date" length="11" width="100"  lb="jbxx" tag="text" maxValue="%server.date.today"/>
	<item id="deadReason" alias="死亡原因" type="string" length="100" width="300"  lb="jbxx" tag="text" />
    
	<item id="regionCode" alias="网格地址" type="string" length="100"  width="360"  lb="jbxx">
		<dic id="chis.dictionary.areaGrid" includeParentMinLen="6" filterMin="10" minChars="4" filterMax="18" render="Tree" onlySelectLeaf="true"/>
	</item>  
	 
	<item id="manaDoctorId" alias="责任医生" type="string" length="100" not-null="1"   width="360" lb="jbxx">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" parentKey="%user.manageUnit.id"/>
		
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="50" colspan="2" anchor="100%" width="360"  fixed="true"   not-null="1"  lb="jbxx"><!--defaultValue="%user.manageUnit.id"-->
		<dic id="chis.@manageUnit" includeParentMinLen="6" sliceType = "3" render="Tree" onlySelectLeaf="true"/>
	</item>
	
	<!--
		<item ref="b.personName" display="1" queryable="true"/>
		<item ref="b.sexCode" display="1" queryable="true"/>
		<item ref="b.birthday" display="1" queryable="true"/>
		<item ref="b.idCard" display="1" queryable="true"/>
		<item ref="b.mobileNumber" display="1" queryable="true"/>
		<item ref="b.contactPhone" display="1" queryable="true"/>
		<item ref="b.registeredPermanent" display="0" queryable="true"/>
		<item id="empiId" alias="empiid" type="string" length="32" fixed="true" notDefaultValue="true" hidden="true"/>

		<item id="manaUnitId" alias="管辖机构" type="string" not-null="true" length="20" colspan="2" anchor="100%" width="180"  fixed="true" queryable="true" defaultValue="%user.manageUnit.id">
			<dic id="manageUnit" showWholeText="true" includeParentMinLen="6" render="Tree"/>
		</item>
		<item id="masterFlag" alias="是否户主" type="string"  length="1">
			<dic id="yesOrNo" render="Tree"/>
		</item>
		<item id="relaCode" alias="与户主关系" type="string" length="2" colspan="2">
			<dic id="relaCode" render="Tree" onlySelectLeaf="true"/>
		</item>
		<item id="familyId" alias="所属家庭" type="string" length="30" hidden="true"/>
		<item id="fatherId" alias="父亲编号" type="string" length="32" hidden="true" display="2"/>
		<item id="fatherName" alias="父亲姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
		<item id="motherId" alias="母亲编号" type="string" length="32" hidden="true" display="2"/>
		<item id="motherName" alias="母亲姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
		<item id="partnerId" alias="配偶编号" type="string" length="32" hidden="true" display="2"/>
		<item id="partnerName" alias="配偶姓名" type="string" length="20" display="2" xtype="lookupfieldex"/>
		<item id="signFlag" alias="签约标志" type="string" length="1" defaultValue="n" canInput="true" validateOnBlur="false">
			<dic id="yesOrNo"/>
		</item>
		<item id="isAgrRegister" alias="是否农业户籍" type="string" length="1" not-null="1" display="2">
			<dic id="yesOrNo"/>
		</item>
		<item id="incomeSource" alias="经济来源" type="string" length="10" not-null="1" display="2">
			<dic render="LovCombo">
				<item key="1" text="社会救济"/>
				<item key="2" text="工作收入"/>
				<item key="3" text="其他"/>
			</dic>
		</item>
		<item id="deadFlag" alias="死亡标志" type="string" length="1" defaultValue="n" display="2">
			<dic id="yesOrNo"/>
		</item>
		<item id="deadDate" alias="死亡日期" type="date" fixed="true" display="2" maxValue="%server.date.date"/>
		<item id="deadReason" alias="死亡原因" type="string" fixed="true" length="100" display="2" colspan="3"/>
		<item id="createUnit" alias="建档单位" type="string" length="20" update="false" width="180" fixed="true" defaultValue="%user.manageUnit.id">
			<dic id="manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
			<set type="exp">['$','%user.manageUnit.id']</set>
		</item>
		<item id="createUser" alias="建档人" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
			<dic id="user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
			<set type="exp">['$','%user.userId']</set>
		</item>
		<item id="createDate" alias="建档日期" type="datetime"  xtype="datefield" update="false" fixed="true" defaultValue="%server.date.date" queryable="true">
			<set type="exp">['$','%server.date.datetime']</set>
		</item>
		<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" width="180" display="1" defaultValue="%user.manageUnit.id">
			<dic id="manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
			<set type="exp">['$','%user.manageUnit.id']</set>
		</item>
		<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="1" defaultValue="%user.userId">
			<dic id="user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
			<set type="exp">['$','%user.userId']</set>
		</item>
		<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="1" defaultValue="%server.date.date">
			<set type="exp">['$','%server.date.datetime']</set>
		</item>
		<item id="cancellationUnit" alias="注销单位" type="string" length="20" width="180" hidden="true">
			<dic id="manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		</item>
		<item id="cancellationUser" alias="注销人" type="string" length="20" hidden="true">
			<dic id="user" render="Tree" onlySelectLeaf="true" parentKey="%user.manageUnit.id"/>
		</item>
		<item id="cancellationDate" alias="注销日期" type="date" hidden="true"/>
		<item id="status" alias="档案状态" type="string" length="1" defaultValue="0" hidden="true">
			<dic>
				<item key="0" text="正常"/>
				<item key="1" text="已注销"/>
			</dic>
		</item>
		<item id="cancellationReason" alias="注销原因" type="string" length="1" hidden="true">
			<dic>
				<item key="1" text="死亡"/>
				<item key="2" text="迁出"/>
				<item key="3" text="失访"/>
				<item key="4" text="拒绝"/>
				<item key="9" text="其他"/>
			</dic>
		</item>
		<item id="oldlastVisitDate" alias="老年人最后随访时间" type="date" hidden="true"/>
		<item id="isDiabetes" alias="是否糖尿病" type="string" length="1" defaultValue="n" queryable="true" display="1">
			<dic id="yesOrNo"/>
		</item>
		<item id="isHypertension" alias="是否高血压" type="string" length="1" defaultValue="n" queryable="true" display="1">
			<dic id="yesOrNo"/>
		</item>
		<relations>
			<relation type="parent" entryName="MPI_DemographicInfo"/>
		</relations>-->
</entry>
