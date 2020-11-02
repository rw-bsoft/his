<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_AccessoryExamination" alias="辅助检查" >
	<item id="recordId" alias="记录序号" length="16" width="150" type="string" pkey="true" generator="assigned" not-null="1" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="healthCheck" alias="年检编号" length="16" type="string" display="0" />
	
	<!--<item id="lip" alias="口唇"  type="string" length="100"  >
		  <dic>
			<item key="1" text="红润" />
			<item key="2" text="苍白" />
			<item key="3" text="发绀" />
			<item key="4" text="皲裂" />
			<item key="5" text="疱疹" />
		</dic>
	</item>
	
	<item id="denture" alias="齿列"  type="string" length="100"  >
		<dic render="LovCombo">
			<item key="1" text="正常" />
			<item key="2" text="缺齿" />
			<item key="3" text="龋齿" />
			<item key="4" text="义齿" />
		</dic>
	</item>
	<item id="pharyngeal" alias="咽部"  type="string" length="100"  >
		<dic>
			<item key="1" text="无充血" />
			<item key="2" text="充血" />
			<item key="3" text="淋巴滤泡增生" />
		</dic>
	</item>
	
	<item id="hearing" alias="听力"  type="string" length="100"  >
		<dic>
			<item key="1" text="听见" />
			<item key="2" text="听不清或无法听见" />
		</dic>
	</item>
	-->
	<item id="leftUp" alias="左上"  type="int" length="10"  />
	<item id="leftDown" alias="左下"  type="int" length="10"  />
	<item id="rightUp" alias="右上"  type="int" length="10"  />
	<item id="rightDown" alias="右下"  type="int" length="10"  />
	<item id="leftUp2" alias="（龋齿）左上"  type="int" length="10"  />
	<item id="leftDown2" alias="（龋齿）左下"  type="int" length="10"  />
	<item id="rightUp2" alias="（龋齿）右上"  type="int" length="10"  />
	<item id="rightDown2" alias="（龋齿）右下"  type="int" length="10"  />
	<item id="leftUp3" alias="（义齿(假牙)）左上"  type="int" length="10"  />
	<item id="leftDown3" alias="（义齿(假牙)）左下"  type="int" length="10"  />
	<item id="rightUp3" alias="（义齿(假牙)）右上"  type="int" length="10"  />
	<item id="rightDown3" alias="（义齿(假牙)）右下"  type="int" length="10"  />
	<!--
	<item id="hypodontia" alias="缺齿位置"  type="string" length="100" fixed="true" />
	<item id="decay" alias="龋齿位置"  type="string" length="100" fixed="true" />
	<item id="falsethooh" alias="义齿位置"  type="string" length="100" fixed="true" />
	-->
	
	
	
	
	<item id="leftEye" alias="左眼视力"  type="double" length="10" minValue="0" precision="1" maxValue="5.3"/>
	<item id="rightEye" alias="右眼视力"  type="double" length="10"  minValue="0" precision="1" maxValue="5.3"/>
	<item id="recLeftEye" alias="矫正左眼视力"  type="double" length="10"   precision="1"/>
	<item id="recRightEye" alias="矫正右眼视力"  type="double" length="10"   precision="1"/>
	

	
	<item id="hgb" alias="血红蛋白(g/L)"  type="double" length="10"   precision="2"/>
	
	<item id="xjc" alias="血常规" type="string" length="1"  precision="2">
		<dic>
			<item key="1" text="拒测" />
		</dic>
	</item>
	
	<item id="wbc" alias="白细胞(10^9/L)"  type="double" length="10"  precision="2"/>
	<item id="platelet" alias="血小板(10^9/L)"  type="double" length="10"  precision="2"/>
	<item id="bloodOther" alias="血常规其他"  type="string" length="100"  />
	
	<item id="proteinuria" alias="尿蛋白"  type="string" length="10"  />
	<item id="glu" alias="尿糖"  type="string" length="10"  />
	<item id="dka" alias="尿酮体"  type="string" length="10"  />
	<item id="oc" alias="尿潜血"  type="string" length="10"  />
	<item id="urineOther" alias="尿常规其他"  type="string" length="100"  />
	
	<item id="fbs" alias="空腹血糖(mmol/L)"  type="double" length="10"   precision="2"/>
	<item id="fbs2" alias="空腹血糖(mmol/L)"  type="double" length="10"  precision="2" display="0"/>
	
	<!-- <item id="ecg" alias="心电图" type="string" length="1">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item> 
	<item id="fob" alias="大便潜血" type="string" length="1">
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	<item id="hbsag" alias="乙型肝炎表面抗原" length="1" type="string"  >
		<dic>
			<item key="1" text="阴性" />
			<item key="2" text="阳性" />
		</dic>
	</item>
	<item id="b" alias="B超" type="string" length="1">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="ps" alias="宫颈涂片" type="string" length="1">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	-->
	<item id="ecgText" alias="异常描述"  type="string" length="200" fixed="true" />
	
	<item id="malb" alias="尿微量白蛋白"  type="double" length="10"  precision="2"/>
	
	
	
	<item id="hba1c" alias="糖化血红蛋白(%)"  type="double" length="10"  precision="2"  />
	
	
	
	<item id="alt" alias="血清谷丙转氨酶(U/L)"  type="double" length="10"   precision="2"/>
	<item id="ast" alias="血清谷草转氨酶(U/L)"  type="double" length="10"  precision="2" />
	<item id="alb" alias="白蛋白(g/L)"  type="double" length="10"   precision="2"/>
	<item id="tbil" alias="总胆红素(μmol/L)"  type="double" length="10"   precision="2"/>
	<item id="dbil" alias="结合胆红素(μmol/L)"  type="double" length="10"  precision="2" />
	
	<item id="cr" alias="血清肌酐(μmol/L)"  type="double" length="10"   precision="2"/>
	<item id="bun" alias="血尿素氮(mmol/L)"  type="double" length="10"   precision="2"/>
	<item id="kalemia" alias="血钾浓度(mmol/L)"  type="double" length="10"   precision="2"/>
	<item id="natremia" alias="血钠浓度(mmol/L)"  type="double" length="10"   precision="2"/>
	
	<item id="tc" alias="总胆固醇(mmol/L)"  type="double" length="10"  precision="2" />
	<item id="tg" alias="甘油三酯(mmol/L)"  type="double" length="10"  precision="2" />
	<item id="ldl" alias="血清LDL-C(mmol/L)"  type="double" length="10"  precision="2" />
	<item id="hdl" alias="血清HDL-C(mmol/L)"  type="double" length="10"  precision="2" />
	
	<item id="x" alias="胸部X线片" type="string" length="1">
		<dic>
			<item key="1" text="正常" />
			<item key="2" text="异常" />
		</dic>
	</item>
	<item id="xText" alias="异常描述"  type="string" length="200" fixed="true" />
	
	
	<item id="bText" alias="异常描述"  type="string" length="200" fixed="true" />
	
	
	<item id="psText" alias="异常描述"  type="string" length="200" fixed="true"/>
	
	<item id="fuOther" alias="其他辅助检查" type="string" length="1000" colspan="4"/>
	<item queryable="true" id="createUser" alias="录入员工" length="20" update="false" display="1"
		type="string" fixed="true" defaultValue="%user.userId">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item queryable="true" id="createUnit" alias="录入单位" length="20" update="false"  display="1"
		type="string" fixed="true" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree"
			onlySelectLeaf="true" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item queryable="true" id="createDate" alias="录入日期" type="datetime"  xtype="datefield" update="false"  display="1"
		fixed="true" defaultValue="%server.date.today" colspan="2">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	
</entry>
