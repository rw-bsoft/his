<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JLXX_DETAILLIST" tableName="WL_JLXX" alias="计量信息(WL_JLXX)">
	<item id="JLXH" alias="计量序号" length="10" not-null="1" type="long" generator="assigned" pkey="true" display="0">
		<key>
			<rule name="increaseId" defaultFill="0" type="increase" length="16"
				startPos="1" />
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" length="20" display="0"/>
	<item id="KFXH" alias="库房序号" length="8" type="int" not-null="1" display="0"/>
	<item id="JLBH" alias="计量编号" length="12" fixed="true"/>
	<item id="WZMC" alias="物资名称" length="60" fixed="true" width="150"/>
	<item id="ZBXH" alias="帐薄序号" length="10" type="long" display="0"/>
	<item id="WZXH" alias="物资序号" length="10" type="long" display="0"/>
	<item id="CJXH" alias="生产厂家" length="12" type="long" display="0"/>
	<item id="KSDM" alias="科室代码" length="18" type="long" display="0"/>
	<item id="KSMC" alias="在用科室" length="60" fixed="true" width="150" />
	<item id="DWXH" alias="供货单位" length="12" type="long" display="0"/>
	<item id="CCBH" alias="出厂编号" length="50" display="0"/>
	<item id="WZDW" alias="计量单位" length="10" display="0"/>
	<item id="WZDJ" alias="物资单价" length="18" fixed="true" precision="4" type="double" display="0"/>
	<item id="GRRQ" alias="购入日期" type="date" display="0"/>
	<item id="GRGH" alias="购入人" length="10" display="0"/>
	<item id="JLQJFL" alias="计量器具分类" length="2" fixed="true" type="int" width="100">
		<dic id="phis.dictionary.jlqjfl">
		</dic>
	</item>
	<item id="JLLB" alias="计量类别" length="1" fixed="true">
		<dic id="phis.dictionary.jlfl">
		</dic>
	</item>
	<item id="QJBZ" alias="强检标志" length="1" type="int" fixed="true">
		<dic>
            <item key="0" text="不强检"/>
            <item key="1" text="强检"/>
        </dic>
	</item>
	<item id="CLFW" alias="测量范围" length="30" display="0"/>
	<item id="ZQDJ" alias="准确度等级" length="10" display="0"/>
	<item id="FDZ" alias="分度值" length="10" display="0"/>
	<item id="DDMC" alias="地点名称" length="50" display="0"/>
	<item id="CCRQ" alias="出厂日期" type="date" display="0"/>
	<item id="SJQD" alias="时间区段" length="14" display="0"/>
	<item id="BGGH" alias="保管人" length="10" display="0"/>
	<item id="JDZQ" alias="检定周期" length="2" type="int" display="0"/>
	<item id="XCJD" alias="下次检定" type="date" display="0"/>
	<item id="DQJDXH" alias="当前检定序号" length="10" type="int" display="0"/>
	<item id="JDRQ" alias="检定日期" type="date" display="0"/>
	<item id="HGZH" alias="合格证号" length="20" />
	<item id="JDJG" alias="检定结果" length="1"  defaultValue="1" type="int" >
		<dic>
			<item key="0" text="不合格"/>
			<item key="1" text="合格"/>
		</dic>
	</item>
	<item id="JDJL" alias="检定结论" length="1" defaultValue="1" type="int" >
		<dic>
			<item key="0" text="不正常"/>
			<item key="1" text="正常"/>
		</dic>
	</item>
	<item id="ZFBZ" alias="作废标志" length="1" type="int" display="0"/>
</entry>
