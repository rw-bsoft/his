<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SFXM_GB"  tableName="GY_SFXM" alias="收费项目">
		<item id="SFXM" alias="收费项目" length="18" not-null="1" type="long" display="0" generator="assigned" pkey="true" >
		<key>
			<rule name="increaseId" type="increase" length="18"
				startPos="1" />
		</key>
	</item>
	<item id="FYLB" alias="归并项目" type="int" length="4" not-null="1" layout="XMGB">
	<dic id="phis.dictionary.feesDic" autoLoad="true"></dic>
	</item>
	<item id="MZGB" alias="门诊归并" type="int" length="4" not-null="1" layout="XMGB">
	<dic id="phis.dictionary.feesDic" autoLoad="true"></dic>
	</item>
	<item id="ZYGB" alias="住院归并" type="int" length="4" not-null="1" layout="XMGB">
	<dic id="phis.dictionary.feesDic" autoLoad="true"></dic>
	</item>
	<item id="BASYGB" alias="病案首页归并" colspan="2" type="int" length="4" not-null="1" layout="XMGB">
		<dic id="phis.dictionary.BASYGBDIC" render="Tree" onlySelectLeaf="true" autoLoad="true"></dic>
	</item>
</entry>
