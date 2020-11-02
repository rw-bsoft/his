<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MPI_Phone" alias="EMPI个人基本信息">
	<item id="phoneId" alias="编号" type="string" length="16" hidden="true" pkey="true"  >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId"    		alias="EMPI"         type="string" length="32"  hidden="true"/>
    <item id="phoneTypeCode"  alias="电话类型"  defaultValue="04"   type="string" length="2" not-null="1">
    	<dic id="phis.dictionary.phone"/>
    </item>
    <item id="phoneNo"      	alias="电话号码"       type="string" length="16" not-null="1" width="100"/>
</entry>	