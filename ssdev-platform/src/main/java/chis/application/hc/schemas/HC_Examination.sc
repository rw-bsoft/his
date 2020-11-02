<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.HC_Examination" alias="查体">
	<item queryable="true" id="examination" alias="记录编号" length="16" type="string" pkey="true" generator="assigned" not-null="1" display="1">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item queryable="true" id="healthCheck" alias="年检编号" length="16" type="string" display="1"/>
	<item queryable="true" id="fundus" alias="眼底" length="1" type="string">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="fundusDesc" alias="眼底描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="skin" alias="皮肤" length="1" type="string">
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
	<item queryable="true" id="skinDesc" alias="皮肤其他" length="100" type="string" fixed="true"/>
	<item queryable="true" id="sclera" alias="巩膜" length="1" type="string">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="黄染"/>
			<item key="3" text="充血"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item queryable="true" id="scleraDesc" alias="巩膜其他" length="100" type="string"/>
	<item queryable="true" id="lymphnodes" alias="淋巴结" length="1" type="string">
		<dic>
			<item key="1" text="未触及"/>
			<item key="2" text="锁骨上"/>
			<item key="3" text="腋窝"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item queryable="true" id="lymphnodesDesc" alias="淋巴结其他" length="100" type="string" fixed="true"/>
	<item queryable="true" id="barrelChest" alias="桶状胸" length="1" type="string">
		<dic>
			<item key="1" text="否"/>
			<item key="2" text="是"/>
		</dic>
	</item>
	<item queryable="true" id="breathSound" alias="呼吸音" length="1" type="string">
		<dic>
			<item key="1" text="正常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="breathSoundDesc" alias="异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="rales" alias="罗音" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="干罗音"/>
			<item key="3" text="湿罗音"/>
			<item key="4" text="其他"/>
		</dic>
	</item>
	<item queryable="true" id="ralesDesc" alias="其他罗音描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="heartRate" alias="心率(次/分)" type="int"/>
	<item queryable="true" id="rhythm" alias="心律" length="1" type="string">
		<dic>
			<item key="1" text="齐"/>
			<item key="2" text="不齐"/>
			<item key="3" text="绝对不齐"/>
		</dic>
	</item>
	<item queryable="true" id="heartMurmur" alias="心脏杂音" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="heartMurmurDesc" alias="杂音描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="abdominAltend" alias="腹部压痛" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="abdominAltendDesc" alias="压痛描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="adbominAlmass" alias="腹部包块" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="adbominAlmassDesc" alias="包块描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="liverBig" alias="肝大" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="liverBigDesc" alias="肝大描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="splenomegaly" alias="脾大" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="splenomegalyDesc" alias="脾大描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="dullness" alias="腹部移动性浊音" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="有"/>
		</dic>
	</item>
	<item queryable="true" id="dullnessDesc" alias="描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="edema" alias="下肢水肿" length="1" type="string">
		<dic>
			<item key="1" text="无"/>
			<item key="2" text="单侧"/>
			<item key="3" text="双侧不对称"/>
			<item key="4" text="双侧对称"/>
		</dic>
	</item>
	<item queryable="true" id="footPulse" alias="足背动脉搏动" length="10" type="string" colspan="2">
		<dic>
			<item key="1" text="未触及"/>
			<item key="2" text="触及双侧对称"/>
			<item key="3" text="触及左侧弱或消失"/>
			<item key="4" text="触及右侧弱或消失"/>
		</dic>
	</item>
	<item queryable="true" id="dre" alias="肛门指诊" length="1" type="string">
		<dic render="LovCombo">
			<item key="1" text="未及异常"/>
			<item key="2" text="触痛"/>
			<item key="3" text="包块"/>
			<item key="4" text="前列腺异常"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item queryable="true" id="dreDesc" alias="其他描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="breast" alias="乳腺" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="乳房切除"/>
			<item key="3" text="异常泌乳"/>
			<item key="4" text="乳腺包块"/>
			<item key="5" text="其他"/>
		</dic>
	</item>
	<item queryable="true" id="breastDesc" alias="乳腺其他描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="vulva" alias="外阴" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="vulvaDesc" alias="外阴异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="vaginal" alias="阴道" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="vaginalDesc" alias="阴道异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="cervix" alias="宫颈" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="cervixDesc" alias="宫颈异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="palace" alias="宫体" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="palaceDesc" alias="宫体异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="attachment" alias="附件" length="1" type="string">
		<dic>
			<item key="1" text="未见异常"/>
			<item key="2" text="异常"/>
		</dic>
	</item>
	<item queryable="true" id="attachmentDesc" alias="附件异常描述" length="100" type="string" fixed="true"/>
	<item queryable="true" id="tjother" alias="其他查体" length="100" type="string" colspan="4"/>
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
