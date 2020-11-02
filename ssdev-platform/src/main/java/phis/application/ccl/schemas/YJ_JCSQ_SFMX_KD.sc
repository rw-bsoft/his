<?xml version="1.0" encoding="UTF-8"?>
<entry entityName="YJ_JCSQ_SFMX_KD" alias="检查申请-收费明细-开单">
	<item id="LBID" alias="检查类别ID" type="long" length="12" not-null="1" display="0"/>
	<item id="SSLX" alias="所属类型" type="string" length="2" not-null="1" display="0"/>
	<item id="BWID" alias="检查部位ID" type="long" length="12" not-null="1" display="0"/>
	<item id="XMID" alias="检查项目ID" type="long" length="12" not-null="1" display="0"/>
	<item id="FYXH" alias="费用序号" type="long" length="12" not-null="1" display="0"/>
	<item id="JYBS" alias="" type="string" length="30" not-null="1" width="30" renderer="showColor"/>
	<item id="FYMC" alias="费用名称" type="string" length="30" not-null="1" width="150"/>
	<item id="QEZFBZ" alias="自费标志" type="string" length="3" width="150" defaultValue="0" >
    	<dic autoLoad="true">
			<item key="0" text="否"/>
			<item key="1" text="是"/>
		</dic>
	</item>
	<item id="FYSL" alias="数量" type="long" length="12" not-null="1" width="50"/>
	<item id="FYDW" alias="费用单位" type="string" length="12" not-null="1" width="50" display="0"/>
	<item id="FYDJ" alias="费用单价" type="double"  length="12" maxValue="99999999.99" minValue="0" not-null="1" />
	<item id="FYZJ" alias="费用总价" type="double"  length="12" maxValue="99999999.99" minValue="0" not-null="1" />
	<item id="LBMC" alias="类别名称" type="string" length="20" not-null="1" width="150" display="0"/>
	<item id="BWMC" alias="部位名称" type="string" length="20" not-null="1" width="150" display="0"/>
	<item id="XMMC" alias="项目名称" type="string" length="20" not-null="1" width="150"/>
</entry>