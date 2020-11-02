<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="chis.application.scm.schemas.SCM_INCREASEITEMS" tableName="SCM_INCREASEITEMS" alias="家医签约增值项目" sort="a.SCIID">
	<item id="SCIID" alias="主键" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
  	<item id="SCINID" alias="增值服务主键" length="18" type="long" />
  	<item id="FYXH" alias="对应医疗费用序号" length="32" type="string" />
  	<item id="ZFBL" alias="自付比例" type="double" length="6" precision="2" />
  	<item id="DISCOUNT" alias="家医折扣" type="double" length="6" precision="2" />
  	<item id="SCID" alias="签约记录编码" length="18" type="long"/>
  	<item id="SPIID" alias="对应服务项目编号" length="32" type="string"/>
  	<item id="SPID" alias="对应服务包编号" length="32" type="string"/>
  	<item id="TASKCODE" alias="对应项目编号" length="100" type="string"/>
  	<item id="TASKID" alias="对应计划编号" length="18" type="long"/>
	<item id="SERVICETIMES" alias="服务次数" type="int"/>
	<item id="TOTSERVICETIMES" alias="签约服务次数" type="int"/>
	<!--<item id="LOGOFF" alias="激活状态" length="2"  type="string">-->
        <!--<dic>-->
            <!--<item key="1" text="激活"/>-->
            <!--<item key="2" text="未缴费"/>-->
            <!--<item key="3" text="注销"/>-->
        <!--</dic>-->
    <!--</item>-->
</entry>