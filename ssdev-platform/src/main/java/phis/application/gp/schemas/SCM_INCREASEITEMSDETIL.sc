<?xml version="1.0" encoding="UTF-8"?>

<entry entityName="SCM_INCREASEITEMSDETIL" tableName="SCM_INCREASEITEMS" alias="家医签约增值项目清单" sort="a.SCIID">
	<item id="SCIID" alias="主键" length="18" type="long" not-null="1" generator="assigned" display="0" pkey="true" isGenerate="false">
		<key>
			<rule name="increaseId"  type="increase" length="18" startPos="1"  />
		</key>
	</item>
	<item ref="c.FYMC" alias="项目名称" mode="remote" width="150"/>
	<item id="SERVICETIMES" alias="剩余次数" type="int" width="80" renderer="showColor"/>
	<item id="TOTSERVICETIMES" alias="签约服务次数" type="int" display="0"/>
	<item id="TASKID" alias="签约计划id" type="long" display="0"/>
  	<item id="TASKCODE" alias="对应项目编号" type="string" display="0"/>
    <item id="FAVOREENAME" alias="签约人" type="string" display="0" />
	<item id="CREATEUNIT" alias="签约单位" type="string" display="0" />	
	<item id="CREATEUSER" alias="签约医生" type="string" display="0" />
	<item id="PACKAGENAME" alias="对应服务包名称" type="string" display="0" />		
	<item ref="b.EMPIID" alias="个人主索引" length="32" type="string" />
	<item ref="b.MANAUNIT" alias="签约机构" length="32" type="string" width="180" >
		<dic id="phis.@manageUnit"/>
	</item>
	<item ref="b.MANADOC" alias="签约医生" length="32" type="string" width="60">
		<dic id="phis.dictionary.doctor" autoLoad="true"/>
	</item>
	<item ref="b.SCDATE" alias="签约时间" type="date" width="70"/>
	<item ref="b.LOGOFF" alias="激活状态" length="2"  type="string" display="0">
		<dic>
			<item key="1" text="激活"/>
			<item key="2" text="未缴费"/>
			<item key="3" text="注销"/>
		</dic>
	</item>
	<item ref="b.STARTDATE" alias="生效时间"  type="date" width="70"/>
	<item ref="b.ENDDATE" alias="截至时间" type="date" width="70"/>
  	<item id="SCINID" alias="增值服务主键" length="18" type="long" display="0"/>
  	<item id="FYXH" alias="对应医疗费用序号" display="0" length="32" type="string"/>
  	<item id="DISCOUNT" alias="家医折扣" type="double" length="6" precision="2" />
  	<item id="ZFBL" alias="自付比例" type="double" length="6" precision="2" />
  	<item id="SPIID" alias="对应服务项目编号" length="32" type="string" display="0"/>
  	<item id="SPID" alias="对应服务包编号" length="32" type="string" display="0"/>
  	<item id="SCID" alias="签约记录编码" length="18" type="long" display="0"/>
	<!--<item id="LOGOFF" alias="激活状态" length="2"  type="string">-->
        <!--<dic>-->
            <!--<item key="1" text="激活"/>-->
            <!--<item key="2" text="未缴费"/>-->
            <!--<item key="3" text="注销"/>-->
        <!--</dic>-->
    <!--</item>-->
	<relations>
		<relation type="parent" entryName="phis.application.gp.schemas.SCM_INCREASESERVER">
			<join parent="SCINID" child="SCINID" />
		</relation>
		<relation type="parent" entryName="phis.application.cic.schemas.GY_YLSF_CIC">
			<join parent="FYXH" child="FYXH"></join>
		</relation>
		<!--<relation type="parent" entryName="chis.application.scm.schemas.SCM_SignContractRecord">
			<join parent="SCID" child="SCID"></join>
		</relation>
		<relation type="parent" entryName="chis.application.scm.schemas.SCM_ServicePackage">
			<join parent="SPID" child="SPID"></join>
		</relation>-->
	</relations>
</entry>