<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="MPI_Card" alias="卡管理">
	<item id="cardId" alias="编号" type="string" length="16" pkey="true" hidden="true" >
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16" startPos="1"/>
		</key>
	</item>
	<item id="empiId" alias="EMPI" type="string" length="32"
		hidden="true" />
	<item id="cardTypeCode" alias="卡类型" type="string" length="2" defaultValue="04" not-null="1">
		<dic id="phis.dictionary.card"/>
	</item>
	<item id="cardNo" alias="卡号" type="string" length="20"  not-null="1" width="160"/>
	<item id="createUnit" alias="创建机构" type="string" length="16"  hidden="true"/>
	<item id="createUser" alias="创建人" type="string" length="20"  hidden="true"/>
	<item id="status" alias="状态" type="string" length="1" not-null="1" defaultValue="0" display="0">
		<dic>
			<item key="0" text="正常" mCode="0" pyCode="zc"/>  
			<item key="1" text="挂失" mCode="1" pyCode="gs"/>  
			<item key="2" text="注销" mCode="2" pyCode="zx"/>  
			<item key="3" text="失效" mCode="3" pyCode="sx"/> 
		</dic>
	</item>
	<item id="createTime" alias="创建时间" type="date"  hidden="true" />
	<item id="validTime" alias="有效期" type="date"  hidden="true"/>
	<item id="lastModifyTime" alias="最后修改时间" type="date" hidden="true"/>
	<item id="lastModifyUnit" alias="最后修改机构" type="string" length="16"  hidden="true"/>
	<item id="lastModifyUser" alias="最后修改人" type="string" length="20"  hidden="true"/>
</entry>
