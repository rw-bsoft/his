<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SFXM_LX"  tableName="GY_SFXM" alias="收费项目-药品类型">
	<item id="SFXM" alias="收费项目" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	
	<item id="SFMC" alias="名称" type="string" length="20" not-null="1" layout="JBXX"/>
	<item id="MCSX" alias="缩写" type="string" length="10" layout="JBXX"/>
	<item id="MZSY" alias="门诊项目" length="1" layout="JBXX" xtype="checkbox" display="2"/>
	<item id="PLSX" alias="顺序号" type="string" length="10" layout="JBXX"/>
	<item id="ZYPL" alias="住院顺序号" type="string" length="10" layout="JBXX"/>
	<item id="MZPL" alias="门诊顺序号" type="string" length="10" layout="JBXX"/>
	<item id="PYDM" alias="拼音码" type="string" length="6" layout="JBXX" target="SFMC" codeType="py" queryable="true" />
	
	<item id="ZYSY" alias="住院项目" length="1"  layout="JBXX" xtype="checkbox" display="2"/>
	<item id="FYLB" alias="归并项目" type="int" length="4" defaultValue="0">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="MZGB" alias="门诊归并" type="int" length="4" defaultValue="0">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="ZYGB" alias="住院归并" type="int" length="4" defaultValue="0">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="BASYGB" alias="病案首页归并" type="int" length="4" width="150">
		<dic id="phis.dictionary.BASYGBDIC" autoLoad="true"/>
	</item>
	<item id="MZSY" alias="门诊使用" layout="JBXX" not-null="1" length="1" defaultValue="1">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
	<item id="ZYSY" alias="住院使用" layout="JBXX" not-null="1" length="1" defaultValue="1">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
	<item id="BXXM" alias="保险项目" type="int" length="4"  layout="QT" display="2"/>
	<item id="KMBM" alias="科目编码" type="string" length="10" layout="QT" display="2"/>
	<item id="ZBLB" alias="账簿类别" type="int" length="1" layout="QT"  defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="FYFL" alias="费用分类" type="int" length="1" display="0" />
</entry>
