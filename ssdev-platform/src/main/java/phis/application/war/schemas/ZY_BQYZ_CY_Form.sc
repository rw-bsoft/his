<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="ZY_BQYZ" alias="病区医嘱">
	<item id="YZMC" alias="名称" type="string" length="100" width="200" virtual="true" display="0"/>
	<item id="SYPC" alias="频次"  type="string" length="6" width="60" virtual="true">
		<dic id="phis.dictionary.useRate" searchField="text" fields="key,text,MRCS,ZXSJ" autoLoad="true"/>
	</item>
	<item id="YPZS" alias="煎法" type="int" display="0">
		<dic id="phis.dictionary.suggested" autoLoad="true"/>
	</item>
	<item id="YPYF" alias="服法" type="long" length="18" listWidth="90" width="90">
        <dic id="phis.dictionary.drugWay" searchField="PYDM" fields="key,text,PYDM,FYXH" autoLoad="true"/>
    </item>
	<item id="CFTS" alias="贴数" type="int" virtual="true"/>
	
	
</entry>
