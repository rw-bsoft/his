<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SCM_INCREASESERVER" tableName="SCM_INCREASESERVER" alias="家医签约增值服务" sort="a.SCINID">
	<item id="SCINID" alias="主键" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
  	<item id="EMPIID" alias="个人主索引" length="32" type="string" display="0"/>
  	<item id="MANAUNIT" alias="签约机构" length="32" type="string" >
		<dic id="phis.@manageUnit"/>
	</item>
  	<item id="MANADOC" alias="签约医生" length="32" type="string">
  		<dic id="phis.dictionary.doctor" autoLoad="true"/>
  	</item>
	<item id="SCDATE" alias="签约时间" type="datetime"/>
	<item id="LOGOFF" alias="激活状态" length="2"  type="string">
        <dic>
            <item key="1" text="激活"/>
            <item key="2" text="未缴费"/>
            <item key="3" text="注销"/>
        </dic>
    </item>
	<item id="STARTDATE" alias="生效时间"  type="datetime"/>
	<item id="ENDDATE" alias="截至时间" type="datetime"/>
	<item id="MODIFYUSER" alias="最后操作人" type="string" length="32"/>
	<item id="MODIFYUNIT" alias="最后操作机构" type="string" length="32"/>
	<item id="GPKS" alias="家医科室"  type="string" length="32"/>
	<item id="FPHM" alias="医技收费对应发票"  type="string" length="32"/>
	<item id="SBXH" alias="医技02主键"  type="string" length="32"/>
	<item id="YJXH" alias="医技01主键"  type="string" length="32"/>
	<item id="SCID" alias="签约主键"  type="long" length="18"/>
</entry>