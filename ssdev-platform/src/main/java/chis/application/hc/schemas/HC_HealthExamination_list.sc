<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_HealthExamination_list" tableName="chis.application.hc.schemas.HC_HealthExamination" alias="基本情况">
	<item id="healthCheck" alias="检查单号" length="16" width="130"
		type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPIID" length="32"
		type="string" display="0" />
	<item id="phrId" alias="健康档案号" length="30" type="string"
		display="0" />
	<item  id="checkDate" alias="年检日期" type="date"
		not-null="true" defaultValue="%server.date.today"/>
	<item ref="b.personName" display="0" queryable="false"/>
	<item ref="b.sexCode" display="0" queryable="false"/>
	<item ref="b.birthday" display="0" queryable="false"/>
	<item ref="b.idCard" display="0" queryable="false"/>
	<item ref="c.regionCode" 	display="0" queryable="false"/> 

	<item id="manaDoctorId" alias="责任医生" type="string" fixed="true" length="20"  
		defaultValue="%user.userId" not-null="1" update="false" display="0">
		<dic id="chis.dictionary.user01" render="Tree" onlySelectLeaf="true" keyNotUniquely="true" />
	</item>
	<item id="manaUnitId" alias="管辖机构" type="string" length="20" colspan="2" anchor="100%" 
		display="0" width="180" not-null="1" fixed="true"  defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit"  includeParentMinLen="6" render="Tree"/>
	</item>
	<item id="personalCode" alias="个人编码" length="30"
		type="string" display="0" />
	<item  id="symptom" alias="症状" length="80" type="string"
		colspan="2" display="0">
		<dic render="LovCombo">
			<item key="01" text="无症状" />
			<item key="02" text="头痛" />
			<item key="03" text="头晕" />
			<item key="04" text="心悸" />
			<item key="05" text="胸闷" />
			<item key="06" text="胸痛" />
			<item key="07" text="慢性咳嗽" />
			<item key="08" text="咳痰" />
			<item key="09" text="呼吸困难" />
			<item key="10" text="多饮" />
			<item key="11" text="多尿" />
			<item key="12" text="体重下降" />
			<item key="13" text="乏力" />
			<item key="14" text="关节肿痛" />
			<item key="15" text="视力模糊" />
			<item key="16" text="手脚麻木" />
			<item key="17" text="尿急" />
			<item key="18" text="尿痛" />
			<item key="19" text="便秘" />
			<item key="20" text="腹泻" />
			<item key="21" text="恶心呕吐" />
			<item key="22" text="眼花" />
			<item key="23" text="耳鸣" />
			<item key="24" text="乳房胀痛" />
			<item key="25" text="其他" />
		</dic>
	</item>
	<item id="symptomOt" alias="其他症状" length="100"
		type="string" fixed="true" display="0" />
	<item id="temperature" alias="体温(℃)" length="5"
		precision="2" type="double" display="0" />
	<item id="breathe" alias="呼吸(次/分)" type="int" display="0" />
	<item id="pulse" alias="脉率(次/分)" type="int" display="0" />
	<item id="constriction" alias="收缩压R(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" display="0"/>
	<item id="diastolic" alias="舒张压R(mmHg)" minValue="10" width="110"
		maxValue="500" type="int" not-null="true" display="0"/>
	<item id="constriction_L" alias="收缩压L(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" display="0"/>
	<item id="diastolic_L" alias="舒张压L(mmHg)" width="110"
		minValue="10" maxValue="500" type="int" not-null="true" display="0"/>
	<item  id="height" alias="身高(cm)" length="8"
		precision="2" type="double" not-null="true" display="0"/>
	<item  id="weight" alias="体重(kg)" length="8"
		precision="2" type="double" not-null="true" display="0"/>
	<item id="waistline" alias="腰围(cm)" length="8"
		precision="2" type="double" display="0"/>
	<item  id="bmi" alias="BMI(Kg/m2)" length="8"
		precision="2" type="double" fixed="true" display="0"/>
	<item id="healthStatus" alias="老年人健康状态" length="1"
		type="string" display="0">
		<dic>
			<item key="1" text="满意" />
			<item key="2" text="基本满意" />
			<item key="3" text="说不清楚" />
			<item key="4" text="不太满意" />
			<item key="5" text="不满意" />
		</dic>
	</item>
	<item id="selfCare" alias="老年人自理能力" length="1"
		type="string" display="0" >
		<dic>
			<item key="1" text="可自理(0～3分)" />
			<item key="2" text="轻度依赖(4～8分)" />
			<item key="3" text="中度依赖(9～18分)" />
			<item key="4" text="不能自理(≥19分)" />
		</dic>
	</item>
	<item id="cognitive" alias="老年人认知" length="20"
		type="string" display="0">
		<dic>
			<item key="1" text="粗筛阴性" />
			<item key="2" text="粗筛阳性" />
		</dic>
	</item>
	<item id="cognitiveZf" alias="智力检查总分" length="5"
		precision="2" type="double"  display="0"/>
	<item id="emotion" alias="老年人情感状态" length="20"
		type="string" display="0">
		<dic>
			<item key="1" text="粗筛阴性" />
			<item key="2" text="粗筛阳性" />
		</dic>
	</item>
	<item id="emotionZf" alias="抑郁评分" length="5"
		precision="2" type="double" display="0" />
	<!-- ===============生活方式==s===================== -->	
	<item  id="physicalExerciseFrequency" alias="体育锻炼频率" length="1" type="string" display="0">
		<dic>
			<item key="1" text="每天"/>
			<item key="2" text="每周一次以上"/>
			<item key="3" text="偶尔"/>
			<item key="4" text="不锻炼"/>
		</dic>
	</item>
	<item  id="everyPhysicalExerciseTime" alias="锻炼时间(分/次)" type="int" fixed="true" display="0"/>
	<item  id="insistexercisetime" alias="坚持锻炼(年)" type="int" fixed="true" display="0"/>
	<item  id="exerciseStyle" alias="体育锻炼方式" length="60" type="string" fixed="true" display="0"/>
	<item  id="dietaryHabit" alias="饮食习惯" length="64" type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="荤素均衡"/>
			<item key="2" text="荤食为主"/>
			<item key="3" text="素食为主"/>
			<item key="4" text="嗜盐"/>
			<item key="5" text="嗜油"/>
			<item key="6" text="嗜糖"/>
		</dic>
	</item>
	<item  id="wehtherSmoke" alias="吸烟状况" length="1" type="string" display="0">
		<dic>
			<item key="1" text="从不吸烟"/>
			<item key="2" text="已戒烟"/>
			<item key="3" text="吸烟"/>
		</dic>
	</item>
	<item  id="beginSmokeTime" alias="开始吸烟(岁)" type="int" fixed="true" display="0"/>
	<item  id="stopSmokeTime" alias="戒烟年龄(岁)" type="int" fixed="true" display="0"/>
	<item  id="smokes" alias="日吸烟量(支)" type="int" fixed="true" display="0"/>
	<item  id="drinkingFrequency" alias="饮酒频率" length="1" type="string" display="0">
		<dic>
			<item key="1" text="从不"/>
			<item key="2" text="偶尔"/>
			<item key="3" text="经常"/>
			<item key="4" text="每天"/>
		</dic>
	</item>
	<item  id="alcoholConsumption" alias="日饮酒量(两)" type="int" fixed="true" display="0"/>
	<item  id="whetherDrink" alias="是否戒酒" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="未戒酒"/>
			<item key="2" text="已戒酒"/>
		</dic>
	</item>
	<item  id="stopDrinkingTime" alias="戒酒年龄(岁)" type="int" fixed="true" display="0"/>
	<item  id="geginToDrinkTime" alias="开始饮酒(岁)" type="int" fixed="true" display="0"/>
	<item  id="isDrink" alias="一年内醉酒否" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="否"/>
		</dic>
	</item>
	<item  id="mainDrinkingVvarieties" alias="饮酒种类" length="20" type="string" fixed="true" display="0">
		<dic id="chis.dictionary.drinkTypeCode_life" render="LovCombo"/>
	</item>
	<item  id="drinkOther" alias="其他饮酒种类"
		length="50" type="string" fixed="true" display="0"/>
	<item  id="occupational" alias="职业病危害" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="jobs" alias="工种" length="50" type="string" fixed="true" display="0"/>
	<item  id="workTime" alias="从业时间(年)" type="int" fixed="true" display="0"/>
	<item  id="dust" alias="粉尘" length="50" type="string" fixed="true" display="0"/>
	<item  id="dustPro" alias="有无防护措施" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="dustProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true" display="0"/>
	<item  id="ray" alias="放射物质" length="50" type="string" fixed="true" display="0"/>
	<item  id="rayPro" alias="有无防护措施" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="rayProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true" display="0"/>
	<item  id="physicalFactor" alias="物理因素" length="50" type="string" fixed="true" display="0"/>
	<item  id="physicalFactorPro" alias="有无防护措施" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="physicalFactorProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true" display="0"/>
	<item  id="chemicals" alias="化学物质" length="50" type="string" fixed="true" display="0"/>
	<item  id="chemicalsPro" alias="有无防护措施" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="chemicalsProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true" display="0"/>
	<item  id="other" alias="其他毒物" length="50" type="string" fixed="true" display="0"/>
	<item  id="otherPro" alias="有无防护措施" length="1" type="string" fixed="true" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="otherProDesc" alias="防护描述" colspan="2" length="50" type="string" fixed="true" display="0"/>
	<!-- ===============生活方式==e===================== -->	
	<!-- ===============辅助检查=1=s=====脏器功能==== -->
	<item id="lip" alias="口唇"  type="string" length="100"  display="0">
		<dic>
			<item key="1" text="红润" />
			<item key="2" text="苍白" />
			<item key="3" text="发绀" />
			<item key="4" text="皲裂" />
			<item key="5" text="疱疹" />
		</dic>
	</item>
	<item id="denture" alias="齿列"  type="string" length="100"  display="0">
		<dic render="LovCombo">
			<item key="1" text="正常" />
			<item key="2" text="缺齿" />
			<item key="3" text="龋齿" />
			<item key="4" text="义齿" />
		</dic>
	</item>
	<item id="leftUp" alias="左上" type="int" display="0"/>
	<item id="leftDown" alias="左下" type="int" display="0"/>
	<item id="rightUp" alias="右上" type="int" display="0"/>
	<item id="rightDown" alias="右下" type="int" display="0"/>
	
	<item id="hypodontia" alias="缺齿位置"  type="string" length="100" fixed="true" display="0"/>
	
	<item id="decay" alias="龋齿位置"  type="string" length="100" fixed="true" display="0"/>
	
	<item id="falsethooh" alias="义齿位置"  type="string" length="100" fixed="true" display="0"/>
	
	<item id="pharyngeal" alias="咽部"  type="string" length="100"  display="0">
		<dic>
			<item key="1" text="无充血" />
			<item key="2" text="充血" />
			<item key="3" text="淋巴滤泡增生" />
		</dic>
	</item>
	
	<item id="leftEye" alias="左眼视力"  type="double" length="10"  precision="1" maxValue="5.3" display="0"/>
	<item id="rightEye" alias="右眼视力"  type="double" length="10"  precision="1"  maxValue="5.3" display="0"/>
	<item id="recLeftEye" alias="矫正左眼视力"  type="double" length="10" display="0" />
	<item id="recRightEye" alias="矫正右眼视力"  type="double" length="10"  display="0"/>
	
	<item id="hearing" alias="听力"  type="string" length="100"  display="0">
		<dic>
			<item key="1" text="听见" />
			<item key="2" text="听不清或无法听见" />
		</dic>
	</item>
	<item id="motion" alias="运动功能"  type="string" length="100" display="0" >
		<dic>
			<item key="1" text="可顺利完成" />
			<item key="2" text="无法独立完成其中任何一个动作" />
		</dic>
	</item>
	<!-- ===============辅助检查=1=e===================== -->	
	<!-- ===============检体==s===================== -->
	<item  id="fundus" alias="眼底" length="1" type="string" display="0">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="fundusDesc" alias="眼底描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="skin" alias="皮肤" length="1" type="string" display="0">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="潮红"/>
			<item key="3" text="苍白"/>
			<item key="4" text="发绀"/>
			<item key="5" text="黄染"/>
			<item key="6" text="色素沉着"/>
			<item key="7" text="其他"/>
		</dic>
	</item>
	<item  id="skinDesc" alias="皮肤其他" length="100" type="string" fixed="true" display="0"/>
	<item  id="sclera" alias="巩膜" length="1" type="string" display="0">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="黄染"/>
			<item key="3" text="充血"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item  id="scleraDesc" alias="巩膜其他" length="100" type="string" display="0"/>
	<item  id="lymphnodes" alias="淋巴结" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未触及"/>
			<item key="2" text="锁骨上"/>
			<item key="3" text="腋窝"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item  id="lymphnodesDesc" alias="淋巴结其他" length="100" type="string" fixed="true" display="0"/>
	<item  id="barrelChest" alias="桶状胸" length="1" type="string" display="0">
		<dic>
			<item key="1" text="否"/>
			<item key="2" text="是"/>
		</dic>
	</item>
	<item  id="breathSound" alias="呼吸音" length="1" type="string" display="0">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="breathSoundDesc" alias="异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="rales" alias="罗音" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无 　"/>
			<item key="2" text="干罗音"/>
			<item key="3" text="湿罗音"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item  id="ralesDesc" alias="其他罗音描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="heartRate" alias="心率(次/分)" type="int" display="0"/>
	<item  id="rhythm" alias="心律" length="1" type="string" display="0">
		<dic>
			<item key="1" text="齐"/>
			<item key="2" text="不齐"/>
			<item key="3" text="绝对不齐"/>
		</dic>
	</item>
	<item  id="heartMurmur" alias="心脏杂音" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="heartMurmurDesc" alias="杂音描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="abdominAltend" alias="腹部压痛" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="abdominAltendDesc" alias="压痛描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="adbominAlmass" alias="腹部包块" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="adbominAlmassDesc" alias="包块描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="liverBig" alias="肝大" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="liverBigDesc" alias="肝大描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="splenomegaly" alias="脾大" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="splenomegalyDesc" alias="脾大描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="dullness" alias="腹部移动性浊音" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item  id="dullnessDesc" alias="描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="edema" alias="下肢水肿" length="1" type="string" display="0">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="单侧"/>
			<item key="3" text="双侧不对称"/>
			<item key="4" text="双侧对称"/>
		</dic>
	</item>
	<item  id="footPulse" alias="足背动脉搏动" length="1" type="string" colspan="2" display="0">
		<dic>
			<item key="1" text="未触及"/>
			<item key="2" text="触及双侧对称"/>
			<item key="3" text="触及左侧弱或消失"/>
			<item key="4" text="触及右侧弱或消失"/>
		</dic>
	</item>
	<item  id="dre" alias="肛门指诊" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未及异常"/>
			<item key="2" text="触痛"/>
			<item key="3" text="包块"/>
			<item key="4" text="前列腺异常"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item  id="dreDesc" alias="其他描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="breast" alias="乳腺" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="乳房切除"/>
			<item key="3" text="异常泌乳"/>
			<item key="4" text="乳腺包块"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item  id="breastDesc" alias="乳腺其他描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="vulva" alias="外阴" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="vulvaDesc" alias="外阴异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="vaginal" alias="阴道" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="vaginalDesc" alias="阴道异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="cervix" alias="宫颈" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="cervixDesc" alias="宫颈异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="palace" alias="宫体" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="palaceDesc" alias="宫体异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="attachment" alias="附件" length="1" type="string" display="0">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item  id="attachmentDesc" alias="附件异常描述" length="100" type="string" fixed="true" display="0"/>
	<item  id="tjother" alias="其他查体" length="100" type="string" colspan="4" display="0"/>
	<!-- ===============检体==e===================== -->
	<!-- ===============辅助检查=2=s========= -->
	<item id="hgb" alias="血红蛋白(g/L)"  type="double" length="10" display="0" />
	<item id="wbc" alias="白细胞(10^9/L)"  type="double" length="10"  display="0"/>
	<item id="platelet" alias="血小板(10^9/L)"  type="double" length="10" display="0" />
	<item id="bloodOther" alias="血常规其他"  type="string" length="100"  display="0"/>
	
	<item id="proteinuria" alias="尿蛋白"  type="string" length="10"  display="0"/>
	<item id="glu" alias="尿糖"  type="string" length="10"  display="0"/>
	<item id="dka" alias="尿酮体"  type="string" length="10"  display="0"/>
	<item id="oc" alias="尿潜血"  type="string" length="10"  display="0"/>
	<item id="urineOther" alias="尿常规其他"  type="string" length="100" display="0" />
	
	<item id="fbs" alias="空腹血糖(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="fbs2" alias="空腹血糖2(mmol/L)"  type="double" length="10"  display="0"/>
	
	<item id="ecg" alias="心电图" type="string" length="1" display="0">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="ecgText" alias="异常描述"  type="string" length="200" fixed="true" display="0"/>
	
	<item id="malb" alias="尿微量白蛋白"  type="double" length="10"  display="0"/>
	
	<item id="fob" alias="大便潜血" type="string" length="1" display="0">
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	
	<item id="hba1c" alias="糖化血红蛋白(%)"  type="int" length="3"  display="0"/>
	
	<item id="hbsag" alias="乙型肝炎表面抗原" length="1" type="string" display="0" >
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	
	<item id="alt" alias="血清谷丙转氨酶(U/L)"  type="double" length="10"  display="0"/>
	<item id="ast" alias="血清谷草转氨酶(U/L)"  type="double" length="10"  display="0"/>
	<item id="alb" alias="白蛋白(g/L)"  type="double" length="10"  display="0"/>
	<item id="tbil" alias="总胆红素(μmol/L)"  type="double" length="10"  display="0"/>
	<item id="dbil" alias="结合胆红素(μmol/L)"  type="double" length="10"  display="0"/>
	
	<item id="cr" alias="血清肌酐(μmol/L)"  type="double" length="10"  display="0"/>
	<item id="bun" alias="血尿素氮(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="kalemia" alias="血钾浓度(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="natremia" alias="血钠浓度(mmol/L)"  type="double" length="10"  display="0"/>
	
	<item id="tc" alias="总胆固醇(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="tg" alias="甘油三酯(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="ldl" alias="血清LDL-C(mmol/L)"  type="double" length="10"  display="0"/>
	<item id="hdl" alias="血清HDL-C(mmol/L)"  type="double" length="10"  display="0"/>
	
	<item id="x" alias="胸部X线片" type="string" length="1" display="0">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="xText" alias="异常描述"  type="string" length="200" fixed="true" display="0"/>
	
	<item id="b" alias="B超" type="string" length="1" display="0">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="bText" alias="异常描述"  type="string" length="200" fixed="true"  display="0"/>
	
	<item id="ps" alias="宫颈涂片" type="string" length="1" display="0">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="psText" alias="异常描述"  type="string" length="200"  colspan="2" fixed="true" display="0"/>
	
	<item id="fuOther" alias="其他辅助检查" type="string" length="1000" colspan="4" display="0"/>
	<!-- ===============辅助检查=2=e========= -->
	<item id="cerebrovascularDiseases" alias="脑血管疾病"
		length="20" type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="缺血性卒中" />
			<item key="3" text="脑出血" />
			<item key="4" text="蛛网膜下腔出血" />
			<item key="5" text="短暂性脑缺血发作" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="othercerebrovascularDiseases" alias="其他脑血管疾病"
		length="50" type="string" fixed="true" display="0" />
	<item id="heartDisease" alias="心脏疾病" length="20"
		type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="心肌梗塞" />
			<item key="3" text="心绞痛" />
			<item key="4" text="冠状动脉血运重建" />
			<item key="5" text="充血性心力衰竭" />
			<item key="6" text="心前区疼痛" />
			<item key="7" text="其他" />
		</dic>
	</item>
	<item id="otherheartDisease" alias="其他心脏疾病"
		length="50" type="string" fixed="true" display="0"/>
	<item id="kidneyDiseases" alias="肾脏疾病" length="20"
		type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="糖尿病肾病" />
			<item key="3" text="肾衰竭" />
			<item key="4" text="急性肾炎" />
			<item key="5" text="慢性肾炎" />
			<item key="6" text="其他" />
		</dic>
	</item>
	<item id="otherkidneyDiseases" alias="其他肾脏疾病"
		length="50" type="string" fixed="true" display="0" />
	<item id="VascularDisease" alias="血管疾病" length="20"
		type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="夹层动脉瘤" />
			<item key="3" text="动脉闭塞性疾病" />
			<item key="4" text="其他" />
		</dic>
	</item>
	<item id="otherVascularDisease" alias="其他血管疾病"
		length="50" type="string" fixed="true" display="0" />
	<item id="eyeDiseases" alias="眼部疾病" length="20"
		type="string" display="0">
		<dic render="LovCombo">
			<item key="1" text="未发现" />
			<item key="2" text="视网膜出血或渗出" />
			<item key="3" text="视乳头水肿" />
			<item key="4" text="白内障" />
			<item key="5" text="其他" />
		</dic>
	</item>
	<item id="othereyeDiseases" alias="其他眼部疾病"
		length="50" type="string" fixed="true" display="0" />
	<item id="neurologicalDiseases" alias="神经疾病"
		length="1" type="string" display="0">
		<dic>
			<item key="1" text="未发现" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="neurologicalDiseasesDesc" alias="神经疾病描述"
		length="100" type="string" fixed="true" display="0" />
	<item id="otherDiseasesone" alias="其他疾病" length="1"
		type="string" display="0">
		<dic>
			<item key="1" text="未发现" />
			<item key="2" text="有" />
		</dic>
	</item>
	<item id="otherDiseasesoneDesc" alias="其他疾病描述"
		length="100" type="string" fixed="true" display="0" />
	<!-- ===============健康评价==s========= -->
	<item id="hePing" alias="平和质" type="string" length="1" display="0" >
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="qiXu" alias="气虚质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="yangXu" alias="阳虚质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="yinXu" alias="阴虚质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="tanShi" alias="痰湿质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="shiRe" alias="湿热质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="xueYu" alias="血瘀质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="qiYu" alias="气郁质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="teBing" alias="特禀质" type="string" length="1" display="0">
		<dic>
			<item key="1" text="是"/>
			<item key="2" text="基本是"/>
		</dic>
	</item>
	<item id="healthAssessment" alias="健康评价" length="1"  type="string" colspan="2" display="0">
		<dic>
			<item key = "1" text="体检无异常" />
			<item key = "2" text="有异常" />
		</dic>
	</item>
	<item id="healthAssessmentExce1" alias="异常1" length="500"  type="string" colspan="2" fixed="true" display="0"/>
	<item id="healthAssessmentExce2" alias="异常2" length="500"  type="string" colspan="2" fixed="true" display="0"/>
	<item id="healthAssessmentExce3" alias="异常3" length="500"  type="string" colspan="2" fixed="true" display="0"/>
	<item id="healthAssessmentExce4" alias="异常4" length="500"  type="string" colspan="2" fixed="true" display="0"/>
	<item id="healthGuidance" alias="健康指导" length="1"  type="string" colspan="2" display="0">
		<dic render="LovCombo">
			<item key = "1" text="纳入慢性病患者健康管理" />
			<item key = "2" text="建议复查" />
			<item key = "3" text="建议转诊" />
		</dic>
	</item>
	<item id="riskFactorsForControl" alias="危险因素控制" length="100"  type="string" colspan="2" display="0">
		<dic render="LovCombo">
			<item key = "1" text="戒烟" />
			<item key = "2" text="健康饮酒" />
			<item key = "3" text="饮食" />
			<item key = "4" text="锻炼" />
			<item key = "5" text="减体重" />
			<item key = "6" text="建议接种疫苗" />
			<item key = "7" text="其他" />
		</dic>
	</item>
	<item id="targetWeight" alias="目标体重(kg)" length="3"  type="int" colspan="2" fixed="true" display="0"/>
	<item id="inoculateVaccine" alias="建议接种疫苗" length="100"  type="string" colspan="2" fixed="true" display="0"/>
	<item id="RFFCOther" alias="其他控制" length="100"  type="string" colspan="4" fixed="true" display="0"/>
	<!-- ===============健康评价==s========= -->
	<item  id="createUser" alias="录入员工" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.userId" display="0">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item  id="createUnit" alias="录入单位" length="20" update="false"
		type="string" fixed="true" defaultValue="%user.manageUnit.id" display="0">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item  id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"
		fixed="true" defaultValue="%server.date.today" colspan="2" display="0">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="0"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20" display="0"
		defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield" display="0" width="100"
		defaultValue="%server.date.today">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<relations>
		<relation type="parent" entryName="chis.application.mpi.schemas.MPI_DemographicInfo" />
		<relation type="children" entryName="chis.application.hr.schemas.EHR_HealthRecord">
			<join parent="phrId" child="phrId" />
		</relation>
	</relations>
</entry>