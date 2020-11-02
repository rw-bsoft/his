<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="chis.application.mobile.schemas.DLL_UpLoadLog"
	alias="移动离线数据上传日志表">
	<item id="recordId" alias="记录序号" type="string" length="16"
		not-null="1" generator="assigned" pkey="true" hidden="true">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="empiId" alias="EMPI" type="string" length="32" display="0" />
	<item id="phrId" alias="phrId" type="string" length="30" display="0" />
	<item id="businessType" alias="业务类型" type="string" length="2"
		not-null="1" display="2">
		<dic render="LovCombo">
			<item key="1" text="高血压随访" />
			<item key="2" text="糖尿病随访" />
			<item key="3" text="老年人随访" />
			<item key="4" text="产后访视" />
			<item key="5" text="产后42天" />
			<item key="6" text="新生儿访视基本信息" />
			<item key="7" text="家医服务记录" />
			<item key="8" text="问卷调查" />
			<item key="9" text="高血压服药" />
			<item key="10" text="糖尿病服药" />
			<item key="11" text="新生儿访视记录" />
			<item key="12" text="服务有效性" />
		</dic>
	</item>
	<item id="businessData" alias="业务数据" type="string" length="4000"
		display="0" />
	<item id="dataStatus" alias="数据状态" type="string" length="10"
		not-null="1" display="2">
		<dic render="LovCombo">
			<item key="2" text="超过服务有效性时间" />
			<item key="3" text="档案注销" />
			<item key="4" text="数据重复(不覆盖原来数据)" />
		</dic>
	</item>
	<item id="userId" alias="用户Id" type="string" length="50" display="0" />
	<item id="manaUnitId" alias="管辖机构" type="string" length="32"
		display="0" />
	<item id="lastModifyDate" alias="最后修改时间" type="date" display="2"
		defaultValue="%server.date.today" enableKeyEvents="true" not-null="1"
		maxValue="%server.date.today" />
</entry>
  	