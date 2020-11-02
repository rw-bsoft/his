<?xml version="1.0" encoding="UTF-8"?>

<entry id="WL_JLXX_INFO" tableName="WL_JLXX" alias="计量信息(WL_JLXX)">
	<item id="JLXH" alias="计量序号" length="10" not-null="1" type="long" display="0" generator="assigned" pkey="true">
		<key>
			<value name="increaseId" defaultFill="0" type="increase" startPos="24"/>
		</key>
	</item>
	<item id="JGID" alias="机构ID" type="string" display="0" length="20"/>
	<item id="KFXH" alias="库房序号" type="int" display="0" length="8" not-null="1"/>
	<item id="JLBH" alias="计量编号" type="string" width="120" length="12"/>
	<item id="ZBXH" alias="帐薄序号" type="long" length="10" display="0"/>
	<item id="WZXH" alias="物资序号" type="long" length="10" display="0"/>
	<item id="WZMC" alias="物资名称" type="string" width="120" length="12" virtual="true"/>
	<item id="CJXH" alias="厂家序号" type="long" length="12" display="0"/>
	<item id="CJMC" alias="生产厂家" type="string" length="12" width="120" virtual="true"/>
	<item id="KSDM" alias="在用科室" type="long" width="100" length="18">
		<dic id="phis.dictionary.department"/>
	</item>
	<item id="DWXH" alias="单位序号" type="long" display="0" length="12"/>
	<item id="DWMC" alias="供货单位" type="string" width="120" length="12" virtual="true"/>
	<item id="CCBH" alias="出厂编号" type="string" width="100" length="50"/>
	<item id="WZDW" alias="物资单位" type="string" length="10"/>
	<item id="WZDJ" alias="物资价格" type="double" width="100" length="18" precision="4"/>
	<item id="GRRQ" alias="购入日期" type="date" display="0"/>
	<item id="GRGH" alias="购入人" type="string" length="10" display="0"/>
	<item id="JLQJFL" alias="计量器具分类" type="int" width="100" length="2">
		<dic id="phis.dictionary.jlqjfl"/>
	</item>
	<item id="JLLB" alias="计量类别" type="int" length="1">
		<dic id="phis.dictionary.jlfl"/>
	</item>
	<item id="CLFW" alias="测量范围" type="string" length="30" display="0"/>
	<item id="ZQDJ" alias="准确度等级" type="string" length="10" display="0"/>
	<item id="FDZ" alias="分度值" type="string" length="10" display="0"/>
	<item id="QJBZ" alias="强检标志" type="int" length="1" display="0"/>
	<item id="DDMC" alias="地点名称" type="string" length="50" display="0"/>
	<item id="CCRQ" alias="出厂日期" type="date" display="0"/>
	<item id="SJQD" alias="时间区段" type="string" length="14" display="0"/>
	<item id="BGGH" alias="保管人" type="string" length="10" display="0"/>
	<item id="JDZQ" alias="检定周期" type="int" length="2" display="0"/>
	<item id="XCJD" alias="下次检定" type="date" display="0"/>
	<item id="DQJDXH" alias="当前检定序号" type="string" length="10" display="0"/>
	<item id="JDRQ" alias="检定日期" type="date" display="0"/>
	<item id="JDJG" alias="检定结果" type="int" length="1" display="0"/>
	<item id="JDJL" alias="检定结论" type="int" length="1" display="0"/>
	<item id="HGZH" alias="合格证号" type="string" length="20" display="0"/>
	<item id="ZFBZ" alias="作废标志" type="int" length="1" display="0"/>
</entry>
