<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.hc.schemas.hc_m_progestationcheck_list" tableName="chis.application.hc.schemas.hc_m_progestationcheck" alias="丈夫孕前检查信息" sort="a.recordId">
	<item alias="主键" id="recordId" length="16" not-null="true" type="string" pkey="true" generator="assigned" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>	
    <item id="phrId" alias="phrid" length="20" type="string" display="0"/>
    <item id="empiId" alias="empiid" length="32" type="string" display="0"/>
    <item alias="身高" id="SG" type="int"  hidden="true"/>
    <item alias="体重" id="TZ" type="int" hidden="true"/>
    <item alias="体重指数" id="TZZS" type="double" hidden="true"/>
    <item alias="心率" id="XL" type="int" hidden="true"/>
    <item alias="收缩压" id="SSY" type="int" hidden="true"/>
    <item alias="舒张压" id="SZY" type="int" hidden="true"/>
    <item alias="精神状态" id="JSZT" length="1" type="string"  hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="精神状态异常情况" id="JSYCQK" length="30" type="string" hidden="true"/>
    <item alias="五官" id="WG" length="1" type="string"  hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="五官异常情况" id="WGYCQK" length="30" type="string"  hidden="true"/>
    <item alias="智力" id="ZL" length="1" type="string"  hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="智力异常情况" id="ZLYCQK" length="30" type="string"  hidden="true"/>
    <item alias="特殊体态" id="TSTT" length="1" type="string"  hidden="true">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="特殊体态情况" id="TSTTQK" length="30" type="string"  hidden="true"/>
    <item alias="特殊面容" id="TSMR" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="特殊面容情况" id="TSMRQK" length="30" type="string" hidden="true"/>
    <item alias="皮肤毛发" id="PFMF" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="皮肤毛发异常情况" id="PFMFYCQK" length="30" type="string" hidden="true"/>
    <item alias="甲状腺" id="JZX" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="甲状腺异常情况" id="JZXYCQK" length="30" type="string" hidden="true"/>
    <item alias="肺部" id="FB" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="肺部异常情况" id="FBYCQK" length="30" type="string" hidden="true"/>
    <item alias="心脏节律整齐" id="XZJLZQ" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="是" />
			<item key="1" text="否" />
		</dic>
	</item>
    <item alias="杂音" id="ZY" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="肝脾" id="GP" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="未触及" />
			<item key="1" text="触及" />
		</dic>
	</item>
    <item alias="肝脾情况" id="GPQK" length="30" type="string" hidden="true"/>
    <item alias="四肢脊柱" id="SZJZ" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="四肢脊柱异常情况" id="SZJZYCQK" length="30" type="string" hidden="true"/>
    <item alias="其他问题" id="QTWT" length="50" type="string" hidden="true"/>
    <item alias="转诊建议" id="ZZJY" length="100" type="string" hidden="true"/>
    <item id="YBJCRQ" alias="一般检查日期" type="date"  hidden="true"/>
    <item id="YBJCYS" alias="一般检查医师"  length="100" type="string" hidden="true"/>
    <item alias="阴毛" id="YM" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="阴毛异常情况" id="YMYCQK" length="30" type="string" hidden="true"/>
    <item alias="喉结" id="HJ" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="有" />
			<item key="1" text="无" />
		</dic>
	</item>
    <item alias="喉结异常情况" id="HJYCQK" length="30" type="string" hidden="true"/>
    <item alias="阴茎" id="YJ" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="阴茎异常情况" id="YJYCQK" length="30" type="string" hidden="true"/>
    <item alias="包皮" id="BP" length="1" type="string"  hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="过长" />
			<item key="2" text="包茎" />
		</dic>
	</item>
    <item alias="睾丸" id="GW" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="扪及" />
			<item key="1" text="左侧未扪及" />
			<item key="1" text="右侧未扪及" />
		</dic>
	</item>
    <item alias="睾丸体积" id="GWTJ" type="double" hidden="true"/>
    <item alias="附睾" id="FG" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="正常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="附睾异常情况" id="FGYCQK" length="30" type="string" hidden="true"/>
    <item alias="输精管" id="SJG" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="输精管异常情况" id="SJGYCQK" length="30" type="string" hidden="true"/>
    <item alias="精索静脉曲张" id="JSJMQZ" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="无" />
			<item key="1" text="有" />
		</dic>
	</item>
    <item alias="精索静脉曲张部位" id="JSJMQZBW" length="30" type="string" hidden="true">
        <dic>
			<item key="1" text="左侧" />
			<item key="2" text="右侧" />
			<item key="3" text="双侧" />
		</dic>
	</item>
    <item alias="精索静脉曲张程度" id="JSJMQZCD" length="30" type="string" hidden="true">
        <dic>
		    <item key="1" text="轻微" />
			<item key="2" text="中度" />
			<item key="3" text="严重" />
		</dic>
	</item>
	<item id="NKJCZZJY" alias="男科检查转诊建议"  length="200" colspan="3" type="string" hidden="true"/>
    <item id="NKJCRQ" alias="男科检查日期" type="date" colspan="2" hidden="true"/>
    <item id="NKJCYS" alias="男科检查医师"  length="100" type="string" hidden="true"/>
    <item alias="血型" id="XX" length="1" type="string" hidden="true">
	    <dic>
			<item key="1" text="A型" />
			<item key="2" text="B型" />
			<item key="3" text="AB型" />
			<item key="4" text="O型" />
		</dic>
	</item>
    <item alias="rh血型" id="RHXX" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="阳性" />
			<item key="1" text="阴性" />
		</dic>
	</item>
    <item alias="尿液" id="NY" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="未见异常" />
			<item key="1" text="异常" />
		</dic>
	</item>
    <item alias="尿液异常情况" id="NQKCQK" length="30" type="string" hidden="true"/>
    <item alias="梅毒螺旋体" id="MDLXT" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
    <item alias="乙肝血清学" id="YGXQX" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="阴性" />
			<item key="1" text="阳性" />
			<item key="9" text="可疑" />
		</dic>
	</item>
		<item alias="HBs—Ag" id="HBSAG" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBs—Ab" id="HBSAB" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBe—Ag" id="HBEAG" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBe—Ab" id="HBEAB" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>
	<item alias="HBc—Ab" id="HBCAB" length="1" type="string" hidden="true">
        <dic>
			<item key="0" text="-" />
			<item key="1" text="+" />
		</dic>
	</item>	
    <item alias="谷丙转氨酶" id="GBZAM" type="double" hidden="true"/>
    <item alias="肌酐" id="GG" type="double" hidden="true"/>
    <item alias="其他检验" id="QTJY" length="50" type="string" hidden="true"/>
    <item id="JYJCRQ" alias="检验检查日期" type="date" colspan="2" hidden="true"/>
    <item id="JYJCYS" alias="检验检查医师"  length="100" type="string" hidden="true"/>
    <item id="QTJYZYJG" alias="其他检查主要结果" length="500" type="string" colspan="3" xtype="textarea" hidden="true"/>
    <item id="QTJCRQ" alias="其他检查日期" type="date"  hidden="true"/>
    <item id="QTJCYS" alias="其他检查医师"  length="100" type="string" hidden="true"/> 
	<item id="inputUnit" alias="录入机构" type="string" length="20" update="false" 
		fixed="true" defaultValue="%user.manageUnit.id" colspan="2" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="inputUser" alias="录入人员" type="string" length="20" fixed="true" update="false" defaultValue="%user.userId" queryable="true">
		<dic id="chis.dictionary.Personnel"/>
		<set type="exp">['$','%user.userId']</set>
	</item>
	<item id="inputDate" alias="录入日期" type="datetime"  xtype="datefield" fixed="true" update="false" 
		defaultValue="%server.date.today" queryable="true" >
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyDate" alias="最后修改日期" type="datetime"  xtype="datefield"
		defaultValue="%server.date.today" display="1" hidden="true">
		<set type="exp">['$','%server.date.datetime']</set>
	</item>
	<item id="lastModifyUnit" alias="最后修改单位" type="string" length="20" display="1"
		width="180" defaultValue="%user.manageUnit.id" hidden="true">
		<dic id="chis.@manageUnit" includeParentMinLen="6"  render="Tree" onlySelectLeaf="true" 
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.manageUnit.id']</set>
	</item>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"
		defaultValue="%user.userId" display="1" hidden="true">
		<dic id="chis.dictionary.user" render="Tree" onlySelectLeaf="true"
			parentKey="%user.manageUnit.id" />
		<set type="exp">['$','%user.userId']</set>
	</item> 
</entry>
