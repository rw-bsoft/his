<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_HSLB_ZBLB" alias="帐薄类别(WL_ZBLB)">

  	<item id="SJHS" alias="上级核算" type="int" length="8"  fixed="true" not-null="1"  layout="HSLB">
  		<dic id="phis.dictionary.WL_HSLB_SJHS" autoLoad="true"/>
	</item>
  	<item id="HSMC" alias="核算名称" length="30" maxValue="30" not-null="1" layout="HSLB"/>
    <item id="PLSX" alias="排列顺序" length="4" layout="HSLB"/>
   
	<item id="b.ZBMC" alias="帐薄名称" length="30" fixed="true" layout="ZBLB"/>
	<item id="b.JGID" alias="公用标志" type="string"  length="20" defaultValue="0" fixed="true" layout="ZBLB">
		<dic id="phis.dictionary.confirm" autoLoad="true"/>
	</item>
	<item id="b.ZBZT" alias="帐薄状态"  type="int" length="1" defaultValue="0" renderer="onRenderer" fixed="true" >
		<dic id="phis.dictionary.qyzt" autoLoad="true"/>
	</item>
	
	
</entry>
