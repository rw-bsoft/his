<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="GY_SSDM" alias="手术代码">
	<item id="SSNM" alias="手术序号" type="long" not-null="1" generator="assigned" pkey="true" >
	    <key>
    		<rule name="increaseId" type="sequence" startPos="1" />
    	</key>
    </item>
	<item id="SSDM" alias="手术代码" type="string" length="10"/>
	<item id="SSMC" alias="手术名称"  type="string" length="80"/>
	<item id="SSDJ" display="手术单价"  type="long" length="18"/>
	<item id="ZJDM" display="拼音码"  type="string" length="6"/>
	<item id="FYXH" alias="费用序号"  type="long" length="18"/>
</entry>
