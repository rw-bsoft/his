<?xml version="1.0" encoding="UTF-8"?>
<entry alias="" entityName="NJJB_KXX" sort="">
<item alias="社会保障卡号" id="SHBZKH" length="20" pkey="true" fixed="true" not-null="true" type="string"/>
<item alias="识别序号" id="SBXH" length="20" fixed="true" type="string"/>
<item alias="姓名" id="XM" length="20" fixed="true" type="string"/>
<item alias="性别" id="XB" length="3" fixed="true" type="string">
	<dic id="phis.dictionary.gender" fixed="true" autoLoad="true" />
</item>
<item alias="医疗人员类别" id="YLRYLB" length="3" fixed="true" type="string" hidden="true" >
	<dic render="LovCombo">
		<item key="11" text="在职"/>
		<item key="21" text="退休"/>
		<item key="22" text="退职"/>
		<item key="23" text="70岁以上退休"/>
		<item key="24" text="退休待审核"/>
		<item key="31" text="离休"/>
		<item key="41" text="建国前老工人"/>
		<item key="42" text="二乙伤残军人"/>
		<item key="51" text="普通居民"/>
		<item key="52" text="儿童学生"/>
		<item key="53" text="大学生"/>
		<item key="62" text="建筑业农民工"/>
	</dic>
</item>
<item id="NJJBYLLB" alias="医疗类别" type="string" fixed="true" length="18">
		<dic id="phis.dictionary.ybyllb" searchField="PYDM" autoLoad="true"/>
</item>
<item id="YBZY" alias="医保转院" type="string" length="18">
		<dic id="phis.dictionary.ybzy" searchField="PYBM" autoLoad="true"/>
</item>
<item id="YBMCS" alias="医保病种选择" type="string" updates="true">
	<dic id="phis.dictionary.ybJbbm"  searchField="PYDM" autoLoad="true" />
</item>
<item id="YBMC" alias="医保病种" type="string" updates="true" not-null="1">
	<dic id="phis.dictionary.ybJbbm" render="LovCombo" searchField="PYDM" autoLoad="true" />
</item>
</entry>
