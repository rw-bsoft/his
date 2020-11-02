<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SFXM" alias="收费项目">
	<item id="SFXM" alias="收费项目" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="44" />
		</key>
	</item>
	<item id="PLSX" alias="顺序号" type="string" length="10"/>
	<item id="ZYPL" alias="住院顺序号" type="string" length="10"/>
	<item id="MZPL" alias="门诊顺序号" type="string" length="10"/>
	<item id="SFMC" alias="名称" type="string" length="20" not-null="1"/>
	<item id="MCSX" alias="缩写" type="string" length="10"/>
	<item id="PYDM" alias="拼音码" type="string" length="6" target="SFMC" codeType="py" queryable="true"/>
	<item id="FYLB" alias="项目归并" type="int" length="4" not-null="1">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="MZGB" alias="门诊归并" type="int" length="4" not-null="1">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="ZYGB" alias="住院归并" type="int" length="4" not-null="1">
		<dic id="phis.dictionary.feesDic" autoLoad="true"/>
	</item>
	<item id="MZSY" alias="门诊使用" length="1" not-null="1" >
		<dic id="phis.dictionary.confirm" ></dic>
	</item>
	<item id="ZYSY" alias="住院使用" length="1"  not-null="1" >
		<dic id="phis.dictionary.confirm"></dic>
	</item>
		
	<item id="BXXM" alias="保险项目" type="int" length="4" display="0"/>
	<item id="KMBM" alias="科目编码" type="string" length="10" display="0"/>
	<item id="LBDM" alias="类别代码" length="18" display="0"/>
	<item id="ZBLB" alias="账簿类别" type="int" length="1" display="0"  defaultValue="0">
		<dic id="phis.dictionary.confirm"/>
	</item>
	<item id="FYFL" alias="费用分类" type="int" length="1" not-null="1" >
		<dic>
			<item key="1" text="医疗项目" />
			<item key="2" text="药品项目" />
			<item key="3" text="其他项目" />
		</dic>
	</item>
	<item id="BASYGB" alias="病案首页归并" type="int" length="4" not-null="1">
		<dic id="phis.dictionary.BASYGBDIC" autoLoad="true"></dic>
	</item>
</entry>
